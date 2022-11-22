package lab.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lab.App;
import lab.enums.GameState;
import lab.gui.Score;

public class MenuController {
    private ControllerHandler controllerHandler;
    private Scene scene;
    @FXML private Canvas canvas;
    @FXML private TextField nameField;
    @FXML private Button selectLevelButton;
    @FXML private Button exitButton;
    @FXML private Button leaderboardsButton;
    @FXML private Button backButton;
    @FXML private ListView<Score> scoreListView;

    public void load(Scene scene, ControllerHandler controllerHandler) {
        this.controllerHandler = controllerHandler;
        this.scene = scene;
        this.scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());

        this.scoreListView.visibleProperty().set(false);
        this.backButton.visibleProperty().set(false);

        this.scoreListView.getItems().add(new Score("Pacxon", 100));
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
    public void showLeaderboards() {
        this.nameField.visibleProperty().set(false);
        this.selectLevelButton.visibleProperty().set(false);
        this.leaderboardsButton.visibleProperty().set(false);
        this.exitButton.visibleProperty().set(false);

        this.scoreListView.visibleProperty().set(true);
        this.backButton.visibleProperty().set(true);
    }

    @FXML
    public void backToSelect() {
        this.nameField.visibleProperty().set(true);
        this.selectLevelButton.visibleProperty().set(true);
        this.leaderboardsButton.visibleProperty().set(true);
        this.exitButton.visibleProperty().set(true);

        this.scoreListView.visibleProperty().set(false);
        this.backButton.visibleProperty().set(false);
    }

    @FXML
    public void exitGame() {
        controllerHandler.exit();
    }

    public ListView<Score> getScoreListView() {
        return scoreListView;
    }

}
