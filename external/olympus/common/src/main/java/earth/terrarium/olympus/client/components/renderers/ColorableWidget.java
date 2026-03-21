package earth.terrarium.olympus.client.components.renderers;

import com.teamresourceful.resourcefullib.common.color.Color;

public interface ColorableWidget {

    ColorableWidget withColor(Color color);

    ColorableWidget withShadow();
}
