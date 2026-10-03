package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.client.utils.TexturePlacements;
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

    private TexturePlacements.Info info = TexturePlacements.NO_OFFSET_24X;

    public QuestWidget(ClientQuests.QuestEntry entry, ModUtils.QuestStatus status) {
        this.entry = entry;
        this.quest = entry.value();
        this.status = status;
        this.id = entry.key();
    }

    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int mouseX, int mouseY, boolean hovered, float ignoredPartialTicks) {
        hovered = hovered && isMouseOver(mouseX - x, mouseY - y);

        info = TexturePlacements.getOrDefault(quest.display().iconBackground(), TexturePlacements.NO_OFFSET_24X);

        RenderSystem.enableBlend();
        
        graphics.blit(quest.display().iconBackground(),
            x + x() + info.xOffset(), y + y() + info.yOffset(),
            status.ordinal() * info.width(), 0,
            info.width(), info.height(),
            info.width() * 5, info.height()
        );

        if (hovered) {
            graphics.blit(quest.display().iconBackground(),
                x + x() + info.xOffset(), y + y() + info.yOffset(),
                4 * info.width(), 0,
                info.width(), info.height(),
                info.width() * 5, info.height()
            );
        }
        RenderSystem.disableBlend();
        quest.display().icon().render(graphics, x + x() + 4, y + y() + 4, 24, 24);
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
        return this.quest.display().position(HeraclesClient.lastGroup);
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

    public TexturePlacements.Info getTextureInfo() {
        return this.info;
    }
}
