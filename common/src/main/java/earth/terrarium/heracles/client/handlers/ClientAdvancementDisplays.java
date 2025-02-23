package earth.terrarium.heracles.client.handlers;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientAdvancementDisplays {

    private static final Map<ResourceLocation, DisplayInfo> ADVANCEMENTS = new HashMap<>();

    public static void add(Map<ResourceLocation, DisplayInfo> advancements) {
        ADVANCEMENTS.clear();
        ADVANCEMENTS.putAll(advancements);
    }

    public static Optional<DisplayInfo> get(ResourceLocation id) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            var list = connection.getAdvancements();
            var advancement = list.get(id);

            if (advancement != null) {
                return advancement.value().display();
            }
        }
        return Optional.ofNullable(ADVANCEMENTS.get(id));
    }

    public static Set<ResourceLocation> getAdvancements() {
        Set<ResourceLocation> advancements = new HashSet<>();
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            var list = connection.getAdvancements().getTree();
            for (var node : list.nodes()) {
                var display = node.advancement().display();
                if (display.isPresent()) {
                    advancements.add(node.holder().id());
                }
            }
        }
        advancements.addAll(ADVANCEMENTS.keySet());
        return advancements;
    }
}
