package lab.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import lab.App;
import lab.enums.GameState;

public class MenuController {
    private ControllerHandler controllerHandler;
    private Scene scene;
    @FXML private Canvas canvas;
    @FXML private TextField nameField;

    public void load(Scene scene, ControllerHandler controllerHandler) {
        this.controllerHandler = controllerHandler;
        this.scene = scene;
        this.scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    @FXML
    public void startGame() {
        controllerHandler.setName(nameField.getText());
        controllerHandler.changeScene(GameState.LEVELS);
    }

    @FXML
    public void exitGame() {
        controllerHandler.exit();
    }
}
