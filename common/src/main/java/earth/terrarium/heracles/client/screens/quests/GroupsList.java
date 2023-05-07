package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GroupsList extends SelectionList<GroupsList.Entry> {

    public GroupsList(int x, int y, int width, int height, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, 20, onSelection);
    }

    public void update(List<String> groups, String selected) {
        List<Entry> entries = new ArrayList<>(groups.size());
        Entry selectedEntry = null;
        for (String group : groups) {
            Entry entry = new Entry(group);
            if (group.equals(selected)) {
                selectedEntry = entry;
            }
            entries.add(entry);
        }
        updateEntries(entries);
        if (selectedEntry != null) {
            setSelected(selectedEntry);
        }
    }

    public static class Entry extends ListEntry {

        private final String name;

        public Entry(String name) {
            this.name = name;
        }

        @Override
        protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            Gui.fill(stack, left, top, left + width, top + height, selected ? 0x11FFFFFF : 0x11808080);
            if (hovered) {
                Gui.renderOutline(stack, left, top, width, height, 0x33FFFFFF);
            }
            Gui.drawCenteredString(stack, Minecraft.getInstance().font, name, left + width / 2, top + height / 2 - 4, 0xFFFFFF);
            CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
        }

        @Override
        public void setFocused(boolean bl) {

        }

        @Override
        public boolean isFocused() {
            return false;
        }

        public String name() {
            return name;
        }
    }
}
