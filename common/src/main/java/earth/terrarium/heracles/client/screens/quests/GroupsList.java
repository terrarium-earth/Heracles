package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.components.context.ContextualMenuScreen;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.api.groups.Group;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.DeleteGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.EditGroupPacket;
import earth.terrarium.heracles.common.utils.ItemValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class GroupsList extends SelectionList<GroupsList.Entry> {

    private final int width;
    private final Consumer<@Nullable Entry> onSelection;
    private final int x;

    public GroupsList(int x, int y, int width, int height, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, 20, entry -> {}, true);
        this.x = x;
        this.onSelection = onSelection;
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

    public void update(Map<String, Group> groups, String selected) {
        List<Entry> entries = new ArrayList<>(groups.size());
        Entry selectedEntry = null;
        for (var group : groups.entrySet()) {
            Entry entry = new Entry(this, group.getKey(), group.getValue());
            if (group.getKey().equals(selected)) {
                selectedEntry = entry;
            }
            entries.add(entry);
        }
        updateEntries(entries);
        if (selectedEntry != null) {
            setSelected(selectedEntry);
        }
    }

    public void addGroup(String id, Group group) {
        addEntry(new Entry(this, id, group));
    }

    public static class Entry extends ListEntry {

        private final GroupsList list;
        private final String name;
        private Group group;

        public Entry(GroupsList list, String name, Group group) {
            this.list = list;
            this.name = name;
            this.group = group;
        }

        @Override
        @SuppressWarnings("SuspiciousNameCombination")
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            graphics.fill(left, top, left + width, top + height, selected ? 0x22FFFFFF : 0x22808080);
            if (hovered) {
                graphics.renderOutline(left, top, width, height, 0x44FFFFFF);
            }
            int x = left + 5;
            if (group.icon().isPresent()) {
                int size = height - 2;
                group.icon().get().render(graphics, scissorStack, x, top + 1 + ((size - 16) / 2), size, size);
            }
            x += height;
            graphics.drawString(Minecraft.getInstance().font, Component.translatable(group.title()), x, top + height / 2 - 4, 0xFFFFFF);
            CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen) {
                if (mouseX - left >= width - 11 && mouseX - left <= width - 2 && mouseY - top >= 2 && mouseY - top <= 12 && hovered) {
                    boolean cant = !ClientQuests.byGroup(name).isEmpty() || this.list.children().size() == 1;
                    CursorUtils.setCursor(cant, CursorScreen.Cursor.DISABLED);
                    ScreenUtils.setTooltip(cant ? ConstantComponents.Groups.DELETE_WITH_QUESTS : ConstantComponents.DELETE);
                    graphics.drawString(
                        Minecraft.getInstance().font,
                        "x", left + width - 9, top + 2, 0xFFFFFF,
                        false
                    );
                } else if (hovered) {
                    graphics.drawString(
                        Minecraft.getInstance().font,
                        "x", left + width - 9, top + 2, 0x808080,
                        false
                    );
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean cant = !ClientQuests.byGroup(name).isEmpty() || this.list.children().size() == 1;
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen && button == InputConstants.MOUSE_BUTTON_LEFT && !cant) {
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
            if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
                MouseClick mouse = ClientUtils.getMousePos();
                ContextualMenuScreen.getMenu()
                    .ifPresent(menu -> menu.start(this.list.x + this.list.width + 6, mouse.y())
                        .addOption(Component.literal("\uD83D\uDCAC Edit Name"), () -> {
                            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                                screen.textModal().setVisible(true);
                                screen.textModal().setText(this.group.title());
                                screen.textModal().setCallback((unused, text) -> {
                                    this.group = this.group.withTitle(text);
                                    NetworkHandler.CHANNEL.sendToServer(new EditGroupPacket(
                                        name,
                                        Optional.empty(),
                                        Optional.of(text),
                                        Optional.empty()
                                    ));
                                    screen.textModal().setVisible(false);
                                });
                            }
                        })
                        .addOption(Component.literal("\uD83D\uDDBC Edit Icon"), () -> {
                            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                                screen.itemModal().setVisible(true);
                                screen.itemModal().setCallback(item -> {
                                    this.group = this.group.withIcon(new ItemQuestIcon(new ItemValue(item)));
                                    NetworkHandler.CHANNEL.sendToServer(new EditGroupPacket(
                                        name,
                                        this.group.icon(),
                                        Optional.empty(),
                                        Optional.empty()
                                    ));
                                    screen.itemModal().setVisible(false);
                                });
                            }
                        })
                        .addDivider()
                        .addOption(Component.literal("⬆ Move Up"), () ->
                            System.out.println("Move UP") //TODO
                        )
                        .addOption(Component.literal("⬇ Move Down"), () ->
                            System.out.println("Move Down") //TODO
                        )
                        .open());
                return true;
            }
            this.list.onSelection.accept(this);
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
