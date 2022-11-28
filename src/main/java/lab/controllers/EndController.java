package lab.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lab.App;
import lab.gui.Score;

public class EndController {
    private ControllerHandler controllerHandler;
    private Scene scene;
    @FXML private Canvas canvas;
    @FXML private Label score;
    @FXML private Button exitButton;

    public void load(Scene scene, ControllerHandler controllerHandler) {
        this.controllerHandler = controllerHandler;
        this.scene = scene;
        this.scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    public void setScore(Score score) {
        this.score.setText("" + score.getAmount());
    }

    @FXML
    public void exitGame() {
        controllerHandler.exit();
    }
}
