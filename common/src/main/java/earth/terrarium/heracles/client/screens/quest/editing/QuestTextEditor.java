package earth.terrarium.heracles.client.screens.quest.editing;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.editor.TextEditor;
import earth.terrarium.heracles.client.widgets.editor.TextEditorContent;
import earth.terrarium.heracles.mixins.client.FontManagerAccessor;
import earth.terrarium.heracles.mixins.client.MinecraftAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2i;

import java.util.function.Function;

public class QuestTextEditor extends TextEditor {

    private CursorScreen.Cursor cursor = null;

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/editor.png");
    private static final Font FONT = new Font(id -> ((FontManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(Heracles.MOD_ID, "monocraft")), false);

    public QuestTextEditor(int x, int y, int width, int height) {
        super(x, y + 13, width, height, 0x000000, 0x000000, FONT, text -> {
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
        this.cursor = null;
        graphics.fill(this.x - 1, this.y - 13, this.x + this.width + 1, this.y + this.height + 1, 0xFF000000);
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFFF0F0F0);
        super.render(graphics, mouseX, mouseY, partialTicks);

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y - 12, this.width, 11)) {

            graphics.fill(x, y - 12, x + width, y - 1, 0xFFC6C6C6);
            graphics.fill(x, y - 1, x + width, y, 0xFF000000);

            graphics.blit(TEXTURE, x + 20, y - 12, 0, isHovered(mouseX, mouseY, 0) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, x + 31, y - 12, 11, isHovered(mouseX, mouseY, 1) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, x + 42, y - 12, 22, isHovered(mouseX, mouseY, 2) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, x + 53, y - 12, 33, isHovered(mouseX, mouseY, 3) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, x + 64, y - 12, 44, isHovered(mouseX, mouseY, 4) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, x + 75, y - 12, 55, isHovered(mouseX, mouseY, 5) ? 11 : 0, 11, 11);

            for (int i = 0; i < 8; i++) {
                graphics.blit(TEXTURE, x + 97 + i * 11, y - 12, i * 11, isHovered(mouseX, mouseY, 7 + i) ? 33 : 22, 11, 11);
            }
            for (int i = 0; i < 8; i++) {
                graphics.blit(TEXTURE, x + 174 + i * 11, y - 12, i * 11, isHovered(mouseX, mouseY, 14 + i) ? 55 : 44, 11, 11);
            }
        }

        if (this.cursor == null && !canClickText(mouseX, mouseY) && isMouseOver(mouseX, mouseY)) {
            cursor = CursorScreen.Cursor.DEFAULT;
        }
    }

    public boolean isHovered(int mouseX, int mouseY, int index) {
        if (mouseX >= this.x + 20 + index * 11 && mouseX < this.x + 20 + index * 11 + 11 && mouseY >= this.y - 12 && mouseY <= this.y - 1) {
            cursor = CursorScreen.Cursor.POINTER;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && !canClickText(mouseX, mouseY) && button == 0) {
            int x = 20;
            for (Buttons value : Buttons.values()) {
                if (mouseX >= this.x + x && mouseX < this.x + x + 11 && value.change(this.content)) {
                    return true;
                }
                x += 11;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y - 13 && mouseY <= this.y + this.height;
    }

    @Override
    public boolean canClickText(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    private enum Buttons {
        BOLD("**"),
        ITALIC("--"),
        UNDERLINE("__"),
        STRIKETHROUGH("~~"),
        BLOCKQUOTE(content -> {
            Vector2i selection = content.selection();
            if (selection == null) {
                blockquote(content, content.cursor().y(), content.cursor().y());
            } else {
                int min = Math.min(selection.y(), content.cursor().y());
                int max = Math.max(selection.y(), content.cursor().y());
                blockquote(content, min, max);
            }
            return true;
        }),
        OBFUSCATED("||"),
        EMPTY(content -> false),
        DARK_RED("/4/"),
        RED("/c/"),
        GOLD("/6/"),
        YELLOW("/e/"),
        DARK_GREEN("/2/"),
        GREEN("/a/"),
        AQUA("/b/"),
        DARK_AQUA("/3/"),
        DARK_BLUE("/1/"),
        BLUE("/9/"),
        LIGHT_PURPLE("/d/"),
        DARK_PURPLE("/5/"),
        WHITE("/f/"),
        GRAY("/7/"),
        DARK_GRAY("/8/"),
        BLACK("/0/"),
        ;
        private final Function<TextEditorContent, Boolean> change;

        Buttons(String text) {
            this((TextEditorContent content) -> {
                Vector2i selection = content.selection();
                if (selection == null) return false;
                if (selection.y() != content.cursor().y()) return false;
                content.addText(text + content.getSelectedText() + text);
                return true;
            });
        }
        Buttons(Function<TextEditorContent, Boolean> change) {
            this.change = change;
        }


        public boolean change(TextEditorContent content) {
            return this.change.apply(content);
        }
    }

    private static void blockquote(TextEditorContent content, int min, int max) {
        int lines = content.lines().size();
        boolean add = false;
        for (int i = min; i <= max; i++) {
            if (i >= lines) break;
            var s = content.lines().get(i);
            if (!s.startsWith("> ")) {
                add = true;
                break;
            }
        }

        for (int i = min; i <= max; i++) {
            if (i >= lines) break;
            var s = content.lines().get(i);
            if (s.startsWith("> ")) {
                if (add) continue;
                content.lines().set(i, s.substring(2));
            } else if (add) {
                content.lines().set(i, "> " + s);
            }
        }


        int x = Mth.clamp(content.cursor().x() + (add ? 2 : -2), 0, content.lines().get(content.cursor().y()).length());
        content.cursor().set(x, content.cursor().y());
        Vector2i selection = content.selection();
        if (selection != null) {
            x = Mth.clamp(selection.x() + (add ? 2 : -2), 0, content.lines().get(selection.y()).length());
            selection.set(x, selection.y());
        }
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        if (cursor != null) {
            return cursor;
        }
        return super.getCursor();
    }
}
