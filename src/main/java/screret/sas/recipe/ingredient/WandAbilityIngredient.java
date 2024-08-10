package screret.sas.recipe.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import screret.sas.Util;
import screret.sas.api.capability.ability.CapabilityWandAbility;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.item.ModItems;
import screret.sas.item.item.WandCoreItem;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class WandAbilityIngredient extends AbstractIngredient {
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

    public static WandAbilityIngredient fromCapability(CapabilityWandAbility cap, Item item) {
        return new WandAbilityIngredient(cap.getMainAbility(), cap.getCrouchAbility(), item, cap.getPoweredUp());
    }

    @Nullable
    public static WandAbilityIngredient fromStack(ItemStack stack) {
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
            var cap = stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
            return new WandAbilityIngredient(cap.getMainAbility(), cap.getCrouchAbility(), stack.getItem(), cap.getPoweredUp());
        } else if(stack.is(ModItems.WAND_CORE.get())) {
            return new WandAbilityIngredient(new WandAbilityInstance(WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(new ResourceLocation(stack.getTag().getString(WandCoreItem.ABILITY_KEY)))), null, stack.getItem(), false);
        }
        return null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public ItemStack getStack() {
        return Util.createWand(this.item, this.ability, this.crouchAbility);
    }

    public boolean test(@Nullable ItemStack input)
    {
        if (input == null)
            return false;
        boolean isCorrectItem = this.item == input.getItem();
        if(this.item == ModItems.WAND.get()) {
            boolean hasCorrectAbility;
            if(input.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
                var cap = input.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
                hasCorrectAbility = cap.getMainAbility().equals(this.ability) && cap.getCrouchAbility().equals(this.crouchAbility) && cap.getPoweredUp() == this.isPoweredUp;
            } else {
                return false;
            }
            return isCorrectItem && hasCorrectAbility;
        } else if(this.item == ModItems.WAND_CORE.get()) {
            if(input.getTag().contains(WandCoreItem.ABILITY_KEY, Tag.TAG_COMPOUND)) {
                return new WandAbilityInstance(input.getTag().getCompound(WandCoreItem.ABILITY_KEY)).equals(ability);
            }
        }
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return WandAbilityIngredient.Serializer.INSTANCE;
    }


    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(WandAbilityIngredient.Serializer.INSTANCE).toString());
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(item).toString());
        JsonObject main = new JsonObject();
        recurseChildren(main, ability);

        json.add(WandCoreItem.ABILITY_KEY, main);

        if (crouchAbility != null) {
            JsonObject crouch = new JsonObject();
            recurseChildren(crouch, crouchAbility);
            json.add("crouch_ability", crouch);
        }
        json.addProperty("powered_up", this.isPoweredUp);
        return json;
    }

    private void recurseChildren(JsonObject in, WandAbilityInstance ability) {
        in.addProperty("id", ability.getId().toString());
        JsonArray children = new JsonArray();
        if(ability.getChildren() != null) {
            for (var child : ability.getChildren()) {
                JsonObject childJson = new JsonObject();
                recurseChildren(childJson, child);
                children.add(childJson);
            }
        }
        in.add("children", children);
    }

    public static class Serializer implements IIngredientSerializer<WandAbilityIngredient> {
        public static final WandAbilityIngredient.Serializer INSTANCE = new WandAbilityIngredient.Serializer();

        @Override
        public WandAbilityIngredient parse(FriendlyByteBuf buffer) {
            var item = buffer.<Item>readRegistryId();
            WandAbilityInstance ability = new WandAbilityInstance(buffer.readNbt());
            WandAbilityInstance crouch = null;
            if(buffer.readBoolean()) {
                crouch = new WandAbilityInstance(buffer.readNbt());
            }
            return new WandAbilityIngredient(ability, crouch, item, buffer.readBoolean());
        }

        @Override
        public WandAbilityIngredient parse(JsonObject json) {
            WandAbilityInstance main = unRecurseChildren(GsonHelper.getAsJsonObject(json, WandCoreItem.ABILITY_KEY));


            WandAbilityInstance crouch = null;
            if(json.has("crouch_ability")) {
                crouch = unRecurseChildren(GsonHelper.getAsJsonObject(json, "crouch_ability"));
            }

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "item")));

            return new WandAbilityIngredient(main, crouch, item, GsonHelper.getAsBoolean(json, "powered_up"));
        }

        private WandAbilityInstance unRecurseChildren(JsonObject object) {
            WandAbilityInstance abilityInstance = new WandAbilityInstance(Util.getAbilityFromJson(object, "id"));
            if(object.has("children")) {
                var childrenJson = object.getAsJsonArray("children");
                for(int i = 0; i < childrenJson.size(); ++i) {
                    if(childrenJson.get(i).isJsonObject()) {
                        abilityInstance.getChildren().add(unRecurseChildren(childrenJson.get(i).getAsJsonObject()));
                    }
                }
            }
            return abilityInstance;
        }

        @Override
        public void write(FriendlyByteBuf buffer, WandAbilityIngredient ingredient) {
            Item item = ingredient.item;
            buffer.writeRegistryId(ForgeRegistries.ITEMS, item);
            buffer.writeNbt(ingredient.ability.serializeNBT());
            if(ingredient.crouchAbility != null) {
                buffer.writeBoolean(true);
                buffer.writeNbt(ingredient.crouchAbility.serializeNBT());
            } else {
                buffer.writeBoolean(false);
            }
            buffer.writeBoolean(ingredient.isPoweredUp);
        }
    }
}
