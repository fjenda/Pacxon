package lab.entity;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import lab.enums.BlockState;
import lab.enums.Direction;
import lab.enums.GhostTexture;
import lab.enviroment.Game;
import lab.enviroment.Grid;
import lab.enviroment.GridBlock;
import lab.interfaces.Collisionable;

import static lab.Constants.*;
import static lab.enums.Direction.*;

public class Ghost extends WorldEntity implements Collisionable {
    private final Image[] textures = new Image[]{ BLINKY_SPRITE, INKY_SPRITE, PINKY_SPRITE, CLYDE_SPRITE };
    private Point2D speed;
    private final GhostTexture texture;
    private Direction direction = DOWN;
    private Point2D above, under, left, right;
    private long switchCooldown = 0L;
    private long currentTime = 0L;

    //textureIndex - 0 - 3, 0 - blinky / 1 - inky / 2 - pinky / 3 - clyde
    public Ghost(Game game, Point2D position, GhostTexture texture) {
        super(game, position, new Point2D(20, 20));

        this.texture = texture;

        switch (this.texture) {
            case BLINKY -> this.speed = new Point2D(50, 50);    //Blinky - Red - Slow, breaks blocks
            case INKY -> this.speed = new Point2D(5, 5);        //Inky - Blue - Hides in walls
            case PINKY -> this.speed = new Point2D(100, 100);   //Pinky - Pink - Fast
            case CLYDE -> this.speed = new Point2D(50, 0);      //Clyde - Orange - Moves around walls
            default -> throw new IllegalStateException("Unexpected value: " + this.texture);
        }
    }

    public void drawInternal(GraphicsContext gc) {
        gc.save();

        gc.setFill(Color.RED);

        gc.drawImage(textures[texture.ordinal()], position.getX(), position.getY(), size.getX(), size.getY());


//        double posX = 0;
//        double posY = 0;
//        for (GridBlock block : game.getGrid().getBlocks()) {
//            if (block.getBoundingBox().contains(centerPoint)) {
//                posX = block.getBoundingBox().getMinX();
//                posY = block.getBoundingBox().getMinY();
//            }
//        }
//        gc.fillRect(posX, posY, size.getX(), size.getY());


        gc.restore();
    }

    public void resetPosition() {
        this.position = startPosition;
    }

    public void getNeighbours() {
        above = new Point2D(centerPoint.getX(), centerPoint.getY() - 10);
        under = new Point2D(centerPoint.getX(), centerPoint.getY() + 10);
        left = new Point2D(centerPoint.getX() - 10, centerPoint.getY());
        right = new Point2D(centerPoint.getX() + 10, centerPoint.getY());
    }
    @Override
    public void hit(Grid grid) {
        currentTime = System.currentTimeMillis();
        if (currentTime - switchCooldown < 100) {
            return;
        }
        switchCooldown = currentTime;

        switch (texture) {
            case BLINKY -> hitBlinky(grid);
            case INKY -> hitInky(grid);
            case PINKY -> hitPinky(grid);
            case CLYDE -> hitClyde(grid);
        }
    }
    public void spawnInky(Grid grid) {
        if (!texture.equals(GhostTexture.INKY)) {
            return;
        }

        //System.out.println("SPAWN INKY");
        for (GridBlock block : grid.getBlocks()) {
            if (block.getPosition().getX() >= 20 && block.getPosition().getY() >= 70 && block.getPosition().getX() <= game.getWidth() - 20 && block.getPosition().getY() <= game.getHeight() - 20) {
                if (block.getState().equals(BlockState.FILLED)) {
                    this.position = block.getPosition();
                }
            }
        }
    }
    public void hitPinky(Grid grid) {
        getNeighbours();
        for (GridBlock block : grid.getBlocks()) {
            if (block.getState().equals(BlockState.FILLED) || block.getState().equals(BlockState.WALL)) {
                if (bounce(block)) return;
            }
        }
    }
    public void hitBlinky(Grid grid) {
        getNeighbours();
        for (GridBlock block : grid.getBlocks()) {
            if (block.getState().equals(BlockState.FILLED) || block.getState().equals(BlockState.WALL)) {
                if (bounce(block)) return;
            }
        }
    }
    public void hitInky(Grid grid) {
        getNeighbours();
        for (GridBlock block : grid.getBlocks()) {
            if (block.getState().equals(BlockState.EMPTY)) {
                if (bounce(block)) return;
            }
        }
    }
    private boolean bounce(GridBlock block) {
        if (block.getBoundingBox().contains(above) || block.getBoundingBox().contains(under)) {
            if (texture.equals(GhostTexture.BLINKY) && block.getState().equals(BlockState.FILLED)) {
                block.setState(BlockState.EMPTY);
            }

            speed = new Point2D(speed.getX(), -speed.getY());
            calculateDirection();
            return true;
        } else if (block.getBoundingBox().contains(left) || block.getBoundingBox().contains(right)) {
            if (texture.equals(GhostTexture.BLINKY) && block.getState().equals(BlockState.FILLED)) {
                block.setState(BlockState.EMPTY);
            }

            speed = new Point2D(-speed.getX(), speed.getY());
            calculateDirection();
            return true;
        }
        return false;
    }

    public void hitClyde(Grid grid) {
        getNeighbours();
        // CLYDE ALGORITHM

    }
    public void simulate(double deltaT) {
        position = position.add(speed.multiply(deltaT));
        this.centerPoint = new Point2D(position.getX() + (size.getX() / 2), position.getY() + (size.getY() / 2));
    }

    public Point2D getPosition() {
        return position;
    }

    public Point2D getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public void calculateDirection() {
        if (speed.equals(new Point2D(100, 100))) {
            this.direction = DOWN;
        } else if (speed.equals(new Point2D(100, -100))) {
           this.direction = RIGHT;
        } else if (speed.equals(new Point2D(-100, 100))) {
            this.direction = LEFT;
        } else if (speed.equals(new Point2D(-100, -100))) {
            this.direction = UP;
        }
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(position.getX(), position.getY(), size.getX(), size.getY());
    }


}
