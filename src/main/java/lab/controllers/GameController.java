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
import lab.interfaces.GameListener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;


public class GameController {
    private Game game;
    private ControllerHandler controllerHandler;
    @FXML private Canvas canvas;
    private AnimationTimer animationTimer;
    private Scene scene;
    private List<Score> scoresList = new LinkedList<>();

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

        game.setGameListener(new GameListener() {
            @Override
            public void stateChanged(int score) {
                controllerHandler.setScore(new Score(name, score));
            }

            @Override
            public void gameOver() {
                stopGame();
            }
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
        try (PrintWriter pw = new PrintWriter(new FileWriter("scores.csv"))) {
            for (Score score : this.scoresList) {
                pw.printf("%s;%d", score.getName(), score.getAmount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopGame() {
        animationTimer.stop();
        saveScore();
        controllerHandler.changeScene(GameState.END);
    }

}
