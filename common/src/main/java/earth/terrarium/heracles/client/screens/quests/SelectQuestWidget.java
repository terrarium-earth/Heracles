package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.widgets.NumberEditBox;
import earth.terrarium.heracles.client.widgets.base.BaseWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SelectQuestWidget extends BaseWidget {

    private ClientQuests.QuestEntry entry;

    private final int width;
    private final int height;
    private final int x;
    private final int y;

    private final NumberEditBox xBox;
    private final NumberEditBox yBox;
    private final MultiLineEditBox subtitleBox;

    public SelectQuestWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        int boxWidth = (this.width - 40) / 2;

        this.xBox = this.addChild(new NumberEditBox(this.font, this.x + 16, this.y + 38, boxWidth, 10, Component.literal("x")));
        this.yBox = this.addChild(new NumberEditBox(this.font, this.x + 33 + boxWidth, this.y + 38, boxWidth, 10, Component.literal("y")));
        this.xBox.setNumberResponder(value -> changeOption(quest -> quest.display().position().x = value));
        this.yBox.setNumberResponder(value -> changeOption(quest -> quest.display().position().y = value));

        this.subtitleBox = this.addChild(new MultiLineEditBox(this.font, this.x + 6, this.y + 70, this.width - 12, 40, CommonComponents.EMPTY, CommonComponents.EMPTY));
        this.subtitleBox.setValueListener(s -> changeOption(quest -> quest.display().setSubtitle(s.isEmpty() ? null : Component.literal(s))));
    }


    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (this.entry == null) return;
        updateWidgets();

        RenderUtils.bindTexture(AbstractQuestScreen.HEADING);
        Gui.blitRepeating(stack, this.x - 2, this.y, 2, this.height, 128, 0, 2, 256);

        //Position
        font.draw(stack, Component.literal("Position"), this.x + 7, this.y + 38 - font.lineHeight - 2, 0x808080);

        Gui.fill(stack, this.x + 4, this.y + 54, this.x + this.width - 4, this.y + 55, 0xff808080);

        //Subtitle
        font.draw(stack, Component.literal("Subtitle"), this.x + 7, this.y + 70 - font.lineHeight - 2, 0x808080);


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
    }

    public void setEntry(ClientQuests.QuestEntry entry) {
        this.entry = entry;
    }

    private void changeOption(Consumer<Quest> consumer) {
        if (this.entry == null) return;
        consumer.accept(this.entry.value());
        ClientQuests.setDirty(this.entry.key());
    }
}
