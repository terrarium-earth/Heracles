package earth.terrarium.heracles.api;

import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.quests.QuestIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public interface CustomizableQuestElement {
    String title();
    QuestIcon<?> icon();
    default Component titleOr(Component overridden) {
        return !title().isEmpty() ? Component.translatable(title()) : overridden;
    }
    static NbtPredicate nbtPredicate() {
        return new NbtPredicate(new CompoundTag());
    }
}
