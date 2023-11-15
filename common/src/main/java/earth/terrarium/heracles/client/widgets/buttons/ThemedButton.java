package earth.terrarium.heracles.client.widgets.buttons;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.theme.GenericTheme;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ThemedButton {
    ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/buttons.png");

    default ResourceLocation getTexture() {
        return TEXTURE;
    }

    default TextureBounds getTextureBounds(boolean active, boolean hovered) {
        return new TextureBounds(0, getTextureY(active, hovered), 20, 4, 200, 20);
    }

    default int getTextureY(boolean active, boolean hovered) {
      return active ? (hovered ? 40 : 20) : 0;
    }

    default int getTextColor(boolean active, float alpha) {
        return GenericTheme.getButton(active) | Mth.ceil(alpha * 255.0F) << 24;
    }

    record TextureBounds(int sourceX, int sourceY, int sliceWidth, int sliceHeight, int sourceWidth, int sourceHeight) {
    }

    static SimpleThemedButton.Builder builder(Component component, Button.OnPress onPress) {
        return new SimpleThemedButton.Builder(component, onPress);
    }

    class SimpleThemedButton extends Button implements ThemedButton {
        private final @Nullable ResourceLocation texture;
        private final int yOffset;

        protected SimpleThemedButton(@Nullable ResourceLocation texture, int yOffset, int x, int y, int w, int h, Component component, OnPress onPress, CreateNarration createNarration) {
            super(x, y, w, h, component, onPress, createNarration);
            this.texture = texture;
            this.yOffset = yOffset;
        }

        @Override
        public ResourceLocation getTexture() {
            return texture != null ? texture : ThemedButton.super.getTexture();
        }

        @Override
        public int getTextureY(boolean active, boolean hovered) {
            return yOffset + ThemedButton.super.getTextureY(active, hovered);
        }

        public static class Builder extends Button.Builder implements ThemedButton {
            private final Button.OnPress onPress;
            private Button.CreateNarration createNarration = Button.DEFAULT_NARRATION;
            private @Nullable ResourceLocation texture = null;
            private int yOffset = 0;

            public Builder(Component component, Button.OnPress onPress) {
                super(component, onPress);
                this.onPress = onPress;
            }

            @Override
            public @NotNull Button.Builder createNarration(CreateNarration createNarration) {
                this.createNarration = createNarration;
                return this;
            }

            public @NotNull Builder textureYOffset(int yOffset) {
                this.yOffset = yOffset;
                return this;
            }

            public @NotNull Builder texture(ResourceLocation texture) {
                this.texture = texture;
                return this;
            }

            @Override
            public @NotNull Button build() {
                Button button = super.build();
                return new SimpleThemedButton(texture, yOffset, button.getX(), button.getY(), button.getWidth(), button.getHeight(), button.getMessage(), onPress, createNarration);
            }
        }
    }
}
