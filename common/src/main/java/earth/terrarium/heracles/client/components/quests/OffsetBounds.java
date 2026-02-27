package earth.terrarium.heracles.client.components.quests;

import net.minecraft.util.Mth;
import org.joml.Vector2i;

public class OffsetBounds {

    private static final Vector2i MAX = new Vector2i(5000, 5000);
    private static final Vector2i MIN = new Vector2i(-5000, -5000);

    private static final Vector2i offset = new Vector2i();

    private static final Vector2i start = new Vector2i();
    private static final Vector2i startOffset = new Vector2i();

    protected int minX;
    protected int minY;
    protected int maxX;
    protected int maxY;

    public void setBounds() {
        setBounds(MIN.x, MIN.y, MAX.x, MAX.y);
    }

    public void center(int minX, int minY, int maxX, int maxY) {
        offset.x = (maxX - minX) / 2;
        offset.y = (maxY - minY) / 2;
    }

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setStart(double x, double y) {
        start.x = (int) x;
        start.y = (int) y;

        startOffset.x = offset.x;
        startOffset.y = offset.y;
    }


    public Vector2i getDragPosition(double mouseX, double mouseY) {
        int newX = (int) (mouseX - start.x + startOffset.x);
        int newY = (int) (mouseY - start.y + startOffset.y);

        return new Vector2i(Mth.clamp(newX, minX, maxX), Mth.clamp(newY, minY, maxY));
    }

    public void drag(double mouseX, double mouseY) {
        Vector2i position = getDragPosition(mouseX, mouseY);
        offset.x = position.x;
        offset.y = position.y;
    }

    public int x() {
        return offset.x;
    }

    public int y() {
        return offset.y;
    }

    public void add(int x, int y) {
        offset.x += x;
        offset.x = Mth.clamp(offset.x, minX, maxX);

        offset.y += y;
        offset.y = Mth.clamp(offset.y, minY, maxY);
    }
}
