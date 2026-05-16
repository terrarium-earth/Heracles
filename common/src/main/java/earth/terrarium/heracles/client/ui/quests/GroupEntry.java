package earth.terrarium.heracles.client.ui.quests;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.common.color.Color;import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.modals.GroupSettingsModal;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundUpdateGroupOrderPacket;
import earth.terrarium.olympus.client.components.Widgets;import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.string.TextWidget;import earth.terrarium.olympus.client.ui.context.ContextMenu;
import net.minecraft.Util;import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupEntry extends BaseWidget implements ListWidget.Item {

    private static final ResourceLocation NORMAL =  Heracles.id("groups/normal");
    private static final ResourceLocation SELECTED =  Heracles.id("groups/selected");
    private static final ResourceLocation DRAG_HANDLE = Heracles.id("groups/drag_handle");
    private static final int PADDING = 4;
    private static final int DRAG_HANDLE_WIDTH = 8;
    private static final int DRAG_HANDLE_HEIGHT = 12;
    private static final int DRAG_THRESHOLD = 3;
    private final String id;
    private final boolean selected;
    private final Runnable onReorder;
    private final ListWidget parentList;
    private boolean dragging = false;
    private double dragAccumulatedY = 0;

    public GroupEntry(int width, int height, String id, boolean selected, Runnable onReorder, ListWidget parentList) {
        super(width, height);
        this.id = id;
        this.selected = selected;
        this.onReorder = onReorder;
        this.parentList = parentList;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered() || this.selected) {
            ResourceLocation texture = this.selected ? SELECTED : NORMAL;
            UIUtils.blitWithEdge(graphics, texture, getX(), getY(), getWidth(), getHeight(), 4);
            CursorUtils.setCursor(this.isHovered() && !this.selected, CursorScreen.Cursor.POINTER);
        }

        int textX = this.getX() + PADDING;
        GroupSettings settings = ClientQuests.getGroupSettings(this.id);
        if (settings.iconEnabled() && !settings.icon().isEmpty()) {
            int iconY = this.getY() + (this.getHeight() - 16) / 2;
            graphics.renderFakeItem(settings.icon(), textX, iconY);
            textX += 18;
        }

        int availableWidth = this.getWidth() - (textX - this.getX()) - (2 * PADDING);
        if(QuestTab.isEditing()) {
            availableWidth -= DRAG_HANDLE_WIDTH + PADDING;
        }

        int textWidth = Minecraft.getInstance().font.width(this.id);
        int finalTextX = textX;
        if(textWidth > availableWidth) {

            long time = Util.getMillis();
            double speed = 0.02; // 0.02 pixels per ms = 20 pixels per second
            double pauseMs = 1500.0; // 1.5 second pause
            double scrollDistance = textWidth - availableWidth;
            double scrollDuration = scrollDistance / speed;

            // Total loop duration: Start Pause + Scrolling + End Pause
            double totalLoopTime = pauseMs + scrollDuration + pauseMs;
            double currentTimeInLoop = time % totalLoopTime;

            int offset;
            if (currentTimeInLoop < pauseMs) {
                // Phase 1: Initial Pause (2 seconds)
                offset = 0;
            } else if (currentTimeInLoop < pauseMs + scrollDuration) {
                // Phase 2: Scrolling
                offset = (int) Math.round((currentTimeInLoop - pauseMs) * speed);
            } else {
                // Phase 3: End Pause (2 seconds)
                offset = (int) Math.round(scrollDistance);
            }

            graphics.enableScissor(textX + PADDING, getY(), textX + PADDING + availableWidth, getY() + getHeight());

            Widgets.text(Component.literal(this.id), textWidget -> {
                textWidget.setPosition(finalTextX + PADDING - offset, this.getY() + ((this.getHeight() - 10) / 2) + 1);
                textWidget.withColor(Color.parse("WHITE"));
                textWidget.withShadow();
                textWidget.withFont(Minecraft.getInstance().font);
                textWidget.withLeftAlignment();
                textWidget.render(graphics, mouseX, mouseY, partialTick);
            });

            graphics.disableScissor();
        } else {
            Widgets.text(Component.literal(this.id), textWidget -> {
                textWidget.setPosition(finalTextX + PADDING , this.getY() + ((this.getHeight() - 10) / 2) + 1);
                textWidget.withColor(Color.parse("WHITE"));
                textWidget.withShadow();
                textWidget.withFont(Minecraft.getInstance().font);
                textWidget.withLeftAlignment();
                textWidget.render(graphics, mouseX, mouseY, partialTick);
            });
        }

        if (QuestTab.isEditing()) {
            int handleX = this.getX() + this.getWidth() - DRAG_HANDLE_WIDTH - PADDING;
            int handleY = this.getY() + (this.getHeight() - DRAG_HANDLE_HEIGHT) / 2;
            graphics.blitSprite(DRAG_HANDLE, handleX, handleY, DRAG_HANDLE_WIDTH, DRAG_HANDLE_HEIGHT);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) return false;

        if (button == 1 && QuestTab.isEditing()) {
            ContextMenu.open(mouseX, mouseY, menu -> {
                menu.button(Component.literal("\u2699 Settings"), () -> {
                    Minecraft.getInstance().tell(() -> GroupSettingsModal.open(this.id, this.onReorder));
                });
            });
            return true;
        }

        if (button == 0 && QuestTab.isEditing()) {
            this.dragging = true;
            this.dragAccumulatedY = 0;
            return true;
        }

        this.onClick(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragging && button == 0) {
            this.dragAccumulatedY += dragY;
            int itemHeight = this.getHeight();
            List<ListWidget.Item> items = parentList.items();
            int currentIndex = items.indexOf(this);

            while (this.dragAccumulatedY >= itemHeight && currentIndex < items.size() - 1) {
                Collections.swap(items, currentIndex, currentIndex + 1);
                currentIndex++;
                this.dragAccumulatedY -= itemHeight;
            }
            while (this.dragAccumulatedY <= -itemHeight && currentIndex > 0) {
                Collections.swap(items, currentIndex, currentIndex - 1);
                currentIndex--;
                this.dragAccumulatedY += itemHeight;
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragging && button == 0) {
            this.dragging = false;
            if (Math.abs(this.dragAccumulatedY) < DRAG_THRESHOLD && parentList.items().indexOf(this) == ClientQuests.groupOrders().indexOf(this.id)) {
                this.onClick(mouseX, mouseY);
            } else {
                List<String> newOrder = new ArrayList<>();
                for (ListWidget.Item item : parentList.items()) {
                    if (item instanceof GroupEntry entry) {
                        newOrder.add(entry.id);
                    }
                }
                ClientQuests.syncGroupOrders(newOrder);
                NetworkHandler.CHANNEL.sendToServer(new ServerboundUpdateGroupOrderPacket(newOrder));
                this.onReorder.run();
            }
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(this.id));
    }
}
