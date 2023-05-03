package earth.terrarium.heracles.common.menus.quests;

import earth.terrarium.heracles.common.regisitries.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class QuestsMenu extends AbstractContainerMenu {

    private final QuestsContent content;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public QuestsMenu(int id, Inventory ignored, Optional<QuestsContent> content) {
        this(id, content.orElse(null));
    }

    protected QuestsMenu(int id, QuestsContent content) {
        super(ModMenus.QUESTS.get(), id);
        this.content = content;
    }

    public boolean canEdit() {
        return this.content != null && this.content.canEdit();
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
