package forge.ipad;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.backends.iosrobovm.IOSFiles;
import forge.Forge;
import java.io.File;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

/**
 * Main iPad application delegate class for Forge.
 * Handles initialization of the iOS application and LibGDX context.
 */
public final class ForgeiPadApp extends IOSApplication.Delegate {

    /**
     * Private constructor to prevent instantiation.
     */
    private ForgeiPadApp() {
        super();
    }

    /**
     * Creates and configures the main iOS application instance.
     * Sets up LibGDX configuration and initializes the Forge application.
     *
     * @return The configured iOS application instance
     */
    @Override
    protected IOSApplication createApplication() {
        final IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useGL30 = false;
        config.useAccelerometer = false;
        config.useCompass = false;
        config.preferredFramesPerSecond = 60;

        final String assetsDir = getAssetsDirectory();
        System.out.println("Assets directory: " + assetsDir);

        // Verify assets directory exists and is accessible
        final File assetsDirFile = new File(assetsDir);
        if (!assetsDirFile.exists()) {
            System.out.println("Creating assets directory");
            assetsDirFile.mkdirs();
        }

        // List contents of assets directory
        if (assetsDirFile.exists()) {
            System.out.println("Assets directory contents:");
            final File[] files = assetsDirFile.listFiles();
            if (files != null) {
                for (final File file : files) {
                    System.out.println(" - " + file.getName());
                }
            } else {
                System.out.println("No files found or unable to list directory");
            }
        }

        final IOSApplication app = new IOSApplication(
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

        System.out.println("Application created successfully");
        return app;
    }

    /**
     * Gets the directory path where application assets should be stored.
     *
     * @return The full path to the assets directory
     */
    private String getAssetsDirectory() {
        final IOSFiles files = new IOSFiles();
        final String baseDir = files.getLocalStoragePath();
        System.out.println("Base storage path: " + baseDir);
        return baseDir + "/Assets";
    }

    /**
     * Main entry point for the iPad application.
     * Creates the autorelease pool and launches the UIApplication.
     *
     * @param argv Command line arguments (not used)
     */
    public static void main(final String[] argv) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            System.out.println("Starting Forge iPad application");
            UIApplication.main(argv, null, ForgeiPadApp.class);
        }
    }
}