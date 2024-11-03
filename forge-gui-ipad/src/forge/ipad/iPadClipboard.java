package forge.ipad;

import org.robovm.apple.uikit.UIPasteboard;
import com.badlogic.gdx.utils.Clipboard;

public class iPadClipboard implements Clipboard {
    @Override
    public boolean hasContents() {
        return !UIPasteboard.getGeneralPasteboard().toString().isEmpty();
    }

    @Override
    public String getContents() {
        return UIPasteboard.getGeneralPasteboard().getString();
    }

    @Override
    public void setContents(String contents) {
        UIPasteboard.getGeneralPasteboard().setString(contents);
    }
}