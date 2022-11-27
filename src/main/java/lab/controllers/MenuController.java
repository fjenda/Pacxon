package lab.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lab.App;
import lab.ScoreComparator;
import lab.enums.GameState;
import lab.gui.Score;

import java.io.*;
import java.util.*;

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
    @FXML private TableView<Score> scoreTableView;
    private List<Score> scoresList = new LinkedList<>();

    public void load(Scene scene, ControllerHandler controllerHandler) {
        this.controllerHandler = controllerHandler;
        this.scene = scene;
        this.scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());

        this.scoreListView.visibleProperty().set(false);
        this.backButton.visibleProperty().set(false);
        this.scoreTableView.visibleProperty().set(false);

        TableColumn<Score, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setPrefWidth(127.5);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Score, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setPrefWidth(127.5);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        this.scoreTableView.getColumns().add(nameColumn);
        this.scoreTableView.getColumns().add(scoreColumn);
        loadScores();
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
        this.scoreTableView.visibleProperty().set(true);
    }

    @FXML
    public void backToSelect() {
        this.nameField.visibleProperty().set(true);
        this.selectLevelButton.visibleProperty().set(true);
        this.leaderboardsButton.visibleProperty().set(true);
        this.exitButton.visibleProperty().set(true);

        this.scoreListView.visibleProperty().set(false);
        this.backButton.visibleProperty().set(false);
        this.scoreTableView.visibleProperty().set(false);
    }

    private void sortScores() {
        Set<Score> tempScores = new HashSet<>(scoresList);
        scoresList.clear();
        scoresList.addAll(tempScores);
        scoresList.sort(Comparator.comparingInt(Score::getAmount).reversed());
    }
    private void loadScores() {
        scoresList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader("scores.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                scoresList.add(new Score(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sortScores();
        this.scoreTableView.getItems().addAll(scoresList);
    }

    private void saveScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("scores.csv"))) {
            for (Score score : this.scoresList) {
                pw.printf("%s;%d", score.getName(), score.getAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitGame() {
        controllerHandler.exit();
    }

    public ListView<Score> getScoreListView() {
        return scoreListView;
    }

}
