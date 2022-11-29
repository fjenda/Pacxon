package lab.entity;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import lab.interfaces.DrawableSimulable;
import lab.enviroment.Game;

public abstract class WorldEntity implements DrawableSimulable {
    protected Game game;
    protected Point2D position;
    protected Point2D previousPosition;
    protected final Point2D startPosition;
    protected final Point2D size;
    protected Point2D centerPoint;

    public WorldEntity(Game game, Point2D position, Point2D size) {
        this.game = game;
        this.position = position;
        this.startPosition = position;
        this.previousPosition = position;
        this.size = size;
    }

    public Point2D getCenterPoint() {
        return this.centerPoint;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        this.centerPoint = new Point2D(position.getX() + (size.getX() / 2), position.getY() + (size.getY() / 2));
        drawInternal(gc);

        gc.restore();
    }
    public abstract void drawInternal(GraphicsContext gc);
}
