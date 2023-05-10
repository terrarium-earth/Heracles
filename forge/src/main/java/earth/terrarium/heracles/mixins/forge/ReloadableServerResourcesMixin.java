package earth.terrarium.heracles.mixins.forge;

import earth.terrarium.heracles.forge.RegistryAccessHolder;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin implements RegistryAccessHolder {

    private RegistryAccess heracles$registryAccess;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void heracles$onInit(RegistryAccess.Frozen arg, FeatureFlagSet arg2, Commands.CommandSelection arg3, int i, CallbackInfo ci) {
        this.heracles$registryAccess = arg;
    }

    @Override
    public RegistryAccess heracles$getRegistryAccess() {
        return heracles$registryAccess;
    }
}
