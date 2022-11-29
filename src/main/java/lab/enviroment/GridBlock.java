package lab.enviroment;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lab.enums.BlockState;

import static lab.Constants.*;

public class GridBlock extends Enviroment {
    private BlockState state;
    private Point2D centerPoint;
    private boolean isBlockVisible;
    public GridBlock(Game game, Point2D position) {
        super(game, position, new Point2D(20, 20), false);
        this.state = BlockState.EMPTY;
        this.centerPoint = new Point2D(position.getX() + size.getX() / 2, position.getY() + size.getY() / 2);
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.save();

        switch (state) {
            case WALL, FILLED -> gc.drawImage(BLOCK_SPRITE, position.getX(), position.getY(), size.getX(), size.getY());
            case PATH -> gc.drawImage(BLOCK_TRANSPARENT_SPRITE, position.getX(), position.getY(), size.getX(), size.getY());
            case EMPTY -> {
                if (isBlockVisible) {
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(position.getX(), position.getY(), size.getX(), size.getY());
                }
            }
        }

        this.centerPoint = new Point2D(position.getX() + size.getX() / 2, position.getY() + size.getY() / 2);

        gc.restore();
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(position.getX(), position.getY(), size.getX(), size.getY());
    }

    public Point2D getPosition() {
        return this.position;
    }

    public BlockState getState() {
        return state;
    }

    public Point2D getCenterPoint() {
        return centerPoint;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setBlockVisible(boolean blockVisible) {
        this.isBlockVisible = blockVisible;
    }
}
