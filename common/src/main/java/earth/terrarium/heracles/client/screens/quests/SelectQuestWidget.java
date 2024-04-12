package earth.terrarium.heracles.client.screens.quests;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.handlers.ClientQuestNetworking;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.QuestClipboard;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.ui.quests.QuestSettingsInitializer;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.widgets.base.BaseWidget;
import earth.terrarium.heracles.client.widgets.boxes.IntEditBox;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import earth.terrarium.heracles.client.widgets.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ItemValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

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

    private GuiEventListener loseFocusListener;

    public SelectQuestWidget(int x, int y, int width, int height, QuestsWidget widget) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widget = widget;
        this.group = ClientUtils.screen() instanceof QuestsScreen screen ? screen.getGroup() : "";

        this.titleBox = this.addChild(new EditBox(this.font, this.x + 6, this.y + 14, this.width - 12, 10, CommonComponents.EMPTY));
        this.titleBox.setResponder(s -> ClientQuests.updateQuest(
            this.entry,
            quest -> {
                quest.display().setTitle(s.isEmpty() ? null : Component.translatable(s));
                return NetworkQuestData.builder().title(quest.display().title());
            },
            false
        ));

        int boxWidth = (this.width - 40) / 2;

        this.xBox = this.addChild(new PositionBox(this.font, this.x + 16, this.y + 44, boxWidth, 10, ConstantComponents.X));
        this.yBox = this.addChild(new PositionBox(this.font, this.x + 33 + boxWidth, this.y + 44, boxWidth, 10, Component.literal("y")));
        this.xBox.setNumberResponder(value -> ClientQuests.updateQuest(this.entry, quest -> NetworkQuestData.builder().group(quest, this.group, pos -> {
            pos.x = value;
            return pos;
        }), false));
        this.yBox.setNumberResponder(value -> ClientQuests.updateQuest(this.entry, quest -> NetworkQuestData.builder().group(quest, this.group, pos -> {
            pos.y = value;
            return pos;
        }), false));

        this.subtitleBox = this.addChild(new MultiLineEditBox(this.font, this.x + 6, this.y + 76, this.width - 12, 40, CommonComponents.EMPTY, CommonComponents.EMPTY));
        this.subtitleBox.setValueListener(s -> ClientQuests.updateQuest(
            this.entry,
            quest -> {
                quest.display().setSubtitle(s.isEmpty() ? null : Component.translatable(s));
                return NetworkQuestData.builder().subtitle(quest.display().subtitle());
            },
            false
        ));

        addChild(ThemedButton.builder(Component.literal("ℹ"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    screen.itemModal().setVisible(true);
                    screen.itemModal().setCallback(item -> {
                        updateQuest(quest -> NetworkQuestData.builder().icon(new ItemQuestIcon(new ItemValue(item))));
                        screen.itemModal().setVisible(false);
                    });
                }
                loseFocusListener = b;
            }).bounds(this.x + 6, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.translatable("gui.heracles.quests.change_icon")))
            .build());

        addChild(ThemedButton.builder(Component.literal("□"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                    screen.iconBackgroundModal().setVisible(true);
                    screen.iconBackgroundModal().update(ClientUtils.getTextures("gui/quest_backgrounds"), selected -> {
                        updateQuest(quest -> NetworkQuestData.builder().background(selected));
                        screen.iconBackgroundModal().setVisible(false);
                    });
                }
                loseFocusListener = b;
            }).bounds(this.x + 24, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.translatable("gui.heracles.quests.change_background")))
            .build());

        addChild(ThemedButton.builder(Component.literal("⬈"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen && this.entry != null) {
                    screen.dependencyModal().setVisible(true);
                    screen.dependencyModal().update(this.entry, () -> updateQuest(quest -> NetworkQuestData.builder().dependencies(quest.dependencies())));
                }
                loseFocusListener = b;
            }).bounds(this.x + 42, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.translatable("gui.heracles.quests.change_dependencies")))
            .build());

        addChild(ThemedButton.builder(ConstantComponents.X, b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen && this.entry != null) {
                    screen.confirmModal().setVisible(true);
                    screen.confirmModal().setCallback(() -> {
                        if (this.entry.value().display().groups().size() == 1) {
                            ClientQuestNetworking.remove(entry.key());
                        } else {
                            ClientQuests.updateQuest(entry, quest -> {
                                quest.display().groups().remove(widget.group());
                                return NetworkQuestData.builder().groups(quest.display().groups());
                            });
                        }
                        screen.questsWidget.removeQuest(this.entry);
                    });
                }
                loseFocusListener = b;
            }).bounds(this.x + 60, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(ConstantComponents.DELETE))
            .build());

        addChild(ThemedButton.builder(Component.literal("\uD83D\uDD89"), b -> {
                if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen && this.entry != null) {
                    EditObjectModal edit = screen.findOrCreateEditWidget();
                    ResourceLocation id = new ResourceLocation(Heracles.MOD_ID, "quest");
                    QuestSettings settings = this.entry.value().settings();
                    edit.init(
                        id,
                        QuestSettingsInitializer.INSTANCE.create(settings),
                        data -> updateQuest(quest -> {
                            QuestSettings questSettings = QuestSettingsInitializer.INSTANCE.create("quest", settings, data);
                            return NetworkQuestData.builder()
                                .individualProgress(questSettings.individualProgress())
                                .hiddenUntil(questSettings.hiddenUntil())
                                .unlockNotification(questSettings.unlockNotification())
                                .showDependencyArrow(questSettings.showDependencyArrow())
                                .repeatable(questSettings.repeatable())
                                .autoClaimRewards(questSettings.autoClaimRewards());
                        })
                    );
                    edit.setTitle(Component.translatable("gui.heracles.quests.edit_quest_settings"));
                }
                loseFocusListener = b;
            }).bounds(this.x + 78, this.y + 137, 16, 16)
            .tooltip(Tooltip.create(Component.translatable("gui.heracles.quests.edit_quest_settings")))
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
            Component.translatable("gui.heracles.quests.title"), this.x + 7, this.y + 4, 0x808080,
            false
        );

        graphics.fill(this.x + 4, this.y + 29, this.x + this.width - 4, this.y + 30, 0xff808080);

        //Position
        graphics.drawString(
            font,
            Component.translatable("gui.heracles.quests.position"), this.x + 7, this.y + 33, 0x808080,
            false
        );

        graphics.fill(this.x + 4, this.y + 60, this.x + this.width - 4, this.y + 61, 0xff808080);

        //Subtitle
        graphics.drawString(
            font,
            Component.translatable("gui.heracles.quests.subtitle"), this.x + 7, this.y + 65, 0x808080,
            false
        );

        graphics.fill(this.x + 4, this.y + 121, this.x + this.width - 4, this.y + 122, 0xff808080);

        //Actions
        graphics.drawString(
            font,
            Component.translatable("gui.heracles.quests.actions"), this.x + 7, this.y + 126, 0x808080,
            false
        );

        renderChildren(graphics, mouseX, mouseY, partialTick);

        if (loseFocusListener != null) {
            setFocused(null);
            loseFocusListener.setFocused(false);
            loseFocusListener = null;
        }
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
        this.setFocused(null);
    }

    public ClientQuests.QuestEntry entry() {
        return this.entry;
    }

    private void updateQuest(Function<Quest, NetworkQuestData.Builder> supplier) {
        ClientQuests.updateQuest(this.entry, supplier);
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
