package earth.terrarium.heracles.api.groups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;

import java.util.Optional;

public record Group(
    Optional<QuestIcon<?>> icon,
    String title,
    String description
) {

    public static final Codec<Group> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestIcons.CODEC.optionalFieldOf("icon").forGetter(Group::icon),
        Codec.STRING.fieldOf("title").forGetter(Group::title),
        Codec.STRING.fieldOf("description").forGetter(Group::description)
    ).apply(instance, Group::new));

    public Group(String id) {
        this(Optional.empty(), id, "");
    }

    public Group withIcon(QuestIcon<?> icon) {
        return new Group(Optional.of(icon), this.title, this.description);
    }

    public Group withTitle(String title) {
        return new Group(this.icon, title, this.description);
    }

    public Group withDescription(String description) {
        return new Group(this.icon, this.title, description);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Group edit(Optional<QuestIcon<?>> icon, Optional<String> title, Optional<String> description) {
        return new Group(
            icon.or(() -> this.icon),
            title.orElse(this.title),
            description.orElse(this.description)
        );
    }
}
