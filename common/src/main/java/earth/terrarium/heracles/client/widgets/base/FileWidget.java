package earth.terrarium.heracles.client.widgets.base;

import java.nio.file.Path;
import java.util.List;

public interface FileWidget {

    void onFilesDrop(List<Path> packs);
}
