package earth.terrarium.heracles.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record BlockSource(ServerLevel level, BlockPos pos) {
    public Vec3 center() {
        return this.pos.getCenter();
    }

    public ServerLevel level() {
        return this.level;
    }

    public BlockPos pos() {
        return this.pos;
    }

    public BlockState blockState() {
        return level().getBlockState(pos());
    }

    public BlockEntity blockEntity() {
        return level().getBlockEntity(pos());
    }
}
