package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.common.menus.quests.QuestsMenu;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.OpenGroupPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class QuestsScreen extends AbstractQuestScreen<QuestsMenu> {

    public QuestsScreen(QuestsMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.hasBackButton = false;
    }

    @Override
    protected void init() {
        super.init();
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        addRenderableWidget(new ImageButton(sidebarWidth - 12, 1, 11, 11, 22, 15, 11, HEADING, 256, 256, (button) -> {
            //TODO ADD GROUP
        })).setTooltip(Tooltip.create(Component.literal("Add Group")));
        addRenderableWidget(new ImageButton(this.width - 24, 1, 11, 11, 33, 15, 11, HEADING, 256, 256, (button) -> {
            //TODO EDIT
        })).setTooltip(Tooltip.create(Component.literal("Enable Edit Mode")));
        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests = new ArrayList<>();
        menu.quests().forEach((id, status) ->
            ClientQuests.get(id).ifPresent(quest -> quests.add(Pair.of(quest, status)))
        );
        addRenderableWidget(new QuestsWidget(
            (int) (this.width * 0.25f),
            15,
            (int) (this.width * 0.75f),
            this.height - 15
        )).update(quests);

        addRenderableWidget(new GroupsList(
            0,
            15,
            sidebarWidth,
            this.height - 15,
            entry -> {
                if (entry == null || this.menu.group().equals(entry.name())) return;
                NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(entry.name()));
            }
        )).update(ClientQuests.groups(), this.menu.group());
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(stack, partialTick, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        int center = ((int) (this.width * 0.25f) - 2) / 2;
        int textX = center - font.width("Groups") / 2;
        font.draw(poseStack, "Groups", textX, 3, 0x404040);
    }
}
