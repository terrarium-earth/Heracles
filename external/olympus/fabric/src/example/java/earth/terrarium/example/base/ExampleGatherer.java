package earth.terrarium.example.base;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ExampleGatherer {

    interface ThrowingConsumer {
        void accept(Class<?> clazz) throws Exception;
    }

    private static Path getPath(String classPath) throws URISyntaxException {
        Path path = Paths.get(ExampleGatherer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        for (String s : classPath.split("\\.")) {
            path = path.resolve(s);
        }
        return path;
    }

    private static void walkClasses(String classPath, ThrowingConsumer consumer) {
        try {
            final Path path = getPath(classPath);
            try (var stream = Files.walk(path)) {
                stream.filter(it -> it.toString().endsWith(".class"))
                        .map(it -> {
                            String className = it.toString().replace(path.toString(), "")
                                    .replace(".class", "")
                                    .replace("/", ".")
                                    .substring(1);
                            try {
                                return Class.forName(classPath + "." + className);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .forEach(clazz -> {
                            try {
                                consumer.accept(clazz);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Pair<OlympusExample, Supplier<Screen>>> getExamples() {
        List<Pair<OlympusExample, Supplier<Screen>>> examples = new ArrayList<>();
        walkClasses("earth.terrarium.example.examples", clazz -> {
            if (clazz.isMemberClass()) return;


            OlympusExample example = Objects.requireNonNull(clazz.getAnnotation(OlympusExample.class), "Example class must be annotated with @OlympusExample");
            assert clazz.isAssignableFrom(Screen.class) : "Example class must extend Screen";
            Constructor<? extends Screen> constructor = (Constructor<? extends Screen>) clazz.getDeclaredConstructor();
            examples.add(Pair.of(example, () -> {
                try {
                    return constructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        });
        return examples;
    }
}
