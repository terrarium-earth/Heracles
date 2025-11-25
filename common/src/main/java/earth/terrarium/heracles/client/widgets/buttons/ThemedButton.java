package earth.terrarium.heracles.client.widgets.buttons;

import dev.emi.emi.api.widget.Widget;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.theme.GenericTheme;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface ThemedButton {
    ResourceLocation SPRITE_UNPRESSED = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/unpressed");
    ResourceLocation SPRITE_PRESSED = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/pressed");
    ResourceLocation SPRITE_UNPRESSED_HOVERED = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/unpressed_hovered");
    ResourceLocation SPRITE_PRESSED_HOVERED = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/pressed_hovered");
    ResourceLocation SPRITE_DISABLED = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/disabled");
    ResourceLocation SPRITE_COMPLETABLE = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/completable");
    ResourceLocation SPRITE_COMPLETABLE_HOVERED = ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "buttons/completable_hovered");


    default ResourceLocation getSprite(boolean active, boolean hovered) {
        if (active) {
            return hovered ? SPRITE_COMPLETABLE_HOVERED : SPRITE_COMPLETABLE;
        } else {
            return hovered ? SPRITE_DISABLED : SPRITE_DISABLED;
        }
    }

    default int getTextColor(boolean active, float alpha) {
        return GenericTheme.getButton(active) | Mth.ceil(alpha * 255.0F) << 24;
    }

    static SimpleThemedButton.Builder builder(Component component, Button.OnPress onPress) {
        return new SimpleThemedButton.Builder(component, onPress);
    }

    class SimpleThemedButton extends Button implements ThemedButton {
        private final WidgetSprites sprites;

        public static WidgetSprites woodStyleButtons = new WidgetSprites(
            SPRITE_PRESSED ,
            SPRITE_UNPRESSED,
            SPRITE_PRESSED_HOVERED,
            SPRITE_UNPRESSED_HOVERED);

        protected SimpleThemedButton(@Nullable WidgetSprites sprites, int x, int y, int w, int h, Component component, OnPress onPress, CreateNarration createNarration) {
            super(x, y, w, h, component, onPress, createNarration);
            this.sprites = sprites;
        }

        @Override
        public ResourceLocation getSprite(boolean active, boolean hovered) {
            return Objects.requireNonNullElseGet(sprites, () -> new WidgetSprites(SPRITE_COMPLETABLE, SPRITE_DISABLED, SPRITE_COMPLETABLE_HOVERED, SPRITE_DISABLED)).get(active, hovered);
        }

        public static class Builder extends Button.Builder implements ThemedButton {
            private final Button.OnPress onPress;
            private Button.CreateNarration createNarration = Button.DEFAULT_NARRATION;
            private @Nullable WidgetSprites sprites;

            public Builder(Component component, Button.OnPress onPress) {
                super(component, onPress);
                this.onPress = onPress;
            }

            @Override
            public @NotNull Button.Builder createNarration(CreateNarration createNarration) {
                this.createNarration = createNarration;
                return this;
            }

            public @NotNull Builder sprites(@Nullable WidgetSprites sprites) {
                this.sprites = sprites;
                return this;
            }


            @Override
            public @NotNull Button build() {
                Button button = super.build();
                SimpleThemedButton themedButton = new SimpleThemedButton(sprites,
                    button.getX(), button.getY(),
                    button.getWidth(), button.getHeight(),
                    button.getMessage(), onPress, createNarration
                );
                themedButton.setTooltip(button.getTooltip());
                return themedButton;
            }
        }
    }
}
