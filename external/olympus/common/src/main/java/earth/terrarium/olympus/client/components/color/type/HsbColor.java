package earth.terrarium.olympus.client.components.color.type;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public record HsbColor(float hue, float saturation, float brightness, int alpha) {

    public static HsbColor of(float hue, float saturation, float brightness, int alpha) {
        return new HsbColor(hue, saturation, brightness, alpha);
    }

    public static HsbColor fromRgb(int rgba) {
        int r = ARGB.red(rgba);
        int g = ARGB.green(rgba);
        int b = ARGB.blue(rgba);
        int a = ARGB.alpha(rgba);

        int cmax = Math.max(Math.max(r, g), b);
        int cmin = Math.min(Math.min(r, g), b);

        float brightness = ((float) cmax) / 255.0f;
        float saturation = cmax != 0 ? ((float) (cmax - cmin)) / ((float) cmax) : 0;
        float hue;

        if (saturation == 0) {
            hue = 0;
        } else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            } else {
                hue = 4.0f + greenc - redc;
            }
            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }
        return new HsbColor(hue, saturation, brightness, a);
    }

    public int toRgba(boolean hasRgb) {
        return Mth.hsvToArgb(hue, saturation, brightness, hasRgb ? alpha : 0);
    }

    public int toRgba() {
        return toRgba(true);
    }

    public HsbColor withAlpha(int alpha) {
        return new HsbColor(hue, saturation, brightness, alpha);
    }

    public HsbColor withBrightness(float brightness) {
        return new HsbColor(hue, saturation, brightness, alpha);
    }

    public HsbColor withSaturation(float saturation) {
        return new HsbColor(hue, saturation, brightness, alpha);
    }

    public HsbColor withHue(float hue) {
        return new HsbColor(hue, saturation, brightness, alpha);
    }


}
