package dev.screret.mitm.client.event;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.entity.BossWizardEntity;
import dev.screret.mitm.data.MITMEntityTypes;
import dev.screret.mitm.data.MITMItems;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = MagicOfTheMind.MODID, value = Dist.CLIENT)
public class ClientForgeEvents {

    private static BossWizardEntity hallucination;
    private static final int MIN_DISTANCE = 6;

    @SubscribeEvent
    public static void renderEyeHallucinations(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            if (Minecraft.getInstance().player.getInventory().contains(new ItemStack(MITMItems.CTHULHU_EYE.get()))) {
                var level = Minecraft.getInstance().level;
                var clientPlayer = Minecraft.getInstance().player;
                var camEntPos = clientPlayer.position();

                if (hallucination == null) {
                    hallucination = MITMEntityTypes.BOSS_WIZARD.get().create(level);
                    hallucination.setSilent(false);
                    hallucination.moveTo(camEntPos);
                    hallucination.setInvulnerable(true);
                    hallucination.setNoAi(true);
                    level.addFreshEntity(hallucination);
                }

                if (camEntPos.distanceToSqr(hallucination.position()) < MIN_DISTANCE * MIN_DISTANCE) {
                    playHallucinationSound(level);
                    moveHallucination(camEntPos, level);
                }

                if (clientPlayer.tickCount % level.getRandom().nextIntBetweenInclusive(80, 120) == 0) {
                    playHallucinationSound(level);

                    if (level.getRandom().nextInt(3) > 1) {
                        hallucination.setInvisible(true);
                    } else {
                        hallucination.setInvisible(false);
                        moveHallucination(camEntPos, level);
                    }
                }

                var renderer = Minecraft.getInstance().getEntityRenderDispatcher();

                double xPos = hallucination.getX() - camEntPos.x;
                double zPos = hallucination.getZ() - camEntPos.z;
                hallucination.setYRot((float) ((Mth.atan2(xPos, zPos) * Mth.RAD_TO_DEG) - 90.0F));

                float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

                double x = Mth.lerp(partialTick, hallucination.xOld, hallucination.getX());
                double y = Mth.lerp(partialTick, hallucination.yOld, hallucination.getY());
                double z = Mth.lerp(partialTick, hallucination.zOld, hallucination.getZ());
                float headYRot = Mth.lerp(partialTick, hallucination.yRotO, hallucination.getYRot());

                double playerX = Mth.lerp(partialTick, clientPlayer.xOld, camEntPos.x);
                double playerY = Mth.lerp(partialTick, clientPlayer.yOld, camEntPos.y);
                double playerZ = Mth.lerp(partialTick, clientPlayer.zOld, camEntPos.z);

                renderer.render(
                        hallucination,
                        x - playerX,
                        y - playerY,
                        z - playerZ,
                        headYRot,
                        partialTick,
                        event.getPoseStack(),
                        Minecraft.getInstance().renderBuffers().bufferSource(),
                        renderer.getPackedLightCoords(hallucination, partialTick));
            }

        }
    }

    private static void moveHallucination(Vec3 camEntPos, Level level) {
        var randX = MITMUtil.randomInRange(level.getRandom(), -10, 10);
        var randY = MITMUtil.randomInRange(level.getRandom(), -5, 5);
        var randZ = MITMUtil.randomInRange(level.getRandom(), -5, -10);
        hallucination.moveTo(camEntPos.x + randX, camEntPos.y + randY, camEntPos.z + randZ);
    }

    private static void playHallucinationSound(Level level) {
        switch (level.getRandom().nextInt(3)) {
            case 0 -> hallucination.playSound(SoundEvents.WITCH_AMBIENT, 10, 1);
            case 1 -> hallucination.playSound(SoundEvents.CREEPER_PRIMED, 10, 1);
            case 2 -> hallucination.playSound(SoundEvents.EVOKER_AMBIENT, 10, 1);
            case 3 -> hallucination.playSound(SoundEvents.BEACON_AMBIENT, 10, 1);
        }
    }
}
