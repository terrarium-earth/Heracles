package earth.terrarium.heracles.api.client.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;

public record EditorTheme(
    Color modalUploadingTitle,
    Color modalDependenciesTitle,
    Color modalIconsTitle,
    Color modalTextTitle,
    Color modalUploadingFileName,
    Color modalDependenciesDependencyTitle,
    Color modalUploadingFileSize,
    Color modalEditSettingTitle,
    Color error
) {

    public static final EditorTheme DEFAULT = new EditorTheme(
        new Color(0x404040),
        new Color(0xFFFFFF),
        new Color(0x404040),
        new Color(0x404040),
        new Color(0xFFFFFF),
        new Color(0xFFFFFF),
        new Color(0xFFFFFF),
        new Color(0xFFFFFF),
        new Color(0xFF0000)
    );

    public static final Codec<EditorTheme> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Color.CODEC.fieldOf("modalUploadingTitle").orElse(DEFAULT.modalUploadingTitle()).forGetter(EditorTheme::modalUploadingTitle),
        Color.CODEC.fieldOf("modalDependenciesTitle").orElse(DEFAULT.modalDependenciesTitle()).forGetter(EditorTheme::modalDependenciesTitle),
        Color.CODEC.fieldOf("modalIconsTitle").orElse(DEFAULT.modalIconsTitle()).forGetter(EditorTheme::modalIconsTitle),
        Color.CODEC.fieldOf("modalTextTitle").orElse(DEFAULT.modalTextTitle()).forGetter(EditorTheme::modalTextTitle),
        Color.CODEC.fieldOf("modalUploadingFileName").orElse(DEFAULT.modalUploadingFileName()).forGetter(EditorTheme::modalUploadingFileName),
        Color.CODEC.fieldOf("modalDependenciesDependencyTitle").orElse(DEFAULT.modalDependenciesDependencyTitle()).forGetter(EditorTheme::modalDependenciesDependencyTitle),
        Color.CODEC.fieldOf("modalUploadingFileSize").orElse(DEFAULT.modalUploadingFileSize()).forGetter(EditorTheme::modalUploadingFileSize),
        Color.CODEC.fieldOf("modalEditSettingTitle").orElse(DEFAULT.modalEditSettingTitle()).forGetter(EditorTheme::modalEditSettingTitle),
        Color.CODEC.fieldOf("error").orElse(DEFAULT.error()).forGetter(EditorTheme::error)
    ).apply(instance, EditorTheme::new));

    public static int getModalUploadingTitle() {
        return Theme.getInstance().editor().modalUploadingTitle().getValue();
    }

    public static int getModalDependenciesTitle() {
        return Theme.getInstance().editor().modalDependenciesTitle().getValue();
    }

    public static int getModalIconsTitle() {
        return Theme.getInstance().editor().modalIconsTitle().getValue();
    }

    public static int getModalTextTitle() {
        return Theme.getInstance().editor().modalTextTitle().getValue();
    }

    public static int getModalUploadingFileName() {
        return Theme.getInstance().editor().modalUploadingFileName().getValue();
    }

    public static int getModalDependenciesDependencyTitle() {
        return Theme.getInstance().editor().modalDependenciesDependencyTitle().getValue();
    }

    public static int getModalUploadingFileSize() {
        return Theme.getInstance().editor().modalUploadingFileSize().getValue();
    }

    public static int getModalEditSettingTitle() {
        return Theme.getInstance().editor().modalEditSettingTitle().getValue();
    }

    public static int getError() {
        return Theme.getInstance().editor().error().getValue();
    }
}
