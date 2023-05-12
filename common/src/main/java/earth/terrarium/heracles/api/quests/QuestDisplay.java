package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;

import java.util.Objects;

public final class QuestDisplay {

    private static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(Heracles.MOD_ID, "textures/gui/quest_backgrounds/default.png");

    public static Codec<QuestDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.MAP)).forGetter(QuestDisplay::icon),
        ResourceLocation.CODEC.fieldOf("icon_background").orElse(DEFAULT_BACKGROUND).forGetter(QuestDisplay::iconBackground),
        ExtraCodecs.COMPONENT.fieldOf("title").orElse(Component.literal("New Quest")).forGetter(QuestDisplay::title),
        ExtraCodecs.COMPONENT.fieldOf("subtitle").orElse(CommonComponents.EMPTY).forGetter(QuestDisplay::subtitle),
        Codec.STRING.fieldOf("description").orElse("").forGetter(QuestDisplay::description),
        ModUtils.VECTOR2I.fieldOf("position").orElse(new Vector2i()).forGetter(QuestDisplay::position),
        Codec.STRING.fieldOf("group").orElse("Main").forGetter(QuestDisplay::group)
    ).apply(instance, QuestDisplay::new));

    private QuestIcon<?> icon;
    private ResourceLocation iconBackground;
    private Component title;
    private Component subtitle;
    private String description;
    private Vector2i position;
    private String group;

    public QuestDisplay(
        QuestIcon<?> icon,
        ResourceLocation iconBackground,
        Component title,
        Component subtitle,
        String description,
        Vector2i position,
        String group
    ) {
        this.icon = icon;
        this.iconBackground = iconBackground;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.position = position;
        this.group = group;
    }

    public QuestIcon<?> icon() {
        return icon;
    }

    public void setIcon(QuestIcon<?> icon) {
        this.icon = icon;
    }

    public ResourceLocation iconBackground() {
        return iconBackground;
    }

    public void setIconBackground(ResourceLocation iconBackground) {
        this.iconBackground = iconBackground;
    }

    public Component title() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public Component subtitle() {
        return subtitle;
    }

    public void setSubtitle(Component subtitle) {
        this.subtitle = subtitle == null ? CommonComponents.EMPTY : subtitle;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector2i position() {
        return position;
    }

    public void setPosition(Vector2i position) {
        this.position = position;
    }

    public String group() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QuestDisplay) obj;
        return Objects.equals(this.icon, that.icon) && Objects.equals(this.iconBackground, that.iconBackground) && Objects.equals(this.title, that.title) && Objects.equals(this.subtitle, that.subtitle) && Objects.equals(this.description, that.description) && Objects.equals(this.position, that.position) && Objects.equals(this.group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, iconBackground, title, subtitle, description, position, group);
    }

}
