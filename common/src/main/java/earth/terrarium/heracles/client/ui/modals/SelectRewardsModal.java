package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.function.Consumer;

public class SelectRewardsModal extends BaseModal {

    private static final int WIDTH = 250;
    private static final int WIDGET_HEIGHT = 24;
    private static final int SELECTION_COLOR = 0x6044AA44;

    private final List<QuestReward<?>> rewards;
    private final int maxSelections;
    private final Consumer<Collection<String>> callback;
    private final Set<String> selectedRewards = new LinkedHashSet<>();
    private final List<RewardEntry> rewardEntries = new ArrayList<>();
    private Button confirmButton;

    protected SelectRewardsModal(Collection<QuestReward<?>> rewards, int maxSelections, Consumer<Collection<String>> callback, Screen background) {
        super(maxSelections == 1 ? Component.translatable("reward.heracles.select.title.singular") :
            Component.translatable("reward.heracles.select.title.plural"), background);
        this.rewards = new ArrayList<>(rewards);
        this.maxSelections = maxSelections;
        this.callback = callback;

        this.minWidth = WIDTH;
        this.minHeight = 200;
        this.ratio = 0.5f;
    }

    @Override
    protected void init() {
        super.init();

        this.rewardEntries.clear();
        for (QuestReward<?> reward : this.rewards) {
            DisplayWidget widget = QuestRewardWidgets.create(reward, false);
            if (widget != null) {
                this.rewardEntries.add(new RewardEntry(reward.id(), widget));
            }
        }

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        this.confirmButton = layout.addChild(Widgets.button().withCallback(() -> {
                    if (selectedRewards.size() == maxSelections) {
                        this.onClose();
                        callback.accept(new ArrayList<>(selectedRewards));
                    }
                })
                .withRenderer(WidgetRenderers.text(getConfirmMessage()).withColor(Color.tryParse("#FEFEFE")))
                .withSize(this.modalContentWidth, WIDGET_HEIGHT)
                .withTexture(UIConstants.PRIMARY_BUTTON),
            0, 0
        );
        this.confirmButton.active = selectedRewards.size() == maxSelections;
        layout.arrangeElements();
        int bottomTop = this.top + this.modalHeight - INNER_PADDING - layout.getHeight();
        layout.setPosition(this.modalContentLeft, bottomTop);
        layout.visitWidgets(this::addRenderableWidget);

        this.modalContentHeight = bottomTop - this.modalContentTop - INNER_PADDING;

        int scrollbarWidth = 10;
        int contentWidth = this.modalContentWidth - scrollbarWidth;

        GridLayout rewardsLayout = new GridLayout().rowSpacing(INNER_PADDING);
        int row = 0;
        for (RewardEntry entry : this.rewardEntries) {
            rewardsLayout.addChild(new RewardWidget(entry, contentWidth), row++, 0);
        }
        rewardsLayout.arrangeElements();

        LayoutWidget<GridLayout> scrollable = new LayoutWidget<>(rewardsLayout)
            .withScrollableY(TriState.UNDEFINED)
            .withContents(l -> l.columnSpacing(INNER_PADDING));

        scrollable.setSize(this.modalContentWidth, this.modalContentHeight);
        scrollable.setPosition(this.modalContentLeft, this.modalContentTop);
        this.addRenderableWidget(scrollable);
    }


    private class RewardWidget extends AbstractWidget {
        private final RewardEntry entry;

        public RewardWidget(RewardEntry entry, int width) {
            super(0, 0, width, entry.widget().getHeight(width), Component.empty());
            this.entry = entry;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();

            boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

            if (selectedRewards.contains(entry.id())) {
                graphics.fill(x, y, x + width, y + height, SELECTION_COLOR);
            }
            entry.widget().render(graphics, null, x, y, width, mouseX, mouseY, hovered, partialTick);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            String id = entry.id();
            if (selectedRewards.contains(id)) {
                selectedRewards.remove(id);
            } else if (selectedRewards.size() < maxSelections) {
                selectedRewards.add(id);
            }
            updateConfirmButton();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    private void updateConfirmButton() {
        if (this.confirmButton != null) {
            this.confirmButton.active = selectedRewards.size() == maxSelections;
            this.confirmButton.setMessage(getConfirmMessage());
        }
    }

    private Component getConfirmMessage() {
        return selectedRewards.size() == maxSelections ? Component.translatable("gui.heracles.confirm") :
            maxSelections == 1 ? Component.translatable("reward.heracles.select.modal.title.singular", maxSelections) :
                Component.translatable("reward.heracles.select.modal.title.plural", maxSelections);
    }

    public static void open(Collection<QuestReward<?>> rewards, int maxSelections, Consumer<Collection<String>> callback) {
        Screen background = Minecraft.getInstance().screen;
        SelectRewardsModal modal = new SelectRewardsModal(rewards, maxSelections, callback, background);
        Minecraft.getInstance().setScreen(modal);
    }

    private record RewardEntry(String id, DisplayWidget widget) {}
}
