package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.caches.CacheableFunction;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record KillEntityTaskWidget(KillEntityQuestTask task, TaskProgress<NumericTag> progress,
                                   CacheableFunction<EntityType<?>, Entity> factory) implements DisplayWidget {

    private static final String DESC_SINGULAR = "task.heracles.kill_entity.desc.singular";
    private static final String DESC_PLURAL = "task.heracles.kill_entity.desc.plural";

    public KillEntityTaskWidget(KillEntityQuestTask task, TaskProgress<NumericTag> progress) {
        this(task, progress, new CacheableFunction<>(type -> {
            Entity entity = type.create(Minecraft.getInstance().level);
            if (entity != null) {
                task.entity().getTag().ifPresent(entity::load);
            }
            return entity;
        }));
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int iconSize = 32;
        if (!task.icon().render(graphics, x + 5, y + 5, iconSize, iconSize)) {
            Entity entity = factory.apply(this.task.entity().entityType());
            try (var ignored = RenderUtils.createScissorBoxStack(scissor, Minecraft.getInstance(), graphics.pose(), x + 5, y + 5, iconSize, iconSize)) {
                WidgetUtils.drawEntity(graphics, x + 5, y + 5, iconSize, entity);
            }
        }
        String desc = this.task.target() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        Component entityName = this.task.entity().entityType().getDescription();
        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(this.task)), x + iconSize + 16, y + 6, QuestScreenTheme.getTaskTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.task.target(), entityName), x + iconSize + 16, y + 8 + font.lineHeight, QuestScreenTheme.getTaskDescription(),
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 5, x + width - 5, y + height - 6, this.task, this.progress);
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}
