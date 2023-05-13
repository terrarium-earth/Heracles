package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.client.ClientUtils;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.widgets.NumberEditBox;
import earth.terrarium.heracles.client.widgets.base.BaseWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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

    private final EditBox titleBox;

    private final NumberEditBox xBox;
    private final NumberEditBox yBox;
    private final MultiLineEditBox subtitleBox;

    public SelectQuestWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.titleBox = this.addChild(new EditBox(this.font, this.x + 6, this.y + 14, this.width - 12, 10, CommonComponents.EMPTY));
        this.titleBox.setResponder(s -> changeOption(quest -> quest.display().setTitle(s.isEmpty() ? null : Component.literal(s))));

        int boxWidth = (this.width - 40) / 2;

        this.xBox = this.addChild(new NumberEditBox(this.font, this.x + 16, this.y + 44, boxWidth, 10, Component.literal("x")));
        this.yBox = this.addChild(new NumberEditBox(this.font, this.x + 33 + boxWidth, this.y + 44, boxWidth, 10, Component.literal("y")));
        this.xBox.setNumberResponder(value -> changeOption(quest -> quest.display().position().x = value));
        this.yBox.setNumberResponder(value -> changeOption(quest -> quest.display().position().y = value));

        this.subtitleBox = this.addChild(new MultiLineEditBox(this.font, this.x + 6, this.y + 76, this.width - 12, 40, CommonComponents.EMPTY, CommonComponents.EMPTY));
        this.subtitleBox.setValueListener(s -> changeOption(quest -> quest.display().setSubtitle(s.isEmpty() ? null : Component.literal(s))));

        addChild(Button.builder(Component.literal("⇄ Icon"), b -> {
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                screen.iconModal().setVisible(true);
                screen.iconModal().setCallback(item -> {
                    changeOption(quest -> quest.display().setIcon(new ItemQuestIcon(item)));
                    screen.iconModal().setVisible(false);
                });
            }
        }).bounds(this.x + 6, this.y + 137, this.width - 12, 14).build());

        addChild(Button.builder(Component.literal("⇄ Background"), b -> {
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                screen.iconBackgroundModal().setVisible(true);
                screen.iconBackgroundModal().update(ClientUtils.getTextures("gui/quest_backgrounds"), selected -> {
                    changeOption(quest -> quest.display().setIconBackground(selected));
                    screen.iconBackgroundModal().setVisible(false);
                });
            }
        }).bounds(this.x + 6, this.y + 157, this.width - 12, 14).build());
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

        //Icon
        font.draw(stack, Component.literal("Icon"), this.x + 7, this.y + 126, 0x808080);


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
}
