package earth.terrarium.heracles.client.components.lists;

import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class QuestList<T> extends BaseWidget {

    public static final double OVERSCROLL = 5.0D;

    private final QuestContent content;

    private final List<ListEntry<T>> entries = new ArrayList<>();

    private double scroll;
    private int fullHeight;

    public QuestList(@Nullable QuestList<T> list, int width, int height, QuestContent content) {
        super(width, height);
        this.content = content;

        if (list != null) {
            this.scroll = list.scroll;
            this.fullHeight = list.fullHeight;
            this.entries.addAll(list.entries);
            this.entries.forEach(entry -> entry.setList(this));
        } else {
            this.scroll = 0.0D;
            this.fullHeight = this.height;
            update();
        }
    }

    public QuestList(int width, int height, QuestContent content) {
        super(width, height);
        this.content = content;

        this.scroll = 0.0D;
        this.fullHeight = this.height;
        update();
    }

    public abstract ListEntry<T> create(T value, DisplayWidget widget);

    public void update() {
        update(this.content.fromGroup());
    }

    public abstract void update(String group);

    public void clear() {
        this.entries.clear();
    }

    public void add(ListEntry<T> entry) {
        this.entries.add(entry);
        entry.setList(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateScroll(0D);
        this.fullHeight = 0;

        int x = getX();
        int y = getY() - Mth.floor(this.scroll);

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, getX(), getY(), getWidth(), getHeight())) {
            for (ListEntry<T> entry : this.entries) {

                entry.render(graphics, scissor.stack(), x, y, this.width, mouseX, mouseY, isHovered(), partialTick);

                int height = entry.getHeight(this.width);

                y += height;
                this.fullHeight += height;
            }
        }
    }

    public void updateScroll(double amount) {
        this.scroll = Mth.clamp(this.scroll - amount * 10, -OVERSCROLL, Math.max(-OVERSCROLL, this.fullHeight - this.height + OVERSCROLL));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        updateScroll(delta);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            int y = getY() - Mth.floor(this.scroll);
            for (ListEntry<T> entry : this.entries) {
                int height = entry.getHeight(this.width);
                if (mouseY >= y && mouseY <= y + height) {
                    return entry.mouseClicked(mouseX - getX(), mouseY - y, button, this.width);
                }
                y += height;
            }
        }
        return false;
    }

    public QuestContent content() {
        return this.content;
    }
}
