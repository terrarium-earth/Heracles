package earth.terrarium.heracles.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarrierItem extends BlockItem {

    public BarrierItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("quests")) {
            ListTag quests = tag.getCompound("BlockEntityTag").getList("quests", Tag.TAG_STRING);
            tooltipComponents.add(Component.nullToEmpty("Quests:"));
            for (int i = 0; i < quests.size(); i++) {
                tooltipComponents.add(Component.nullToEmpty(quests.getString(i)));
            }
            tooltipComponents.add(CommonComponents.EMPTY);
            tooltipComponents.add(Component.nullToEmpty("Until UI refactor you can only use /heracles barrier [add|remove] [quest]"));
        }
    }
}
