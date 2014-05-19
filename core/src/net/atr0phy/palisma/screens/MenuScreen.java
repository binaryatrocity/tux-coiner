package net.atr0phy.palisma.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.atr0phy.palisma.game.Assets;
import net.atr0phy.palisma.util.Constants;
import net.atr0phy.palisma.util.CharacterSkin;
import net.atr0phy.palisma.util.GamePreferences;

public class MenuScreen extends BaseScreen {
    private Stage stage;
    private Skin skinPalisma;
    private Skin skinLibgdx;

    // menu
    private Image imgBackground;
    private Image imgLogo;
    private Image imgInfo;
    private Image imgCoins;
    private Image imgBunny;
    private Button btnMenuPlay;
    private Button btnMenuOptions;
    private static final String TAG = MenuScreen.class.getName();

    // options
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;
    private CheckBox chkSound;
    private Slider sldSound;
    private CheckBox chkMusic;
    private Slider sldMusic;
    private SelectBox selCharSkin;
    private Image imgCharSkin;
    private CheckBox chkShowFpsCounter;

    // debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f;
    private boolean debugEnabled = false;
    private float debugRebuildStage;

    public MenuScreen (Game game) {
        super(game);
    }

    @Override
    public void render (float deltaTime) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(debugEnabled) {
            debugRebuildStage -= deltaTime;
            if (debugRebuildStage <= 0) {
                debugRebuildStage = DEBUG_REBUILD_INTERVAL;
                rebuildStage();
            }
        }
        stage.act(deltaTime);
        stage.draw();
        Table.drawDebug(stage);
    }
    
    @Override public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void hide () {
        stage.dispose();
        skinPalisma.dispose();
        skinLibgdx.dispose();
    }
    @Override public void show () {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
    }
    @Override public void pause () {}

    private void rebuildStage () {
        skinPalisma = new Skin(Gdx.files.internal(Constants.SKIN_PALISMA_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
        skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

        // build layers
        Table layerBackground = buildBackgroundLayer();
        Table layerObjects = buildObjectsLayer();
        Table layerLogos = buildLogosLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();

        // assemble stage for menu
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerObjects);
        stack.add(layerLogos);
        stack.add(layerControls);
        stage.addActor(layerOptionsWindow);
    }

    // Individual layer creati on functions
    private Table buildBackgroundLayer () {
        Table layer = new Table();
        imgBackground = new Image(skinPalisma, "background");
        layer.add(imgBackground);
        return layer;
    }
    private Table buildObjectsLayer () {
        Table layer = new Table();

        imgCoins = new Image(skinPalisma, "coins");
        layer.addActor(imgCoins);
        imgCoins.setPosition(135, 80);

        imgBunny = new Image(skinPalisma, "bunny");
        layer.addActor(imgBunny);
        imgBunny.setPosition(355, 40);

        return layer;
    }
    private Table buildLogosLayer () {
        Table layer = new Table();
        layer.left().top();

        imgLogo = new Image(skinPalisma, "logo");
        layer.add(imgLogo);
        layer.row().expandY();

        imgInfo = new Image(skinPalisma, "info");
        layer.add(imgInfo).bottom();
        if (debugEnabled) layer.debug();

        return layer;
    }
    private Table buildControlsLayer () {
        Table layer = new Table();
        layer.right().bottom();

        btnMenuPlay = new Button(skinPalisma, "play");
        layer.add(btnMenuPlay);
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        layer.row();

        btnMenuOptions = new Button(skinPalisma, "options");
        layer.add(btnMenuOptions);
        btnMenuOptions.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });
        if (debugEnabled) layer.debug();

        return layer;
    }
    private Table buildOptionsWindowLayer () {
        winOptions = new Window("Options", skinLibgdx);
        winOptions.add(buildOptWinAudioSettings()).row();
        winOptions.add(buildOptWinSkinSelection()).row();
        winOptions.add(buildOptWinDebug()).row();
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

        winOptions.setColor(1, 1, 1, 0.8f);
        winOptions.setVisible(false);
        if(debugEnabled) winOptions.debug();
        winOptions.pack();
        winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);

        return winOptions;
    }


    // button handlers
    private void onPlayClicked () {
        game.setScreen(new GameScreen(game));
    }

    private void onOptionsClicked () {
        loadSettings();
        btnMenuPlay.setVisible(false);
        btnMenuOptions.setVisible(false);
        winOptions.setVisible(true);
    }


    // settings
    private void loadSettings() {
        GamePreferences prefs = GamePreferences.instance;
        prefs.load();
        chkSound.setChecked(prefs.sound);
        sldSound.setValue(prefs.volSound);
        chkMusic.setChecked(prefs.music);
        sldMusic.setValue(prefs.volMusic);
        selCharSkin.setSelected(prefs.charSkin);
        onCharSkinSelected(prefs.charSkin);
        chkShowFpsCounter.setChecked(prefs.showFpsCounter);
    }

    private void saveSettings() {
        GamePreferences prefs = GamePreferences.instance;
        prefs.sound = chkSound.isChecked();
        prefs.volSound = sldSound.getValue();
        prefs.music = chkMusic.isChecked();
        prefs.volMusic = sldMusic.getValue();
        prefs.charSkin =  selCharSkin.getSelectedIndex();
        prefs.showFpsCounter = chkShowFpsCounter.isChecked();
        prefs.save();
    }

    private void onCharSkinSelected(int index) {
        CharacterSkin skin = CharacterSkin.values()[index];
        imgCharSkin.setColor(skin.getColor());
    }

    private void onSaveClicked() {
        saveSettings();
        onCancelClicked();
    }

    private void onCancelClicked() {
        btnMenuPlay.setVisible(true);
        btnMenuOptions.setVisible(true);
        winOptions.setVisible(false);
    }

    // Options menu widget creation
    private Table buildOptWinAudioSettings() {
        Table tbl = new Table();
        tbl.pad(10,10,0,10);
        tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        chkSound = new CheckBox("", skinLibgdx);
        tbl.add(chkSound);
        tbl.add(new Label("Sound", skinLibgdx));
        sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldSound);
        tbl.row();
        chkMusic = new CheckBox("", skinLibgdx);
        tbl.add(chkMusic);
        tbl.add(new Label("Music", skinLibgdx));
        sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldMusic);
        tbl.row();

        return tbl;
    }

    private Table buildOptWinSkinSelection() {
        Table tbl = new Table();
        tbl.pad(10,10,0,10);
        tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2);
        tbl.row();
        selCharSkin = new SelectBox(skinLibgdx);
        Gdx.app.log("******MyTag", ""+CharacterSkin.values());
        selCharSkin.setItems(CharacterSkin.values());
        selCharSkin.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCharSkinSelected(((SelectBox)actor).getSelectedIndex());
            }
        });
        tbl.add(selCharSkin).width(120).padRight(20);
        imgCharSkin = new Image(Assets.instance.hero.hero);
        tbl.add(imgCharSkin).width(50).height(50);

        return tbl;
    }

    private Table buildOptWinDebug() {
        Table tbl = new Table();
        tbl.pad(10,10,0,10);
        tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        chkShowFpsCounter = new CheckBox("", skinLibgdx);
        tbl.add(new Label("Show FPS Counter", skinLibgdx));
        tbl.add(chkShowFpsCounter);
        tbl.row();
        
        return tbl;
    }

    private Table buildOptWinButtons() {
        Table tbl = new Table();
        Label lbl = null;
        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        tbl.row();

        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.5f, 0.5f, 0.5f, 1);
        lbl.setStyle(new LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        tbl.row();

        btnWinOptSave = new TextButton("Save", skinLibgdx);
        tbl.add(btnWinOptSave).padRight(30);
        btnWinOptSave.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onSaveClicked();
            }
        });

        btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
        tbl.add(btnWinOptCancel);
        btnWinOptCancel.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });

        return tbl;
    }
}
