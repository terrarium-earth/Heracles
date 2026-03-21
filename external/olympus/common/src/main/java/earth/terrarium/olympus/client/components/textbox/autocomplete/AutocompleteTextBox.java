package earth.terrarium.olympus.client.components.textbox.autocomplete;

import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.List;
import java.util.function.Function;

public class AutocompleteTextBox<T> extends TextBox {

    protected Function<String, List<T>> suggestions = s -> List.of();
    protected Function<T, String> mapper = Object::toString;
    protected boolean alwaysShow = false;

    public AutocompleteTextBox(State<String> state) {
        super(state);
    }

    public AutocompleteTextBox<T> withSuggestions(Function<String, List<T>> suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    public AutocompleteTextBox<T> withAlwaysShow(boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
        return this;
    }

    public AutocompleteTextBox<T> withMapper(Function<T, String> mapper) {
        this.mapper = mapper;
        return this;
    }

    protected State<String> state() {
        return this.state;
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean bl) {
        Minecraft.getInstance().setScreen(new AutocompleteScreen<>(this));
    }
}
