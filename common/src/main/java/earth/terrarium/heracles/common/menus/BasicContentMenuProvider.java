package earth.terrarium.heracles.common.menus;

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BasicContentMenuProvider<T extends MenuContent<T>>(
    T content,
    Component displayName,
    Factory<T> factory
) implements ContentMenuProvider<T> {

    public static <T extends MenuContent<T>> void open(T content, Component displayName, Factory<T> factory, ServerPlayer player) {
        new BasicContentMenuProvider<>(content, displayName, factory).openMenu(player);
    }

    @Override
    public T createContent() {
        return content;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return factory.createMenu(i, content);
    }

    @FunctionalInterface
    public interface Factory<T extends MenuContent<T>> {
        AbstractContainerMenu createMenu(int i, T content);
    }
}