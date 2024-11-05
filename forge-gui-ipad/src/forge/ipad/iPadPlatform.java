package forge.ipad;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import forge.interfaces.IDeviceAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSDateFormatter;
import org.robovm.apple.foundation.NSFileManager;
import org.robovm.apple.foundation.NSProcessInfo;
import org.robovm.apple.foundation.NSSearchPathDirectory;
import org.robovm.apple.foundation.NSSearchPathDomainMask;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.systemconfiguration.SCNetworkReachability;
import org.robovm.apple.systemconfiguration.SCNetworkReachabilityFlags;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIUserInterfaceIdiom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * iOS-specific implementation of IDeviceAdapter.
 */
public class iPadPlatform implements IDeviceAdapter {
    private static final long MIN_MEMORY_BYTES = 50 * 1024 * 1024;
    private final String documentsDirectory;

    /**
     * Constructs a new iPadPlatform instance.
     */
    public iPadPlatform() {
        NSFileManager fileManager = NSFileManager.getDefaultManager();
        documentsDirectory = fileManager.getURLsForDirectory(
                NSSearchPathDirectory.DocumentDirectory,
                NSSearchPathDomainMask.UserDomainMask).get(0).getPath();
    }

    @Override
    public boolean isConnectedToInternet() {
        try (SCNetworkReachability reach = new SCNetworkReachability("www.apple.com")) {
            long flags = reach.getFlags().value();
            return (flags & SCNetworkReachabilityFlags.Reachable.value()) != 0;
        } catch (Exception e) {
            System.err.println("Network check failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isConnectedToWifi() {
        return isConnectedToInternet();
    }

    @Override
    public String getDownloadsDir() {
        return documentsDirectory + "/Downloads/";
    }

    @Override
    public boolean openFile(String filename) {
        try {
            NSURL fileUrl = new NSURL(new File(filename));
            return UIApplication.getSharedApplication().openURL(fileUrl);
        } catch (Exception ex) {
            System.err.println("Error opening file: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void restart() {
        exit();
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public boolean isTablet() {
        return UIDevice.getCurrentDevice().getUserInterfaceIdiom()
                == UIUserInterfaceIdiom.Pad;
    }

    @Override
    public void setLandscapeMode(boolean landscapeMode) {
        // Not supported on iOS - controlled via Info.plist
    }

    @Override
    public void preventSystemSleep(boolean preventSleep) {
        UIApplication.getSharedApplication().setIdleTimerDisabled(preventSleep);
    }

    @Override
    public void convertToJPEG(InputStream input, OutputStream output)
            throws IOException {
        try {
            // Create temporary file to handle the input stream
            File tempFile = File.createTempFile("forge_temp", ".tmp");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            // Use FileHandle to create Pixmap
            FileHandle fileHandle = new FileHandle(tempFile);
            Pixmap pixmap = new Pixmap(fileHandle);

            // Convert to byte array
            byte[] pixels = pixmap.getPixels().array();
            output.write(pixels);

            // Cleanup
            pixmap.dispose();
            tempFile.delete();

        } catch (Exception ex) {
            throw new IOException("Image conversion error", ex);
        }
    }

    @Override
    public String getVersionString() {
        return NSProcessInfo.getSharedProcessInfo()
                .getOperatingSystemVersionString();
    }

    @Override
    public void closeSplashScreen() {
        // Managed by iOS
    }

    @Override
    public ArrayList<String> getGamepads() {
        return new ArrayList<>();
    }

    @Override
    public Pair<Integer, Integer> getRealScreenSize(boolean useNativeResolution) {
        UIScreen screen = UIScreen.getMainScreen();
        if (useNativeResolution) {
            return Pair.of(
                    (int) screen.getNativeBounds().getWidth(),
                    (int) screen.getNativeBounds().getHeight()
            );
        }
        return Pair.of(
                (int) screen.getBounds().getWidth(),
                (int) screen.getBounds().getHeight()
        );
    }

    @Override
    public String getLatestChanges(String commitsAtom,
                                   Date buildDateOriginal, Date maxDate) {
        return "";
    }

    @Override
    public String getReleaseTag(String releaseAtom) {
        return "";
    }

    private boolean hasEnoughMemory() {
        NSProcessInfo processInfo = NSProcessInfo.getSharedProcessInfo();
        return processInfo.getPhysicalMemory() > MIN_MEMORY_BYTES;
    }

    private String formatDate(Date date) {
        NSDateFormatter formatter = new NSDateFormatter();
        formatter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // Use date's toString() since stringFromDate is not available
        return new NSDate(date.getTime()).toString();
    }
}