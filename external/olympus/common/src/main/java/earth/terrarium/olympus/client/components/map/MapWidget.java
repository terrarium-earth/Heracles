package earth.terrarium.olympus.client.components.map;

import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UITexts;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class MapWidget extends BaseWidget {
    private static final Identifier MAP_ICONS = Identifier.withDefaultNamespace("textures/map/decorations/player.png");

    private final State<MapRenderer> mapRenderer;

    private int scale;
    private boolean initialized = false;
    private Identifier texture = UIConstants.MODAL_INSET;

    public MapWidget(State<MapRenderer> state) {
        super();
        this.mapRenderer = state;
    }

    private void renderLoading(GuiGraphics graphics) {
        var font = Minecraft.getInstance().font;
        graphics.drawCenteredString(font, UITexts.LOADING, (int) (getX() + getWidth() / 2f), (int) (getY() + getHeight() / 2f), 0xFFFFFF);
    }

    public MapWidget withTexture(Identifier texture) {
        this.texture = texture;
        return this;
    }

    public MapWidget withScale(int scale) {
        this.scale = scale;
        return this;
    }

    public MapWidget withRenderDistanceScale() {
        this.scale = Minecraft.getInstance().options.renderDistance().get() * 8;
        this.scale -= this.scale % 16;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        if (!initialized) {
            this.refreshMap();
            initialized = true;
        }

        if (mapRenderer.get() == null) {
            this.renderLoading(graphics);
        } else {
            if (mapRenderer.get().getScale() != this.scale * 2 + 16) {
                this.refreshMap();
            }

            var player = Minecraft.getInstance().player;
            if (player == null) return;

            mapRenderer.get().render(graphics, this.getX() + 1, this.getY() + 1, this.getWidth() - 2, this.getHeight() - 2);
            this.renderPlayerAvatar(player, graphics);
        }
    }

    public void refreshMap() {
        var player = Minecraft.getInstance().player;
        var level = Minecraft.getInstance().level;
        if (player == null || level == null) return;

        var chunkPos = player.chunkPosition();
        int minX = chunkPos.getMinBlockX() - scale;
        int minZ = chunkPos.getMinBlockZ() - scale;
        int maxX = chunkPos.getMaxBlockX() + scale + 1;
        int maxZ = chunkPos.getMaxBlockZ() + scale + 1;

        if (scale / 8 > 12) {
            // If the render distance is greater than 12 chunks, run asynchronously to avoid stuttering.
            CompletableFuture.supplyAsync(() -> MapTopologyAlgorithm.getColors(minX, minZ, maxX, maxZ, level, player)).thenAcceptAsync(colors ->
                    this.mapRenderer.set(new MapRenderer(colors, scale * 2 + 16)), Minecraft.getInstance());
        } else {
            int[][] colors = MapTopologyAlgorithm.getColors(minX, minZ, maxX, maxZ, level, player);
            this.mapRenderer.set(new MapRenderer(colors, scale * 2 + 16));
        }
    }

    private void renderPlayerAvatar(LocalPlayer player, GuiGraphics graphics) {
        float left = this.getWidth() / 2f;
        float top = this.getHeight() / 2f;

        double playerX = player.getX();
        double playerZ = player.getZ();
        double x = (playerX % 16) + (playerX >= 0 ? -8 : 8);
        double y = (playerZ % 16) + (playerZ >= 0 ? -8 : 8);

        x *= this.getWidth() / 144.0;
        y *= this.getHeight() / 144.0;

        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate((float) (this.getX() + left + x), (float) (this.getY() + top + y));
        pose.rotate((float) Math.toRadians(player.getYRot()));
        pose.translate(-4f, -4f);

        graphics.blit(RenderPipelines.GUI_TEXTURED, MAP_ICONS, 0, 0, 0f, 0f, 8, 8, 8, 8);

        pose.popMatrix();
    }
}