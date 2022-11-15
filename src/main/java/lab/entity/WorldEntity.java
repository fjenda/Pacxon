package lab.entity;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import lab.interfaces.Collisionable;
import lab.interfaces.DrawableSimulable;
import lab.enviroment.Game;

public abstract class WorldEntity implements DrawableSimulable {
    protected Game game;
    protected Point2D position;
    protected final Point2D startPosition;
    protected final Point2D size;
    protected Point2D centerPoint;

    public WorldEntity(Game game, Point2D position, Point2D size) {
        this.game = game;
        this.position = position;
        this.startPosition = position;
        this.size = size;
    }

    public Point2D getCenterPoint() {
        return this.centerPoint;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        drawInternal(gc);
        this.centerPoint = new Point2D(position.getX() + (size.getX() / 2), position.getY() + (size.getY() / 2));

        gc.restore();
    }
    public abstract void drawInternal(GraphicsContext gc);
}
