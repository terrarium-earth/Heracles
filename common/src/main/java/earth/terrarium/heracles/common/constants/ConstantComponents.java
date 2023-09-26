package earth.terrarium.heracles.common.constants;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import earth.terrarium.heracles.common.annotations.Translate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.lang.reflect.Field;

public final class ConstantComponents {

    private ConstantComponents() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final Component DOT = Component.literal(" • ");
    public static final Component DASH = Component.literal(" - ");
    public static final Component EM_DASH = Component.literal(" — ");
    public static final Component ARROW_RIGHT = Component.literal("▶");
    public static final Component ARROW_DOWN = Component.literal("▼");
    public static final Component X = Component.literal("x");

    @Translate("Save")
    public static final Component SAVE = Component.translatable("gui.heracles.save");

    @Translate("Close")
    public static final Component CLOSE = Component.translatable("gui.heracles.close");

    @Translate("Submit")
    public static final Component SUBMIT = Component.translatable("gui.heracles.submit");

    @Translate("Search")
    public static final Component SEARCH = Component.translatable("gui.heracles.search");

    @Translate("Delete")
    public static final Component DELETE = Component.translatable("gui.heracles.delete");

    @Translate("Identifier")
    public static final Component ID = Component.translatable("gui.heracles.id");

    @Translate("Toggle Edit Mode")
    public static final Component TOGGLE_EDIT = Component.translatable("gui.heracles.toggle_edit");

    @Translate("Open Quest File")
    public static final Component OPEN_QUEST_FILE = Component.translatable("gui.heracles.open_quest_file");

    public static final class Tools {

        @Translate("Move/Select [V]")
        public static final Component MOVE = Component.translatable("gui.heracles.tools.move");

        @Translate("Hand/Drag Tool [H]")
        public static final Component DRAG = Component.translatable("gui.heracles.tools.drag");

        @Translate("Add Quest [U]")
        public static final Component ADD_QUEST = Component.translatable("gui.heracles.tools.add_quest");

        @Translate("Link Tool [L]")
        public static final Component LINK = Component.translatable("gui.heracles.tools.link");
    }

    public static final class Groups {

        @Translate("Create Group")
        public static final Component CREATE = Component.translatable("gui.heracles.group.create");

        @Translate("You can't delete this while it has quests.")
        public static final Component DELETE_WITH_QUESTS = Component.translatable("gui.heracles.group.delete");
    }

    public static final class Quests {

        @Translate("Create Quest")
        public static final Component CREATE = Component.translatable("gui.heracles.quests.create");

        @Translate("Import Quests")
        public static final Component IMPORT = Component.translatable("gui.heracles.quests.import");

        @Translate("View Quests")
        public static final Component VIEW = Component.translatable("gui.heracles.quests.view");

        @Translate("Overview")
        public static final Component OVERVIEW = Component.translatable("gui.heracles.quests.overview");
    }

    public static final class Tasks {

        @Translate("Tasks")
        public static final Component TITLE = Component.translatable("gui.heracles.tasks.title");

        @Translate("Complete all tasks")
        public static final Component DESC = Component.translatable("gui.heracles.tasks.description");

        @Translate("Create Task")
        public static final Component CREATE = Component.translatable("gui.heracles.tasks.create");

        @Translate("Edit Task")
        public static final Component EDIT = Component.translatable("gui.heracles.tasks.edit");

        @Translate("Check Task")
        public static final Component CHECK = Component.translatable("gui.heracles.tasks.check");

        @Translate("Submit Items")
        public static final Component SUBMIT = Component.translatable("gui.heracles.tasks.submit");
    }

    public static final class Rewards {

        @Translate("Rewards")
        public static final Component TITLE = Component.translatable("gui.heracles.rewards.title");

        @Translate("Create Reward")
        public static final Component CREATE = Component.translatable("gui.heracles.rewards.create");

        @Translate("Edit Reward")
        public static final Component EDIT = Component.translatable("gui.heracles.rewards.edit");

        @Translate("Claim this Reward")
        public static final Component SELECT_CLAIM = Component.translatable("gui.heracles.rewards.select_claim");

        @Translate("Claim Rewards")
        public static final Component CLAIM = Component.translatable("gui.heracles.rewards.claim");

        @Translate("Claim")
        public static final Component CLAIM_REWARD = Component.translatable("gui.heracles.rewards.claim_reward");
    }

    public static class PinnedQuests {

        @Translate("Pinned Quests")
        public static final Component TITLE = Component.translatable("gui.heracles.pinned_quests");

        @Translate("Move Pinned Quests")
        public static final Component MOVE = Component.translatable("gui.heracles.pinned_quests.move");
    }

    static {
        //printTranslations(ConstantComponents.class);
    }

    private static void printTranslations(Class<?> clazz) {
        for (Class<?> clazzz : clazz.getDeclaredClasses()) {
            printTranslations(clazzz);
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Translate.class)) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(MutableComponent.class)) {
                    try {
                        if (field.get(null) instanceof MutableComponent component && component.getContents() instanceof TranslatableContents translation) {
                            String key = translation.getKey();
                            String fallback = field.getAnnotation(Translate.class).value();
                            System.out.println("\"" + key + "\": \"" + fallback + "\",");
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}
