package earth.terrarium.heracles.common.utils;

public record PlatformSettings(
    boolean prefixedLogger
) {

    public void apply(PlatformLogger logger) {
        logger.setPrefix(prefixedLogger);
    }
}
