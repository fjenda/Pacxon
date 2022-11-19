package lab.enviroment;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import lab.interfaces.Drawable;

public abstract class Enviroment implements Drawable {
    protected final Game game;
    protected final Point2D position;
    protected final Point2D size;
    protected final boolean horizontal;

    public Enviroment(Game game, Point2D position, Point2D size, boolean horizontal) {
        this.game = game;
        this.position = position;
        this.size = size;
        this.horizontal = horizontal;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        drawInternal(gc);

        gc.restore();
    }

    public abstract void drawInternal(GraphicsContext gc);
}
