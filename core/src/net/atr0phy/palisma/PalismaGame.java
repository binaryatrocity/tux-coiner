package net.atr0phy.palisma;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import net.atr0phy.palisma.game.Assets;
import net.atr0phy.palisma.screens.MenuScreen;

public class PalismaGame extends Game {
    @Override
    public void create () {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Assets.instance.init(new AssetManager());
        setScreen(new MenuScreen(this));
    }
}
