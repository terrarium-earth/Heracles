package earth.terrarium.heracles.client.widgets.editor;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class TextEditorContent {

    private final List<String> lines = new ArrayList<>();
    private final Vector2i cursor = new Vector2i();
    private Vector2i selection = null;


    public List<String> lines() {
        return lines;
    }

    public String line() {
        if (lines.isEmpty()) return "";
        if (cursor.y() >= lines.size()) return lines.get(lines.size() - 1);
        if (cursor.y() < 0) return lines.get(0);
        return lines.get(cursor.y());
    }

    public Vector2i cursor() {
        return cursor;
    }

    @Nullable
    public Vector2i selection() {
        return selection;
    }

    public void addChar(char c) {
        if (selection != null) {
            deleteSelection();
        }
        String line = lines.get(cursor.y());
        lines.set(cursor.y(), line.substring(0, cursor.x()) + c + line.substring(cursor.x()));
        cursor.add(1, 0);
    }

    public void addText(String s) {
        if (selection != null) {
            deleteSelection();
        }
        if (s.contains("\n")) {
            String[] split = s.split("\n");
            String line = lines.get(cursor.y());
            lines.set(cursor.y(), line.substring(0, cursor.x()) + split[0]);
            cursor.add(1, 0);
            for (int i = 1; i < split.length; i++) {
                lines.add(cursor.y() + i, split[i]);
            }
            String end = line.length() > cursor.x() ? line.substring(cursor.x()) : "";
            lines.set(cursor.y() + split.length - 1, lines.get(cursor.y() + split.length - 1) + end);
            cursor.set(0, cursor.y() + split.length - 1);
        } else {
            String line = lines.get(cursor.y());
            String end = line.length() > cursor.x() ? line.substring(cursor.x()) : "";
            lines.set(cursor.y(), line.substring(0, cursor.x()) + s + end);
            cursor.add(s.length(), 0);
        }
    }

    public void deleteSelection() {
        Vector2i selection = this.selection;
        if (selection != null) {
            if (selection.y() == cursor.y()) {
                int x1 = Math.min(selection.x(), cursor.x());
                int x2 = Math.max(selection.x(), cursor.x());
                String line = lines.get(selection.y());
                lines.set(selection.y(), line.substring(0, x1) + line.substring(x2));
                cursor.set(x1, selection.y());
            } else {
                Vector2i start = selection.y() < cursor.y() ? selection : cursor;
                Vector2i end = selection.y() < cursor.y() ? cursor : selection;

                String line = lines.get(start.y());
                lines.set(start.y(), line.substring(0, start.x()) + lines.get(end.y()).substring(end.x()));
                if (end.y() >= start.y() + 1) {
                    lines.subList(start.y() + 1, end.y() + 1).clear();
                }

                cursor.set(start.x(), start.y());
            }
            this.selection = null;
        }
    }

    public void newline() {
        String line = lines.get(cursor.y());
        lines.set(cursor.y(), line.substring(0, cursor.x()));
        lines.add(cursor.y() + 1, line.substring(cursor.x()));
        cursor.set(0, cursor.y() + 1);
    }

    public void backspace() {
        if (selection != null) {
            deleteSelection();
            return;
        }
        if (cursor.x() == 0) {
            if (cursor.y() > 0) {
                String line = lines.get(cursor.y());
                lines.remove(cursor.y());
                cursor.add(0, -1);
                cursor.add(lines.get(cursor.y()).length(), 0);
                lines.set(cursor.y(), lines.get(cursor.y()) + line);
            }
        } else {
            String line = lines.get(cursor.y());
            lines.set(cursor.y(), line.substring(0, cursor.x() - 1) + line.substring(cursor.x()));
            cursor.add(-1, 0);
        }
    }

    public void moveX(int amount, boolean selecting) {
        updateSelection(selecting);
        cursor.add(amount, 0);
        if (cursor.x() < 0) {
            if (cursor.y() > 0) {
                cursor.set(lines.get(cursor.y() - 1).length(), cursor.y() - 1);
            } else {
                cursor.set(0, 0);
            }
        }
        if (cursor.x() > lines.get(cursor.y()).length()) {
            if (cursor.y() < lines.size() - 1) {
                cursor.add(-lines.get(cursor.y()).length(), 1);
            } else {
                cursor.set(lines.get(cursor.y()).length(), cursor.y());
            }
        }
    }

    public void moveY(int amount, boolean selecting) {
        updateSelection(selecting);
        cursor.add(0, amount);
        if (cursor.y() < 0) {
            cursor.set(cursor.x(), 0);
        }
        if (cursor.y() >= lines.size()) {
            cursor.set(cursor.x(), lines.size() - 1);
        }
        if (cursor.x() > lines.get(cursor.y()).length()) {
            cursor.set(lines.get(cursor.y()).length(), cursor.y());
        }
    }

    public void setCursor(int x, int y, boolean selecting) {
        updateSelection(selecting);
        cursor.set(x, y);
    }

    public String getSelectedText() {
        if (selection == null) return "";
        if (selection.y() == cursor.y()) {
            int x1 = Math.min(selection.x(), cursor.x());
            int x2 = Math.max(selection.x(), cursor.x());
            return lines.get(selection.y()).substring(x1, x2);
        } else {
            Vector2i start = selection.y() < cursor.y() ? selection : cursor;
            Vector2i end = selection.y() < cursor.y() ? cursor : selection;

            StringBuilder builder = new StringBuilder();
            builder.append(lines.get(start.y()).substring(start.x()));
            for (int i = start.y() + 1; i < end.y(); i++) {
                builder.append("\n").append(lines.get(i));
            }
            builder.append("\n").append(lines.get(end.y()), 0, end.x());
            return builder.toString();
        }
    }

    private void updateSelection(boolean selecting) {
        if (selecting) {
            if (selection == null) {
                selection = new Vector2i(cursor);
            }
        } else {
            selection = null;
        }
    }
}
