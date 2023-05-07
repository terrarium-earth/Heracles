package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.OpenQuestPacket;
import net.minecraft.client.gui.Gui;

public class QuestWidget {

    private final Quest quest;
    private final String id;

    public QuestWidget(ClientQuests.QuestEntry entry) {
        this.quest = entry.value();
        this.id = entry.key();
    }

    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Gui.renderOutline(pose, x + x(), y + y(), 32, 32, isMouseOver(mouseX - x, mouseY - y) ? 0xFF00FF00 : 0xFF000000);
        quest.icon().render(pose, scissor, x + x(), y + y(), 32, 32);
    }

    public void onClicked() {
        NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(this.id));
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x() && mouseX <= x() + 32 && mouseY >= y() && mouseY <= y() + 32;
    }

    public int x() {
        return this.quest.position().x;
    }

    public int y() {
        return this.quest.position().y;
    }
}
