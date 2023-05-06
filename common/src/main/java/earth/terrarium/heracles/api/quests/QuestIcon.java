package earth.terrarium.heracles.api.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;

public interface QuestIcon<T extends QuestIcon<T>> {

    void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int height);

    QuestIconType<T> type();
}
