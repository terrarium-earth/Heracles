package earth.terrarium.heracles.client.screens.mousemode;

public enum MouseButtonType {
    MOVE,
    DRAG,
    ADD,
    LINK;

    public int u() {
        return switch (this) {
            case MOVE -> 0;
            case DRAG -> 11;
            case ADD -> 22;
            case LINK -> 0;
        };
    }

    public int v() {
        return switch (this) {
            case MOVE -> 37;
            case DRAG -> 37;
            case ADD -> 37;
            case LINK -> 59;
        };
    }
}
