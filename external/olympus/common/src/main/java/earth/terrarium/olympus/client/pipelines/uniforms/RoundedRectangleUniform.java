package earth.terrarium.olympus.client.pipelines.uniforms;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public record RoundedRectangleUniform(
        Vector4f borderColorTopLeft,
        Vector4f borderColorTopRight,
        Vector4f borderColorBottomLeft,
        Vector4f borderColorBottomRight,
        Vector4f radius,
        float borderWidth,
        Vector2f size,
        Vector2f center,
        float scaleFactor
) implements RenderPipelineUniforms {

    /** @deprecated Use the static factory method 'of' instead. */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="26.1")
    public RoundedRectangleUniform(
        Vector4f borderColor,
        Vector4f radius,
        float borderWidth,
        Vector2f size,
        Vector2f center,
        float scaleFactor
    ) {
        this(
                borderColor,
                borderColor,
                borderColor,
                borderColor,
                radius,
                borderWidth,
                size,
                center,
                scaleFactor
        );
    }

    public static final String NAME = "RoundedRectangleUniform";
    public static final Supplier<DynamicUniformStorage<RoundedRectangleUniform>> STORAGE = RenderPipelineUniformsStorage.register(
            "Rounded Rectangle UBO",
            2,
            new Std140SizeCalculator().putVec4().putVec4().putVec4().putVec4().putVec4().putFloat().putVec2().putVec2().putFloat()
    );

    public static RoundedRectangleUniform of(
            Vector4f borderColor,
            Vector4f radius,
            float borderWidth,
            Vector2f size,
            Vector2f center,
            float scaleFactor
    ) {
        return of(
                borderColor,
                borderColor,
                borderColor,
                borderColor,
                radius,
                borderWidth,
                size,
                center,
                scaleFactor
        );
    }

    public static RoundedRectangleUniform of(
            Vector4f borderColorTopLeft,
            Vector4f borderColorTopRight,
            Vector4f borderColorBottomLeft,
            Vector4f borderColorBottomRight,
            Vector4f radius,
            float borderWidth,
            Vector2f size,
            Vector2f center,
            float scaleFactor
    ) {
        return new RoundedRectangleUniform(
                borderColorTopLeft,
                borderColorTopRight,
                borderColorBottomLeft,
                borderColorBottomRight,
                radius,
                borderWidth,
                size,
                center,
                scaleFactor
        );
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void write(ByteBuffer buffer) {
        Std140Builder.intoBuffer(buffer)
                .putVec4(borderColorTopLeft)
                .putVec4(borderColorTopRight)
                .putVec4(borderColorBottomLeft)
                .putVec4(borderColorBottomRight)
                .putVec4(radius)
                .putFloat(borderWidth)
                .putVec2(size)
                .putVec2(center)
                .putFloat(scaleFactor)
                .get();
    }
}
