package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.heracles.common.blocks.BarrierBlockEntity;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;
import java.util.stream.Collectors;

public class BarrierCommand {

    private static final SimpleCommandExceptionType NO_BARRIER = new SimpleCommandExceptionType(
        Component.literal("You must be holding a Quest Barrier to use this command.")
    );
    private static final SuggestionProvider<CommandSourceStack> QUESTS = (context, builder) -> {
        ItemStack stack = context.getSource().getPlayerOrException().getMainHandItem();
        if (!stack.is(ModItems.BARRIER.get())) return builder.buildFuture();
        var questData = getQuests(stack);
        SharedSuggestionProvider.suggest(
            questData.quests().stream().map(StringArgumentType::escapeIfRequired),
            builder
        );
        return builder.buildFuture();
    };

    public static LiteralArgumentBuilder<CommandSourceStack> barrier() {
        return Commands.literal("barrier")
            .requires(source -> source.hasPermission(2))
            .then(add())
            .then(remove());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("add")
            .then(Commands.argument("quest", StringArgumentType.string())
                .suggests(ModCommands.QUESTS)
                .executes(context -> update(context, true))
            );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("remove")
            .then(Commands.argument("quest", StringArgumentType.string())
                .suggests(QUESTS)
                .executes(context -> update(context, false))
            );
    }

    private static int update(CommandContext<CommandSourceStack> context, boolean add) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String quest = StringArgumentType.getString(context, "quest");
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.BARRIER.get())) {
            if (stack.isEmpty()) {
                stack = new ItemStack(ModItems.BARRIER.get());
                player.setItemSlot(EquipmentSlot.MAINHAND, stack);
            } else {
                throw NO_BARRIER.create();
            }
        }

        var quests = new ArrayList<>(getQuests(stack).quests());

        if (add) {
            quests.add(quest);
        } else {
            quests.remove(quest);
        }

        BarrierBlockEntity.BarrierQuests.CODEC.codec().encodeStart(NbtOps.INSTANCE, new BarrierBlockEntity.BarrierQuests(new HashSet<>(quests)));

        return 1;
    }

    private static BarrierBlockEntity.BarrierQuests getQuests(ItemStack stack) {
        CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);

        if (blockEntityData == null) {
            return new BarrierBlockEntity.BarrierQuests(Set.of());
        }

        return blockEntityData.read(BarrierBlockEntity.BarrierQuests.CODEC).getOrThrow();
    }
}
