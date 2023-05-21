package earth.terrarium.heracles.common.menus.quest;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class QuestMenu extends AbstractContainerMenu {

    private final QuestContent content;

    public QuestMenu(MenuType<?> type, int id, QuestContent content) {
        super(type, id);
        this.content = content;
    }

    public static QuestMenu ofOptional(int id, Inventory ignored, Optional<QuestContent> content) {
        return new QuestMenu(ModMenus.QUEST.get(), id, content.orElse(null));
    }

    public static QuestMenu of(int id, QuestContent content) {
        return new QuestMenu(ModMenus.QUEST.get(), id, content);
    }

    public static QuestMenu ofEditingOptional(int id, Inventory ignored, Optional<QuestContent> content) {
        return new QuestMenu(ModMenus.EDIT_QUEST.get(), id, content.orElse(null));
    }

    public static QuestMenu ofEditing(int id, QuestContent content) {
        return new QuestMenu(ModMenus.EDIT_QUEST.get(), id, content);
    }

    public String id() {
        return this.content != null ? this.content.id() : null;
    }

    public String fromGroup() {
        return this.content != null ? this.content.fromGroup() : null;
    }

    public Quest quest() {
        return this.content != null ? this.content.quest() : null;
    }

    public QuestProgress progress() {
        return this.content != null ? this.content.progress() : null;
    }

    public Map<String, ModUtils.QuestStatus> quests() {
        return this.content != null ? this.content.quests() : null;
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
