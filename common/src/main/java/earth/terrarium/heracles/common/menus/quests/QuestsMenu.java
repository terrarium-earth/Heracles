package earth.terrarium.heracles.common.menus.quests;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class QuestsMenu extends AbstractContainerMenu {

    private final QuestsContent content;

    public QuestsMenu(MenuType<?> type, int id, QuestsContent content) {
        super(type, id);
        this.content = content;
    }

    public static QuestsMenu ofOptional(int id, Inventory ignored, Optional<QuestsContent> content) {
        return new QuestsMenu(ModMenus.QUESTS.get(), id, content.orElse(null));
    }

    public static QuestsMenu of(int id, QuestsContent content) {
        return new QuestsMenu(ModMenus.QUESTS.get(), id, content);
    }

    public static QuestsMenu ofEditingOptional(int id, Inventory ignored, Optional<QuestsContent> content) {
        return new QuestsMenu(ModMenus.EDIT_QUESTS.get(), id, content.orElse(null));
    }

    public static QuestsMenu ofEditing(int id, QuestsContent content) {
        return new QuestsMenu(ModMenus.EDIT_QUESTS.get(), id, content);
    }

    public boolean canEdit() {
        return this.content != null && this.content.canEdit();
    }

    public Map<String, ModUtils.QuestStatus> quests() {
        Map<String, ModUtils.QuestStatus> quests = new HashMap<>();
        if (this.content != null) {
            for (var entry : this.content.quests().entrySet()) {
                ClientQuests.get(entry.getKey())
                    .map(ClientQuests.QuestEntry::value)
                    .map(Quest::display)
                    .map(QuestDisplay::group).filter(group -> group.equals(this.content.group()))
                    .ifPresent(group -> quests.put(entry.getKey(), entry.getValue()));
            }
        }
        return quests;
    }

    public String group() {
        return this.content != null ? this.content.group() : null;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
