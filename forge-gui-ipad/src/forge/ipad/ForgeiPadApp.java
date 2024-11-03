package forge.ipad;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSFiles;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import forge.Forge;

public class ForgeiPadApp extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        // Explicitly configure graphics settings
        config.useGL30 = false;  // Force OpenGL ES 2.0
        config.useAccelerometer = false;
        config.useCompass = false;
        config.preferredFramesPerSecond = 60;

        String assetsDir = getAssetsDirectory();
        return new IOSApplication(
                Forge.getApp(
                        new iPadClipboard(),
                        new iPadPlatform(),
                        assetsDir,
                        false,  // enableGaTracker
                        false,  // enableMusic
                        0,      // musicVolume
                        false,  // enableSounds
                        0,      // soundVolume
                        "",     // androidLauncher
                        ""     // androidStoreDir
                ),
                config
        );
    }

    private String getAssetsDirectory() {
        return new IOSFiles().getLocalStoragePath() + "/Assets";
    }

    public static void main(String[] argv) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(argv, null, ForgeiPadApp.class);
        }
    }
}