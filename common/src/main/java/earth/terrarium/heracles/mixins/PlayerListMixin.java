package earth.terrarium.heracles.mixins;

import earth.terrarium.heracles.common.handlers.syncing.QuestSyncer;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V"
        )
    )
    private void heracles$afterSyncData(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        QuestSyncer.sync(player);
    }

    @Inject(
        method = "reloadResources",
        at = @At("TAIL")
    )
    private void heracles$afterSyncDataToAll(CallbackInfo ci) {
        QuestSyncer.syncToAll(this.players);
    }
}
