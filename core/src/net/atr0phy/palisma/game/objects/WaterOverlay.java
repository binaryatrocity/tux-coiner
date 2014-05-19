package net.atr0phy.palisma.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.atr0phy.palisma.game.Assets;

public class WaterOverlay extends Entity {
    private TextureRegion regWaterOverlay;
    private float length;

    public WaterOverlay (float length) {
        this.length = length;
        init();
    }

    public void init () {
        dimension.set(length * 10, 3);
        regWaterOverlay = Assets.instance.decorations.waterOverlay;
        origin.x = -dimension.x / 2;

    }

    @Override
    public void render (SpriteBatch batch) {
        TextureRegion reg = null;
        reg = regWaterOverlay;
        batch.draw(reg.getTexture(),
                position.x + origin.x, position.y + origin.y,
                origin.x, origin.y,
                dimension.x, dimension.y,
                scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                false, false
        );
    }
}
