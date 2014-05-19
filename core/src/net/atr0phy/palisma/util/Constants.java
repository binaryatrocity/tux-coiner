package net.atr0phy.palisma.util;

public class Constants {

    // Visible viewport for the player camera
    public static final float VIEWPORT_WIDTH = 5.0f;
    public static final float VIEWPORT_HEIGHT = 5.0f;

    // Interface with (UI)
    public static final float VIEWPORT_GUI_WIDTH = 800.0f;
    public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

    // Our sprite sheet descriptor file
    public static final String TEXTURE_ATLAS_OBJECTS = "palisma.pack";
    public static final String TEXTURE_ATLAS_UI = "palisma-ui.pack";
    public static final String TEXTURE_ATLAS_LIBGDX_UI = "uiskin.atlas";

    // Skin descriptor files
    public static final String SKIN_LIBGDX_UI = "uiskin.json";
    public static final String SKIN_PALISMA_UI = "palisma-ui.json";

    // Location of level definitions
    public static final String LEVEL_01 = "levels/level-01.png";

    // Preference/Options file
    public static final String PREFERENCES = "palisma.prefs";

    
    /* START GAME VARIABLES */

    public static final int LIVES_START = 3; // Lives a player starts teh game with
    public static final float ITEM_DISK_POWERUP_DURATION = 9; // Seconds a powerup lasts
    public static final float TIME_DELAY_GAME_OVER = 3; // Delay before game over
}
