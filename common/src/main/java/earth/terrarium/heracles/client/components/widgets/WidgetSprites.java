package earth.terrarium.heracles.client.components.widgets;

import net.minecraft.resources.ResourceLocation;

public record WidgetSprites(
    ResourceLocation normal,
    ResourceLocation hovered,
    ResourceLocation disabled
) {

    public WidgetSprites(ResourceLocation normal, ResourceLocation hovered) {
        this(normal, hovered, normal);
    }


    public ResourceLocation get(boolean hovered) {
        return this.get(hovered, false);
    }
    public ResourceLocation get(boolean hovered, boolean disabled) {
        if (disabled) {
            return this.disabled;
        } else if (hovered) {
            return this.hovered;
        } else {
            return this.normal;
        }
    }
}
