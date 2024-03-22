package screret.sas.integration.rei.wand;

import net.minecraft.world.item.ItemStack;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.ability.WandAbilityProvider;

public class WandSubtypeInterpreter implements Subtype<ItemStack> {
    public static final WandSubtypeInterpreter INSTANCE = new WandSubtypeInterpreter();

    private WandSubtypeInterpreter() {

    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        if (!itemStack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
            return IIngredientSubtypeInterpreter.NONE;
        }
        ICapabilityWandAbility cap = itemStack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
        String mainAbility = cap.getAbility().getId().toString();
        String crouchAbility = cap.getCrouchAbility() == null ? "" : cap.getCrouchAbility().getId().toString();
        return mainAbility + ";" + crouchAbility;
    }
}
