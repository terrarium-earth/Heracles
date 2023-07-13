package earth.terrarium.heracles.client.screens.quest.editing;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.editor.TextEditor;
import earth.terrarium.heracles.mixins.client.FontManagerAccessor;
import earth.terrarium.heracles.mixins.client.MinecraftAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class QuestTextEditor extends TextEditor {

    private static final Font FONT = new Font(id -> ((FontManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(Heracles.MOD_ID, "monocraft")), false);

    public QuestTextEditor(int x, int y, int width, int height) {
        super(x, y, width, height, 0x000000, 0x000000, FONT, text -> {
            if (text.startsWith("> ")) {
                return Component.literal("> ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(2)).withStyle(ChatFormatting.DARK_GRAY));
            } else if (text.startsWith("- ")) {
                return Component.literal("- ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(2)).withStyle(ChatFormatting.DARK_GRAY));
            } else if (text.startsWith("# ")) {
                return Component.literal("# ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(2)).withStyle(ChatFormatting.BLUE));
            } else if (text.startsWith("## ")) {
                return Component.literal("## ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(3)).withStyle(ChatFormatting.BLUE));
            } else if (text.equals("---")) {
                return Component.literal("---").withStyle(ChatFormatting.DARK_AQUA);
            }
            return Component.literal(text).withStyle(ChatFormatting.BLACK);
        });
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0xFF000000);
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFFF0F0F0);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
