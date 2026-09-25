package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.caches.CacheableFunction;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.EntityInteractTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public record EntityInteractTaskWidget(
    EntityInteractTask task, TaskProgress<NumericTag> progress, CacheableFunction<EntityType<?>, Entity> factory
) implements DisplayWidget {

    private static final String DESC = "task.heracles.entity_interaction.desc.singular";

    public EntityInteractTaskWidget(EntityInteractTask task, TaskProgress<NumericTag> progress) {
        this(task, progress, new CacheableFunction<>(type -> {
            Entity entity = type.create(Minecraft.getInstance().level);
            if (entity != null && task.nbt().tag() != null) {
                entity.load(task.nbt().tag());
            }
            return entity;
        }));
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int iconSize = 32;
        if (!task.icon().render(graphics, x + 5, y + 5, iconSize, iconSize)) {
            EntityType<?> type = getType();
            if (type != null) {
                Entity entity = factory.apply(type);
                try (var ignored = RenderUtils.createScissorBoxStack(scissor, Minecraft.getInstance(), graphics.pose(), x + 5, y + 5, iconSize, iconSize)) {
                    WidgetUtils.drawEntity(graphics, x + 5, y + 5, iconSize, entity);
                }
            }
        }
        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(this.task)), x + iconSize + 16, y + 6, QuestScreenTheme.getTaskTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC, Component.keybind("key.use")), x + iconSize + 16, y + 8 + font.lineHeight, QuestScreenTheme.getTaskDescription(),
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

    private EntityType<?> getType() {
        return this.task.entity().getValue()
            .map(
                type -> type,
                tag -> {
                    List<EntityType<?>> value = ModUtils.getValue(Registries.ENTITY_TYPE, tag);
                    if (value.isEmpty()) return null;
                    int index = Math.max(0, (int) (System.currentTimeMillis() / 1000) % value.size());
                    return value.get(index);
                }
            );
    }
}
