package earth.terrarium.heracles.client.components.quest.editor.parser;

import java.util.ArrayList;
import java.util.List;

public class MarkdownBodyParser {

    public static List<String> parse(List<String> lines) {
        List<String> builder = new ArrayList<>();
        MarkdownState state = new MarkdownState(builder);
        for (String line : lines) {
            line = MarkdownParagraphParser.replaceColor(line);
            String trimed = line.trim();
            state.check(line);

            if (trimed.startsWith("# ")) {
                builder.add("<h1>" + line.substring(2) + "</h1>");
            } else if (trimed.startsWith("## ")) {
                builder.add("<h2>" + line.substring(3) + "</h2>");
            } else if (trimed.equals("---")) {
                builder.add("<hr/>");
            } else if (trimed.startsWith("> ")) {
                state.blockquote();
                builder.add(MarkdownParagraphParser.parse(trimed.substring(2)));
            } else if (trimed.startsWith("- ")) {
                state.list();
                builder.add("<li>" + MarkdownParagraphParser.parse(trimed.substring(2)) + "</li>");
            } else if (trimed.startsWith("<")) {
                builder.add(line);
            } else if (trimed.isEmpty()) {
                builder.add("<br/>");
            } else {
                builder.add(MarkdownParagraphParser.parse(line));
            }
        }
        return builder;
    }


}
