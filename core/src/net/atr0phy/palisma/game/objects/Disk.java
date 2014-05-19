package net.atr0phy.palisma.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.atr0phy.palisma.game.Assets;

public class Disk extends Entity {
    private TextureRegion regDisk;
    public boolean collected;

    public Disk () {
        init();
    }

    private void init () {
        dimension.set(0.5f, 0.5f);
        regDisk = Assets.instance.disk.disk;

        // set bounding box
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    public void render (SpriteBatch batch) {
        if (collected) return;
        TextureRegion reg = null;
        reg = regDisk;
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
        return 250;
    }
}
