package net.atr0phy.palisma.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public abstract class Entity {
    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public Vector2 velocity;
    public Vector2 terminalVelocity;
    public Vector2 friction;
    public Vector2 acceleration;
    public Rectangle bounds;
    public float rotation;

    public Entity () {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        velocity = new Vector2();
        terminalVelocity = new Vector2(1, 1);
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
        rotation = 0;
    }

    public void update (float deltaTime) {
        updateMotionX(deltaTime);
        updateMotionY(deltaTime);

        // move to new position
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
    }

    public abstract void render (SpriteBatch batch);

    ////////////////////////////////////////////////
    protected void updateMotionX (float deltaTime) {
        if (velocity.x != 0) {
            // apply friction
            if (velocity.x > 0) {
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
            } else {
                velocity.x = Math.min(velocity.x - friction.x * deltaTime, 0);
            }
        }

        // apply acceleration
        velocity.x += acceleration.x * deltaTime;

        // but within the bounds of terminalVelocity
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }

    protected void updateMotionY (float deltaTime) {
        if (velocity.y != 0) {
            // apply friction
            if (velocity.y > 0) {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            } else {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }

        // apply acceleration
        velocity.y += acceleration.y * deltaTime;

        // but within the bounds of terminalVelocity
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }
}
