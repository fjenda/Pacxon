package lab.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lab.App;
import lab.enums.GameState;

import java.io.IOException;

public class ControllerHandler {
    private final App app;
    private final GameController gameController;
    private final MenuController menuController;
    private final EndController endController;
    private final LevelSelectionController levelSelectionController;
    private final Stage primaryStage;
    private String name;
    private String level;

    public ControllerHandler(App app, Stage primaryStage) throws IOException {
        this.app = app;
        this.primaryStage = primaryStage;

        FXMLLoader menuLoader = new FXMLLoader(App.class.getResource("StartScreen.fxml"));
        AnchorPane menuRoot = menuLoader.load();
        menuController = menuLoader.getController();
        menuController.load(new Scene(menuRoot), this);

        FXMLLoader levelsLoader = new FXMLLoader(App.class.getResource("LevelSelection.fxml"));
        AnchorPane levelsRoot = levelsLoader.load();
        levelSelectionController = levelsLoader.getController();
        levelSelectionController.load(new Scene(levelsRoot), this);

        FXMLLoader gameLoader = new FXMLLoader(App.class.getResource("GameView.fxml"));
        BorderPane gameRoot = gameLoader.load();
        gameController = gameLoader.getController();
        gameController.load(new Scene(gameRoot), this);

        FXMLLoader endLoader = new FXMLLoader(App.class.getResource("EndScreen.fxml"));
        AnchorPane endRoot = endLoader.load();
        endController = endLoader.getController();
        endController.load(new Scene(endRoot), this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.endController.setScore(score);
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void changeScene(GameState scene) {
        switch (scene) {
            case MENU -> {
                primaryStage.setScene(menuController.getScene());
            }

            case LEVELS -> {
                primaryStage.setScene(levelSelectionController.getScene());
            }

            case GAME -> {
                primaryStage.setScene(gameController.getScene());
                gameController.startGame(name, level);
            }

            case END -> {
                primaryStage.setScene(endController.getScene());
            }

        }

        primaryStage.centerOnScreen();
    }

    public void exit() {
        primaryStage.close();
    }

    public GameController getGameController() {
        return gameController;
    }

    public MenuController getMenuController() {
        return menuController;
    }

    public EndController getEndController() {
        return endController;
    }

    public LevelSelectionController getLevelSelectionController() {
        return levelSelectionController;
    }
}
