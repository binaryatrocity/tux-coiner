package net.atr0phy.palisma.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

import net.atr0phy.palisma.util.Constants;
import net.atr0phy.palisma.util.GamePreferences;

public class WorldRenderer implements Disposable {
    private OrthographicCamera camera;
    private OrthographicCamera uicam;
    private SpriteBatch batch;
    private WorldController worldController;

    public WorldRenderer (WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init () {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();

        uicam = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        uicam.position.set(0, 0, 0);
        uicam.setToOrtho(true); // flip y-axis
        uicam.update();
    }

    public void render () {
        renderWorld(batch);
        renderUI(batch);
    }

    public void resize (int width, int height) {
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();

        uicam.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        uicam.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float)height) * (float)width;
        uicam.position.set(uicam.viewportWidth / 2, uicam.viewportHeight /2, 0);
        uicam.update();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    private void renderWorld(SpriteBatch batch) {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.level.render(batch);
        batch.end();
    }

    private void renderUI (SpriteBatch batch) {
        batch.setProjectionMatrix(uicam.combined);
        batch.begin();
        renderUIScore(batch);
        renderUIPowerup(batch);
        renderUILives(batch);
        if(GamePreferences.instance.showFpsCounter)
            renderUIFpsCounter(batch);
        renderUIGameOver(batch);
        batch.end();
    }

    private void renderUIScore (SpriteBatch batch) {
        float x = -15;
        float y = -15;
        batch.draw(Assets.instance.coin.coin, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
        Assets.instance.fonts.defaultNormal.draw(batch, "" + worldController.score, x + 75, y + 40);
    }

    private void renderUILives (SpriteBatch batch) {
        float x = uicam.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y = -15;
        for (int i = 0; i < Constants.LIVES_START; i++) {
            if (worldController.lives <= i)
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            batch.draw(Assets.instance.hero.hero, x+i*50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void renderUIFpsCounter (SpriteBatch batch) {
        float x = uicam.viewportWidth - 55;
        float y = uicam.viewportHeight - 15;
        int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
        if (fps >= 45) {
            fpsFont.setColor(0, 1, 0, 1);
        } else if (fps >= 30) {
            fpsFont.setColor(1, 1, 0, 1);
        } else {
            fpsFont.setColor(1, 0, 0, 1);
        }
        fpsFont.draw(batch, "FPS: " + fps, x, y);
        fpsFont.setColor(1, 1, 1, 1);
    }

    private void renderUIGameOver (SpriteBatch batch) {
        float x = uicam.viewportWidth / 2;
        float y = uicam.viewportHeight / 2;
        if (worldController.isGameOver()) {
            BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
            fontGameOver.setColor(1, 0.75f, 0.25f, 1);
            fontGameOver.drawMultiLine(batch, "GAME OVER", x, y, 0, BitmapFont.HAlignment.CENTER);
        }
    }
    
    private void renderUIPowerup (SpriteBatch batch) {
        float x = -15;
        float y = 30;
        float timeLeftPowerup = worldController.level.hero.timeLeftDiskPowerup;
        if (timeLeftPowerup > 0) {
            if (timeLeftPowerup < 4) {
                if (((int)(timeLeftPowerup * 5) % 2) != 0) {
                    batch.setColor(1, 1, 1, 0.5f);
                }
            }
            batch.draw(Assets.instance.disk.disk, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
            Assets.instance.fonts.defaultSmall.draw(batch, ""+(int)timeLeftPowerup, x+70, y+57);
        }
    }
}
