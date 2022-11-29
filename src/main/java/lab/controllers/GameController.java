package lab.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import lab.DrawingThread;
import lab.enums.Direction;
import lab.enums.GameState;
import lab.enviroment.Game;
import lab.gui.Score;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class GameController {
    private Game game;
    private ControllerHandler controllerHandler;
    @FXML private Canvas canvas;
    private AnimationTimer animationTimer;
    private Scene scene;
    private final List<Score> scoresList = new LinkedList<>();

    public void load(Scene scene, ControllerHandler controllerHandler) {
        this.scene = scene;
        this.controllerHandler = controllerHandler;
        scene.addEventHandler(KeyEvent.ANY, (this::fire));
    }

    public Scene getScene() {
        return scene;
    }

    public Game getGame() {
        return game;
    }

    public void startGame(String name, String level) {
        this.game = new Game(canvas.getWidth(), canvas.getHeight(), level);
        this.game.setName(name);
        //Draw scene on a separate thread to avoid blocking UI.
        animationTimer = new DrawingThread(canvas, game);
        animationTimer.start();

        game.setGameListener(() -> {
            controllerHandler.setScore((Score) game.getPacman().getScore());
            stopGame();
        });
    }

    public void fire(KeyEvent keyEvent) {
        //if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
        //    System.out.println(keyEvent.getEventType().getName() + " - " + keyEvent.getCharacter());
        //}

        switch (keyEvent.getCode()) {
            case A, LEFT -> game.getPacman().move(Direction.LEFT);
            case S, DOWN -> game.getPacman().move(Direction.DOWN);
            case D, RIGHT -> game.getPacman().move(Direction.RIGHT);
            case W, UP -> game.getPacman().move(Direction.UP);
            case F1 -> game.getGrid().showGrid();
            case F2 -> game.getPacman().getHealth().update(0);
            case F3 -> game.getPacman().getProgress().update(80);
            case ESCAPE -> stopGame();
        }
    }

    private void saveScore() {
        loadScores();

        String name = game.getName();
        int scoreVal = game.getPacman().getScore().getAmount();
        scoresList.add(new Score(name, scoreVal));

        try (PrintWriter pw = new PrintWriter(new FileWriter("scores.csv"))) {
            for (Score score : this.scoresList) {
                pw.printf("%s;%d\n", score.getName(), score.getAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScores() {
        scoresList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader("scores.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                scoresList.add(new Score(split[0], Integer.parseInt(split[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopGame() {
        saveScore();
        animationTimer.stop();
        controllerHandler.changeScene(GameState.END);
    }

}
