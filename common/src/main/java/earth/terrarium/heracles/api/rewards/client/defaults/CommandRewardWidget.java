package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.rewards.defaults.CommandReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record CommandRewardWidget(CommandReward reward) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.command.title.singular";
    private static final String DESC_SINGULAR = "reward.heracles.command.desc.singular";

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.COMMAND_BLOCK);
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        int firstSpace = reward.command().indexOf(" ");
        String desc = firstSpace > 0 ? reward.command().substring(0, reward.command().indexOf(" ")) : reward.command();
        graphics.drawString(
            font,
            Component.translatable(TITLE_SINGULAR), x + (int) (width * 0.1f) + 10, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC_SINGULAR, desc), x + (int) (width * 0.1f) + 10, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
    }
}
