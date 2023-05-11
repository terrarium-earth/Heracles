package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.OpenQuestPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.Gui;
import org.joml.Vector2i;

public class QuestWidget {

    private final ClientQuests.QuestEntry entry;
    private final Quest quest;
    private final ModUtils.QuestStatus status;
    private final String id;

    public QuestWidget(ClientQuests.QuestEntry entry, ModUtils.QuestStatus status) {
        this.entry = entry;
        this.quest = entry.value();
        this.status = status;
        this.id = entry.key();
    }

    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        RenderUtils.bindTexture(quest.display().iconBackground());
        int offset = switch (status) {
            case COMPLETED -> 24;
            case LOCKED -> 48;
            default -> 0;
        };
        hovered = hovered && isMouseOver(mouseX - x, mouseY - y);
        Gui.blit(pose, x + x(), y + y(), offset, 0, 24, 24, 72, 24);
        if (hovered) {
            Gui.fill(pose, x + x(), y + y(), x + x() + 24, y + y() + 24, 0x50FFFFFF);
        }
        quest.display().icon().render(pose, scissor, x + x(), y + y(), 24, 24);
        CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
    }

    public void onClicked() {
        NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(this.id));
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x() && mouseX <= x() + 24 && mouseY >= y() && mouseY <= y() + 24;
    }

    public int x() {
        return this.quest.display().position().x();
    }

    public int y() {
        return this.quest.display().position().y;
    }

    public Vector2i position() {
        return this.quest.display().position();
    }

    public Quest quest() {
        return this.quest;
    }

    public ClientQuests.QuestEntry entry() {
        return this.entry;
    }

    public String id() {
        return this.id;
    }
}
