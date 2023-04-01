package earth.terrarium.hercules.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Objects;

public record FunctionQuestReward(CommandFunction.CacheableFunction function) implements QuestReward {
    public static final MapCodec<FunctionQuestReward> CODEC = ResourceLocation.CODEC
            .xmap(CommandFunction.CacheableFunction::new, CommandFunction.CacheableFunction::getId)
            .fieldOf("function")
            .xmap(FunctionQuestReward::new, FunctionQuestReward::function);

    @Override
    public void reward(ServerPlayer player) {
        ServerFunctionManager functions = player.server.getFunctions();
        function().get(functions).ifPresent(commandFunction ->
                functions.execute(
                        commandFunction,
                        player.createCommandSourceStack().withSuppressedOutput().withPermission(2)
                )
        );
    }

    @Override
    public Codec<? extends QuestReward> codec() {
        return CODEC.codec();
    }
}
