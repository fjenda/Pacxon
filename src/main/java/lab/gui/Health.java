package lab.gui;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lab.enviroment.Game;

import static lab.Constants.HEART_SPRITE;

public class Health extends Interface {
    private final Point2D position;
    private final Point2D size;

    public Health(Game game) {
        super(game, 3);

        this.size = new Point2D(40, 40);
        this.position = new Point2D(5, 5);
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.save();

        gc.setFont(Font.font("emulogic", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("" + amount, position.getX() + size.getX(), position.getY() + 30);
        gc.drawImage(HEART_SPRITE, position.getX(), position.getY(), size.getX(), size.getY());

        gc.restore();
    }

    @Override
    public void updateInternal(int amount) {
        this.amount += amount;
    }
}
