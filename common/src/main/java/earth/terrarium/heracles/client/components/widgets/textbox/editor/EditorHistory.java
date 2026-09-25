package earth.terrarium.heracles.client.components.widgets.textbox.editor;

import earth.terrarium.heracles.client.handlers.DisplayConfig;

import java.util.Stack;

public class EditorHistory {

    private final int maxHistory = DisplayConfig.maxEditorHistory;
    private final Stack<String> undoStack = new Stack<>();
    private final Stack<String> redoStack = new Stack<>();

    public void push(String value) {
        if (undoStack.size() >= maxHistory) {
            undoStack.remove(0);
        }
        undoStack.push(value);
        redoStack.clear();
    }

    public String undo(String value) {
        if (undoStack.isEmpty()) return value;
        redoStack.push(value);
        return undoStack.pop();
    }

    public String redo(String value) {
        if (redoStack.isEmpty()) return value;
        undoStack.push(value);
        return redoStack.pop();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
