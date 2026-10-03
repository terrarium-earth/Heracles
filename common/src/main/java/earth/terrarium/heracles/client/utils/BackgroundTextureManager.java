package earth.terrarium.heracles.client.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.Heracles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BackgroundTextureManager {

    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> LOADING_STATE = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> FAILURE_CACHE = new ConcurrentHashMap<>();

    public static ResourceLocation getTexture(String background) {
        if (background == null || background.isEmpty()) return null;

        ResourceLocation cached = TEXTURE_CACHE.get(background);
        if (cached != null) return cached;

        if (FAILURE_CACHE.containsKey(background)) return null;

        if (isResourceLocation(background)) {
            ResourceLocation loc = ResourceLocation.tryParse(background);
            if (loc != null) {
                TEXTURE_CACHE.put(background, loc);
                return loc;
            }
            FAILURE_CACHE.put(background, true);
            return null;
        }

        if (!LOADING_STATE.containsKey(background)) {
            LOADING_STATE.put(background, true);
            loadExternalTexture(background);
        }
        return null;
    }

    private static boolean isResourceLocation(String value) {
        // A string is a resource location if it contains a colon (namespace separator)
        // but is not a URL (doesn't contain "://").
        return value.contains(":") && !value.contains("://");
    }

    private static void loadExternalTexture(String background) {
        CompletableFuture.runAsync(() -> {
            try {
                NativeImage image = loadImage(background);
                if (image != null) {
                    RenderSystem.recordRenderCall(() -> {
                        try {
                            DynamicTexture texture = new DynamicTexture(image);
                            // Generate a unique ID for the dynamic texture based on the path's hash
                            String textureId = "heracles_bg_" + Math.abs(background.hashCode());
                            ResourceLocation loc = Minecraft.getInstance().getTextureManager().register(textureId, texture);

                            TEXTURE_CACHE.put(background, loc);
                        } catch (Exception e) {
                            Heracles.LOGGER.error("Failed to register texture for: " + background, e);
                            FAILURE_CACHE.put(background, true);
                        } finally {
                            LOADING_STATE.remove(background);
                        }
                    });
                } else {
                    FAILURE_CACHE.put(background, true);
                    LOADING_STATE.remove(background);
                }
            } catch (Exception e) {
                Heracles.LOGGER.error("Error while loading external texture: " + background, e);
                FAILURE_CACHE.put(background, true);
                LOADING_STATE.remove(background);
            }
        });
    }

    private static NativeImage loadImage(String background) {
        try {
            byte[] data;
            if (background.startsWith("http://") || background.startsWith("https://")) {
                InputStream connection = new URL(background).openStream();
                try (connection) {
                    data = connection.readAllBytes();
                } catch (Exception e) {
                    Heracles.LOGGER.error("Failed to open stream for: " + background, e);
                    return null;
                }
            } else {
                Path path = Path.of(background);
                if (Files.exists(path)) {
                    data = Files.readAllBytes(path);
                } else {
                    Heracles.LOGGER.warn("External background file not found: {}", background);
                    return null;
                }
            }

            // Try standard Minecraft/NativeImage loading first (optimized for PNG)
            try {
                NativeImage img = NativeImage.read(new ByteArrayInputStream(data));
                if (img != null) return img;
            } catch (Exception e) {
                // Not a PNG or NativeImage-compatible format, proceed to fallback
            }

            // Fallback: use ImageIO for JPEGs and other formats
            BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(data));
            if (buffered != null) {
                return bufferedImageToNativeImage(buffered);
            }
        } catch (Exception e) {
            Heracles.LOGGER.error("Failed to load image data from: " + background, e);
        }
        return null;
    }

    private static NativeImage bufferedImageToNativeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);
                // Convert AARRGGBB (BufferedImage) to ABGR (NativeImage RGBA format expects this order)
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                nativeImage.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }
        return nativeImage;
    }

    public static void invalidate(String background) {
        if (background == null || background.isEmpty()) return;
        TEXTURE_CACHE.remove(background);
        LOADING_STATE.remove(background);
        FAILURE_CACHE.remove(background);
    }
}