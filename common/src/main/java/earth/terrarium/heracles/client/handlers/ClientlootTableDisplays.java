package earth.terrarium.heracles.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootDataType;

import java.util.HashSet;
import java.util.Set;

public class ClientlootTableDisplays {

    private static final Set<ResourceLocation> LOOT_TABLES = new HashSet<>();

    public static void set(Set<ResourceLocation> lootTables) {
        LOOT_TABLES.clear();
        LOOT_TABLES.addAll(lootTables);
    }

    public static Set<ResourceLocation> getLootTables() {
        Set<ResourceLocation> lootTables = new HashSet<>();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                lootTables.addAll(server.getLootData().getKeys(LootDataType.TABLE));
            }
        }
        lootTables.addAll(LOOT_TABLES);
        return lootTables;
    }
}
