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


public class GameController {
    private Game game;
    private ControllerHandler controllerHandler;
    @FXML private Canvas canvas;
    private AnimationTimer animationTimer;
    private Scene scene;

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
            case ESCAPE -> stopGame();
        }
    }

    public void stopGame() {
        animationTimer.stop();
        controllerHandler.setScore(game.getPacman().getScore().toString());
        controllerHandler.changeScene(GameState.END);
    }

}
