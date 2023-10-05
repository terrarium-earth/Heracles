package earth.terrarium.heracles.api.quests;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import net.minecraft.client.gui.GuiGraphics;

public interface QuestIcon<T extends QuestIcon<T>> {

    void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int height);

    QuestIconType<T> type();

    boolean isVisible();
}
