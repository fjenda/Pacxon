package lab.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import lab.App;
import lab.enums.GameState;

import static lab.Constants.*;

public class LevelSelectionController {
    private ControllerHandler controllerHandler;
    private Scene scene;

    public void load(Scene scene, ControllerHandler controllerHandler) {
        this.controllerHandler = controllerHandler;
        this.scene = scene;
        this.scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    @FXML
    public void select() {
        controllerHandler.changeScene(GameState.GAME);
    }

    @FXML
    private void select1() {
        controllerHandler.setLevel(LEVEL1);
        select();
    }

    @FXML
    private void select2() {
        controllerHandler.setLevel(LEVEL2);
        select();
    }

    @FXML
    private void select3() {
        controllerHandler.setLevel(LEVEL3);
        select();
    }

    @FXML
    public void back() {
        controllerHandler.changeScene(GameState.MENU);
        //controllerHandler.exit();
    }
}
