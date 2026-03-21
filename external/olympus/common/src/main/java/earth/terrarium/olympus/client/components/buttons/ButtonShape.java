package earth.terrarium.olympus.client.components.buttons;

public interface ButtonShape {

    /**
     * Checks if the given point is inside the shape.
     * @param x the localized x coordinate
     * @param y the localized y coordinate
     * @param width the width of the shape
     * @param height the height of the shape
     * @return true if the point is inside the shape, false otherwise
     */
    boolean isInside(double x, double y, int width, int height);
}
