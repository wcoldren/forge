package forge.ios;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.glkit.GLKViewDrawableColorFormat;
import org.robovm.apple.glkit.GLKViewDrawableDepthFormat;
import org.robovm.apple.glkit.GLKViewDrawableMultisample;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;

public class IOSLauncher extends IOSApplication.Delegate {
    private static final boolean USE_GL30 = false;
    private static final int FRAMES_PER_SECOND = 60;

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        // Basic configuration
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGL30 = USE_GL30;
        config.preferredFramesPerSecond = FRAMES_PER_SECOND;

        // OpenGL ES configuration
        config.colorFormat = GLKViewDrawableColorFormat.RGBA8888;
        config.depthFormat = GLKViewDrawableDepthFormat._24;
        config.stencilFormat = GLKViewDrawableStencilFormat._8;
        config.multisample = GLKViewDrawableMultisample.None;

        // Additional configuration for stable GL context
        config.useHaptics = false;
        config.preventScreenDimming = true;

        return new IOSApplication(new Game(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        try {
            // Force load GLES classes
            Class.forName("com.badlogic.gdx.backends.iosrobovm.IOSGLES20");
            Class.forName("com.badlogic.gdx.backends.iosrobovm.IOSGraphics");

            UIApplication.main(argv, null, IOSLauncher.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close();
        }
    }
}