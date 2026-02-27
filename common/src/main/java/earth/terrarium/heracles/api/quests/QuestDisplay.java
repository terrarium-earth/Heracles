package earth.terrarium.heracles.api.quests;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import net.minecraft.Optionull;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;

public final class QuestDisplay {

    public static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(Heracles.MOD_ID, "textures/gui/quest_backgrounds/default.png");

    public static final Codec<List<String>> DESCRIPTION_CODEC = Codec.either(Codec.STRING, Codec.STRING.listOf())
        .xmap(either -> either.map(List::of, Function.identity()), Either::right);

    public static Codec<QuestDisplay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.MAP)).forGetter(QuestDisplay::icon),
        ResourceLocation.CODEC.fieldOf("icon_background").orElse(DEFAULT_BACKGROUND).forGetter(QuestDisplay::iconBackground),
        ExtraCodecs.COMPONENT.fieldOf("title").orElse(Component.literal("New Quest")).forGetter(QuestDisplay::title),
        ExtraCodecs.COMPONENT.fieldOf("subtitle").orElse(CommonComponents.EMPTY).forGetter(QuestDisplay::subtitle),
        DESCRIPTION_CODEC.fieldOf("description").orElse(List.of()).forGetter(QuestDisplay::description),
        DispatchMapCodec.of(Codec.STRING, GroupDisplay::codec).fieldOf("groups").orElse(Map.of("Main", GroupDisplay.createDefault())).forGetter(QuestDisplay::groups)
    ).apply(instance, QuestDisplay::new));

    private final Map<String, GroupDisplay> groups = new HashMap<>();

    private QuestIcon<?> icon;
    private ResourceLocation iconBackground;
    private Component title;
    private Component subtitle;
    private List<String> description;

    public QuestDisplay(
        QuestIcon<?> icon,
        ResourceLocation iconBackground,
        Component title,
        Component subtitle,
        List<String> description,
        Map<String, GroupDisplay> groups
    ) {
        this.icon = icon;
        this.iconBackground = iconBackground;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.groups.putAll(groups);
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
        this.title = title == null ? CommonComponents.EMPTY : title;
    }

    public Component subtitle() {
        return subtitle;
    }

    public void setSubtitle(Component subtitle) {
        this.subtitle = subtitle == null ? CommonComponents.EMPTY : subtitle;
    }

    public List<String> description() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = new ArrayList<>(description);
    }

    public Vector2i position(String group) {
        return Optionull.mapOrDefault(groups.get(group), GroupDisplay::position, new Vector2i());
    }

    public Map<String, GroupDisplay> groups() {
        return groups;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QuestDisplay) obj;
        return Objects.equals(this.icon, that.icon) && Objects.equals(this.iconBackground, that.iconBackground) && Objects.equals(this.title, that.title) && Objects.equals(this.subtitle, that.subtitle) && Objects.equals(this.description, that.description) && Objects.equals(this.groups, that.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, iconBackground, title, subtitle, description, groups);
    }

    public static QuestDisplay createDefault(GroupDisplay display) {
        return new QuestDisplay(
            new ItemQuestIcon(Items.MAP),
            DEFAULT_BACKGROUND,
            Component.literal("New Quest"),
            CommonComponents.EMPTY,
            List.of(),
            Map.of(display.id(), display)
        );
    }
}
