package earth.terrarium.heracles.mixins.client;

import earth.terrarium.heracles.client.handlers.ClientStructureDisplays;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(method = "clearLevel()V", at = @At("HEAD"))
    private void heracles$onDisconnect(CallbackInfo ci) {
        // Clear cached structure data when disconnecting from server
        // This ensures clean state when switching between servers or going to single player
        System.out.println("[DEBUG_LOG] Client disconnecting - clearing cached structure data");
        ClientStructureDisplays.clear();
    }
}