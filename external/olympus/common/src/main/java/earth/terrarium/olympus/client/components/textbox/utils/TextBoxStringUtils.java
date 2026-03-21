package earth.terrarium.olympus.client.components.textbox.utils;

import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TextBoxStringUtils {

    private static boolean feedChar(FormattedCharSink consumer, int i, char c) {
        return Character.isSurrogate(c) ? consumer.accept(i, Style.EMPTY, 65533) : consumer.accept(i, Style.EMPTY, c);
    }

    private static boolean iterate(String string, int start, FormattedCharSink consumer) {
        for (int i = start; i < string.length(); i++) {
            char character = string.charAt(i);
            if (Character.isHighSurrogate(character)) {
                if (i + 1 >= string.length()) {
                    return consumer.accept(i, Style.EMPTY, 65533);
                }

                char nextCharacter = string.charAt(i + 1);
                if (Character.isLowSurrogate(nextCharacter)) {
                    if (!consumer.accept(i, Style.EMPTY, Character.toCodePoint(character, nextCharacter))) {
                        return false;
                    }

                    i++;
                } else if (!consumer.accept(i, Style.EMPTY, 65533)) {
                    return false;
                }
            } else if (!feedChar(consumer, i, character)) {
                return false;
            }
        }

        return true;
    }

    public static FormattedCharSequence format(String text) {
        return FormattedCharSequence.forward(text, Style.EMPTY);
    }

    public static int width(Font font, String text) {
        return font.width(format(text));
    }

    public static void split(Font font, String text, int maxWidth, StringSplitter.LinePosConsumer consumer) {
        int index = 0;

        while (index < text.length()) {
            LineBreakFinder finder = new LineBreakFinder(maxWidth, font);
            if (iterate(text, index, finder)) {
                consumer.accept(Style.EMPTY, index, text.length());
                break;
            }

            int pos = finder.getSplitPosition();
            char character = text.charAt(pos);
            consumer.accept(Style.EMPTY, index, pos);
            index = character != '\n' && character != ' ' ? pos : pos + 1;
        }
    }

    public static String plainHeadByWidth(Font font, String string, int maxWidth) {
        var sink = new WidthLimitedCharSink(font, maxWidth);
        iterate(string, 0, sink);
        return string.substring(0, sink.position);
    }

    public static String plainTailByWidth(Font font, String string, int maxWidth) {
        var totalWidth = new MutableFloat();
        var pos = new MutableInt();
        StringDecomposer.iterateBackwards(string, Style.EMPTY, (i, style, c) -> {
            float width = totalWidth.addAndGet(width(font, Character.toString(c)));
            if (width > maxWidth) {
                return false;
            } else {
                pos.setValue(i);
                return true;
            }
        });
        return string.substring(pos.getValue());
    }

    public static boolean isAllowedChatCharacter(char character) {
        return StringUtil.isAllowedChatCharacter(character) || character == '§';
    }

    public static String filterText(String text) {
        StringBuilder builder = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            if (isAllowedChatCharacter(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static class LineBreakFinder implements FormattedCharSink {
        private final int maxWidth;
        private final Font font;

        private int lineBreak = -1;
        private boolean hadNonZeroWidthChar;
        private int width;
        private int lastSpace = -1;
        private int nextChar;

        public LineBreakFinder(int maxWidth, Font font) {
            this.maxWidth = Math.max(maxWidth, 1);
            this.font = font;
        }

        @Override
        public boolean accept(int i, Style style, int j) {
            switch (j) {
                case 10:
                    this.lineBreak = i;
                    return false;
                case 32:
                    this.lastSpace = i;
                default:
                    int width = width(this.font, Character.toString(j));
                    this.width += width;
                    if (!this.hadNonZeroWidthChar || !(this.width > this.maxWidth)) {
                        this.hadNonZeroWidthChar |= width != 0.0F;
                        this.nextChar = i + Character.charCount(j);
                        return true;
                    } else {
                        this.lineBreak = this.lastSpace != -1 ? this.lastSpace : i;
                        return false;
                    }
            }
        }

        public int getSplitPosition() {
            return this.lineBreak != -1 ? this.lineBreak : this.nextChar;
        }
    }

    private static class WidthLimitedCharSink implements FormattedCharSink {

        private final Font font;

        private float maxWidth;
        private int position;

        public WidthLimitedCharSink(Font font, float maxWidth) {
            this.font = font;
            this.maxWidth = maxWidth;
        }

        @Override
        public boolean accept(int i, Style ignored, int j) {
            this.maxWidth -= width(font, Character.toString(j));
            if (this.maxWidth >= 0.0F) {
                this.position = i + Character.charCount(j);
                return true;
            } else {
                return false;
            }
        }
    }
}
