package forge.ipad;

import forge.Forge;
import forge.util.FileUtil;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.glkit.GLKViewDrawableColorFormat;
import org.robovm.apple.glkit.GLKViewDrawableDepthFormat;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.glkit.GLKViewDrawableMultisample;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSFileManager;
import org.robovm.apple.foundation.NSSearchPathDirectory;
import org.robovm.apple.foundation.NSSearchPathDomainMask;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIUserInterfaceIdiom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Main iPad application delegate class for Forge.
 */
public final class ForgeiPadApp extends IOSApplication.Delegate {
    private static final String ASSETS_DIRECTORY = "Forge";
    private static final int BUFFER_SIZE = 16384;
    private static final String RES_PATH = "res";
    private String assetsDirectory;

    /**
     * Creates and configures the iOS application instance.
     *
     * @return The configured iOS application instance
     */
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = configureApplication();
        assetsDirectory = initializeAssetsDirectory();
        createRequiredDirectories();
        copyBundledResources();
        createNoMediaFile();

        return new IOSApplication(createForgeInstance(), config);
    }

    private IOSApplicationConfiguration configureApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useGL30 = false;  // Force OpenGL ES 2.0
        config.colorFormat = GLKViewDrawableColorFormat.RGBA8888;
        config.depthFormat = GLKViewDrawableDepthFormat._24;
        config.stencilFormat = GLKViewDrawableStencilFormat._8;
        config.multisample = GLKViewDrawableMultisample.None;
        config.preferredFramesPerSecond = 60;
        config.useAccelerometer = false;
        config.useCompass = false;

        // Add these lines
        System.setProperty("apple.awt.UIElement", "true");

        return config;
    }

    private String initializeAssetsDirectory() {
        NSFileManager fileManager = NSFileManager.getDefaultManager();
        String documentsPath = fileManager.getURLsForDirectory(
                NSSearchPathDirectory.DocumentDirectory,
                NSSearchPathDomainMask.UserDomainMask).get(0).getPath();

        return documentsPath + "/" + ASSETS_DIRECTORY;
    }

    private void createRequiredDirectories() {
        String[] directories = {
                assetsDirectory,
                assetsDirectory + "/cache",
                assetsDirectory + "/cache/pics",
                assetsDirectory + "/cache/pics/cards",
                assetsDirectory + "/cache/pics/tokens",
                assetsDirectory + "/cache/pics/icons",
                assetsDirectory + "/user",
                assetsDirectory + "/logs"
        };

        for (String directory : directories) {
            FileUtil.ensureDirectoryExists(directory);
        }
    }

    private void copyBundledResources() {
        NSBundle bundle = NSBundle.getMainBundle();
        File resourcesDir = new File(bundle.getBundlePath(), RES_PATH);
        File targetDir = new File(assetsDirectory);

        if (!new File(targetDir, "LICENSE.txt").exists()) {
            try {
                System.out.println("Copying resources to: " + targetDir);
                copyDirectoryContents(resourcesDir, targetDir);
            } catch (IOException ex) {
                System.err.println("Resource copy failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void copyDirectoryContents(File source, File target) throws IOException {
        if (!source.isDirectory()) {
            copyFile(source, target);
            return;
        }

        if (!target.exists() && !target.mkdir()) {
            throw new IOException("Failed to create directory: " + target);
        }

        File[] files = source.listFiles();
        if (files != null) {
            for (File child : files) {
                copyDirectoryContents(
                        new File(source, child.getName()),
                        new File(target, child.getName())
                );
            }
        }
    }

    private void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    private void createNoMediaFile() {
        File noMedia = new File(assetsDirectory, ".nomedia");
        if (!noMedia.exists()) {
            try {
                if (!noMedia.createNewFile()) {
                    System.err.println("Failed to create .nomedia file");
                }
            } catch (IOException ex) {
                System.err.println("Error creating .nomedia: " + ex.getMessage());
            }
        }
    }

    private ApplicationListener createForgeInstance() {
        return Forge.getApp(
                new iPadClipboard(),
                new iPadPlatform(),
                assetsDirectory,
                false,
                false,
                getDeviceMemory(),
                isDeviceTablet(),
                getDeviceSystemVersion(),
                getDeviceName(),
                UIDevice.getCurrentDevice().getSystemName()
        );
    }

    private int getDeviceMemory() {
        return isDeviceTablet() ? 4096 : 2048;
    }

    private boolean isDeviceTablet() {
        return UIDevice.getCurrentDevice().getUserInterfaceIdiom()
                == UIUserInterfaceIdiom.Pad;
    }

    private int getDeviceSystemVersion() {
        String version = UIDevice.getCurrentDevice().getSystemVersion();
        return Integer.parseInt(version.split("\\.")[0]);
    }

    private String getDeviceName() {
        return UIDevice.getCurrentDevice().getLocalizedModel();
    }

    /**
     * Application entry point.
     *
     * @param argv Command line arguments
     */
    public static void main(String[] argv) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(argv, null, ForgeiPadApp.class);
        }
    }
}