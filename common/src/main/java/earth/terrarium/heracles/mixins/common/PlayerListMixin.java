package earth.terrarium.heracles.mixins.common;

import earth.terrarium.heracles.common.handlers.syncing.QuestSyncer;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.ClientboundAdvancementDisplayPacket;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
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

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(
        method = "placeNewPlayer",
        at = @At("TAIL")
    )
    private void heracles$afterSyncData(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        QuestSyncer.sync(player);
        NetworkHandler.CHANNEL.sendToPlayer(
            new ClientboundAdvancementDisplayPacket(this.server),
            player
        );
    }

    @Inject(
        method = "reloadResources",
        at = @At("TAIL")
    )
    private void heracles$afterSyncDataToAll(CallbackInfo ci) {
        QuestSyncer.syncToAll(this.server, this.players);
        NetworkHandler.CHANNEL.sendToPlayers(
            new ClientboundAdvancementDisplayPacket(this.server),
            this.players
        );
    }
}
