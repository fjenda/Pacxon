package lab.entity;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lab.enums.BlockState;
import lab.enums.Direction;
import lab.enums.GhostTexture;
import lab.enviroment.Enviroment;
import lab.enviroment.Game;
import lab.enviroment.Grid;
import lab.enviroment.GridBlock;
import lab.interfaces.Collisionable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static lab.Constants.*;
import static lab.enums.Direction.*;

public class Ghost extends WorldEntity implements Collisionable {
    private final Image[] textures = new Image[]{ BLINKY_SPRITE, INKY_SPRITE, PINKY_SPRITE, CLYDE_SPRITE };
    private Point2D speed;
    private final Point2D defaultSpeed;
    private final GhostTexture texture;
    private Direction direction = RIGHT;
    private Point2D above, under, left, right;
    private long switchCooldown = 0L;
    private long currentTime = 0L;
    Random rnd = new Random();

    //textureIndex - 0 - 3, 0 - blinky / 1 - inky / 2 - pinky / 3 - clyde
    public Ghost(Game game, Point2D position, GhostTexture texture) {
        super(game, position, new Point2D(20, 20));

        this.texture = texture;

        switch (this.texture) {
            case BLINKY -> this.speed = new Point2D(50, 50);    //Blinky - Red - Slow, breaks blocks
            case INKY -> this.speed = new Point2D(0, 0);    //Inky - Blue - Hides in walls
            case PINKY -> this.speed = new Point2D(100, 100);   //Pinky - Pink - Fast
            case CLYDE -> this.speed = new Point2D(50, 0);      //Clyde - Orange - Moves around walls
            default -> throw new IllegalStateException("Unexpected value: " + this.texture);
        }

        this.defaultSpeed = new Point2D(this.speed.getX(), this.speed.getX());
    }

    public void drawInternal(GraphicsContext gc) {
        gc.save();

        gc.setFill(Color.RED);

        gc.drawImage(textures[texture.ordinal()], position.getX(), position.getY(), size.getX(), size.getY());

        gc.restore();
    }

    public void resetPosition() {
        this.position = startPosition;
    }

    public void getNeighbours() {
        above = new Point2D(centerPoint.getX(), centerPoint.getY() - 11);
        under = new Point2D(centerPoint.getX(), centerPoint.getY() + 11);
        left = new Point2D(centerPoint.getX() - 11, centerPoint.getY());
        right = new Point2D(centerPoint.getX() + 11, centerPoint.getY());
    }
    @Override
    public void hit() {
        currentTime = System.currentTimeMillis();
        if (currentTime - switchCooldown < 50) {
            return;
        }
        switchCooldown = currentTime;

        switch (texture) {
            case BLINKY -> hitBlinky();
            case PINKY -> hitPinky();
            case CLYDE -> hitClyde();
            case INKY -> hitInky();
        }
    }
    public void spawnInky() {
        if (position.getX() >= 20 && position.getX() <= game.getWidth() - 20 && position.getY() >= 70 && position.getY() <= game.getHeight() - 20) {
            return;
        }

        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block && block.getState().equals(BlockState.FILLED)) {
                this.position = block.getPosition();
                this.speed = new Point2D(100, 100);
                return;
            }
        }
    }
    public void hitPinky() {
        getNeighbours();
        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block && (block.getState().equals(BlockState.FILLED) || block.getState().equals(BlockState.WALL))) {
                if (bounce(block)) return;
            }
        }
    }
    public void hitBlinky() {
        getNeighbours();
        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block && (block.getState().equals(BlockState.FILLED) || block.getState().equals(BlockState.WALL))) {
                if (bounce(block)) return;
            }
        }
    }
    public void hitInky() {
        getNeighbours();
        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block && (block.getState().equals(BlockState.EMPTY) || block.getState().equals(BlockState.WALL))) {
                if (bounce(block)) return;
            }
        }
    }

    public void hitClyde() {
        getNeighbours();
        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            for (Enviroment enviroment2 : game.getGrid().getBlocks()) {
                if (enviroment instanceof GridBlock block && enviroment2 instanceof GridBlock block2 && block != block2) {
                    if (hug(block, block2)) return;
                }
            }
        }
    }

    private ArrayList<GridBlock> getRadar() {
        Point2D above = new Point2D(centerPoint.getX(), centerPoint.getY() - 15);
        Point2D under = new Point2D(centerPoint.getX(), centerPoint.getY() + 15);
        Point2D left = new Point2D(centerPoint.getX() - 15, centerPoint.getY());
        Point2D right = new Point2D(centerPoint.getX() + 15, centerPoint.getY());

        // 0 - above / 1 - under / 2 - left / 3 - right
        ArrayList<GridBlock> blocks = new ArrayList<>();

        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block) {
                if (block.getBoundingBox().contains(above)) {
                    blocks.set(0, block);
                }

                if (block.getBoundingBox().contains(under)) {
                    blocks.set(1, block);
                }

                if (block.getBoundingBox().contains(left)) {
                    blocks.set(2, block);
                }

                if (block.getBoundingBox().contains(right)) {
                    blocks.set(3, block);
                }
            }
        }

        return blocks;
    }

    private boolean hug(GridBlock block, GridBlock block2) {
        // 0 - above / 1 - under / 2 - left / 3 - right
        ArrayList<GridBlock> blocks = getRadar();

        if (direction == LEFT || direction == RIGHT) {
            if (blocks.get(0).getState().equals(BlockState.EMPTY) && blocks.get(1).getState().equals(BlockState.EMPTY)) {
                return true;
            }
        } else if (direction == UP || direction == DOWN) {
            if (blocks.get(2).getState().equals(BlockState.EMPTY) && blocks.get(3).getState().equals(BlockState.EMPTY)) {
                return true;
            }
        }

        return false;
    }
    private boolean bounce(GridBlock block) {
        if (block.getBoundingBox().contains(above) || block.getBoundingBox().contains(under)) {
            if (texture.equals(GhostTexture.BLINKY) && block.getState().equals(BlockState.FILLED)) {
                if (rnd.nextInt(1, 10) < 4) {
                    block.setState(BlockState.EMPTY);
                }
            }

            speed = new Point2D(speed.getX(), -speed.getY());
            calculateDirection();
            return true;

        } else if (block.getBoundingBox().contains(left) || block.getBoundingBox().contains(right)) {
            if (texture.equals(GhostTexture.BLINKY) && block.getState().equals(BlockState.FILLED)) {
                if (rnd.nextInt(1, 10) < 4) {
                    block.setState(BlockState.EMPTY);
                }
            }

            speed = new Point2D(-speed.getX(), speed.getY());
            calculateDirection();
            return true;
        }
        return false;
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
        if (speed.getX() > 0 && speed.getY() > 0) {
            this.direction = DOWN;
        } else if (speed.getX() > 0 && speed.getY() < 0) {
            this.direction = RIGHT;
        } else if (speed.getX() < 0 && speed.getY() > 0) {
            this.direction = LEFT;
        } else if (speed.getX() < 0 && speed.getY() < 0) {
            this.direction = UP;
        }
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(position.getX(), position.getY(), size.getX(), size.getY());
    }

    public GhostTexture getTexture() {
        return texture;
    }

    public Point2D getSpeed() {
        return speed;
    }
}
