package earth.terrarium.heracles.client.screens;

public enum MouseMode {
    SELECT_MOVE,
    SELECT_LINK,
    DRAG_SELECT_MOVE,
    DRAG_MOVE,
    ADD;


    public boolean canSelect() {
        return this == SELECT_MOVE || this == DRAG_SELECT_MOVE || this == SELECT_LINK;
    }

    public boolean canDragSelection() {
        return this == SELECT_MOVE ;
    }

    public boolean canDrag() {
        return this == DRAG_SELECT_MOVE || this == DRAG_MOVE;
    }
}
