package earth.terrarium.heracles.client.ui.modals;

import earth.terrarium.heracles.client.components.widgets.ToggleSwitch;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.components.widgets.item.ItemButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.quests.GroupSettings;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.ServerboundUpdateGroupSettingsPacket;
import earth.terrarium.heracles.common.utils.ItemValue;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class GroupSettingsModal extends BaseModal {

    private static final int WIDTH = 200;
    private static final int WIDGET_HEIGHT = 24;

    private final String group;
    private final Runnable onSave;

    private ItemButton itemButton;
    private ToggleSwitch iconToggle;

    protected GroupSettingsModal(Screen background, String group, Runnable onSave) {
        super(Component.literal("Group Settings"), background);
        this.group = group;
        this.onSave = onSave;

        this.minHeight = 4 * INNER_PADDING + 3 * WIDGET_HEIGHT + TITLE_BAR_HEIGHT + 10;
        this.minWidth = WIDTH;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        super.init();

        GroupSettings settings = ClientQuests.getGroupSettings(this.group);

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        // Icon enabled toggle row
        GridLayout toggleRow = new GridLayout().columnSpacing(INNER_PADDING);
        toggleRow.addChild(
            new earth.terrarium.heracles.client.components.string.TextWidget(
                this.modalContentWidth - 30 - INNER_PADDING, WIDGET_HEIGHT,
                Component.literal("Show Icon"),
                Minecraft.getInstance().font
            ),
            0, 0
        );
        this.iconToggle = toggleRow.addChild(
            new ToggleSwitch(settings.iconEnabled()),
            0, 1
        );
        layout.addChild(toggleRow, 0, 0);

        // Item picker
        this.itemButton = layout.addChild(
            new ItemButton(this.itemButton, settings.icon(), this.modalContentWidth, WIDGET_HEIGHT, false),
            1, 0
        );

        // Save button
        layout.addChild(
            new TextButton(this.modalContentWidth, WIDGET_HEIGHT, 0xFEFEFE, UIConstants.PRIMARY_BUTTON, Component.literal("Save"), b -> {
                save();
            }),
            2, 0
        );

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    private void save() {
        ItemValue value = this.itemButton.reference().get();
        ItemStack icon = value.getDefaultInstance();
        boolean iconEnabled = this.iconToggle.isToggled();

        String iconId = icon.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(icon.getItem()).toString();

        ClientQuests.updateGroupSettings(this.group, icon, iconEnabled);
        NetworkHandler.CHANNEL.sendToServer(new ServerboundUpdateGroupSettingsPacket(this.group, iconId, iconEnabled));

        this.onClose();
        this.onSave.run();
    }

    public static void open(String group, Runnable onSave) {
        Screen background = Minecraft.getInstance().screen;
        GroupSettingsModal modal = new GroupSettingsModal(background, group, onSave);
        Minecraft.getInstance().setScreen(modal);
    }
}
