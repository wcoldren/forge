package forge.ios;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Game extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture splashTexture;
    private TextureRegion mainImage;
    private TextureRegion uiElements;  // Store UI elements for later
    private FitViewport viewport;
    private float elapsed = 0;
    private static final float SPLASH_DURATION = 2.0f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(960, 640);

        // Load texture
        splashTexture = new Texture(Gdx.files.internal("bg_splash.png"));
        splashTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Based on the texture size (450x550), crop out just the main image area
        // and store UI elements separately
        mainImage = new TextureRegion(splashTexture, 0, 0, 450, 450);  // Top square portion
        uiElements = new TextureRegion(splashTexture, 0, 450, 450, 100); // Bottom UI elements

        Gdx.app.log("Debug", "Viewport dimensions: " + viewport.getWorldWidth() + "x" + viewport.getWorldHeight());
        Gdx.app.log("Debug", "Full texture: " + splashTexture.getWidth() + "x" + splashTexture.getHeight());
        Gdx.app.log("Debug", "Main image region: " + mainImage.getRegionWidth() + "x" + mainImage.getRegionHeight());
        Gdx.app.log("Debug", "UI elements region: " + uiElements.getRegionWidth() + "x" + uiElements.getRegionHeight());
    }

    @Override
    public void render() {
        elapsed += Gdx.graphics.getDeltaTime();
        float alpha = Math.min(1, elapsed / SPLASH_DURATION);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.setColor(1, 1, 1, alpha);

        // Calculate dimensions to maintain aspect ratio and center the main image
        float imageAspect = (float)mainImage.getRegionWidth() / mainImage.getRegionHeight();
        float screenAspect = viewport.getWorldWidth() / viewport.getWorldHeight();

        float drawWidth, drawHeight, x, y;

        if (imageAspect > screenAspect) {
            // Image is wider than screen
            drawWidth = viewport.getWorldWidth();
            drawHeight = drawWidth / imageAspect;
            x = 0;
            y = (viewport.getWorldHeight() - drawHeight) / 2;
        } else {
            // Image is taller than screen
            drawHeight = viewport.getWorldHeight();
            drawWidth = drawHeight * imageAspect;
            x = (viewport.getWorldWidth() - drawWidth) / 2;
            y = 0;
        }

        // Draw only the main image portion
        batch.draw(mainImage, x, y, drawWidth, drawHeight);

        if (elapsed < 0.1f) {
            Gdx.app.log("Debug", String.format("Drawing main image at: %.0f,%.0f size: %.0fx%.0f",
                    x, y, drawWidth, drawHeight));
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        Gdx.app.log("Debug", String.format("Resize event: %dx%d", width, height));
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (splashTexture != null) splashTexture.dispose();
    }
}