package earth.terrarium.heracles.client.screens.quest.editing;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.editor.MultiLineEditBox;
import earth.terrarium.heracles.client.widgets.editor.MultilineTextField;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class QuestMultiLineEditBox extends MultiLineEditBox implements CursorWidget {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/editor.png");

    private CursorScreen.Cursor cursor = null;
    
    public QuestMultiLineEditBox(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y + 13, width, height - 13, text -> {
            if (text.startsWith("> ")) {
                return Component.literal("> ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(2)).withStyle(ChatFormatting.DARK_GRAY));
            } else if (text.startsWith("- ")) {
                return Component.literal("- ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(2)).withStyle(ChatFormatting.DARK_GRAY));
            } else if (text.startsWith("# ")) {
                return Component.literal("# ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(2)).withStyle(ChatFormatting.DARK_GRAY));
            } else if (text.startsWith("## ")) {
                return Component.literal("## ").withStyle(ChatFormatting.DARK_AQUA)
                    .append(Component.literal(text.substring(3)).withStyle(ChatFormatting.DARK_GRAY));
            } else if (text.equals("---")) {
                return Component.literal("---").withStyle(ChatFormatting.DARK_AQUA);
            }
            return Component.literal(text);
        });
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.cursor = null;
        graphics.fill(this.getX() - 1, this.getY() - 13, this.getX() + this.width + 1, this.getY() + this.height + 1, 0xFF000000);
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFFF0F0F0);
        super.render(graphics, mouseX, mouseY, partialTicks);

        try (var ignored = RenderUtils.createScissor(Minecraft.getInstance(), graphics, this.getX(), this.getY() - 12, this.width, 11)) {

            graphics.fill(this.getX(), this.getY() - 12, this.getX() + width, this.getY() - 1, 0xFFC6C6C6);
            graphics.fill(this.getX(), this.getY() - 1, this.getX() + width, this.getY(), 0xFF000000);

            graphics.blit(TEXTURE, this.getX() + 20, this.getY() - 12, 0, isHovered(mouseX, mouseY, 0) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, this.getX() + 31, this.getY() - 12, 11, isHovered(mouseX, mouseY, 1) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, this.getX() + 42, this.getY() - 12, 22, isHovered(mouseX, mouseY, 2) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, this.getX() + 53, this.getY() - 12, 33, isHovered(mouseX, mouseY, 3) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, this.getX() + 64, this.getY() - 12, 44, isHovered(mouseX, mouseY, 4) ? 11 : 0, 11, 11);
            graphics.blit(TEXTURE, this.getX() + 75, this.getY() - 12, 55, isHovered(mouseX, mouseY, 5) ? 11 : 0, 11, 11);

            for (int i = 0; i < 8; i++) {
                graphics.blit(TEXTURE, this.getX() + 97 + i * 11, this.getY() - 12, i * 11, isHovered(mouseX, mouseY, 7 + i) ? 33 : 22, 11, 11);
            }
            for (int i = 0; i < 8; i++) {
                graphics.blit(TEXTURE, this.getX() + 185 + i * 11, this.getY() - 12, i * 11, isHovered(mouseX, mouseY, 15 + i) ? 55 : 44, 11, 11);
            }
        }

        if (this.cursor == null && !canClickText(mouseX, mouseY) && isMouseOver(mouseX, mouseY)) {
            cursor = CursorScreen.Cursor.DEFAULT;
        }
    }

    public boolean isHovered(int mouseX, int mouseY, int index) {
        if (mouseX >= this.getX() + 20 + index * 11 && mouseX < this.getX() + 20 + index * 11 + 11 && mouseY >= this.getY() - 12 && mouseY <= this.getY() - 1) {
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
                if (mouseX >= this.getX() + x && mouseX < this.getX() + x + 11 && value.change(this.field)) {
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
        return mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() - 13 && mouseY <= this.getY() + this.height;
    }

    public boolean canClickText(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }

    private enum Buttons {
        BOLD("**"),
        ITALIC("--"),
        UNDERLINE("__"),
        STRIKETHROUGH("~~"),
        BLOCKQUOTE(content -> {
            MultilineTextField.StringView selection = content.getSelected();
            blockquote(content, selection.beginIndex(), selection.endIndex());
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
        private final Function<MultilineTextField, Boolean> change;

        Buttons(String text) {
            this((MultilineTextField content) -> {
                if (!content.hasSelection()) return false;
                content.insertText(text + content.getSelectedText() + text);
                return true;
            });
        }
        Buttons(Function<MultilineTextField, Boolean> change) {
            this.change = change;
        }


        public boolean change(MultilineTextField field) {
            return this.change.apply(field);
        }
    }

    private static void blockquote(MultilineTextField content, int min, int max) {
        String[] values = content.value().split("\n");

        boolean add = false;
        int count = 0;
        for (String s : values) {
            int newCount = count + s.length();
            if ((count >= min && count <= max) || (newCount >= min && newCount <= max)) {
                if (!s.startsWith("> ")) {
                    add = true;
                    break;
                }
            }
            count = newCount + 1;
        }

        List<String> newValue = new ArrayList<>();
        count = 0;

        for (String s : values) {
            int newCount = count + s.length();
            if ((count >= min && count <= max) || (newCount >= min && newCount <= max)) {
                if (s.startsWith("> ")) {
                    if (add) {
                        newValue.add(s);
                        continue;
                    }
                    newValue.add(s.substring(2));
                } else if (add) {
                    newValue.add("> " + s);
                }
            } else {
                newValue.add(s);
            }
            count = newCount + 1;
        }

        int cursor = content.cursor();
        int selection = content.selectCursor();

        boolean changed = !String.join("\n", newValue).equals(content.value());
        if (!changed) return;

        content.setValue(String.join("\n", newValue), true);

        if (cursor == selection) {
            int offset = add ? 2 : -2;
            content.setCursor(Mth.clamp(cursor + offset, 0, content.value().length()));
        }
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        if (cursor != null) {
            return cursor;
        }
        return CursorScreen.Cursor.TEXT;
    }
}
