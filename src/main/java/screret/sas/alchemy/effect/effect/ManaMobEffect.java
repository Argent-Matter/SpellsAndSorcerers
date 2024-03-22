package screret.sas.alchemy.effect.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class ManaMobEffect extends MobEffect {
    public ManaMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            player.getCapability(ManaProvider.MANA).ifPresent((cap) -> {
                cap.setMaxManaStored(cap.getMaxManaStored() - 25 * (pAmplifier + 1));
            });
        }
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if(pLivingEntity instanceof Player player){
            player.getCapability(ManaProvider.MANA).ifPresent((cap) -> {
                cap.setMaxManaStored(cap.getMaxManaStored() + 25 * (pAmplifier + 1));
            });
        }
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }
}
