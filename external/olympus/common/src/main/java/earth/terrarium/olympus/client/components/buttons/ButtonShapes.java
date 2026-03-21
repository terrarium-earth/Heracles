package earth.terrarium.olympus.client.components.buttons;

public class ButtonShapes {

    public static final ButtonShape RECTANGLE = (x, y, width, height) -> x >= 0 && x < width && y >= 0 && y < height;

    public static final ButtonShape ELLIPSE = (x, y, width, height) -> {
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double dx = (x - centerX) / centerX;
        double dy = (y - centerY) / centerY;
        return (dx * dx) + (dy * dy) <= 1;
    };

    public static final ButtonShape DIAMOND = (x, y, width, height) -> {
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double dx = Math.abs(x - centerX);
        double dy = Math.abs(y - centerY);
        return (dx / centerX) + (dy / centerY) <= 1;
    };
}
