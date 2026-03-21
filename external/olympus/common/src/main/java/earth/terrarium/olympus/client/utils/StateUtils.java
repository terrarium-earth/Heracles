package earth.terrarium.olympus.client.utils;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;

public class StateUtils {

    public static Runnable booleanToggle(State<Boolean> state) {
        return () -> state.set(!state.get());
    }

    public static RadioState<TriState> tristate(TriState state) {
        return new RadioState<>(state, switch (state) {
            case TRUE -> 0;
            case UNDEFINED -> 1;
            case FALSE -> 2;
        });
    }
}
