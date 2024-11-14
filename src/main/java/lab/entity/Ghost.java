package lab.entity;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lab.enums.BlockState;
import lab.enums.Direction;
import lab.enums.GhostTexture;
import lab.enviroment.Enviroment;
import lab.enviroment.Game;
import lab.enviroment.GridBlock;
import lab.interfaces.Collisionable;

import java.util.*;

import static lab.Constants.*;
import static lab.enums.Direction.*;

public class Ghost extends WorldEntity implements Collisionable {
    private final Image[] textures = new Image[]{ BLINKY_SPRITE, INKY_SPRITE, PINKY_SPRITE, CLYDE_SPRITE, SCARED_SPRITE };
    private Point2D speed;
    private final GhostTexture texture;
    private Direction direction = RIGHT;
    private Point2D above, under, left, right;
    private long switchCooldown = 0L;
    private long currentTime;
    private boolean readyToDecide = false;
    private boolean isSpawned = false;
    Random rnd = new Random();

    //textureIndex - 0 - 3, 0 - blinky / 1 - inky / 2 - pinky / 3 - clyde
    public Ghost(Game game, Point2D position, GhostTexture texture) {
        super(game, position, new Point2D(20, 20));

        this.texture = texture;
        this.isSpawned = true;

        switch (this.texture) {
            case BLINKY -> this.speed = new Point2D(30, 30);    //Blinky - Red - Slow, breaks blocks
            case INKY -> {
                this.speed = new Point2D(0, 0);                 //Inky - Blue - Hides in walls
                this.isSpawned = false;
            }
            case PINKY -> this.speed = new Point2D(75, 75);     //Pinky - Pink - Fast
            case CLYDE -> this.speed = new Point2D(30, 0);      //Clyde - Orange - Moves around walls
            default -> throw new IllegalStateException("Unexpected value: " + this.texture);
        }
    }

    public void drawInternal(GraphicsContext gc) {
        gc.save();

        if (game.getPacman().isPowered()) {
            gc.drawImage(textures[4], position.getX(), position.getY(), size.getX(), size.getY());
        } else {
            gc.drawImage(textures[texture.ordinal()], position.getX(), position.getY(), size.getX(), size.getY());
        }

        gc.restore();
    }

    public void resetPosition() {
        List<GridBlock> points = game.getGrid().getBlocks().stream().filter(b -> b instanceof GridBlock).map(b -> (GridBlock) b).toList();
        GridBlock gridBlock = points.get(rnd.nextInt(points.size()));

        switch (texture) {
            case PINKY, BLINKY, CLYDE -> {
                if (gridBlock.getState().equals(BlockState.EMPTY)) {
                    this.position = gridBlock.getPosition();
                } else {
                    resetPosition();
                }
            }
            case INKY -> {
                if (gridBlock.getState().equals(BlockState.FILLED)) {
                    this.position = gridBlock.getPosition();
                } else {
                    resetPosition();
                }
            }
        }
    }

