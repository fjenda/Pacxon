package lab.gui;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lab.enviroment.Game;

public class Score extends Interface {
    private final Point2D position;
    private final String name;

    public Score(Game game, String name) {
        super(game, 0);

        this.position = new Point2D(game.getWidth() / 2 - 50, 37);
        this.name = name;
    }

    public Score(String name, int amount) {
        super(null, amount);
        this.position = null;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " - " + amount;
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.save();

        gc.setFont(Font.font("emulogic", 15));
        gc.setFill(Color.YELLOW);
        gc.fillText("Score:", position.getX() - 100, position.getY());
        gc.setFill(Color.WHITE);
        gc.fillText("" + amount, position.getX(), position.getY());

        gc.restore();
    }

    @Override
    public void updateInternal(int amount) {
        this.amount = amount;
    }
}
