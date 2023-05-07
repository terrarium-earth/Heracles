package earth.terrarium.heracles.common.utils;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.menus.BasicContentMenuProvider;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.menus.quests.QuestsMenu;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector2i;

import java.util.List;

public class ModUtils {

    public static final Codec<Vector2i> VECTOR2I = Codec.INT
        .listOf()
        .comapFlatMap(
            list -> Util.fixedSize(list, 2).map(listx -> new Vector2i(listx.get(0), listx.get(1))),
            vector3f -> List.of(vector3f.x(), vector3f.y())
        );

    public static void openQuest(ServerPlayer player, String id) {
        Quest quest = QuestHandler.get(id);
        BasicContentMenuProvider.open(
            new QuestContent(
                id,
                quest,
                QuestProgressHandler.getProgress(player.server, player.getUUID()).getProgress(id)
            ),
            CommonComponents.EMPTY,
            QuestMenu::new,
            player
        );
    }

    public static void openGroup(ServerPlayer player, String group) {
        if (!QuestHandler.groups().contains(group)) {
            player.sendSystemMessage(Component.literal("Not a group " + group));
            player.closeContainer();
            return;
        }
        Object2BooleanMap<String> quests = new Object2BooleanOpenHashMap<>();
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        for (String quest : progress.completableQuests().getQuests(progress)) {
            quests.put(quest, false);
        }
        for (String quest : QuestHandler.quests().keySet()) {
            if (!quests.containsKey(quest)) {
                quests.put(quest, progress.isComplete(quest));
            }
        }
        BasicContentMenuProvider.open(
            new QuestsContent(group, quests, true),
            CommonComponents.GUI_TO_TITLE,
            QuestsMenu::new,
            player
        );
    }
}
