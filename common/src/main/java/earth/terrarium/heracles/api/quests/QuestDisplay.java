package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;

import java.util.Optional;

public record QuestDisplay(
    QuestIcon<?> icon,
    ResourceLocation iconBackground,
    Component title,
    Optional<Component> subtitle,
    String description,
    Vector2i position,
    String group
) {

    private static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(Heracles.MOD_ID, "textures/gui/quest_backgrounds/default.png");

    public static Codec<QuestDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.MAP)).forGetter(QuestDisplay::icon),
        ResourceLocation.CODEC.fieldOf("icon_background").orElse(DEFAULT_BACKGROUND).forGetter(QuestDisplay::iconBackground),
        ExtraCodecs.COMPONENT.fieldOf("title").orElse(Component.literal("New Quest")).forGetter(QuestDisplay::title),
        ExtraCodecs.COMPONENT.optionalFieldOf("subtitle").forGetter(QuestDisplay::subtitle),
        Codec.STRING.fieldOf("description").orElse("").forGetter(QuestDisplay::description),
        ModUtils.VECTOR2I.fieldOf("position").orElse(new Vector2i()).forGetter(QuestDisplay::position),
        Codec.STRING.fieldOf("group").orElse("Main").forGetter(QuestDisplay::group)
    ).apply(instance, QuestDisplay::new));
}
