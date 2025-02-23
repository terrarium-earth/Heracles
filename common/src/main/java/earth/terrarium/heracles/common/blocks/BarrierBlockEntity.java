package earth.terrarium.heracles.common.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.regisitries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BarrierBlockEntity extends BlockEntity {

    private BarrierQuests questsHolder = new BarrierQuests(Set.of());

    public BarrierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.BARRIER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public boolean canPassthrough(Entity entity) {
        Level level = this.getLevel();
        if (level == null) return false;
        if (entity instanceof ServerPlayer player) {
            QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
            for (String quest : this.questsHolder.quests()) {
                if (progress.isComplete(quest)) {
                    return true;
                }
            }
        } else if (level.isClientSide) {
            for (String quest : this.questsHolder.quests()) {
                QuestProgress progress = ClientQuests.getProgress(quest);
                if (progress != null && progress.isComplete()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        questsHolder = BarrierQuests.CODEC.codec().parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        BarrierQuests.CODEC.codec().encode(questsHolder, NbtOps.INSTANCE, tag);
    }

    public record BarrierQuests(Set<String> quests) {
        private static final Codec<Set<String>> QUESTS_CODEC = Codec.list(Codec.STRING).xmap(HashSet::new, ArrayList::new);

        public static final MapCodec<BarrierQuests> CODEC = QUESTS_CODEC.fieldOf("quests").xmap(BarrierQuests::new, BarrierQuests::quests);
    }
}
