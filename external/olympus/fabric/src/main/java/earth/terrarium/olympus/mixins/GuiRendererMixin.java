package earth.terrarium.olympus.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import earth.terrarium.olympus.client.fabric.PictureInPictureHandler;
import earth.terrarium.olympus.client.fabric.PictureInPicturePool;
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @Shadow @Final GuiRenderState renderState;
    @Unique private PictureInPictureHandler pipHandler = null;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initPictureInPictureHandler(GuiRenderState guiRenderState, MultiBufferSource.BufferSource source, SubmitNodeCollector submitNodeCollector, FeatureRenderDispatcher featureRenderDispatcher, List list, CallbackInfo ci) {
        this.pipHandler = new PictureInPictureHandler(source);
    }

    @WrapMethod(method = "preparePictureInPictureState")
    private void fixPipState(PictureInPictureRenderState state, int scale, Operation<Void> original) {
        if (this.pipHandler != null && state instanceof OlympusPictureInPictureRenderState<?> olympusState) {
            PictureInPicturePool<PictureInPictureRenderState> pool = this.pipHandler.getPool(olympusState);
            if (pool != null) {
                pool.prepare(state, this.renderState, scale);
                return;
            }
        }

        original.call(state, scale);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void endPictureInPictureStates(GpuBufferSlice gpuBufferSlice, CallbackInfo ci) {
        if (this.pipHandler == null) return;
        this.pipHandler.end();
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void closePictureInPictureHandler(CallbackInfo ci) {
        if (this.pipHandler == null) return;
        this.pipHandler.close();
    }
}
