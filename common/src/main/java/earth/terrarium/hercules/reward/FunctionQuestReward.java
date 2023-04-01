package earth.terrarium.hercules.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.stream.Stream;

public record FunctionQuestReward(CommandFunction.CacheableFunction function, List<Item> items) implements QuestReward {
    public static final String KEY = "function";

    public static final Codec<FunctionQuestReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC
                    .xmap(CommandFunction.CacheableFunction::new, CommandFunction.CacheableFunction::getId)
                    .fieldOf("function")
                    .forGetter(FunctionQuestReward::function),
            BuiltInRegistries.ITEM.byNameCodec()
                    .listOf()
                    .fieldOf("items")
                    .forGetter(FunctionQuestReward::items)
    ).apply(instance, FunctionQuestReward::new));

    @Override
    public Stream<Item> reward(ServerPlayer player) {
        ServerFunctionManager functions = player.server.getFunctions();

        function().get(functions).ifPresent(commandFunction ->
                functions.execute(
                        commandFunction,
                        player.createCommandSourceStack().withSuppressedOutput().withPermission(2)
                )
        );

        return items.stream();
    }

    @Override
    public Codec<? extends QuestReward> codec() {
        return CODEC;
    }
}
