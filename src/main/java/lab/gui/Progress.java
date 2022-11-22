package lab.gui;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lab.enviroment.Game;

public class Progress extends Interface {
    private final Point2D position;

    public Progress(Game game) {
        super(game, 0);

        this.position = new Point2D(game.getWidth() - 120, 37);
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.save();

        gc.setFont(Font.font("emulogic", 15));
        gc.setFill(Color.YELLOW);
        gc.fillText("Progress:", position.getX() - 140, position.getY());
        gc.setFill(Color.WHITE);
        gc.fillText("" + amount, position.getX(), position.getY());
        gc.fillText("/ 80%", position.getX() + 35, position.getY());

        gc.restore();
    }

    @Override
    public void updateInternal(int amount) {
        this.amount = amount;
        //System.out.println("UPDATING SCORE " + this.amount);
    }
}
