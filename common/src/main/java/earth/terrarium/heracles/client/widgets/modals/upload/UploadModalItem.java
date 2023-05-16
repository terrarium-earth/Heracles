package earth.terrarium.heracles.client.widgets.modals.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.utils.ClientUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public record UploadModalItem(Path path, @Nullable Quest quest, String size, List<Component> error, Icon icon) {

    private static final Pattern MISSING_KEY_PATTERN = Pattern.compile("No key (.*) in .*");

    public static final int WIDTH = 152;
    public static final int HEIGHT = 28;

    public UploadModalItem(Path path, String size, String error, Icon icon) {
        this(path, null, size, List.of(Component.literal(error)), icon);
    }

    public static UploadModalItem of(Path path) {
        String size = "";
        try {
            File file = path.toFile();
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            size = " - " + FileUtils.byteCountToDisplaySize(file.length()).toLowerCase(Locale.ROOT);
            if (file.getName().endsWith(".json")) {
                try {
                    JsonObject json = Constants.GSON.fromJson(content, JsonObject.class);
                    final String finalSize = size;
                    return Quest.CODEC.parse(JsonOps.INSTANCE, json).get()
                        .map(
                            quest -> new UploadModalItem(path, quest, finalSize, List.of(), Icon.SUCCESS),
                            error -> {
                                var errors = new ArrayList<Component>();
                                errors.add(Component.literal("Invalid quest file, must be a quest template."));
                                errors.add(CommonComponents.EMPTY);
                                error.message().lines().forEach(line -> {
                                    String formatted = MISSING_KEY_PATTERN.matcher(line).replaceAll("Missing Key '$1' in object.");
                                    errors.add(Component.literal(formatted));
                                });
                                return new UploadModalItem(path, null, finalSize, errors, Icon.ERROR);
                            }
                        );
                } catch (JsonSyntaxException e) {
                    return new UploadModalItem(path, size, "Invalid JSON file.", Icon.ERROR);
                }
            }
            return new UploadModalItem(path, size, "Invalid file type, must be a .json file.", Icon.WARNING);
        } catch (Exception e) {
            return new UploadModalItem(path, size, e.getMessage(), Icon.ERROR);
        }
    }

    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int mouseX, int mouseY, boolean hovering, boolean hoveringRemove) {
        Font font = Minecraft.getInstance().font;

        RenderUtils.bindTexture(UploadModal.TEXTURE);
        Gui.blit(pose, x, y, 0, 173, WIDTH, HEIGHT, 256, 256);
        Gui.blit(pose, x + 4, y + 6 + font.lineHeight, 168 + icon.x, 28, 9, 9, 256, 256);

        Gui.blit(pose, x + WIDTH - 11, y + 2, 168, hovering && hoveringRemove ? 46 : 37, 9, 9, 256, 256);

        String fileName = path.getFileName().toString();

        try (var ignored = RenderUtils.createScissorBoxStack(scissor, Minecraft.getInstance(), pose, x + 4, y + 2, WIDTH - 21, font.lineHeight)) {
            int textWidth = font.width(fileName);
            if (textWidth > WIDTH - 21) {
                int overflow = textWidth - (WIDTH - 21);
                double seconds = (double) Util.getMillis() / 1000.0;
                double e = Math.max((double) overflow * 0.5, 3);
                double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * seconds / e)) / 2 + 0.5;
                int startX = (int) Mth.lerp(f, 0.0, overflow);
                font.draw(pose, fileName, x + 4 - startX, y + 3, 0xFFFFFF);
            } else {
                font.draw(pose, fileName, x + 4, y + 3, 0xFFFFFF);
            }
        }
        font.draw(pose, this.size, x + 13, y + 6 + font.lineHeight, 0xFFFFFF);
        if (hovering) {
            if (hoveringRemove) {
                ClientUtils.setTooltip(Component.literal("Remove"));
            } else if (!this.error.isEmpty() && mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT) {
                ClientUtils.setTooltip(this.error);
            }
        }
    }

    public boolean isErrored() {
        return !this.error.isEmpty();
    }

    private enum Icon {
        WARNING(0),
        ERROR(9),
        SUCCESS(18);

        private final int x;

        Icon(int x) {
            this.x = x;
        }
    }
}
