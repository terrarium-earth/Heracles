package earth.terrarium.heracles.api.rewards.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.rewards.defaults.ItemReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record ItemRewardWidget(ItemReward reward) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.item.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.item.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.item.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.item.desc.plural";

    @Override
    public ItemStack getIcon() {
        return this.reward.stack();
    }

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(pose, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        String title = getIcon().getCount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = getIcon().getCount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        font.draw(pose, Component.translatable(title, getIcon().getHoverName()), x + (int) (width * 0.1f) + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, Component.translatable(desc, getIcon().getCount(), getIcon().getHoverName()), x + (int) (width * 0.1f) + 10, y + 7 + font.lineHeight, 0xFF808080);
    }
}
