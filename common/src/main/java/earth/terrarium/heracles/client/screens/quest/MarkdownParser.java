package earth.terrarium.heracles.client.screens.quest;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {

    private static final Pattern COLOR_PATTERN = Pattern.compile("([^\\\\]|^)&&([0-9a-fA-Fk-oK-OrR])");
    private static final Pattern AMPERSANDS_NOT_ENTITY_PATTERN = Pattern.compile("&(?!([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-fA-F]{1,6});)");
    private static final Map<String, String> CHAR_TO_ENTITY = Map.of(
        "<", "&#60;",
        ">", "&#62;",
        "\"", "&#34;",
        "'", "&#39;",
        "¢", "&#162;",
        "£", "&#163;",
        "¥", "&#165;",
        "€", "&#8364;",
        "©", "&#169;",
        "®", "&#174;"
    );

    private static String replaceColor(String text) {
        Matcher matcher;
        while ((matcher = COLOR_PATTERN.matcher(text)).find()) {
            text = matcher.replaceFirst("$1&#167;$2");
        }
        return text;
    }

    public static List<String> parse(List<String> lines) {
        State state = null;
        List<String> builder = new ArrayList<>();
        for (String line : lines) {
            line = replaceColor(line);
            line = line.replace("\\&&", "&&");
            String trimedText = line.trim();
            if (state != null && !line.startsWith(state.startsWith)) {
                builder.add(state.end);

                state = null;
            }
            if (trimedText.startsWith("# ")) {
                builder.add("<h1>" + xmlEncode(line.substring(2)) + "</h1>");
            } else if (trimedText.startsWith("## ")) {
                builder.add("<h2>" + xmlEncode(line.substring(3)) + "</h2>");
            } else if (trimedText.equals("---")) {
                builder.add("<hr/>");
            } else if (trimedText.startsWith("- ")) {
                if (state == null) {
                    state = State.LIST;
                    builder.add("<ul>");
                }
                builder.add("<li>" + xmlEncode(line.substring(2)) + "</li>");
            } else if (trimedText.startsWith("> ")) {
                if (state == null) {
                    state = State.BLOCKQUOTE;
                    builder.add("<blockquote>");
                }
                builder.add(parse(List.of(line.substring(2).trim())).stream().reduce("", (a, b) -> a + b));
            } else if (trimedText.trim().startsWith("<")) {
                builder.add(line);
            } else {
                String json = Component.Serializer.toJson(parseTextToComponent(line));
                builder.add("<component>" + xmlEncode(json) + "</component>");
            }
        }
        if (state != null) {
            builder.add(state.end);
        }
        return builder;
    }

    private static String xmlEncode(String text) {
        text = AMPERSANDS_NOT_ENTITY_PATTERN.matcher(text).replaceAll("&#38;");
        for (var entry : CHAR_TO_ENTITY.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

    public static MutableComponent parseTextToComponent(String text) {
        MutableComponent component = Component.empty();
        StringBuilder builder = new StringBuilder(text);
        EnumMap<Formatting, Integer> indexes = new EnumMap<>(Formatting.class);
        for (Formatting value : Formatting.values()) {
            indexes.put(value, builder.indexOf(value.symbol));
        }

        if (indexes.values().stream().mapToInt(i -> i).max().orElse(-1) == -1) {
            return Component.literal(builder.toString());
        }

        //smallest non 0 value
        int smallest = indexes.values().stream().filter(i -> i != -1).mapToInt(i -> i).min().orElse(-1);
        Formatting formatting = indexes.entrySet().stream().filter(e -> e.getValue() == smallest).map(Map.Entry::getKey).findFirst().orElse(null);
        if (formatting == null) {
            return Component.literal(builder.toString());
        }
        component.append(Component.literal(builder.substring(0, smallest)));
        builder.delete(0, smallest + formatting.symbol.length());
        int end = builder.indexOf(formatting.symbol);
        if (end == -1) {
            return Component.literal(text);
        }
        component.append(parseTextToComponent(builder.substring(0, end)).withStyle(formatting.formatting));
        builder.delete(0, end + formatting.symbol.length());
        component.append(parseTextToComponent(builder.toString()));
        return component;
    }

    private enum Formatting {
        UNDERLINE("__", ChatFormatting.UNDERLINE),
        BOLD("**", ChatFormatting.BOLD),
        ITALIC("--", ChatFormatting.ITALIC),
        STRIKETHROUGH("~~", ChatFormatting.STRIKETHROUGH),
        OBFUSCATED("||", ChatFormatting.OBFUSCATED),

        BLACK("/0/", ChatFormatting.BLACK),
        DARK_BLUE("/1/", ChatFormatting.DARK_BLUE),
        DARK_GREEN("/2/", ChatFormatting.DARK_GREEN),
        DARK_AQUA("/3/", ChatFormatting.DARK_AQUA),
        DARK_RED("/4/", ChatFormatting.DARK_RED),
        DARK_PURPLE("/5/", ChatFormatting.DARK_PURPLE),
        GOLD("/6/", ChatFormatting.GOLD),
        GRAY("/7/", ChatFormatting.GRAY),
        DARK_GRAY("/8/", ChatFormatting.DARK_GRAY),
        BLUE("/9/", ChatFormatting.BLUE),
        GREEN("/a/", ChatFormatting.GREEN),
        AQUA("/b/", ChatFormatting.AQUA),
        RED("/c/", ChatFormatting.RED),
        LIGHT_PURPLE("/d/", ChatFormatting.LIGHT_PURPLE),
        YELLOW("/e/", ChatFormatting.YELLOW),
        WHITE("/f/", ChatFormatting.WHITE),
        ;

        final String symbol;
        final ChatFormatting formatting;

        Formatting(String symbol, ChatFormatting formatting) {
            this.symbol = symbol;
            this.formatting = formatting;
        }
    }

    private enum State {
        BLOCKQUOTE(">", "</blockquote>"),
        LIST("-", "</ul>");

        final String startsWith;
        final String end;

        State(String startsWith, String end) {
            this.startsWith = startsWith;
            this.end = end;
        }
    }
}
