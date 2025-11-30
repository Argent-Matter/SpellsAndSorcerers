package dev.screret.motm.client.event;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.entity.BossWizardEntity;
import dev.screret.motm.data.MOTMEntityTypes;
import dev.screret.motm.data.MOTMItems;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = MagicOfTheMind.MODID, value = Dist.CLIENT)
public class EyeHallucinationHandler {

    private static BossWizardEntity hallucination;
    private static final int MIN_DISTANCE_SQR = 6 * 6;
    private static int lastCheckTick = 0;

    @SubscribeEvent
    public static void renderEyeHallucinations(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        int currentTick = event.getRenderTick();

        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            return;
        }
        if (lastCheckTick != currentTick &&
                !player.getInventory().contains(stack -> stack.is(MOTMItems.CTHULHU_EYE.get()))) {
            return;
        }
        lastCheckTick = currentTick;

        Vec3 cameraPosition = player.position();

        if (hallucination == null) {
            hallucination = MOTMEntityTypes.BOSS_WIZARD.get().create(level);
            assert hallucination != null;
            hallucination.setSilent(false);
            hallucination.moveTo(cameraPosition);
            hallucination.setInvulnerable(true);
            hallucination.setNoAi(true);
            level.addFreshEntity(hallucination);
        }

        if (cameraPosition.distanceToSqr(hallucination.position()) < MIN_DISTANCE_SQR) {
            playHallucinationSound(level);
            moveHallucination(cameraPosition, level);
        }

        if (currentTick % level.getRandom().nextIntBetweenInclusive(80, 120) == 0) {
            playHallucinationSound(level);

            if (level.getRandom().nextInt(3) > 1) {
                hallucination.setInvisible(true);
            } else {
                hallucination.setInvisible(false);
                moveHallucination(cameraPosition, level);
            }
        }

        var renderer = Minecraft.getInstance().getEntityRenderDispatcher();

        double xPos = hallucination.getX() - cameraPosition.x;
        double zPos = hallucination.getZ() - cameraPosition.z;
        hallucination.setYRot((float) ((Mth.atan2(xPos, zPos) * Mth.RAD_TO_DEG) - 90.0F));

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        double x = Mth.lerp(partialTick, hallucination.xOld, hallucination.getX());
        double y = Mth.lerp(partialTick, hallucination.yOld, hallucination.getY());
        double z = Mth.lerp(partialTick, hallucination.zOld, hallucination.getZ());
        float headYRot = Mth.lerp(partialTick, hallucination.yRotO, hallucination.getYRot());

        double playerX = Mth.lerp(partialTick, player.xOld, cameraPosition.x);
        double playerY = Mth.lerp(partialTick, player.yOld, cameraPosition.y);
        double playerZ = Mth.lerp(partialTick, player.zOld, cameraPosition.z);

        renderer.render(hallucination, x - playerX, y - playerY, z - playerZ, headYRot,
                partialTick, event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(),
                renderer.getPackedLightCoords(hallucination, partialTick));
    }

    private static void moveHallucination(Vec3 cameraPosition, Level level) {
        var randX = MOTMUtil.randomInRange(level.getRandom(), -10, 10);
        var randY = MOTMUtil.randomInRange(level.getRandom(), -5, 5);
        var randZ = MOTMUtil.randomInRange(level.getRandom(), -5, -10);
        hallucination.moveTo(cameraPosition.x + randX, cameraPosition.y + randY, cameraPosition.z + randZ);
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
