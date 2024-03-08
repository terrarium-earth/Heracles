package earth.terrarium.heracles.client.components.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.utils.TexturePlacements;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import org.joml.Vector2i;

public class QuestWidget extends BaseWidget {

    protected final String group;
    protected final Quest quest;
    protected final ClientQuests.QuestEntry entry;
    protected final TexturePlacements.Info info;
    protected final ModUtils.QuestStatus status;
    protected boolean selected;

    public QuestWidget(String group, ClientQuests.QuestEntry entry, ModUtils.QuestStatus status) {
        super(0, 0);
        this.group = group;
        this.entry = entry;
        this.quest = entry.value();

        this.info = TexturePlacements.getOrDefault(entry.value().display().iconBackground(), TexturePlacements.NO_OFFSET_24X);
        this.status = status;
        this.height = info.height();
        this.width = info.width();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        graphics.blit(this.quest.display().iconBackground(),
            getX() + info.xOffset(), getY() + info.yOffset(), this.status.ordinal() * getWidth(), 0,
            getWidth(), getHeight(), getWidth() * 5, getHeight()
        );

        if (isHovered()) {
            graphics.blit(this.quest.display().iconBackground(),
                getX() + info.xOffset(), getY() + info.yOffset(), ModUtils.QuestStatus.SIZE * getWidth(), 0,
                getWidth(), getHeight(), getWidth() * 5, getHeight()
            );
        }

        RenderSystem.disableBlend();

        this.quest.display().icon().render(graphics, getX(), getY(), 24, 24);

        if (this.selected) {
            graphics.renderOutline(
                getX() + info.xOffset() - 2, getY() + info.yOffset() - 2,
                getWidth() + 4, getHeight() + 4,
                0xFFA8EFF0
            );
        }

        if (!isHovered()) return;

        if (this.status == ModUtils.QuestStatus.COMPLETED) {
            withTooltip(CommonComponents.joinLines(
                this.quest.display().title(),
                this.quest.display().subtitle(),
                ConstantComponents.Quests.CLAIMABLE
            ));
        } else {
            withTooltip(CommonComponents.joinLines(
                this.quest.display().title(),
                this.quest.display().subtitle()
            ));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.clicked(mouseX, mouseY);
    }

    public void updatePosition(int centerX, int centerY) {
        Vector2i position = this.quest.display().position(this.group);
        setX(centerX + position.x());
        setY(centerY + position.y());
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.POINTER;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public ClientQuests.QuestEntry entry() {
        return this.entry;
    }

    public Vector2i position() {
        return this.quest.display().position(this.group);
    }
}
