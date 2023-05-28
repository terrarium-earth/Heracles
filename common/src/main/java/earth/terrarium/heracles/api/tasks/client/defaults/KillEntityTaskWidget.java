package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.caches.CacheableFunction;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

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
        WidgetUtils.drawBackground(graphics, x, y, width);
        int iconSize = (int) (width * 0.1f);
        Entity entity = factory.apply(this.task.entity().entityType());
        if (entity instanceof LivingEntity living) {
            try (var ignored = RenderUtils.createScissorBoxStack(scissor, Minecraft.getInstance(), graphics.pose(), x + 5, y + 5, iconSize, iconSize)) {
                InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x + 5 + (int) (iconSize / 2f), y + 5 + iconSize, (int) (iconSize * 0.5f), x - mouseX, y - mouseY, living);
            }
        }
        String desc = this.task.target() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        Component entityName = this.task.entity().entityType().getDescription();
        graphics.drawString(
            font,
            TaskTitleFormatter.create(this.task), x + iconSize + 10, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.task.target(), entityName), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        graphics.drawString(
            font,
            progress, x + width - 5 - font.width(progress), y + 5, 0xFFFFFFFF,
            false
        );

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 10, y + height - font.lineHeight + 2, x + width - 5, y + height - 2, this.task, this.progress);
    }

    @Override
    public int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}
