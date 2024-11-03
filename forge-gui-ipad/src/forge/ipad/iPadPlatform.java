package forge.ipad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSFiles;
import forge.interfaces.IDeviceAdapter;
import org.apache.commons.lang3.tuple.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class iPadPlatform implements IDeviceAdapter {
    @Override
    public boolean isConnectedToInternet() {
        return true; // TODO: Implement proper connectivity check
    }

    @Override
    public boolean isConnectedToWifi() {
        return true; // TODO: Implement proper wifi check
    }

    @Override
    public String getDownloadsDir() {
        return new IOSFiles().getExternalStoragePath();
    }

    @Override
    public String getLatestChanges(String commitsAtom, Date buildDateOriginal, Date maxDate) {
        // For now, return empty string. Can implement GitHub API calls later if needed
        return "";
    }

    @Override
    public String getReleaseTag(String releaseAtom) {
        // For now, return empty string. Can implement release tag fetching later
        return "";
    }

    @Override
    public void closeSplashScreen() {
        // iPad doesn't need this, it's primarily for desktop development
    }

    @Override
    public boolean openFile(String filename) {
        return new IOSFiles().local(filename).exists();
    }

    @Override
    public void setLandscapeMode(boolean landscapeMode) {
        // TODO: Implement orientation control
    }

    @Override
    public void preventSystemSleep(boolean preventSleep) {
        // TODO: Implement screen sleep prevention
    }

    @Override
    public boolean isTablet() {
        return true; // This is always true for iPad
    }

    @Override
    public void restart() {
        // Not possible on iOS
    }

    @Override
    public void exit() {
        // Not possible on iOS
    }

    @Override
    public String getVersionString() {
        return "1.0"; // TODO: Get from build config
    }

    @Override
    public void convertToJPEG(InputStream input, OutputStream output) throws IOException {
        // TODO: Implement image conversion
    }

    @Override
    public Pair<Integer, Integer> getRealScreenSize(boolean real) {
        return Pair.of(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public ArrayList<String> getGamepads() {
        return new ArrayList<>();
    }
}