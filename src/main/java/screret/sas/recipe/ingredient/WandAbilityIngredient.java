package screret.sas.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import screret.sas.Util;
import screret.sas.api.capability.ability.CapabilityWandAbility;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.item.ModItems;
import screret.sas.item.item.WandCoreItem;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class WandAbilityIngredient extends Ingredient {
    public static final Codec<WandAbilityIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WandAbilityInstance.CODEC.fieldOf("ability").forGetter(inst -> inst.ability),
            WandAbilityInstance.CODEC.optionalFieldOf("crouch_ability", null).forGetter(inst -> inst.crouchAbility),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(inst -> inst.item),
            Codec.BOOL.optionalFieldOf("powered_up", false).forGetter(inst -> inst.isPoweredUp)
    ).apply(instance, WandAbilityIngredient::new));

    private final WandAbilityInstance ability;
    @Nullable
    private final WandAbilityInstance crouchAbility;

    private final boolean isPoweredUp;
    private final Item item;

    public WandAbilityIngredient(WandAbilityInstance ability, Item item, boolean isPoweredUp) {
        super(Stream.of(new ItemValue(item.getDefaultInstance())));
        this.ability = ability;
        this.crouchAbility = null;
        this.item = item;
        this.isPoweredUp = isPoweredUp;
    }

    public WandAbilityIngredient(WandAbilityInstance ability, @Nullable WandAbilityInstance crouchAbility, Item item, boolean isPoweredUp) {
        super(Stream.of(new ItemValue(item.getDefaultInstance())));
        this.ability = ability;
        this.item = item;
        this.crouchAbility = crouchAbility;
        this.isPoweredUp = isPoweredUp;
    }

    public static WandAbilityIngredient fromCapability(CapabilityWandAbility cap, Item item){
        return new WandAbilityIngredient(cap.getAbility(), cap.getCrouchAbility(), item, cap.getPoweredUp());
    }

    @Nullable
    public static WandAbilityIngredient fromStack(ItemStack stack){
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY) != null) {
            var cap = stack.getCapability(WandAbilityProvider.WAND_ABILITY);
            return new WandAbilityIngredient(cap.getAbility(), cap.getCrouchAbility(), stack.getItem(), cap.getPoweredUp());
        } else if(stack.is(ModItems.WAND_CORE.get())){
            return new WandAbilityIngredient(new WandAbilityInstance(WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get(new ResourceLocation(stack.getTag().getString(WandCoreItem.ABILITY_KEY)))), null, stack.getItem(), false);
        }
        return null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public ItemStack getStack(){
        return Util.createWand(this.item, this.ability, this.crouchAbility);
    }

    public boolean test(@Nullable ItemStack input)
    {
        if (input == null)
            return false;
        boolean isCorrectItem = this.item == input.getItem();
        if(this.item == ModItems.WAND.get()){
            boolean hasCorrectAbility;
            if(input.getCapability(WandAbilityProvider.WAND_ABILITY) != null) {
                var cap = input.getCapability(WandAbilityProvider.WAND_ABILITY);
                hasCorrectAbility = cap.getAbility().equals(this.ability) && cap.getCrouchAbility().equals(this.crouchAbility) && cap.getPoweredUp() == this.isPoweredUp;
            } else {
                return false;
            }
            return isCorrectItem && hasCorrectAbility;
        } else if(this.item == ModItems.WAND_CORE.get()){
            if(input.getTag().contains(WandCoreItem.ABILITY_KEY, Tag.TAG_COMPOUND)){
                return new WandAbilityInstance(input.getTag().getCompound(WandCoreItem.ABILITY_KEY)).equals(ability);
            }
        }
        return false;
    }
}
