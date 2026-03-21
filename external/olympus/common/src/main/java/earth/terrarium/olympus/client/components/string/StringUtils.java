package earth.terrarium.olympus.client.components.string;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class StringUtils {

    public static Style getStyleAt(Font font, FormattedCharSequence sequence, int x) {
        var sink = new StylingFormattedCharSink(font, x);
        sequence.accept(sink);
        return sink.style;
    }

    private static class StylingFormattedCharSink implements FormattedCharSink {

        private final Font font;
        private float maxWidth;
        private @Nullable Style style;

        public StylingFormattedCharSink(Font font, float maxWidth) {
            this.font = font;
            this.maxWidth = maxWidth;
        }

        @Override
        public boolean accept(int index, @NotNull Style style, int codepoint) {
            this.maxWidth = this.maxWidth - this.font.width(FormattedCharSequence.codepoint(codepoint, style));
            if (this.maxWidth >= 0.0F) return true;
            this.style = style;
            return false;
        }
    }
}
