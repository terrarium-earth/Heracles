package earth.terrarium.heracles.client.components.quest.editor.parser;

import java.util.List;

public class MarkdownState {

    private final List<String> lines;

    private boolean blockquote = false;
    private boolean list = false;

    public MarkdownState(List<String> lines) {
        this.lines = lines;
    }

    public void check(String line) {
        String trimed = line.trim();
        if (!trimed.startsWith("> ") && blockquote) {
            blockquote = false;
            lines.add("</blockquote>");
        }
        if (!trimed.startsWith("- ") && list) {
            list = false;
            lines.add("</ul>");
        }
    }

    public void blockquote() {
        if (!blockquote) {
            blockquote = true;
            lines.add("<blockquote>");
        }
    }

    public void list() {
        if (!list) {
            list = true;
            lines.add("<ul>");
        }
    }
}
