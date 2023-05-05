package earth.terrarium.heracles.api.tasks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;

public interface QuestTaskWidget {

    void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks);

    int getHeight(int width);

    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        return false;
    }
}
