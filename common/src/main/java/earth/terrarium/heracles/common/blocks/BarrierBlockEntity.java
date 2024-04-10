package earth.terrarium.heracles.common.blocks;

import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.regisitries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BarrierBlockEntity extends BlockEntity {

    private final Set<String> quests = new HashSet<>();

    public BarrierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.BARRIER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public boolean canPassthrough(Entity entity) {
        Level level = this.getLevel();
        if (level == null) return false;
        if (entity instanceof ServerPlayer player) {
            QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
            for (String quest : this.quests) {
                if (progress.isComplete(quest)) {
                    return true;
                }
            }
        } else if (level.isClientSide) {
            for (String quest : this.quests) {
                QuestProgress progress = ClientQuests.getProgress(quest);
                if (progress != null && progress.isComplete()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(CompoundTag tag) {
        this.quests.clear();
        for (Tag text : tag.getList("quests", 8)) {
            this.quests.add(text.getAsString());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (String quest : this.quests) {
            listTag.add(StringTag.valueOf(quest));
        }
        tag.put("quests", listTag);
    }
}
