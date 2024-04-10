package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BarrierCommand {

    private static final SimpleCommandExceptionType NO_BARRIER = new SimpleCommandExceptionType(
        Component.literal("You must be holding a Quest Barrier to use this command.")
    );
    private static final SuggestionProvider<CommandSourceStack> QUESTS = (context, builder) -> {
        ItemStack stack = context.getSource().getPlayerOrException().getMainHandItem();
        if (!stack.is(ModItems.BARRIER.get())) return builder.buildFuture();
        List<String> quests = getQuests(stack.getTag());
        SharedSuggestionProvider.suggest(
            quests.stream().map(StringArgumentType::escapeIfRequired),
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
        CompoundTag tag = stack.getOrCreateTag();
        List<String> quests = getQuests(tag);
        if (add) {
            quests.add(quest);
        } else {
            quests.remove(quest);
        }
        ListTag listTag = new ListTag();
        quests.forEach(q -> listTag.add(StringTag.valueOf(q)));
        CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
        blockEntityTag.put("quests", listTag);
        tag.put("BlockEntityTag", blockEntityTag);
        stack.setTag(tag);
        return 1;
    }

    private static List<String> getQuests(CompoundTag tag) {
        if (tag == null) return new ArrayList<>();
        List<String> quests = new ArrayList<>();
        if (tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("quests")) {
            ListTag questsTag = tag.getCompound("BlockEntityTag").getList("quests", Tag.TAG_STRING);
            for (int i = 0; i < questsTag.size(); i++) {
                quests.add(questsTag.getString(i));
            }
        }
        return quests;
    }
}
