package earth.terrarium.heracles.common.items;

import earth.terrarium.heracles.common.blocks.BarrierBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

public class BarrierItem extends BlockItem {

    public BarrierItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && data.contains("quests")) {
            Set<String> quests = data.read(BarrierBlockEntity.BarrierQuests.CODEC).getOrThrow().quests();
            tooltipComponents.add(Component.nullToEmpty("Quests:"));
            for (String quest : quests) {
                tooltipComponents.add(Component.nullToEmpty(quest));
            }
            tooltipComponents.add(CommonComponents.EMPTY);
            tooltipComponents.add(Component.nullToEmpty("Until UI refactor you can only use /heracles barrier [add|remove] [quest]"));
        }
    }
}
