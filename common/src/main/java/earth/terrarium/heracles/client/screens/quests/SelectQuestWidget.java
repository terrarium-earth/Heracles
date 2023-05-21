package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.widgets.base.BaseWidget;
import earth.terrarium.heracles.client.widgets.boxes.IntEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SelectQuestWidget extends BaseWidget {

    private ClientQuests.QuestEntry entry;

    private final int width;
    private final int height;
    private final int x;
    private final int y;

    private final EditBox titleBox;

    private final IntEditBox xBox;
    private final IntEditBox yBox;
    private final MultiLineEditBox subtitleBox;

    public SelectQuestWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.titleBox = this.addChild(new EditBox(this.font, this.x + 6, this.y + 14, this.width - 12, 10, CommonComponents.EMPTY));
        this.titleBox.setResponder(s -> changeOption(quest -> quest.display().setTitle(s.isEmpty() ? null : Component.literal(s))));

        int boxWidth = (this.width - 40) / 2;

        this.xBox = this.addChild(new PositionBox(this.font, this.x + 16, this.y + 44, boxWidth, 10, Component.literal("x")));
        this.yBox = this.addChild(new PositionBox(this.font, this.x + 33 + boxWidth, this.y + 44, boxWidth, 10, Component.literal("y")));
        this.xBox.setNumberResponder(value -> changeOption(quest -> quest.display().position().x = value));
        this.yBox.setNumberResponder(value -> changeOption(quest -> quest.display().position().y = value));

        this.subtitleBox = this.addChild(new MultiLineEditBox(this.font, this.x + 6, this.y + 76, this.width - 12, 40, CommonComponents.EMPTY, CommonComponents.EMPTY));
        this.subtitleBox.setValueListener(s -> changeOption(quest -> quest.display().setSubtitle(s.isEmpty() ? null : Component.literal(s))));

        addChild(Button.builder(Component.literal("ℹ"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    screen.iconModal().setVisible(true);
                    screen.iconModal().setCallback(item -> {
                        changeOption(quest -> quest.display().setIcon(new ItemQuestIcon(item)));
                        screen.iconModal().setVisible(false);
                    });
                }
            }).bounds(this.x + 6, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.literal("Change Icon")))
            .build());

        addChild(Button.builder(Component.literal("□"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    screen.iconBackgroundModal().setVisible(true);
                    screen.iconBackgroundModal().update(ClientUtils.getTextures("gui/quest_backgrounds"), selected -> {
                        changeOption(quest -> quest.display().setIconBackground(selected));
                        screen.iconBackgroundModal().setVisible(false);
                    });
                }
            }).bounds(this.x + 24, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.literal("Change Icon Background")))
            .build());

        addChild(Button.builder(Component.literal("⬈"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    screen.dependencyModal().setVisible(true);
                    screen.dependencyModal().update(this.entry, () -> changeOption(quest -> {}));
                }
            }).bounds(this.x + 42, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.literal("Change Dependencies")))
            .build());

        addChild(Button.builder(Component.literal("x"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    screen.confirmModal().setVisible(true);
                    screen.confirmModal().setCallback(() -> {
                        ClientQuests.removeQuest(this.entry);
                        screen.questsWidget.removeQuest(this.entry);
                    });
                }
            }).bounds(this.x + 58, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.literal("Delete Quest")))
            .build());
    }


    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (this.entry == null) return;
        updateWidgets();

        RenderUtils.bindTexture(AbstractQuestScreen.HEADING);
        Gui.blitRepeating(stack, this.x - 2, this.y, 2, this.height, 128, 0, 2, 256);

        //Title
        font.draw(stack, Component.literal("Title"), this.x + 7, this.y + 4, 0x808080);

        Gui.fill(stack, this.x + 4, this.y + 29, this.x + this.width - 4, this.y + 30, 0xff808080);

        //Position
        font.draw(stack, Component.literal("Position"), this.x + 7, this.y + 33, 0x808080);

        Gui.fill(stack, this.x + 4, this.y + 60, this.x + this.width - 4, this.y + 61, 0xff808080);

        //Subtitle
        font.draw(stack, Component.literal("Subtitle"), this.x + 7, this.y + 65, 0x808080);

        Gui.fill(stack, this.x + 4, this.y + 121, this.x + this.width - 4, this.y + 122, 0xff808080);

        //Actions
        font.draw(stack, Component.literal("Actions"), this.x + 7, this.y + 126, 0x808080);

        renderChildren(stack, mouseX, mouseY, partialTick);
    }

    public void updateWidgets() {
        if (this.entry == null) return;
        var position = this.entry.value().display().position();
        this.xBox.setIfNotFocused(position.x());
        this.yBox.setIfNotFocused(position.y());
        String subtitle = this.entry.value().display().subtitle().getString();
        if (!this.subtitleBox.getValue().equals(subtitle)) {
            this.subtitleBox.setValue(subtitle);
        }
        String title = this.entry.value().display().title().getString();
        if (!this.titleBox.getValue().equals(title)) {
            this.titleBox.setValue(title);
        }
    }

    public void setEntry(ClientQuests.QuestEntry entry) {
        this.entry = entry;
    }

    private void changeOption(Consumer<Quest> consumer) {
        if (this.entry == null) return;
        consumer.accept(this.entry.value());
        ClientQuests.setDirty(this.entry.key());
    }

    private static class PositionBox extends IntEditBox {

        public PositionBox(Font font, int x, int y, int width, int height, Component message) {
            super(font, x, y, width, height, message);
            setBordered(true);
        }

        @Override
        public void renderWidget(PoseStack poseStack, int i, int j, float f) {
            Font font = Minecraft.getInstance().font;
            if (isVisible()) {
                int textWidth = font.width(getMessage());
                font.draw(poseStack, getMessage(), getX() - textWidth - 4, getY() + (this.height - font.lineHeight - 1) / 2f, isFocused() ? 0xffffff : 0x808080);
            }
            super.renderWidget(poseStack, i, j, f);
        }

        @Override
        public int getInnerWidth() {
            return this.width - 8;
        }
    }
}
