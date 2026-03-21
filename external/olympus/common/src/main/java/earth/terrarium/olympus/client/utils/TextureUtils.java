package earth.terrarium.olympus.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

public final class TextureUtils {

    public static TextureSetup single(Identifier texture) {
        return TextureUtils.single(Minecraft.getInstance().getTextureManager().getTexture(texture));
    }

    public static TextureSetup single(AbstractTexture texture) {
        return TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());
    }
}
