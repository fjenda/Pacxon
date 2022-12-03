package lab;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

public final class Constants {
    private Constants() {}

    public static final Image PACMAN_SPRITE;
    public static final Image PACWOMAN_SPRITE;
    public static final Image PACKID_SPRITE;
    public static final Image BLINKY_SPRITE;
    public static final Image INKY_SPRITE;
    public static final Image PINKY_SPRITE;
    public static final Image CLYDE_SPRITE;
    public static final Image SCARED_SPRITE;
    public static final Image BLOCK_SPRITE;
    public static final Image BLOCK_TRANSPARENT_SPRITE;
    public static final Image HEART_SPRITE;
    public static final Image START_SCREEN;
    public static final Image GAME_OVER_SCREEN;
    public static final Image CHERRY_SPRITE;
    public static final String LEVEL1;
    public static final String LEVEL2;
    public static final String LEVEL3;
    public static final String TEST;

    static {
        //LEVELS
        LEVEL1 = "src/main/resources/lab/levels/level1.json";
        LEVEL2 = "src/main/resources/lab/levels/level2.json";
        LEVEL3 = "src/main/resources/lab/levels/level3.json";
        TEST = "src/main/resources/lab/levels/test.json";

        //PACMAN SPRITES
        PACMAN_SPRITE = new Image(Constants.class.getResourceAsStream("entity/pacman/pacman.gif"));
        PACWOMAN_SPRITE = new Image(Constants.class.getResourceAsStream("entity/pacman/pacwoman.gif"));
        PACKID_SPRITE = new Image(Constants.class.getResourceAsStream("entity/pacman/packid.gif"));

        //GHOST SPRITES
        BLINKY_SPRITE = new Image(Constants.class.getResourceAsStream("entity/ghosts/blinky.gif"));
        INKY_SPRITE = new Image(Constants.class.getResourceAsStream("entity/ghosts/inky.gif"));
        PINKY_SPRITE = new Image(Constants.class.getResourceAsStream("entity/ghosts/pinky.gif"));
        CLYDE_SPRITE = new Image(Constants.class.getResourceAsStream("entity/ghosts/clyde.gif"));
        SCARED_SPRITE = new Image(Constants.class.getResourceAsStream("entity/ghosts/blueghost.gif"));

        //ENVIROMENT SPRITES
        BLOCK_SPRITE = new Image(Constants.class.getResourceAsStream("enviroment/block.png"));
        BLOCK_TRANSPARENT_SPRITE = new Image(Constants.class.getResourceAsStream("enviroment/block-transparent-3.png"));
        HEART_SPRITE = new Image(Constants.class.getResourceAsStream("gui/heart.png"));

        //BONUS ITEMS
        CHERRY_SPRITE = new Image(Constants.class.getResourceAsStream("enviroment/cherry.png"));

        //SCREEN ITEMS
        START_SCREEN = new Image(Constants.class.getResourceAsStream("gui/start-screen.gif"));
        GAME_OVER_SCREEN = new Image(Constants.class.getResourceAsStream("gui/end-screen.gif"));

        //FONT
        Font.loadFont(Constants.class.getResource("font/emulogic.ttf").toExternalForm(), 20);
        Font.loadFont(Constants.class.getResource("font/pacfont.ttf").toExternalForm(), 50);
    }

}
