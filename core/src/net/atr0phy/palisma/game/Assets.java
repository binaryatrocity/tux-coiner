package net.atr0phy.palisma.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import net.atr0phy.palisma.util.Constants;

public class Assets implements Disposable, AssetErrorListener {
    public static final String TAG = Assets.class.getName();
    public static final Assets instance = new Assets();
    private AssetManager assetManager;

    public AssetHero hero;
    public AssetRock rock;
    public AssetCoin coin;
    public AssetDisk disk;
    public AssetDecorations decorations;

    public AssetFonts fonts;

    private Assets () {}

    public void init (AssetManager assetManager) {
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);

        // Load our sprite sheet
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        assetManager.finishLoading();
        Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames())
            Gdx.app.debug(TAG, "asset: " + a);

        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

        // Enable texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures())
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        // Create game resource objects
        fonts = new AssetFonts();
        hero = new AssetHero(atlas);
        rock = new AssetRock(atlas);
        coin = new AssetCoin(atlas);
        disk = new AssetDisk(atlas);
        decorations = new AssetDecorations(atlas);
    }

    @Override
    public void dispose () {
        assetManager.dispose();
        fonts.defaultSmall.dispose();
        fonts.defaultNormal.dispose();
        fonts.defaultBig.dispose();
    }

    @Override
    public void error (AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", (Exception)throwable);
    }

    public class AssetHero {
        public final AtlasRegion hero;
        public AssetHero (TextureAtlas atlas) {
            hero = atlas.findRegion("hero");
        }
    }
    public class AssetRock {
        public final AtlasRegion edge;
        public final AtlasRegion middle;
        public AssetRock (TextureAtlas atlas) {
            edge = atlas.findRegion("rock_edge");
            middle = atlas.findRegion("rock_middle");
        }
    }
    public class AssetCoin {
        public final AtlasRegion coin;
        public AssetCoin (TextureAtlas atlas) {
            coin = atlas.findRegion("coin");
        }
    }
    public class AssetDisk {
        public final AtlasRegion disk;
        public AssetDisk (TextureAtlas atlas) {
            disk = atlas.findRegion("disk");
        }
    }
    public class AssetDecorations {
        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountainLeft;
        public final AtlasRegion mountainRight;
        public final AtlasRegion waterOverlay;

        public AssetDecorations (TextureAtlas atlas) {
            cloud01 = atlas.findRegion("cloud01");
            cloud02 = atlas.findRegion("cloud02");
            cloud03 = atlas.findRegion("cloud03");
            mountainLeft = atlas.findRegion("mountain_left");
            mountainRight = atlas.findRegion("mountain_right");
            waterOverlay = atlas.findRegion("water_overlay");
        }
    }

    public class AssetFonts {
        public final BitmapFont defaultSmall;
        public final BitmapFont defaultNormal;
        public final BitmapFont defaultBig;

        public AssetFonts () {
            defaultSmall = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"), true);
            defaultNormal = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"), true);
            defaultBig = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"), true);

            defaultSmall.setScale(0.75f);
            defaultNormal.setScale(1.0f);
            defaultBig.setScale(2.0f);

            // Linear texture filtering for a smoother font face
            defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
    }
}
