package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.boxes.AutocompleteEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddDependencyModal extends BaseModal {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/dependencies.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 179;

    private final AutocompleteEditBox<ClientQuests.QuestEntry> dependencyBox;

    private ClientQuests.QuestEntry entry;
    private List<Quest> dependencies;
    private Runnable callback;

    public AddDependencyModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);

        // dependencies
        this.dependencyBox = addChild(new AutocompleteEditBox<>(Minecraft.getInstance().font, x + 8, y + 19, 152, 16, (text, item) -> {
            text = text.toLowerCase(Locale.ROOT).trim();
            Quest quest = item.value();
            String title = quest.display().title().getString().toLowerCase(Locale.ROOT).trim();
            String id = item.key().toLowerCase(Locale.ROOT).trim();
            return (title.contains(text) || id.contains(text)) && !id.equals(text);
        }, ClientQuests.QuestEntry::key, value -> {
            if (value != null && entry != null) {
                ClientQuests.QuestEntry entry = ClientQuests.get(value).orElse(null);
                if (entry != null) {
                    entry.children().add(this.entry);
                    this.entry.dependencies().add(entry);
                    this.dependencies.add(entry.value());
                    this.entry.value().dependencies().add(value);
                    this.callback.run();
                }
            }
        }));
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);
        Gui.blit(pose, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);

        int tempY = y + 43;

        if (dependencies != null) {
            try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x + 8, y + 43, 152, 120)) {
                for (Quest dependency : dependencies) {
                    RenderUtils.bindTexture(TEXTURE);
                    Gui.blitNineSliced(pose, x + 8, tempY, 152, 24, 1, 1, 1, 1, 19, 19, 168, 0);
                    boolean removeHovered = mouseX >= x + 149 && mouseX <= x + 158 && mouseY >= tempY + 2 && mouseY <= tempY + 11;
                    Gui.blit(pose, x + 149, tempY + 2, 187, removeHovered ? 9 : 0, 9, 9);
                    CursorUtils.setCursor(removeHovered, CursorScreen.Cursor.POINTER);
                    dependency.display().icon().render(pose, scissor.stack(), x + 9, tempY + 1, 22, 22);
                    Minecraft.getInstance().font.draw(pose, dependency.display().title(), x + 36, tempY + 6, 0xffffff);
                    tempY += 24;
                }
            }
        }

        renderChildren(pose, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (button == 0 && this.dependencies != null) {
            int tempY = y + 43;
            for (Quest dependency : this.dependencies) {
                if (mouseX >= x + 149 && mouseX <= x + 158 && mouseY >= tempY + 2 && mouseY <= tempY + 11) {
                    this.dependencies.remove(dependency);
                    for (ClientQuests.QuestEntry questEntry : ClientQuests.entries()) {
                        if (questEntry.value().equals(dependency)) {
                            questEntry.children().remove(this.entry);
                            this.entry.dependencies().remove(questEntry);
                            this.entry.value().dependencies().remove(questEntry.key());
                            this.callback.run();
                            break;
                        }
                    }
                    return true;
                }
                tempY += 24;
            }
        }
        return false;
    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        font.draw(pose, "Dependencies", x + 8, y + 6, 0x404040);
    }

    public void update(ClientQuests.QuestEntry entry, Runnable callback) {
        this.dependencyBox.setSuggestions(ClientQuests.entries());
        this.dependencies = new ArrayList<>();
        this.entry = entry;
        entry.dependencies().forEach(value -> this.dependencies.add(value.value()));
        this.callback = callback;
    }
}
