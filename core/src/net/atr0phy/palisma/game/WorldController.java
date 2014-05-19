package net.atr0phy.palisma.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

import net.atr0phy.palisma.util.CameraHelper;
import net.atr0phy.palisma.util.Constants;
import net.atr0phy.palisma.game.objects.Rock;
import net.atr0phy.palisma.game.objects.Hero;
import net.atr0phy.palisma.game.objects.Hero.JUMP_STATE;
import net.atr0phy.palisma.game.objects.Disk;
import net.atr0phy.palisma.game.objects.Coin;
import net.atr0phy.palisma.screens.MenuScreen;

public class WorldController extends InputAdapter {
    private Game game;
    public CameraHelper cameraHelper;

    public Level level;
    public int lives;
    public int score;

    // collision detection rectangles
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    private float timeLeftGameOverDelay;

    private static final String TAG = WorldController.class.getName();

    public WorldController (Game game) {
        this.game = game;
        init();
    }

    private void init () {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    public void update (float deltaTime) {
        handleDebugInput(deltaTime);
        if(isGameOver()) {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0) backToMenu();
        } else {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollisions();
        cameraHelper.update(deltaTime);
        if(!isGameOver() && isPlayerInWater()) {
            lives--;
            if(isGameOver())
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            else
                initLevel();
        }
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    public boolean isGameOver() {
        return lives < 0;
    }

    public boolean isPlayerInWater() {
        return level.hero.position.y < -5;
    }

    private void initLevel () {
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.hero);
    }

    private void backToMenu () {
        game.setScreen(new MenuScreen(game));
    }

    private void handleInputGame (float deltaTime) {
        if (cameraHelper.hasTarget(level.hero)) {
            // Player movement
            if (Gdx.input.isKeyPressed(Keys.A)) {
                level.hero.velocity.x = -level.hero.terminalVelocity.x;
            } else if (Gdx.input.isKeyPressed(Keys.D)) {
                level.hero.velocity.x = level.hero.terminalVelocity.x;
            } else {
                // Auto-forward on non-desktop platforms
                if (Gdx.app.getType() != ApplicationType.Desktop) {
                    level.hero.velocity.x = level.hero.terminalVelocity.x;
                }
            }

            // Player jumps
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
                level.hero.setJumping(true);
            } else {
                level.hero.setJumping(false);
            }
        }
    }

    private void handleDebugInput (float deltaTime) {
        //if (Gdx.app.getType() != ApplicationType.Desktop) return;

        if (!cameraHelper.hasTarget(level.hero)) {
            // Camera Movement
            float cameraMoveSpeed = 5 * deltaTime;
            float cameraAcceleration= 5;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) cameraMoveSpeed *= cameraAcceleration; // speed up camera
            if (Gdx.input.isKeyPressed(Keys.A)) moveCamera(-cameraMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.D)) moveCamera(cameraMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.W)) moveCamera(0, cameraMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.S)) moveCamera(0, -cameraMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.F)) cameraHelper.setPosition(0, 0); // reset camera
        }

        // Camera Zoom
        float cameraZoomSpeed = 1 * deltaTime;
        float cameraZoomSpeedAcceleration = 5;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) cameraZoomSpeed *= cameraZoomSpeedAcceleration;
        if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(cameraZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-cameraZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
    }

    @Override
    public boolean keyUp (int keycode) {
        // Reset game world
        if (keycode == Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world reset.");
        }

        // Reset player velocity
        if (keycode == Keys.A || keycode == Keys.D) {
            level.hero.velocity.x = 0;
        }

        // Toggle camera follow
        else if (keycode == Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.hero);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }

        else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            backToMenu();
        }

        return false;
    }

    private void onCollisionHeroWithRock (Rock rock) {
        Hero hero = level.hero;
        float heightDifference = Math.abs(hero.position.y - ( rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitLeftEdge = hero.position.x > ( rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
                hero.position.x = rock.position.x + rock.bounds.width;
            } else {
                hero.position.x = rock.position.x - hero.bounds.width;
            }
            return;
        }

        switch (hero.jumpState) {
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLING:
                hero.position.y = rock.position.y + hero.bounds.height + hero.origin.y;
                hero.jumpState = JUMP_STATE.GROUNDED;
                break;
            case JUMP_RISING:
                hero.position.y = rock.position.y + hero.bounds.height + hero.origin.y;
                break;
        }
    };

    private void onCollisionHeroWithCoin (Coin coin) {
        coin.collected = true;
        score += coin.getScore();
        Gdx.app.log(TAG, "Bitcoin collected");
    };

    private void onCollisionHeroWithDisk (Disk disk) {
        disk.collected = true;
        score += disk.getScore();
        level.hero.setDiskPowerup(true);
        Gdx.app.log(TAG, "Disk collected");
    };

    private void testCollisions () {
        r1.set(level.hero.position.x,
                level.hero.position.y,
                level.hero.bounds.width,
                level.hero.bounds.height
        );

        for (Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionHeroWithRock(rock);
            // Important: Must do all collisions for valid edge testing on rocks
        }

        for (Coin coin : level.coins) {
            if (coin.collected) continue;
            r2.set(coin.position.x, coin.position.y, coin.bounds.width, coin.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionHeroWithCoin(coin);
            break;
        }

        for (Disk disk : level.disks) {
            if (disk.collected) continue;
            r2.set(disk.position.x, disk.position.y, disk.bounds.width, disk.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionHeroWithDisk(disk);
            break;
        }
    }
}
