package earth.terrarium.example.renderdoc;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;
import org.lwjgl.system.linux.DynamicLinkLoader;

import java.util.Map;

/**
 * Taken from owo-lib under MIT License.
 * Source: <a href="https://github.com/wisp-forest/owo-lib/blob/ef83709dae78614695e2842cd792990e3db28900/src/main/java/io/wispforest/owo/renderdoc/RenderDoc.java">RenderDoc.java</a>
 */
public final class RenderDoc {

    private static final int VERSION = 1_06_00; // 1.6.0
    private static boolean loaded = false;

    public static void init() {
        if (RenderDoc.loaded || !FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        RenderDoc.loaded = true;

        var apiPointer = new PointerByReference();
        RenderdocLibrary.RenderdocApi apiInstance;

        var os = Util.getPlatform();

        if (os == Util.OS.WINDOWS || os == Util.OS.LINUX) {
            try {
                RenderdocLibrary renderdocLibrary;
                if (os == Util.OS.WINDOWS) {
                    renderdocLibrary = Native.load("renderdoc", RenderdocLibrary.class);
                } else {
                    int flags = DynamicLinkLoader.RTLD_NOW | DynamicLinkLoader.RTLD_NOLOAD;
                    if (DynamicLinkLoader.dlopen("librenderdoc.so", flags) == 0) {
                        throw new UnsatisfiedLinkError();
                    }

                    renderdocLibrary = Native.load("renderdoc", RenderdocLibrary.class, Map.of(Library.OPTION_OPEN_FLAGS, flags));
                }

                int initResult = renderdocLibrary.RENDERDOC_GetAPI(VERSION, apiPointer);
                if (initResult != 1) {
                    System.out.println("Could not connect to RenderDoc API, return code: " + initResult);
                } else {
                    apiInstance = new RenderdocLibrary.RenderdocApi(apiPointer.getValue());

                    var major = new IntByReference();
                    var minor = new IntByReference();
                    var patch = new IntByReference();
                    apiInstance.GetAPIVersion.call(major, minor, patch);
                    System.out.println("Connected to RenderDoc API v" + major.getValue() + "." + minor.getValue() + "." + patch.getValue());
                }
            } catch (UnsatisfiedLinkError ignored) {}
        }
    }

}