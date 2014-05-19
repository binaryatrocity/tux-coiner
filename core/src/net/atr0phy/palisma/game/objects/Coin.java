package net.atr0phy.palisma.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.atr0phy.palisma.game.Assets;

public class Coin extends Entity {
    private TextureRegion regCoin;
    public boolean collected;

    public Coin () {
        init();
    }

    private void init () {
        dimension.set(0.5f, 0.5f);
        regCoin = Assets.instance.coin.coin;

        // set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    public void render (SpriteBatch batch) {
        if (collected) return;

        TextureRegion reg = null;
        reg = regCoin;
        batch.draw(reg.getTexture(),
                position.x, position.y,
                origin.x, origin.y,
                dimension.x, dimension.y,
                scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                false, false
        );
    }

    public int getScore() {
        return 100;
    }
}
