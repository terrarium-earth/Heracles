package earth.terrarium.heracles.client.screens.mousemode;

public enum MouseMode {
    SELECT_MOVE,
    SELECT_LINK,
    DRAG_MOVE_OPEN,
    DRAG_MOVE,
    ADD;

    public boolean canOpen() {
        return this == DRAG_MOVE_OPEN;
    }

    public boolean canSelect() {
        return this == SELECT_MOVE || this == SELECT_LINK;
    }

    public boolean canDragSelection() {
        return this == SELECT_MOVE;
    }

    public boolean canDrag() {
        return this == DRAG_MOVE_OPEN || this == DRAG_MOVE;
    }
}
