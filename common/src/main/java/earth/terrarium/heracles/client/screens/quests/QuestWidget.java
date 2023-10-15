package earth.terrarium.heracles.client.screens.quests;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

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

    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int mouseX, int mouseY, boolean hovered, float ignoredPartialTicks) {
        int offset = switch (status) {
            case COMPLETED_CLAIMED -> 24;
            case COMPLETED -> 48;
            case LOCKED -> 72;
            default -> 0;
        };
        hovered = hovered && isMouseOver(mouseX - x, mouseY - y);
        graphics.blit(quest.display().iconBackground(), x + x(), y + y(), offset, 0, 24, 24, 96, 24);
        if (hovered) {
            graphics.fill(x + x(), y + y(), x + x() + 24, y + y() + 24, 0x50FFFFFF);
        }
        quest.display().icon().render(graphics, scissor, x + x() + 4, y + y() + 4, 24, 24);
        CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
        if (hovered && (!(ClientUtils.screen() instanceof QuestsScreen screen) || !screen.isTemporaryWidgetVisible())) {
            String subtitleText = quest.display().subtitle().getString().trim();
            if (subtitleText.isBlank()) {
                ScreenUtils.setTooltip(quest.display().title().copy().withStyle(style -> style.withBold(true)), false);
            } else {
                List<Component> lines = new ArrayList<>(List.of(
                    quest.display().title().copy().withStyle(style -> style.withBold(true)),
                    quest.display().subtitle()
                ));
                if (status == ModUtils.QuestStatus.COMPLETED) lines.add(ConstantComponents.Quests.CLAIMABLE);
                ScreenUtils.setTooltip(lines, false);
            }
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x() && mouseX <= x() + 24 && mouseY >= y() && mouseY <= y() + 24;
    }

    public int x() {
        return position().x();
    }

    public int y() {
        return position().y();
    }

    public Vector2i position() {
        if (ClientUtils.screen() instanceof QuestsScreen screen) {
            return this.quest.display().position(screen.getGroup());
        }
        return new Vector2i();
    }

    public String group() {
        if (ClientUtils.screen() instanceof QuestsScreen screen) {
            return screen.getGroup();
        }
        return "";
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
