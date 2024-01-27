package earth.terrarium.heracles.client.handlers;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientAdvancementDisplays {

    private static final Map<ResourceLocation, DisplayInfo> ADVANCEMENTS = new HashMap<>();

    public static void add(Map<ResourceLocation, DisplayInfo> advancements) {
        ADVANCEMENTS.clear();
        ADVANCEMENTS.putAll(advancements);
    }

    public static DisplayInfo get(ResourceLocation id) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            AdvancementList list = connection.getAdvancements().getAdvancements();
            Advancement advancement = list.get(id);
            if (advancement != null) {
                return advancement.getDisplay();
            }
        }
        return ADVANCEMENTS.get(id);
    }

    public static Set<ResourceLocation> getAdvancements() {
        Set<ResourceLocation> advancements = new HashSet<>();
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            AdvancementList list = connection.getAdvancements().getAdvancements();
            for (Advancement advancement : list.getAllAdvancements()) {
                if (advancement.getDisplay() == null) continue;
                advancements.add(advancement.getId());
            }
        }
        advancements.addAll(ADVANCEMENTS.keySet());
        return advancements;
    }
}
