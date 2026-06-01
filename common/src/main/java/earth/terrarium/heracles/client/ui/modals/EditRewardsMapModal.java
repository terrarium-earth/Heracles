package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.heracles.api.client.settings.Settings;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.rewards.defaults.SelectableReward;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.Overlay;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;

public class EditRewardsMapModal extends BaseModal {

    private static final int WIDTH = 200;
    private static final int WIDGET_HEIGHT = 20;

    private final Map<String, QuestReward<?>> rewards;
    private final Consumer<Map<String, QuestReward<?>>> callback;

    protected EditRewardsMapModal(Map<String, QuestReward<?>> rewards,
                                  Consumer<Map<String, QuestReward<?>>> callback,
                                  Screen background) {
        super(Component.literal("Edit Selectable Rewards"), background);
        this.rewards = new LinkedHashMap<>(rewards);
        this.callback = callback;
        this.minWidth = WIDTH;
        this.minHeight = 150;
        this.ratio = 0f;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        if (this.background != null) {
            if (this.background instanceof Overlay) {
                this.background.init(minecraft, width, height);
            } else {
                this.background.resize(minecraft, width, height);
            }
        }
        this.init(minecraft, width, height);
    }

    @Override
    protected void repositionElements() {
        this.clearWidgets();
        this.init();
    }

    @Override
    protected void init() {
        super.init();

        int addButtonHeight = WIDGET_HEIGHT + INNER_PADDING;
        int scrollAreaHeight = this.modalContentHeight - addButtonHeight;

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);
        int row = 0;

        for (var entry : new ArrayList<>(rewards.entrySet())) {
            String id = entry.getKey();
            QuestReward<?> reward = entry.getValue();

            layout.addChild(
                Widgets.button(button -> {
                    }).withTexture(UIConstants.BUTTON)
                    .withRenderer(WidgetRenderers.text(Component.literal(id + " (" + reward.type().id().getPath() + ")")))
                    .withCallback(() -> {
                        EditObjectModal.open(
                            reward.type(),
                            ConstantComponents.Rewards.EDIT,
                            id, ModUtils.cast(reward),
                            updated -> {
                                rewards.put(id, ModUtils.cast(updated));
                                callback.accept(rewards);
                                rebuildWidgets();
                            }
                        );
                    }).withSize(this.modalContentWidth - 30 - (2 * INNER_PADDING), WIDGET_HEIGHT), row, 0);

            layout.addChild(
                Widgets.button(button -> {
                    }).withTexture(UIConstants.BUTTON)
                    .withRenderer(WidgetRenderers.text(Component.literal("X"))
                        .withColor(Color.tryParse("#FF5555")))
                    .withCallback(() -> {
                        rewards.remove(id);
                        callback.accept(rewards);
                        rebuildWidgets();
                    }).withSize(24, WIDGET_HEIGHT), row, 1);
            row++;
        }

        LayoutWidget<GridLayout> scrollable = new LayoutWidget<>(layout)
            .withScrollableY(TriState.UNDEFINED)
            .withContents(gridLayout -> gridLayout.columnSpacing(INNER_PADDING));

        scrollable.setSize(this.modalContentWidth, scrollAreaHeight);
        scrollable.setPosition(this.modalContentLeft, this.modalContentTop);
        this.addRenderableWidget(scrollable);

        TextButton addRewardButton = new TextButton(this.modalContentWidth, WIDGET_HEIGHT, 0xFEFEFE, UIConstants.PRIMARY_BUTTON,
            Component.literal("Add Reward"),
            b -> {
                List<ResourceLocation> validTypes = QuestRewards.types().values()
                    .stream()
                    .filter(Settings::hasFactory)
                    .filter(type -> type != SelectableReward.TYPE)
                    .map(QuestRewardType::id)
                    .toList();

                CreateObjectModal.open("rewards",
                    (type, newId) -> {
                        QuestRewardType<?> rewardType = QuestRewards.get(type);
                        if (rewardType == null) return;
                        EditObjectModal.open(
                            rewardType,
                            ConstantComponents.Rewards.EDIT,
                            newId, null,
                            newReward -> {
                                rewards.put(newId, ModUtils.cast(newReward));
                                callback.accept(rewards);
                                rebuildWidgets();
                            });
                    },
                    (type, newId) -> !rewards.containsKey(newId) && type != null,
                    validTypes
                );
            }
        );
        addRewardButton.setPosition(this.modalContentLeft, this.modalContentTop + scrollAreaHeight + INNER_PADDING);
        this.addRenderableWidget(addRewardButton);
    }

    public static void open(Map<String, QuestReward<?>> rewards,
                            Consumer<Map<String, QuestReward<?>>> callback) {
        Screen background = Minecraft.getInstance().screen;
        EditRewardsMapModal modal = new EditRewardsMapModal(rewards, callback, background);
        Minecraft.getInstance().setScreen(modal);
    }
}
