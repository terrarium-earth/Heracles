package earth.terrarium.heracles.condition;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.resource.CriteriaManager;
import net.minecraft.advancements.Criterion;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class PlayerAcquiredCriteria {
    public static final ResourceLocation KEY = new ResourceLocation(Heracles.MOD_ID, "acquired_criteria");

    private final Set<Criterion> criteria = new HashSet<>();

    public final void acquireCriteria(Stream<Criterion> criteria) {
        criteria.forEach(this.criteria::add);
    }

    public final Stream<Criterion> getAcquiredCriteria() {
        return criteria.stream();
    }

    public final ListTag save() {
        ListTag tag = new ListTag();

        for (Criterion criterion : criteria) {
            ResourceLocation id = CriteriaManager.getInstance().getCriteria().inverse().get(criterion);

            if (id != null) {
                tag.add(StringTag.valueOf(id.toString()));
            }
        }

        return tag;
    }

    public final void load(ListTag tag) {
        criteria.clear();

        for (int i = 0; i < tag.size(); i++) {
            Criterion criterion = CriteriaManager.getInstance().getCriteria().get(new ResourceLocation(tag.getString(i)));

            if (criterion != null) {
                criteria.add(criterion);
            }
        }
    }
}
