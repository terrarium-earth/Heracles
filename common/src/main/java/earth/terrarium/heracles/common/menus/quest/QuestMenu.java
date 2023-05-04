package earth.terrarium.heracles.common.menus.quest;

import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import net.minecraft.Optionull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class QuestMenu extends AbstractContainerMenu {

    private final QuestContent content;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public QuestMenu(int id, Inventory ignored, Optional<QuestContent> content) {
        this(id, content.orElse(null));
    }

    public QuestMenu(int id, QuestContent content) {
        super(ModMenus.QUEST.get(), id);
        this.content = content;
    }

    public boolean isEditing() {
        return this.content != null && this.content.editing();
    }

    public Quest quest() {
        Optional<ClientQuests.QuestEntry> entry = Optionull.mapOrDefault(Optionull.map(this.content, QuestContent::id), ClientQuests::get, Optional.empty());
        return entry.map(ClientQuests.QuestEntry::value).orElse(null);
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
