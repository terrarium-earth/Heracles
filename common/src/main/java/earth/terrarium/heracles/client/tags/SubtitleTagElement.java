package earth.terrarium.heracles.client.tags;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.hermes.api.defaults.ComponentTagElement;
import earth.terrarium.hermes.api.themes.Theme;

import java.util.Map;

public class SubtitleTagElement extends ComponentTagElement {

    private final String id;
    private boolean firstRender = true;

    public SubtitleTagElement(Map<String, String> parameters) {
        super(parameters);
        this.id = parameters.get("id");
    }

    @Override
    public void render(Theme theme, PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        if (firstRender) {
            ClientQuests.get(id).map(ClientQuests.QuestEntry::value)
                .map(Quest::display)
                .flatMap(QuestDisplay::subtitle)
                .ifPresent(subtitle -> this.text = subtitle);
            firstRender = false;
        }
    }
}
