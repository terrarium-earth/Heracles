package earth.terrarium.heracles.condition;

import net.minecraft.advancements.Criterion;

import java.util.stream.Stream;

public interface PlayerAcquiredCriteria {
    void acquireCriteria(Stream<Criterion> criteria);

    Stream<Criterion> getAcquiredCriteria();
}
