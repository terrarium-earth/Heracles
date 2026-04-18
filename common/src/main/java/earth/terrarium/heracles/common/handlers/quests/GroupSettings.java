package earth.terrarium.heracles.common.handlers.quests;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GroupSettings {

    private ItemStack icon;
    private boolean iconEnabled;

    public GroupSettings() {
        this(new ItemStack(Items.BOOK), false);
    }

    public GroupSettings(ItemStack icon, boolean iconEnabled) {
        this.icon = icon;
        this.iconEnabled = iconEnabled;
    }

    public ItemStack icon() {
        return this.icon;
    }

    public boolean iconEnabled() {
        return this.iconEnabled;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setIconEnabled(boolean iconEnabled) {
        this.iconEnabled = iconEnabled;
    }

    public String serializeIcon() {
        if (this.icon.isEmpty()) return "";
        return BuiltInRegistries.ITEM.getKey(this.icon.getItem()).toString();
    }

    public static ItemStack deserializeIcon(String id) {
        if (id == null || id.isEmpty()) return new ItemStack(Items.BOOK);
        try {
            ResourceLocation loc = ResourceLocation.parse(id);
            return new ItemStack(BuiltInRegistries.ITEM.get(loc));
        } catch (Exception e) {
            return new ItemStack(Items.BOOK);
        }
    }
}
