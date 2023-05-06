package earth.terrarium.heracles.api.quests.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIconType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record ItemQuestIcon(Item item) implements QuestIcon<ItemQuestIcon> {

    public static final QuestIconType<ItemQuestIcon> TYPE = new Type();

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int height) {
        Minecraft.getInstance().getItemRenderer()
            .renderGuiItem(pose, item.getDefaultInstance(), x + (width - 16) / 2, y + (height - 16) / 2);
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
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemQuestIcon::item)
            ).apply(instance, ItemQuestIcon::new));
        }
    }
}
