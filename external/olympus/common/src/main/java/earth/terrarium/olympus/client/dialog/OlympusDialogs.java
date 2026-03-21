package earth.terrarium.olympus.client.dialog;

import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class OlympusDialogs {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Pattern FILE_FILTER_PATTERN = Pattern.compile("\\*\\.[a-z0-9]+");

    private static void assertValidFileFilters(String... filters) {
        for (String filter : filters) {
            if (!FILE_FILTER_PATTERN.matcher(filter).matches()) {
                throw new IllegalArgumentException("Invalid file type: " + filter);
            }
        }
    }

    public static CompletableFuture<Optional<List<Path>>> openFileSystemDialog(FileSystemDialogType type, @Nullable Path path, String... filters) {
        assertValidFileFilters(filters);
        return CompletableFuture.supplyAsync(() -> {
            String result;

            try (var stack = MemoryStack.stackPush()) {
                PointerBuffer buffer = stack.mallocPointer(filters.length);
                for (String filter : filters) {
                    buffer.put(stack.UTF8(filter));
                }
                buffer.flip();

                String label = Arrays.stream(filters)
                        .map(f -> f.substring(2))
                        .reduce((a, b) -> a + ", " + b)
                        .orElse(null);

                String defaultPath = Optionull.map(path, p -> p.toAbsolutePath().toString());

                result = switch (type) {
                    case OPEN_FILE -> TinyFileDialogs.tinyfd_openFileDialog(
                            "Open File",
                            defaultPath,
                            buffer,
                            label,
                            false
                    );
                    case OPEN_MULTIPLE_FILE -> TinyFileDialogs.tinyfd_openFileDialog(
                            "Open File(s)",
                            defaultPath,
                            buffer,
                            label,
                            true
                    );
                    case SAVE_FILE -> TinyFileDialogs.tinyfd_saveFileDialog(
                            "Save File",
                            defaultPath,
                            buffer,
                            label
                    );
                    case SELECT_FOLDER -> TinyFileDialogs.tinyfd_selectFolderDialog(
                            "Select Folder",
                            Objects.requireNonNullElse(defaultPath, "") // This is because even though it says it's nullable, it's not
                    );
                };
            }
            return Optional.ofNullable(result).map(r ->
                    Arrays.stream(r.split("\\|"))
                            .map(Path::of)
                            .toList()
            );
        }, EXECUTOR);
    }

    public enum FileSystemDialogType {
        OPEN_FILE,
        OPEN_MULTIPLE_FILE,
        SAVE_FILE,
        SELECT_FOLDER
    }
}
