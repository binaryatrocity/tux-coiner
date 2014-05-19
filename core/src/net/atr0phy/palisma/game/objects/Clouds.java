package net.atr0phy.palisma.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.atr0phy.palisma.game.Assets;

public class Clouds extends Entity {
    private float length;
    private Array<TextureRegion> regClouds;
    private Array<Cloud> clouds;

    private class Cloud extends Entity {
        private TextureRegion regCloud;
        
        public Cloud () {}

        public void setRegion (TextureRegion region) {
            regCloud = region;
        }

        @Override
        public void render (SpriteBatch batch) {
            TextureRegion reg = regCloud;
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

    public Clouds (float length) {
        this.length = length;
        init();
    }

    private void init () {
        dimension.set(3.0f, 1.5f);
        regClouds = new Array<TextureRegion>();
        regClouds.add(Assets.instance.decorations.cloud01);
        regClouds.add(Assets.instance.decorations.cloud02);
        regClouds.add(Assets.instance.decorations.cloud03);

        int distanceFactor = 5;
        int numClouds = (int)(length / distanceFactor);
        
        clouds = new Array<Cloud>(2 * numClouds);
        for (int i = 0; i < numClouds; i++) {
            Cloud cloud = spawnCloud();
            cloud.position.x = i * distanceFactor;
            clouds.add(cloud);
        }
    }

    private Cloud spawnCloud () {
        Cloud cloud = new Cloud();
        cloud.dimension.set(dimension);

        // select random cloud image
        cloud.setRegion(regClouds.random());

        Vector2 position = new Vector2();
        position.x = length + 10; // position after end of level
        position.y += 1.75; // base position
        position.y += MathUtils.random(0.0f, 0.2f) * (MathUtils.randomBoolean() ? 1 : -1);

        cloud.position.set(position);
        return cloud;
    }

    @Override
    public void render (SpriteBatch batch) {
        for (Cloud cloud : clouds)
            cloud.render(batch);
    }
}
