package screret.sas.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.item.ModItems;

@Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    private static BossWizardEntity hallucination;
    private static final int MIN_DISTANCE = 6;

    @SubscribeEvent
    public static void renderEyeHallucinations(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            if (Minecraft.getInstance().player.getInventory().contains(new ItemStack(ModItems.CTHULHU_EYE.get()))) {
                var level = Minecraft.getInstance().level;
                var clientPlayer = Minecraft.getInstance().player;
                var camEntPos = clientPlayer.position();

                if (hallucination == null) {
                    hallucination = ModEntities.BOSS_WIZARD.get().create(level);
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

                double x = Mth.lerp(event.getPartialTick(), hallucination.xOld, hallucination.getX());
                double y = Mth.lerp(event.getPartialTick(), hallucination.yOld, hallucination.getY());
                double z = Mth.lerp(event.getPartialTick(), hallucination.zOld, hallucination.getZ());
                float headYRot = Mth.lerp(event.getPartialTick(), hallucination.yRotO, hallucination.getYRot());

                double playerX = Mth.lerp(event.getPartialTick(), clientPlayer.xOld, camEntPos.x);
                double playerY = Mth.lerp(event.getPartialTick(), clientPlayer.yOld, camEntPos.y);
                double playerZ = Mth.lerp(event.getPartialTick(), clientPlayer.zOld, camEntPos.z);

                renderer.render(
                        hallucination,
                        x - playerX,
                        y - playerY,
                        z - playerZ,
                        headYRot,
                        event.getPartialTick(),
                        event.getPoseStack(),
                        Minecraft.getInstance().renderBuffers().bufferSource(),
                        renderer.getPackedLightCoords(hallucination, event.getPartialTick())
                );
            }

        }
    }

    private static void moveHallucination(Vec3 camEntPos, Level level) {
        var randX = Util.randomInRange(level.getRandom(), -10, 10);
        var randY = Util.randomInRange(level.getRandom(), -5, 5);
        var randZ = Util.randomInRange(level.getRandom(), -5, -10);
        hallucination.moveTo(camEntPos.x + randX, camEntPos.y + randY, camEntPos.z + randZ);
    }

    private static void playHallucinationSound(Level level) {
        switch (level.getRandom().nextInt(3)) {
            case 0 ->
                    hallucination.playSound(SoundEvents.WITCH_AMBIENT, 10, 1);
            case 1 ->
                    hallucination.playSound(SoundEvents.CREEPER_PRIMED, 10, 1);
            case 2 ->
                    hallucination.playSound(SoundEvents.EVOKER_AMBIENT, 10, 1);
            case 3 ->
                    hallucination.playSound(SoundEvents.BEACON_AMBIENT, 10, 1);
        }
    }

}
