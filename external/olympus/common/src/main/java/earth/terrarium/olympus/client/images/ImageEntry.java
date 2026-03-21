package earth.terrarium.olympus.client.images;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ImageEntry {

    private final CompletableFuture<NativeImage> image;
    private final Identifier location;
    private boolean loaded = false;

    protected ImageEntry(CompletableFuture<NativeImage> image, Identifier location) {
        this.image = image;
        this.location = location;
    }

    public Identifier id() {
        var manager = Minecraft.getInstance().getTextureManager();
        if (!this.loaded && this.image.state() == CompletableFuture.State.SUCCESS) {
            manager.register(this.location, new DynamicTexture(location::toString, this.image.resultNow()));
            this.loaded = true;
            return this.location;
        } else if (this.loaded) {
            return this.location;
        } else {
            return MissingTextureAtlasSprite.getLocation();
        }
    }

    public void release() {
        if (this.image.state() != CompletableFuture.State.SUCCESS) return;
        Minecraft.getInstance().getTextureManager().release(this.location);
    }
}
