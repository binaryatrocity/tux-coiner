package net.atr0phy.palisma.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
 
import net.atr0phy.palisma.game.Assets;
import net.atr0phy.palisma.util.Constants;
import net.atr0phy.palisma.util.CharacterSkin;
import net.atr0phy.palisma.util.GamePreferences;

public class Hero extends Entity {
    public static final String TAG = Hero.class.getName();

    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

    public enum VIEW_DIRECTION { LEFT, RIGHT }
    public enum JUMP_STATE { GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING }

    private TextureRegion regHero;
    public VIEW_DIRECTION viewDirection;
    public float timeJumping;
    public JUMP_STATE jumpState;
    public boolean hasDiskPowerup;
    public float timeLeftDiskPowerup;

    public Hero () {
        init();
    }

    public void init () {
        dimension.set(1, 1);
        regHero = Assets.instance.hero.hero;
        // Center image on game object
        origin.set(dimension.x / 2, dimension.y /2);

        // set bounding box
        bounds.set(0, 0, dimension.x, dimension.y);

        // set physics values
        terminalVelocity.set(3.0f, 4.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f, -25.0f);

        viewDirection = VIEW_DIRECTION.RIGHT;
        jumpState = JUMP_STATE.FALLING;
        timeJumping = 0;

        hasDiskPowerup = false;
        timeLeftDiskPowerup = 0;
    };

    public void setJumping (boolean jumpKeyPressed) {
        switch (jumpState) {
            case GROUNDED:
                if (jumpKeyPressed) {
                    timeJumping = 0;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
            case JUMP_RISING:
                if (!jumpKeyPressed)
                    jumpState = JUMP_STATE.JUMP_FALLING;
                break;
            case FALLING:
            case JUMP_FALLING:
                if (jumpKeyPressed && hasDiskPowerup) {
                    timeJumping = JUMP_TIME_OFFSET_FLYING;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
        }
    }
    
    public void setDiskPowerup (boolean pickedUp) {
        hasDiskPowerup = pickedUp;
        if (pickedUp) {
            timeLeftDiskPowerup = Constants.ITEM_DISK_POWERUP_DURATION;
        }
    }

    public boolean hasDiskPowerup () {
        return hasDiskPowerup && timeLeftDiskPowerup > 0;
    };
    
    @Override
    public void update (float deltaTime) {
        super.update(deltaTime);
        if (velocity.x != 0 ) {
            viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
        }
        if (timeLeftDiskPowerup > 0) {
            timeLeftDiskPowerup -= deltaTime;
            if (timeLeftDiskPowerup < 0) {
                timeLeftDiskPowerup = 0;
                setDiskPowerup(false);
            }
        }
    }

    @Override
    protected void updateMotionY (float deltaTime) {
        switch (jumpState) {
            case GROUNDED:
                jumpState = JUMP_STATE.FALLING;
                break;
            case JUMP_RISING:
                timeJumping += deltaTime;
                if (timeJumping <= JUMP_TIME_MAX) {
                    velocity.y = terminalVelocity.y;
                }
                break;
            case FALLING:
                break;
            case JUMP_FALLING:
                timeJumping += deltaTime;
                if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
                    velocity.y = terminalVelocity.y;
                }
        }
        if (jumpState != JUMP_STATE.GROUNDED)
            super.updateMotionY(deltaTime);
    }

    @Override
    public void render (SpriteBatch batch) {
        TextureRegion reg = null;
        batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());
        if (hasDiskPowerup)
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        reg = regHero;
        batch.draw(reg.getTexture(),
                position.x, position.y,
                origin.x, origin.y,
                dimension.x, dimension.y,
                scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                viewDirection == VIEW_DIRECTION.LEFT, false
        );
        batch.setColor(1,1,1,1);
    }
}
