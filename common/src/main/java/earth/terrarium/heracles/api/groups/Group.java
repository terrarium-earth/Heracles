package earth.terrarium.heracles.api.groups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record Group(
    Optional<QuestIcon<?>> icon,
    Component title,
    Component description
) {

    public static final Codec<Group> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestIcons.CODEC.optionalFieldOf("icon").forGetter(Group::icon),
        ExtraCodecs.COMPONENT.fieldOf("title").forGetter(Group::title),
        ExtraCodecs.COMPONENT.fieldOf("description").forGetter(Group::description)
    ).apply(instance, Group::new));

    public Group(String id) {
        this(Optional.empty(), Component.nullToEmpty(id), CommonComponents.EMPTY);
    }

    public Group withIcon(QuestIcon<?> icon) {
        return new Group(Optional.of(icon), this.title, this.description);
    }

    public Group withTitle(Component title) {
        return new Group(this.icon, title, this.description);
    }

    public Group withDescription(Component description) {
        return new Group(this.icon, this.title, description);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Group edit(Optional<QuestIcon<?>> icon, Optional<Component> title, Optional<Component> description) {
        return new Group(
            this.icon.or(() -> icon),
            title.orElse(this.title),
            description.orElse(this.description)
        );
    }
}