    public void getNeighbours() {
        above = new Point2D(centerPoint.getX(), centerPoint.getY() - 10);
        under = new Point2D(centerPoint.getX(), centerPoint.getY() + 10);
        left = new Point2D(centerPoint.getX() - 10, centerPoint.getY());
        right = new Point2D(centerPoint.getX() + 10, centerPoint.getY());
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
        if (isSpawned) {
            return;
        }

        Random rnd = new Random();
        Enviroment env = game.getGrid().getBlocks().get(rnd.nextInt(game.getGrid().getBlocks().size()));
        if (env instanceof GridBlock block && block.getState().equals(BlockState.FILLED)) {
            this.position = block.getPosition();
            this.speed = new Point2D(75, 75);
            this.isSpawned = true;
        } else {
            spawnInky();
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
        if (hug()) return;
    }

    public ArrayList<GridBlock> getRadar() {
        Point2D above = new Point2D(centerPoint.getX(), centerPoint.getY() - 15);
        Point2D under = new Point2D(centerPoint.getX(), centerPoint.getY() + 15);
        Point2D left = new Point2D(centerPoint.getX() - 15, centerPoint.getY());
        Point2D right = new Point2D(centerPoint.getX() + 15, centerPoint.getY());

        // 0 - above / 1 - under / 2 - left / 3 - right
        ArrayList<GridBlock> blocks = new ArrayList<>(4);
        blocks.add(null);
        blocks.add(null);
        blocks.add(null);
        blocks.add(null);

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

    private void correctPosition() {
        if (this.direction.equals(Direction.UP)) {
            this.position = new Point2D(this.position.getX(), this.position.getY() + 1);
        } else if (this.direction.equals(Direction.DOWN)) {
            this.position = new Point2D(this.position.getX(), this.position.getY() - 1);
        } else if (this.direction.equals(Direction.LEFT)) {
            this.position = new Point2D(this.position.getX() + 1, this.position.getY());
        } else if (this.direction.equals(Direction.RIGHT)) {
            this.position = new Point2D(this.position.getX() - 1, this.position.getY());
        }
    }

    private boolean hug() {
        // 0 - above / 1 - under / 2 - left / 3 - right
        ArrayList<GridBlock> blocks = getRadar();

        if (direction == LEFT || direction == RIGHT) {
            if (blocks.get(0).getState().equals(BlockState.EMPTY) && (blocks.get(1).getState().equals(BlockState.WALL) || blocks.get(1).getState().equals(BlockState.FILLED))) {
                this.speed = new Point2D(0, -30);
                correctPosition();
                this.direction = UP;
                return true;
            }

            if ((blocks.get(0).getState().equals(BlockState.WALL) || blocks.get(0).getState().equals(BlockState.FILLED)) && blocks.get(1).getState().equals(BlockState.EMPTY)) {
                this.speed = new Point2D(0, 30);
                correctPosition();
                this.direction = DOWN;
                return true;
            }
        } else if (direction == UP || direction == DOWN) {
            if (blocks.get(2).getState().equals(BlockState.EMPTY) && (blocks.get(3).getState().equals(BlockState.WALL) || blocks.get(3).getState().equals(BlockState.FILLED))) {
                this.speed = new Point2D(-30, 0);
                correctPosition();
                this.direction = LEFT;
                return true;
            }

            if ((blocks.get(2).getState().equals(BlockState.WALL) || blocks.get(2).getState().equals(BlockState.FILLED)) && blocks.get(3).getState().equals(BlockState.EMPTY)) {
                this.speed = new Point2D(30, 0);
                correctPosition();
                this.direction = RIGHT;
                return true;
            }
        }

        return false;
    }

    private void decide() {
        // 0 - above / 1 - under / 2 - left / 3 - right
        ArrayList<GridBlock> blocks = getRadar();

        switch (direction) {
            case LEFT -> {
                if (!readyToDecide && blocks.get(0).getState().equals(BlockState.EMPTY) && blocks.get(1).getState().equals(BlockState.EMPTY)) {
                    readyToDecide = true;
                }
                if (readyToDecide) {
                    this.speed = new Point2D(0, -30);
                    this.direction = UP;
                    readyToDecide = false;
                }
            }
            case RIGHT -> {
                if (!readyToDecide && blocks.get(0).getState().equals(BlockState.EMPTY) && blocks.get(1).getState().equals(BlockState.EMPTY)) {
                    readyToDecide = true;
                }
                if (readyToDecide) {
                    this.speed = new Point2D(0, 30);
                    this.direction = DOWN;
                    readyToDecide = false;
                }
            }
            case UP -> {
                if (!readyToDecide && blocks.get(2).getState().equals(BlockState.EMPTY) && blocks.get(3).getState().equals(BlockState.EMPTY)) {
                    readyToDecide = true;
                }
                if (readyToDecide) {
                    this.speed = new Point2D(30, 0);
                    this.direction = RIGHT;
                    readyToDecide = false;
                }
            }
            case DOWN -> {
                if (!readyToDecide && blocks.get(2).getState().equals(BlockState.EMPTY) && blocks.get(3).getState().equals(BlockState.EMPTY)) {
                    readyToDecide = true;
                }
                if (readyToDecide) {
                    this.speed = new Point2D(-30, 0);
                    this.direction = LEFT;
                    readyToDecide = false;
                }
            }
        }
    }

    private void checkBlinkyCollision(GridBlock block) {
        if (texture.equals(GhostTexture.BLINKY) && block.getState().equals(BlockState.FILLED)) {
            if (rnd.nextInt(1, 10) < 4) {
                block.setState(BlockState.EMPTY);
            }
        }
    }

    private boolean bounce(GridBlock block) {
        ArrayList<GridBlock> blocks = getRadar();

        if (block.getBoundingBox().contains(above)) {
            checkBlinkyCollision(block);
            this.position = new Point2D(this.position.getX(), block.getBoundingBox().getMaxY());
            this.speed = new Point2D(speed.getX(), -speed.getY());
            calculateDirection();

            return true;
        } else if (block.getBoundingBox().contains(under)) {
            checkBlinkyCollision(block);
            this.position = new Point2D(this.position.getX(), block.getBoundingBox().getMinY() - this.size.getY());
            this.speed = new Point2D(speed.getX(), -speed.getY());
            calculateDirection();

            return true;
        } else if (block.getBoundingBox().contains(left)) {
            checkBlinkyCollision(block);
            this.position = new Point2D(block.getBoundingBox().getMaxX(), this.position.getY());
            this.speed = new Point2D(-speed.getX(), speed.getY());
            calculateDirection();

            return true;
        } else if (block.getBoundingBox().contains(right)) {
            checkBlinkyCollision(block);
            this.position = new Point2D(block.getBoundingBox().getMinX() - this.size.getX(), this.position.getY());
            this.speed = new Point2D(-speed.getX(), speed.getY());
            calculateDirection();

            return true;
        }

        return false;
    }

    public void simulate(double deltaT) {
        position = position.add(speed.multiply(deltaT));
        centerPoint = new Point2D(position.getX() + (size.getX() / 2), position.getY() + (size.getY() / 2));

        Point2D tmp = new Point2D(Math.abs(position.getX() - previousPosition.getX()), Math.abs(position.getY() - previousPosition.getY()));

        if (texture.equals(GhostTexture.CLYDE) && (tmp.getX() > 20 || tmp.getY() > 20)) {
            previousPosition = position;
            decide();
        }
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
