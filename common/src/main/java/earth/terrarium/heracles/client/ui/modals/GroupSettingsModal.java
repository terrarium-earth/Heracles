package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.heracles.client.components.widgets.item.ItemButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quests.AbstractQuestsScreen;
import earth.terrarium.heracles.client.ui.quests.EditActionHandler;
import earth.terrarium.heracles.client.ui.quests.EditQuestsScreen;
import earth.terrarium.heracles.client.utils.BackgroundTextureManager;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.DeleteGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.ServerboundUpdateGroupSettingsPacket;
import earth.terrarium.heracles.common.utils.ItemValue;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.ListenableState;import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GroupSettingsModal extends BaseModal {

    private static final int WIDTH = 300;
    private static final int WIDGET_HEIGHT = 24;

    private final String group;
    private final Runnable onSave;

    private State<String> nameState;
    private State<String> backgroundState;
    private State<Integer> opacityState;
    private ItemButton itemButton;
    private Button iconToggleButton;
    private ListenableState<Boolean> iconToggleState;

    protected GroupSettingsModal(Screen background, String group, Runnable onSave) {
        super(Component.literal("Group Settings"), background);
        this.group = group;
        this.onSave = onSave;

        this.minHeight = 6 * INNER_PADDING + 6 * WIDGET_HEIGHT + TITLE_BAR_HEIGHT + 10;
        this.minWidth = WIDTH;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        super.init();

        GroupSettings settings = ClientQuests.getGroupSettings(this.group);

        int saveButtonHeight = WIDGET_HEIGHT + INNER_PADDING;
        int scrollAreaHeight = this.modalContentHeight - saveButtonHeight;

        int scrollbarWidth = 10;
        int contentWidth = this.modalContentWidth - scrollbarWidth;

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        GridLayout titleRow = new GridLayout().columnSpacing(INNER_PADDING);

        TextWidget title = Widgets.text(Component.literal("Group Name:"), textWidget -> {
            textWidget.withLeftAlignment().withFont(Minecraft.getInstance().font).withColor(Color.parse("WHITE")).withShadow();
            textWidget.setHeight(12);
            textWidget.setWidth(contentWidth / 2 - INNER_PADDING);
        });

        titleRow.addChild(title, 0, 0, titleRow.newCellSettings().alignVerticallyMiddle());

        // Group name
        if (this.nameState == null) {
            this.nameState = State.of(this.group);
        }
        TextBox nameBox = Widgets.textInput(this.nameState, tb -> tb.withPlaceholder("Group Name"));
        nameBox.setSize(contentWidth / 2, WIDGET_HEIGHT);

        titleRow.addChild(nameBox, 0, 1);
        layout.addChild(titleRow, 0, 0);

        GridLayout iconRow = new GridLayout().columnSpacing(INNER_PADDING);

        iconRow.addChild(
            Widgets.text(Component.literal("Icon:"), textWidget -> {
                textWidget.withLeftAlignment().withFont(Minecraft.getInstance().font).withColor(Color.parse("WHITE")).withShadow();
                textWidget.setHeight(12);
                textWidget.setWidth(contentWidth - contentWidth / 2 - INNER_PADDING);
            }),
            0, 0, iconRow.newCellSettings().alignVerticallyMiddle()
        );
        iconToggleState = new ListenableState<>(settings.iconEnabled());

        this.iconToggleButton = iconRow.addChild(
            Widgets.toggle(iconToggleState).withSize(30,16),
            0, 1,
            iconRow.newCellSettings().alignVerticallyMiddle()
        );

        // Item picker
        this.itemButton = iconRow.addChild(
            new ItemButton(this.itemButton, settings.icon(), contentWidth / 2 - iconToggleButton.getWidth() - INNER_PADDING, WIDGET_HEIGHT, false),
            0, 2
        );

        layout.addChild(iconRow, 2, 0);
        GridLayout backgroundRow = new GridLayout().columnSpacing(INNER_PADDING);

        TextWidget backgroundLabel = Widgets.text(Component.literal("Background URL:"), textWidget -> {
            textWidget.withLeftAlignment().withFont(Minecraft.getInstance().font).withColor(Color.parse("WHITE")).withShadow();
            textWidget.setHeight(12);
            textWidget.setWidth(contentWidth - contentWidth / 2 - INNER_PADDING);
        });

        backgroundRow.addChild(backgroundLabel, 0, 0, backgroundRow.newCellSettings().alignVerticallyMiddle());

        // Background URL/path
        if (this.backgroundState == null) {
            this.backgroundState = State.of(settings.background());
        }
        TextBox backgroundBox = Widgets.textInput(this.backgroundState, tb -> tb.withPlaceholder("Background URL/Path").withMaxLength(1024));
        backgroundBox.setSize(contentWidth / 2, WIDGET_HEIGHT);

        backgroundRow.addChild(backgroundBox, 0, 1);
        layout.addChild(backgroundRow, 3, 0);

        GridLayout opacityRow = new GridLayout().columnSpacing(INNER_PADDING);

        TextWidget opacityLabel = Widgets.text(Component.literal("Background Opacity %:"), textWidget -> {
            textWidget.withLeftAlignment().withFont(Minecraft.getInstance().font).withColor(Color.parse("WHITE")).withShadow();
            textWidget.setHeight(12);
            textWidget.setWidth(contentWidth - contentWidth / 10 - INNER_PADDING);
        });

        // Background Opacity Value
        if (this.opacityState == null) {
            this.opacityState = State.of(settings.backgroundOpacity());
        }
        TextBox opacityBox = Widgets.intInput(this.opacityState, tb -> tb.withPlaceholder("Background Opacity %").withMaxLength(3));
        opacityBox.setSize(contentWidth / 10, WIDGET_HEIGHT);

        opacityRow.addChild(opacityLabel, 0, 0, opacityRow.newCellSettings().alignVerticallyMiddle());
        opacityRow.addChild(opacityBox, 0, 1);
        layout.addChild(opacityRow, 4, 0);

        //delete group button
        Button deleteButton = Widgets.button()
            .withCallback(() -> {
                Minecraft.getInstance().tell(() -> DeleteConfirmModal.open(Component.literal("Delete Group"), Component.literal("Are you sure you want to delete the group \"" + this.group + "\"? This cannot be undone!"), () -> {
                    NetworkHandler.CHANNEL.sendToServer(new DeleteGroupPacket(this.group));
                    ClientQuests.groups().remove(this.group);
                    if (this.background instanceof AbstractQuestsScreen screen && screen.content.group().equals(this.group)) {
                        String firstGroup = ClientQuests.groups().isEmpty() ? "" : ClientQuests.groups().get(0);
                        if (!firstGroup.isEmpty()) {
                            NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(firstGroup));
                        }
                    }
                    this.onClose();
                }));
                this.onClose();
                open(this.group, this.onSave);
            })
            .withRenderer(WidgetRenderers.text(Component.literal("Delete Group")).withColor(Color.tryParse("#000000")))
            .withSize(contentWidth, WIDGET_HEIGHT)
            .withTexture(UIConstants.DANGER_BUTTON);

        layout.addChild(deleteButton, 6, 0);

        LayoutWidget<GridLayout> scrollableSettings = new LayoutWidget<>(layout)
            .withScrollableY(TriState.UNDEFINED)
            .withContents(gridLayout -> {});

        scrollableSettings.setSize(this.modalContentWidth, scrollAreaHeight);
        scrollableSettings.setPosition(this.modalContentLeft, this.modalContentTop);
        this.addRenderableWidget(scrollableSettings);

        // Save button — outside and below the scrollable area
        Button saveButton = Widgets.button()
            .withCallback(() -> {
                this.save();
                this.onClose();
                this.onSave.run();
            })
            .withRenderer(WidgetRenderers.text(Component.literal("Save")).withColor(Color.tryParse("#FEFEFE")))
            .withSize(this.modalContentWidth, WIDGET_HEIGHT)
            .withTexture(UIConstants.PRIMARY_BUTTON);
        saveButton.setPosition(this.modalContentLeft, this.modalContentTop + scrollAreaHeight + INNER_PADDING);
        this.addRenderableWidget(saveButton);
    }


    private void save() {
        if (this.nameState == null) return;
        ItemValue value = this.itemButton.reference().get();
        ItemStack icon = value.getDefaultInstance();
        boolean iconEnabled = this.iconToggleState.get();
        String newName = this.nameState.get();

        String iconId = icon.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(icon.getItem()).toString();

        String oldBackground = ClientQuests.getGroupSettings(this.group).background();
        String newBackground = this.backgroundState != null ? this.backgroundState.get() : "";

        int opacity = 100;
        if (this.opacityState != null) {
            opacity = Math.max(0, Math.min(100, this.opacityState.get()));
        }

        if (!oldBackground.equals(newBackground)) {
            BackgroundTextureManager.invalidate(oldBackground);
            BackgroundTextureManager.getTexture(newBackground);
        }

        ClientQuests.renameGroup(this.group, newName);
        ClientQuests.updateGroupSettings(newName, icon, iconEnabled, newBackground, opacity);


        // update QuestsContent of the background (EditQuestsScreen)
        if (!this.group.equals(newName) && this.background instanceof EditQuestsScreen screen) {
            QuestsContent newContent = new QuestsContent(newName, screen.content.quests(), screen.content.canEdit());
            screen.setContent(newContent);

            if (screen.handler() instanceof EditActionHandler editActionHandler) {
                editActionHandler.setContent(newContent);
            }

            screen.init(Minecraft.getInstance(), screen.width, screen.height);
        }

        NetworkHandler.CHANNEL.sendToServer(new ServerboundUpdateGroupSettingsPacket(this.group, newName, iconId, iconEnabled, newBackground, opacity));
    }

    public static void open(String group, Runnable onSave) {
        Screen background = Minecraft.getInstance().screen;
        GroupSettingsModal modal = new GroupSettingsModal(background, group, onSave);
        Minecraft.getInstance().setScreen(modal);
    }
}