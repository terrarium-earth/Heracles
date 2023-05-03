package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.menu.MenuContentHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.menus.quest.QuestMenu;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.menus.quests.QuestsMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static final ResourcefulRegistry<MenuType<?>> MENUS = ResourcefulRegistries.create(BuiltInRegistries.MENU, Heracles.MOD_ID);

    public static final RegistryEntry<MenuType<QuestsMenu>> QUESTS = MENUS.register("quests", () -> MenuContentHelper.create(QuestsMenu::new, QuestsContent.SERIALIZER));
    public static final RegistryEntry<MenuType<QuestMenu>> QUEST = MENUS.register("quest", () -> MenuContentHelper.create(QuestMenu::new, QuestContent.SERIALIZER));
}
