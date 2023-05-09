package earth.terrarium.heracles.client.screens;

public enum MouseMode {
    SELECT_MOVE,
    DRAG_SELECT_MOVE,
    DRAG_MOVE,
    ADD;


    public boolean canSelect() {
        return this == SELECT_MOVE || this == DRAG_SELECT_MOVE;
    }

    public boolean canDrag() {
        return this == DRAG_SELECT_MOVE || this == DRAG_MOVE;
    }
}
