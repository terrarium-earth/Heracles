package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.ui.context.ContextMenu;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "context", description = "A simple context menu example")
public class ContextExample extends ExampleScreen {

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.button() == 1) {
            ContextMenu.open(menu -> menu
                    .button(Component.literal("Action 1"), () -> System.out.println("Action 1 clicked!"))
                    .dangerButton(Component.literal("Action 2"), () -> System.out.println("Action 2 clicked!"))
                    .primaryButton(Component.literal("Action 3"), () -> System.out.println("Action 3 clicked!"))
                    .divider()
                    .button(Component.literal("Action 4"), () -> System.out.println("Action 4 clicked!"))
            );
        }
        return super.mouseClicked(event, bl);
    }
}
