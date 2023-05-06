package earth.terrarium.heracles.api.rewards.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.rewards.defaults.WeightedItemReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record WeightedItemRewardWidget(WeightedItemReward reward) implements BaseItemRewardWidget {

    private static final Component TITLE_SINGULAR = Component.translatable("reward.heracles.weighted_item.title.singular");
    private static final Component TITLE_PLURAL = Component.translatable("reward.heracles.weighted_item.title.plural");
    private static final String DESC_SINGULAR = "reward.heracles.weighted_item.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.weighted_item.desc.plural";

    @Override
    public ItemStack getIcon() {
        var items = this.reward.items();
        int index = (int) (System.currentTimeMillis() / 1000) % items.size();
        return items.get(index).stack();
    }

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(pose, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        Component title = this.reward.rolls() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.reward.rolls() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        font.draw(pose, title, x + (int) (width * 0.1f) + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, Component.translatable(desc, this.reward.rolls()), x + (int) (width * 0.1f) + 10, y + 7 + font.lineHeight, 0xFF808080);
    }
}
