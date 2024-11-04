package forge.ipad;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.backends.iosrobovm.IOSFiles;
import forge.Forge;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSBundle;
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
     * @return The configured iOS application instance.
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

        final File assetsDirFile = new File(assetsDir);
        if (!assetsDirFile.exists()) {
            System.out.println("Creating assets directory");
            assetsDirFile.mkdirs();
        }

        try {
            copyBundledAssets(assetsDirFile);
        } catch (final IOException e) {
            System.err.println("Error copying assets: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Assets directory contents after setup:");
        listDirectoryContents(assetsDirFile, "");

        try {
            System.out.println("Initializing Forge application...");
            final ApplicationListener forge = Forge.getApp(
                    new iPadClipboard(),
                    new iPadPlatform(),
                    assetsDir,
                    false,
                    false,
                    0,
                    false,
                    0,
                    "",
                    ""
            );

            if (forge == null) {
                throw new RuntimeException("Forge.getApp() returned null");
            }

            System.out.println("Forge application initialized successfully");
            return new IOSApplication(forge, config);
        } catch (final Exception e) {
            System.err.println("Error creating Forge application:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Copies bundled assets from the application bundle to the assets directory.
     *
     * @param assetsDir The destination directory for assets.
     * @throws IOException If there's an error copying files.
     */
    private void copyBundledAssets(final File assetsDir) throws IOException {
        final NSBundle bundle = NSBundle.getMainBundle();
        final String bundlePath = bundle.getBundlePath();
        System.out.println("Bundle path: " + bundlePath);

        final File resourcesDir = new File(bundlePath, "res");
        if (resourcesDir.exists()) {
            System.out.println("Found resources at: " + resourcesDir.getAbsolutePath());
            copyDirectory(resourcesDir, assetsDir);
        } else {
            System.out.println("Resources not found at: " + resourcesDir.getAbsolutePath());

            final File altResourcesDir = new File(bundlePath, "Resources/res");
            if (altResourcesDir.exists()) {
                System.out.println("Found alternate resources at: " + altResourcesDir.getAbsolutePath());
                copyDirectory(altResourcesDir, assetsDir);
            } else {
                System.out.println("No resources found in alternate location: " +
                        altResourcesDir.getAbsolutePath());
            }
        }
    }

    /**
     * Recursively copies a directory.
     *
     * @param sourceDir The source directory.
     * @param targetDir The target directory.
     * @throws IOException If there's an error copying files.
     */
    private void copyDirectory(final File sourceDir, final File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        final File[] files = sourceDir.listFiles();
        if (files != null) {
            for (final File file : files) {
                final File targetFile = new File(targetDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    copyFile(file, targetFile);
                    System.out.println("Copied: " + file + " to " + targetFile);
                }
            }
        }
    }

    /**
     * Copies a single file.
     *
     * @param sourceFile The source file.
     * @param targetFile The target file.
     * @throws IOException If there's an error copying the file.
     */
    private void copyFile(final File sourceFile, final File targetFile) throws IOException {
        try (final InputStream in = new FileInputStream(sourceFile);
             final OutputStream out = new FileOutputStream(targetFile)) {
            final byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    /**
     * Lists the contents of a directory and its subdirectories.
     *
     * @param dir The directory to list.
     * @param indent The indentation string for formatting output.
     */
    private void listDirectoryContents(final File dir, final String indent) {
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                System.out.println(indent + "- " + file.getName() +
                        (file.isDirectory() ? "/" : "") +
                        " (" + file.length() + " bytes)");
                if (file.isDirectory()) {
                    listDirectoryContents(file, indent + "  ");
                }
            }
        } else {
            System.out.println(indent + "(no files or unable to list)");
        }
    }

    /**
     * Gets the directory path where application assets should be stored.
     *
     * @return The full path to the assets directory.
     */
    private String getAssetsDirectory() {
        final IOSFiles files = new IOSFiles();
        final String baseDir = files.getLocalStoragePath();
        System.out.println("Base storage path: " + baseDir);
        return baseDir + File.separator + "Assets";
    }

    /**
     * Main entry point for the iPad application.
     * Creates the autorelease pool and launches the UIApplication.
     *
     * @param argv Command line arguments (not used).
     */
    public static void main(final String[] argv) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            System.out.println("Starting Forge iPad application");
            UIApplication.main(argv, null, ForgeiPadApp.class);
        }
    }
}