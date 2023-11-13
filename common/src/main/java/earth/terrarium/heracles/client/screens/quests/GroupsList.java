package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.utils.ThemeColors;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.DeleteGroupPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GroupsList extends SelectionList<GroupsList.Entry> {

    private final int width;

    public GroupsList(int x, int y, int width, int height, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, 20, onSelection, true);
        this.width = width;
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        if (!(Minecraft.getInstance().screen instanceof QuestsEditScreen screen) || !screen.isTemporaryWidgetVisible()) {
            super.setSelected(entry);
        }
    }

    private void internalSetSelected(@Nullable Entry entry) {
        super.setSelected(entry);
    }

    public void update(List<String> groups, String selected) {
        List<Entry> entries = new ArrayList<>(groups.size());
        Entry selectedEntry = null;
        for (String group : groups) {
            Entry entry = new Entry(this, group);
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

    public void addGroup(String group) {
        addEntry(new Entry(this, group));
    }

    public static class Entry extends ListEntry {

        private final GroupsList list;
        private final String name;

        public Entry(GroupsList list, String name) {
            this.list = list;
            this.name = name;
        }

        @Override
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            RenderSystem.enableBlend();
            graphics.blitNineSliced(AbstractQuestScreen.HEADING, left, top, width, height, 5, 64, 20, 192, selected ? 35 : 15);
            if (hovered) {
                graphics.blitNineSliced(AbstractQuestScreen.HEADING, left, top, width, height, 5, 64, 20, 192, 55);
            }
            RenderSystem.disableBlend();
            graphics.drawCenteredString(Minecraft.getInstance().font, name, left + width / 2, top + height / 2 - 4, ThemeColors.GROUP_NAME);
            CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen) {
                if (mouseX - left >= width - 11 && mouseX - left <= width - 2 && mouseY - top >= 2 && mouseY - top <= 12 && hovered) {
                    boolean cant = !ClientQuests.byGroup(name).isEmpty() || this.list.children().size() == 1;
                    CursorUtils.setCursor(cant, CursorScreen.Cursor.DISABLED);
                    ScreenUtils.setTooltip(cant ? ConstantComponents.Groups.DELETE_WITH_QUESTS : ConstantComponents.DELETE);
                    graphics.drawString(
                        Minecraft.getInstance().font,
                        ConstantComponents.X, left + width - 9, top + 2, 0xFFFFFF,
                        false
                    );
                } else if (hovered) {
                    graphics.drawString(
                        Minecraft.getInstance().font,
                        ConstantComponents.X, left + width - 9, top + 2, 0x808080,
                        false
                    );
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.list.getSelected() != this) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            boolean cant = !ClientQuests.byGroup(name).isEmpty() || this.list.children().size() == 1;
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen && button == 0 && !cant) {
                boolean closingButton = mouseX >= this.list.width - 11 && mouseX <= this.list.width - 2 && mouseY >= 2 && mouseY <= 12;
                if (closingButton) {
                    screen.confirmModal().setVisible(true);
                    screen.confirmModal().setCallback(() -> {
                        if (this.list.getSelected() == this) {
                            int index = this.list.children().indexOf(this) - 1;
                            if (index < 0) {
                                index = 1;
                            }
                            if (index < this.list.children().size()) {
                                this.list.internalSetSelected(this.list.children().get(index));
                            }
                        }
                        this.list.removeEntry(this);
                        ClientQuests.groups().remove(name);
                        NetworkHandler.CHANNEL.sendToServer(new DeleteGroupPacket(name));
                    });
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
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
