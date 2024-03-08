package earth.terrarium.heracles.client.components.quest.editor.parser;

import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParagraphParser {

    private static final Map<String, String> TAG_TO_MARKDOWN = Util.make(new HashMap<>(), map -> {
        map.put("bold", "**");
        map.put("italic", "--");
        map.put("underline", "__");
        map.put("strikethrough", "~~");
        map.put("obfuscated", "||");
        map.put("black", "/0/");
        map.put("dark_blue", "/1/");
        map.put("dark_green", "/2/");
        map.put("dark_aqua", "/3/");
        map.put("dark_red", "/4/");
        map.put("dark_purple", "/5/");
        map.put("gold", "/6/");
        map.put("gray", "/7/");
        map.put("dark_gray", "/8/");
        map.put("blue", "/9/");
        map.put("green", "/a/");
        map.put("aqua", "/b/");
        map.put("red", "/c/");
        map.put("light_purple", "/d/");
        map.put("yellow", "/e/");
        map.put("white", "/f/");
    });

    private static final Map<String, Pattern> FORMATTING = Util.make(new HashMap<>(), map -> {
        for (var entry : TAG_TO_MARKDOWN.entrySet()) {
            String ends = Pattern.quote(entry.getValue());
            String pattern = ends + "(.*?)" + ends;
            map.put(entry.getKey(), Pattern.compile(pattern));
        }
    });

    private static final Pattern LINK_PATTERN = Pattern.compile("\\[([^]]+)]\\(([^)]+)\\)");
    private static final Pattern COLOR_PATTERN = Pattern.compile("([^\\\\]|^)&&([0-9a-fA-Fk-oK-OrR])");

    public static String parse(String line) {
        line = replaceLinks(line);
        line = replaceFormatting(line);
        return "<text>" + line + "</text>";
    }

    public static String replaceColor(String text) {
        text = replace(COLOR_PATTERN, text, "$1§$2");
        text = text.replace("\\&&", "&&");
        return text;
    }

    private static String replaceLinks(String text) {
        return replace(LINK_PATTERN, text, "<link href=\"$2\">$1</link>");
    }

    private static String replaceFormatting(String text) {
        for (var entry : FORMATTING.entrySet()) {
            text = replace(entry.getValue(), text, "<" + entry.getKey() + ">$1</" + entry.getKey() + ">");
        }
        return text;
    }

    private static String replace(Pattern pattern, String text, String replacement) {
        Matcher matcher;
        while ((matcher = pattern.matcher(text)).find()) {
            text = matcher.replaceFirst(replacement);
        }
        return text;
    }
}
