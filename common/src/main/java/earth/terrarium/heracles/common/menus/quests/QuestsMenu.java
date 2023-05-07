package earth.terrarium.heracles.common.menus.quests;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
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

    public QuestsMenu(int id, QuestsContent content) {
        super(ModMenus.QUESTS.get(), id);
        this.content = content;
    }

    public boolean canEdit() {
        return this.content != null && this.content.canEdit();
    }

    public Object2BooleanMap<String> quests() {
        Object2BooleanMap<String> quests = new Object2BooleanOpenHashMap<>();
        if (this.content != null) {
            for (Object2BooleanMap.Entry<String> entry : this.content.quests().object2BooleanEntrySet()) {
                ClientQuests.get(entry.getKey())
                    .map(ClientQuests.QuestEntry::value)
                    .map(Quest::group).filter(group -> group.equals(this.content.group()))
                    .ifPresent(group -> quests.put(entry.getKey(), entry.getBooleanValue()));
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
