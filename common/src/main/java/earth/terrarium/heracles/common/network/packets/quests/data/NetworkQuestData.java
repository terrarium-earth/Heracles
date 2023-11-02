package earth.terrarium.heracles.common.network.packets.quests.data;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.api.quests.GroupDisplay;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.UnaryOperator;

public record NetworkQuestData(
    Optional<NetworkQuestDisplayData> display,
    Optional<NetworkQuestSettingsData> settings,
    Optional<Set<String>> dependencies,
    Optional<Map<String, QuestTask<?, ?, ?>>> tasks,
    Optional<Map<String, QuestReward<?>>> rewards
) {

    public static final ByteCodec<NetworkQuestData> CODEC = ObjectByteCodec.create(
        NetworkQuestDisplayData.CODEC.optionalFieldOf(NetworkQuestData::display),
        NetworkQuestSettingsData.CODEC.optionalFieldOf(NetworkQuestData::settings),
        ByteCodec.STRING.setOf().optionalFieldOf(NetworkQuestData::dependencies),
        QuestTasks.BYTE_CODEC.optionalFieldOf(NetworkQuestData::tasks),
        QuestRewards.BYTE_CODEC.optionalFieldOf(NetworkQuestData::rewards),
        NetworkQuestData::new
    );

    public void update(Quest quest) {
        display.ifPresent(display -> display.update(quest));
        settings.ifPresent(settings -> settings.update(quest));
        dependencies.ifPresent(dependencies -> {
            quest.dependencies().clear();
            quest.dependencies().addAll(dependencies);
        });
        tasks.ifPresent(tasks -> {
            quest.tasks().clear();
            quest.tasks().putAll(tasks);
        });
        rewards.ifPresent(rewards -> {
            quest.rewards().clear();
            quest.rewards().putAll(rewards);
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private QuestIcon<?> icon;
        private ResourceLocation background;
        private Component title;
        private Component subtitle;
        private List<String> description;
        private Map<String, GroupDisplay> groups;
        private TriState individualProgress = TriState.UNDEFINED;
        private ModUtils.QuestStatus hiddenUntil = null;
        private TriState unlockNotification = TriState.UNDEFINED;
        private TriState showDependencyArrow = TriState.UNDEFINED;
        private TriState repeatable = TriState.UNDEFINED;
        private Set<String> dependencies;
        private Map<String, QuestTask<?, ?, ?>> tasks;
        private Map<String, QuestReward<?>> rewards;

        public Builder icon(QuestIcon<?> icon) {
            this.icon = icon;
            return this;
        }

        public Builder background(ResourceLocation background) {
            this.background = background;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder subtitle(Component subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder description(List<String> description) {
            this.description = description;
            return this;
        }

        public Builder groups(Map<String, GroupDisplay> groups) {
            this.groups = new HashMap<>(groups);
            return this;
        }

        public Builder group(Quest quest, String id, UnaryOperator<Vector2i> position) {
            if (this.groups == null) {
                this.groups = new HashMap<>();
                this.groups.putAll(quest.display().groups());
            }
            Vector2i pos = position.apply(quest.display().groups().getOrDefault(id, GroupDisplay.createDefault()).position());
            this.groups.put(id, new GroupDisplay(id, pos));
            return this;
        }

        public Builder individualProgress(boolean individualProgress) {
            this.individualProgress = TriState.of(individualProgress);
            return this;
        }

        public Builder hiddenUntil(ModUtils.QuestStatus hiddenUntil) {
            this.hiddenUntil = hiddenUntil;
            return this;
        }

        public Builder unlockNotification(boolean unlockNotification) {
            this.unlockNotification = TriState.of(unlockNotification);
            return this;
        }

        public Builder showDependencyArrow(boolean showDependencyArrow) {
            this.showDependencyArrow = TriState.of(showDependencyArrow);
            return this;
        }

        public Builder repeatable(boolean repeatable) {
            this.repeatable = TriState.of(repeatable);
            return this;
        }

        public Builder dependencies(Set<String> dependencies) {
            this.dependencies = new HashSet<>(dependencies);
            return this;
        }

        public Builder tasks(Map<String, QuestTask<?, ?, ?>> tasks) {
            this.tasks = new HashMap<>(tasks);
            return this;
        }

        public Builder rewards(Map<String, QuestReward<?>> rewards) {
            this.rewards = new HashMap<>(rewards);
            return this;
        }

        public NetworkQuestData build() {
            NetworkQuestDisplayData display = null;
            if (icon != null || background != null || title != null || subtitle != null || description != null || groups != null) {
                display = new NetworkQuestDisplayData(
                    Optional.ofNullable(icon),
                    Optional.ofNullable(background),
                    Optional.ofNullable(title),
                    Optional.ofNullable(subtitle),
                    Optional.ofNullable(description),
                    Optional.ofNullable(groups)
                );
            }
            NetworkQuestSettingsData settings = null;
            if (individualProgress != TriState.UNDEFINED || hiddenUntil != null || unlockNotification != TriState.UNDEFINED) {
                settings = new NetworkQuestSettingsData(
                    Optional.ofNullable(individualProgress.isUndefined() ? null : individualProgress.isTrue()),
                    Optional.ofNullable(hiddenUntil),
                    Optional.ofNullable(unlockNotification.isUndefined() ? null : unlockNotification.isTrue()),
                    Optional.ofNullable(showDependencyArrow.isUndefined() ? null : showDependencyArrow.isTrue()),
                    Optional.ofNullable(repeatable.isUndefined() ? null : repeatable.isTrue())
                );
            }


            return new NetworkQuestData(
                Optional.ofNullable(display),
                Optional.ofNullable(settings),
                Optional.ofNullable(dependencies),
                Optional.ofNullable(tasks),
                Optional.ofNullable(rewards)
            );
        }
    }
}
