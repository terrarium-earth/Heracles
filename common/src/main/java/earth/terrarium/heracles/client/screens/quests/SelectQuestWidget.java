package earth.terrarium.heracles.client.screens.quests;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.QuestClipboard;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.widgets.base.BaseWidget;
import earth.terrarium.heracles.client.widgets.boxes.IntEditBox;
import earth.terrarium.heracles.client.widgets.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

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

    private final QuestsWidget widget;

    private final String group;

    public SelectQuestWidget(int x, int y, int width, int height, QuestsWidget widget) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widget = widget;
        this.group = ClientUtils.screen() instanceof QuestsScreen screen ? screen.getMenu().group() : "";

        this.titleBox = this.addChild(new EditBox(this.font, this.x + 6, this.y + 14, this.width - 12, 10, CommonComponents.EMPTY));
        this.titleBox.setResponder(s -> changeOption(quest -> quest.display().setTitle(s.isEmpty() ? null : Component.translatable(s))));

        int boxWidth = (this.width - 40) / 2;

        this.xBox = this.addChild(new PositionBox(this.font, this.x + 16, this.y + 44, boxWidth, 10, ConstantComponents.X));
        this.yBox = this.addChild(new PositionBox(this.font, this.x + 33 + boxWidth, this.y + 44, boxWidth, 10, Component.literal("y")));
        this.xBox.setNumberResponder(value -> changeOption(quest -> quest.display().position(this.group).x = value));
        this.yBox.setNumberResponder(value -> changeOption(quest -> quest.display().position(this.group).y = value));

        this.subtitleBox = this.addChild(new MultiLineEditBox(this.font, this.x + 6, this.y + 76, this.width - 12, 40, CommonComponents.EMPTY, CommonComponents.EMPTY));
        this.subtitleBox.setValueListener(s -> changeOption(quest -> quest.display().setSubtitle(s.isEmpty() ? null : Component.translatable(s))));

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
                        if (this.entry.value().display().groups().size() == 1) {
                            ClientQuests.removeQuest(entry);
                        }
                        ClientQuests.removeFromGroup(widget.group(), entry);
                        screen.questsWidget.removeQuest(this.entry);
                    });
                }
            }).bounds(this.x + 60, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(ConstantComponents.DELETE))
            .build());

        addChild(Button.builder(Component.literal("\uD83D\uDD89"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    EditObjectModal edit = screen.findOrCreateEditWidget();
                    ResourceLocation id = new ResourceLocation(Heracles.MOD_ID, "quest");
                    var settings = this.entry.value().settings();
                    edit.init(
                        id,
                        QuestSettingsInitalizer.INSTANCE.create(settings),
                        data -> changeOption(quest ->
                            quest.settings().update(QuestSettingsInitalizer.INSTANCE.create("quest", settings, data))
                        )
                    );
                    edit.setTitle(Component.literal("Edit Quest Settings"));
                }
            }).bounds(this.x + 78, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.literal("Edit Quest Settings")))
            .build());
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.entry == null) return;
        updateWidgets();

        graphics.blitRepeating(AbstractQuestScreen.HEADING, this.x - 2, this.y, 2, this.height, 128, 0, 2, 256);

        //Title
        graphics.drawString(
            font,
            Component.literal("Title"), this.x + 7, this.y + 4, 0x808080,
            false
        );

        graphics.fill(this.x + 4, this.y + 29, this.x + this.width - 4, this.y + 30, 0xff808080);

        //Position
        graphics.drawString(
            font,
            Component.literal("Position"), this.x + 7, this.y + 33, 0x808080,
            false
        );

        graphics.fill(this.x + 4, this.y + 60, this.x + this.width - 4, this.y + 61, 0xff808080);

        //Subtitle
        graphics.drawString(
            font,
            Component.literal("Subtitle"), this.x + 7, this.y + 65, 0x808080,
            false
        );

        graphics.fill(this.x + 4, this.y + 121, this.x + this.width - 4, this.y + 122, 0xff808080);

        //Actions
        graphics.drawString(
            font,
            Component.literal("Actions"), this.x + 7, this.y + 126, 0x808080,
            false
        );

        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) return true;
        return QuestClipboard.INSTANCE.action(keyCode, this);
    }

    public void updateWidgets() {
        if (this.entry == null) return;
        var position = this.entry.value().display().position(this.group);
        this.xBox.setIfNotFocused(position.x());
        this.yBox.setIfNotFocused(position.y());
        String subtitle = getTranslationKey(this.entry.value().display().subtitle());
        if (!this.subtitleBox.getValue().equals(subtitle)) {
            this.subtitleBox.setValue(subtitle);
        }
        String title = getTranslationKey(this.entry.value().display().title());
        if (!this.titleBox.getValue().equals(title)) {
            this.titleBox.setValue(title);
        }
    }

    private static String getTranslationKey(Component component) {
        if (component.getContents() instanceof TranslatableContents t) {
            return t.getKey();
        } else {
            return component.getString();
        }
    }

    public void setEntry(ClientQuests.QuestEntry entry) {
        this.entry = entry;
    }

    public ClientQuests.QuestEntry entry() {
        return this.entry;
    }

    private void changeOption(Consumer<Quest> consumer) {
        if (this.entry == null) return;
        consumer.accept(this.entry.value());
        ClientQuests.setDirty(this.entry.key());
    }

    public QuestsWidget widget() {
        return this.widget;
    }

    private static class PositionBox extends IntEditBox {

        public PositionBox(Font font, int x, int y, int width, int height, Component message) {
            super(font, x, y, width, height, message);
            setBordered(true);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
            Font font = Minecraft.getInstance().font;
            if (isVisible()) {
                int textWidth = font.width(getMessage());
                graphics.drawString(
                    font,
                    getMessage(), getX() - textWidth - 4, getY() + (this.height - font.lineHeight - 1) / 2, isFocused() ? 0xffffff : 0x808080,
                    false
                );
            }
            super.renderWidget(graphics, i, j, f);
        }

        @Override
        public int getInnerWidth() {
            return this.width - 8;
        }
    }
}
