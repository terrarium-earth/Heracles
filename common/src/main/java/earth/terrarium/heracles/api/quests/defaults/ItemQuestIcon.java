package earth.terrarium.heracles.api.quests.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIconType;
import earth.terrarium.heracles.common.utils.ItemValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record ItemQuestIcon(ItemValue item) implements QuestIcon<ItemQuestIcon> {
    public static final QuestIconType<ItemQuestIcon> TYPE = new Type();
    public static final ItemQuestIcon AIR = new ItemQuestIcon(Items.AIR);

    public ItemQuestIcon(Item item) {
        this(new ItemValue(item));
    }

    @Override
    public boolean render(GuiGraphics graphics, int x, int y, int width, int height) {
        return WidgetUtils.drawItemIcon(graphics, item.getDefaultInstance(), x, y, width);
    }

    @Override
    public QuestIconType<ItemQuestIcon> type() {
        return TYPE;
    }

    private static class Type implements QuestIconType<ItemQuestIcon> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "item");
        }

        @Override
        public Codec<ItemQuestIcon> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                ItemValue.CODEC.fieldOf("item").forGetter(ItemQuestIcon::item)
            ).apply(instance, ItemQuestIcon::new));
        }
    }
}
