package earth.terrarium.example.base;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import net.minecraft.network.chat.CommonComponents;

public abstract class ExampleScreen extends BaseCursorScreen {

    public ExampleScreen() {
        super(CommonComponents.EMPTY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
