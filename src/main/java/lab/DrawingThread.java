package lab;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import lab.enviroment.Game;

public class DrawingThread extends AnimationTimer {
	private final GraphicsContext gc;
	private final Game game;
	private long lastTime = -1;

	public DrawingThread(Canvas canvas, Game game) {
		this.gc = canvas.getGraphicsContext2D();
		this.game = game;
	}

	@Override
	public void handle(long now) {
		game.draw(gc);

		if (lastTime > 0) {
			double deltaT = (now - lastTime) / 1e9;
			game.simulate(deltaT);
		}

		lastTime = now;
	}

}
