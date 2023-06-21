package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public record CommandReward(String id, String command) implements QuestReward<CommandReward> {

    public static final QuestRewardType<CommandReward> TYPE = new Type();

    @Override
    public Stream<ItemStack> reward(ServerPlayer player) {
        player.server.getCommands()
            .performPrefixedCommand(
                player.createCommandSourceStack().withSuppressedOutput().withPermission(2),
                command
            );
        return Stream.empty();
    }

    @Override
    public QuestRewardType<CommandReward> type() {
        return TYPE;
    }

    private static class Type implements QuestRewardType<CommandReward> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "command");
        }

        @Override
        public Codec<CommandReward> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("command").forGetter(CommandReward::command)
            ).apply(instance, CommandReward::new));
        }
    }
}
