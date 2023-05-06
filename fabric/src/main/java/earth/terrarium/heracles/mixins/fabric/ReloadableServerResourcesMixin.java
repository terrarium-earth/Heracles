package earth.terrarium.heracles.mixins.fabric;

import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Inject(
        method = "loadResources",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/ReloadableServerResources;<init>(Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;I)V",
            shift = At.Shift.AFTER
        )
    )
    private static void heracles$loadResources(ResourceManager resourceManager, RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
        QuestHandler.load(FabricLoader.getInstance().getConfigDir());
    }
}
