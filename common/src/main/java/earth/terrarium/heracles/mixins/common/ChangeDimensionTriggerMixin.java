package earth.terrarium.heracles.mixins.common;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.tasks.defaults.ChangedDimensionTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChangeDimensionTrigger.class)
public class ChangeDimensionTriggerMixin {


    @Inject(
        method = "trigger",
        at = @At("HEAD")
    )
    private void heracles$changeDimension(ServerPlayer player, ResourceKey<Level> fromLevel, ResourceKey<Level> toLevel, CallbackInfo ci) {
        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, Pair.of(fromLevel, toLevel), ChangedDimensionTask.TYPE);
    }
}
