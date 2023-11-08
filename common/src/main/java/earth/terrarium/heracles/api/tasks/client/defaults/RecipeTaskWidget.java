package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.RecipeTask;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RecipeTaskWidget implements DisplayWidget {

    private static final String DESC_SINGULAR = "task.heracles.recipe.desc.singular";
    private static final String DESC_PLURAL = "task.heracles.recipe.desc.plural";

    private final RecipeTask task;
    private final TaskProgress<ByteTag> progress;
    private final Component title;
    private final List<ItemStack> icons;
    private final List<Component> titles;

    private boolean isOpened = false;

    public RecipeTaskWidget(
        RecipeTask task, TaskProgress<ByteTag> progress,
        Component title,
        List<ItemStack> icons, List<Component> titles
    ) {
        this.task = task;
        this.progress = progress;
        this.title = title;
        this.icons = icons;
        this.titles = titles;
    }

    public RecipeTaskWidget(RecipeTask task, TaskProgress<ByteTag> progress) {
        this(task, progress, TaskTitleFormatter.create(task), getRecipeIcons(task), getRecipeTitles(task));
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        int height = getHeight(width);
        int actualY = y;

        Font font = Minecraft.getInstance().font;
        graphics.fill(x, y, x + width, y + height, 0x80808080);
        graphics.renderOutline(x, y, width, height, 0xFF909090);

        int iconSize = 32;
        this.task.icon().renderOrStack(this.getCurrentItem(), graphics, scissor, x + 5, y + 5, iconSize);
        String desc = this.task.recipes().size() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        Object text = this.task.recipes().size() == 1 ? this.titles.isEmpty() ? "" : this.titles.get(0) : isOpened ? "▼" : "▶";
        graphics.drawString(
            font,
            task.titleOr(this.title), x + iconSize + 16, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, text), x + iconSize + 16, y + 8 + font.lineHeight, 0xFF808080,
            false
        );
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        graphics.drawString(
            font,
            progress, x + width - 5 - font.width(progress), y + 6, 0xFFFFFFFF,
            false
        );

        if (titles.size() > 1 && hovered && mouseY - y >= 7 + font.lineHeight && mouseY - y <= 7 + font.lineHeight * 2 && mouseX - x > (int) (width * 0.1f) && mouseX - x <= width) {
            CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
        }

        y += 5 + (font.lineHeight + 2) * 2;

        if (isOpened) {
            for (Component title : titles) {
                graphics.drawString(
                    font,
                    ConstantComponents.DOT.copy().append(title), x + iconSize + 13, y, 0xFFa0a0a0,
                    false
                );
                y += font.lineHeight + 2;
            }
        }

        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, actualY + height - font.lineHeight - 5, x + width - 5, actualY + height - 6, this.task, this.progress);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (mouseY < 0 || mouseY > getHeight(width)) return false;
        if (mouseX < 32 || mouseX > width) return false;
        if (mouseButton != 0) return false;
        if (this.titles.size() <= 1) return false;
        Font font = Minecraft.getInstance().font;
        if (mouseY >= 7 + font.lineHeight && mouseY <= 7 + font.lineHeight * 2) {
            this.isOpened = !this.isOpened;
            return true;
        }
        return false;
    }

    @Override
    public int getHeight(int width) {
        if (isOpened) {
            return 42 + (Minecraft.getInstance().font.lineHeight + 2) * (this.titles.size());
        }
        return 42;
    }

    private ItemStack getCurrentItem() {
        if (this.icons.size() == 0) {
            return Items.CRAFTING_TABLE.getDefaultInstance();
        }
        int index = Math.max(0, (int) ((System.currentTimeMillis() / 1000) % this.icons.size()));
        return this.icons.get(index);
    }

    private static List<Component> getRecipeTitles(RecipeTask task) {
        List<Component> titles = new ArrayList<>();
        for (ResourceLocation id : task.recipes()) {
            titles.add(getTranslation(id));
        }
        titles.removeIf(Objects::isNull);
        return titles;
    }

    private static Component getTranslation(ResourceLocation id) {
        return Component.translatableWithFallback(
            "recipes." + id.toLanguageKey().replace('/', '.'),
            id.toString()
        );
    }

    private static List<ItemStack> getRecipeIcons(RecipeTask task) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return List.of();
        RecipeManager manager = connection.getRecipeManager();
        List<ItemStack> icons = new ArrayList<>();
        for (ResourceLocation id : task.recipes()) {
            manager.byKey(id)
                .map(recipe -> recipe.getResultItem(Heracles.getRegistryAccess()))
                .map(ItemStack::copy)
                .ifPresent(icons::add);
        }
        icons.removeIf(Objects::isNull);
        return icons;
    }

}
