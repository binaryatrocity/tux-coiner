package net.atr0phy.palisma.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.atr0phy.palisma.game.objects.Entity;
import net.atr0phy.palisma.game.objects.Hero;
import net.atr0phy.palisma.game.objects.Disk;
import net.atr0phy.palisma.game.objects.Coin;
import net.atr0phy.palisma.game.objects.Clouds;
import net.atr0phy.palisma.game.objects.Mountains;
import net.atr0phy.palisma.game.objects.Rock;
import net.atr0phy.palisma.game.objects.WaterOverlay;


public class Level {
    public static final String TAG = Level.class.getName();

    public enum BLOCK_TYPE {
        EMPTY(0, 0, 0),                    //black
        ROCK(0, 255, 0),                   //green
        PLAYER_SPAWNPOINT(255, 255, 255),  //white
        ITEM_DISK(255, 0, 255),            //purple
        ITEM_COIN(255, 255, 0);            //yellow

        private int color;

        private BLOCK_TYPE (int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        public boolean sameColor (int color) {
            return this.color == color;
        }

        public int getColor () {
            return color;
        }
    }

    // Player object
    public Hero hero;

    // Objects
    public Array<Rock> rocks;
    public Array<Coin> coins;
    public Array<Disk> disks;

    public Clouds clouds;
    public Mountains mountains;
    public WaterOverlay waterOverlay;

    public Level (String filename) {
        init(filename);
    }

    private void init (String filename) {
        // player character
        hero = null;

        // level objects
        rocks = new Array<Rock>();
        coins = new Array<Coin>();
        disks = new Array<Disk>();

        // Load level data file
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));

        // Scan through our pixels from top-left to bottom-right
        int lastPixel = -1;
        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                Entity obj = null;
                float offsetHeight = 0;
                float baseHeight = pixmap.getHeight() - pixelY;
                int currentPixel = pixmap.getPixel(pixelX, pixelY);

                // Check against our defined BLOCK_TYPE's
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    // pass, empty block
                }
                else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
                    if (lastPixel != currentPixel) {
                        obj = new Rock();
                        float heightIncreaseFactor = 0.25f;
                        offsetHeight = -2.5f;
                        obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
                        rocks.add((Rock)obj);
                    } else {
                        rocks.get(rocks.size - 1).increaseLength(1);
                    }
                }
                else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
                    obj = new Hero();
                    offsetHeight = -3.0f;
                    obj.position.set(pixelX,baseHeight * obj.dimension.y + offsetHeight);
                    hero = (Hero)obj;
                }
                else if (BLOCK_TYPE.ITEM_DISK.sameColor(currentPixel)) {
                    obj = new Disk();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX,baseHeight * obj.dimension.y + offsetHeight);
                    disks.add((Disk)obj);
                }
                else if (BLOCK_TYPE.ITEM_COIN.sameColor(currentPixel)) {
                    obj = new Coin();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX,baseHeight * obj.dimension.y + offsetHeight);
                    coins.add((Coin)obj);
                }
                else {
                    // Unknown pixel, get rgba
                    int r = 0xff & (currentPixel >>> 24);
                    int g = 0xff & (currentPixel >>> 16);
                    int b = 0xff & (currentPixel >>> 8);
                    int a = 0xff & currentPixel; 
                    Gdx.app.error(TAG, "Unknown object at x<<"+pixelX+"> y<"+pixelY+">: r<<"+r+"> g<"+g+"> b<"+b+"> a<"+a+">");
                }
                lastPixel = currentPixel;
            }
        }

        // decorations
        clouds = new Clouds(pixmap.getWidth());
        clouds.position.set(0, 2);
        mountains = new Mountains(pixmap.getWidth());
        mountains.position.set(-1, -1);
        waterOverlay = new WaterOverlay(pixmap.getWidth());
        waterOverlay.position.set(0, -3.75f);

        // free up memory
        pixmap.dispose();
        Gdx.app.debug(TAG, "level '" + filename + "' loaded");
    }

    public void render (SpriteBatch batch) {
        mountains.render(batch);
        clouds.render(batch);
        for (Rock rock : rocks)
            rock.render(batch);
        for (Coin coin : coins)
            coin.render(batch);
        for (Disk disk : disks)
            disk.render(batch);
        hero.render(batch);
        waterOverlay.render(batch);
    }

    public void update (float deltaTime) {
        hero.update(deltaTime);
        for (Rock rock : rocks)
            rock.update(deltaTime);
        for (Coin coin : coins)
            coin.update(deltaTime);
        for (Disk disk : disks)
            disk.update(deltaTime);
        clouds.update(deltaTime);
    }
}
