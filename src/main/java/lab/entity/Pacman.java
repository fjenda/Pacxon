package lab.entity;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import lab.enums.BlockState;
import lab.enums.Direction;
import lab.enviroment.Enviroment;
import lab.enviroment.GridBlock;
import lab.gui.*;
import lab.interfaces.Collisionable;
import lab.enviroment.Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static lab.Constants.PACMAN_SPRITE;

public class Pacman extends WorldEntity implements Collisionable {
    private final Interface[] gui;
    ArrayList<Enviroment> tmpBlocks = new ArrayList<>();
    private double angle = 180;
    private long switchCooldown = 0L;
    private long currentTime = 0L;

    public Pacman(Game game) {
        super(game, new Point2D(0, 50), new Point2D(20, 18));

        this.gui = new Interface[]{new Health(game), new Score(game, game.getName()), new Progress(game)};
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.save();

        gc.setFill(Color.YELLOW);
        gc.setTransform(new Affine(Affine.rotate(angle, position.getX() + size.getX() / 2, position.getY() + size.getY() / 2)));
        gc.drawImage(PACMAN_SPRITE, position.getX(), position.getY(), size.getX(), size.getY());

        gc.restore();

        for (Interface ui : gui) {
            ui.draw(gc);
        }
    }

    public void resetPosition() {
        for (Enviroment enviroment : tmpBlocks) {
            if (enviroment instanceof GridBlock block && block.getState() == BlockState.PATH) {
                block.setState(BlockState.EMPTY);
            }
        }
        tmpBlocks.clear();

        this.position = new Point2D(0, 50);
    }

    @Override
    public void hit() {
        resetPosition();
        for (Interface inter : gui) {
            if (inter instanceof Health) {
                updateHealth();
                updateScore(-10);
            }
        }
    }

    public void simulate(double deltaT) {
    }

    public Point2D getPosition() {
        return this.position;
    }

    public Point2D getSize() {
        return this.size;
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(position.getX(), position.getY(), size.getX(), size.getY());
    }

    public void move(Direction dir) {
        currentTime = System.currentTimeMillis();
        if (currentTime - switchCooldown < 75) {
            return;
        }
        switchCooldown = currentTime;

        switch (dir) {
            case UP -> {
                angle = 90;
                if (position.getY() > 50) {
                    position = position.add(0, -20);
                }
            }
            case DOWN -> {
                angle = 270;
                if (position.getY() < 490) {
                    position = position.add(0, 20);
                }
            }
            case LEFT -> {
                angle = 0;
                if (position.getX() > 0) {
                    position = position.add(-20, 0);
                }
            }
            case RIGHT -> {
                angle = 180;
                if (position.getX() < 660) {
                    position = position.add(20, 0);
                }
            }
        }

        checkBlocks();
    }

    private void checkBlocks() {
        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block && block.getBoundingBox().contains(centerPoint)) {
                if (block.getState().equals(BlockState.EMPTY)) {
                    block.setState(BlockState.PATH);
                    tmpBlocks.add(block);
                }
                else {
                    if (!tmpBlocks.isEmpty()) {
                        fillBlocks(tmpBlocks);
                    }
                }
            }
        }
    }

    private void floodFill(Enviroment enviroment) {
        if (!(enviroment instanceof GridBlock block)) {
            return;
        }

        Queue<Enviroment> queue = new LinkedList<>();
        queue.add(block);

        if (block.getState().equals(BlockState.FILLED) || block.getState().equals(BlockState.WALL)) {
            return;
        }

        block.setState(BlockState.TEMP);

        while (!queue.isEmpty()) {
            Enviroment current = queue.peek();
            GridBlock tmp = (GridBlock) current;
            queue.remove();

            for (GridBlock gridBlock : game.getGrid().getBlocks()) {
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX(), tmp.getCenterPoint().getY() - 20) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.TEMP);
                    queue.add(gridBlock);
                }
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX(), tmp.getCenterPoint().getY() + 20) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.TEMP);
                    queue.add(gridBlock);
                }
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX() - 20, tmp.getCenterPoint().getY()) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.TEMP);
                    queue.add(gridBlock);
                }
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX() + 20, tmp.getCenterPoint().getY()) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.TEMP);
                    queue.add(gridBlock);
                }
            }
        }
    }

    private void fillBlocks(ArrayList<Enviroment> tmpVisited) {
        // https://www.idogendel.com/en/archives/738
        for (Enviroment enviroment : tmpVisited) {
            if (enviroment instanceof GridBlock block) {
                block.setState(BlockState.FILLED);
            }
        }

        for (WorldEntity ghost : game.getGhosts()) {
            for (Enviroment enviroment : game.getGrid().getBlocks()) {
                if (enviroment instanceof GridBlock block && block.getBoundingBox().contains(ghost.position.getX(), ghost.position.getY())) {
                    floodFill(block);
                }
            }
        }

        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block) {
                if (block.getState().equals(BlockState.EMPTY)) {
                    block.setState(BlockState.FILLED);
                }
                if (block.getState().equals(BlockState.TEMP)) {
                    block.setState(BlockState.EMPTY);
                }
            }
        }

        tmpBlocks.clear();
        updateProgress();
    }

    public Interface getHealth() {
        for (Interface inter : gui) {
            if (inter instanceof Health health) {
                return health;
            }
        }

        return null;
    }

    public Interface getScore() {
        for (Interface inter : gui) {
            if (inter instanceof Score score) {
                return score;
            }
        }

        return null;
    }

    public Interface getProgress() {
        for (Interface inter : gui) {
            if (inter instanceof Progress progress) {
                return progress;
            }
        }

        return null;
    }

    public void updateHealth() {
        getHealth().update(getHealth().getAmount() - 1);
    }

    public void updateProgress() {
        int count = 0;
        for (Enviroment enviroment : game.getGrid().getBlocks()) {
            if (enviroment instanceof GridBlock block && block.getState().equals(BlockState.FILLED)) {
                count++;
            }
        }

        double progress = count / (game.getGrid().getAll() / 100.);

        updateScore((int) (progress) * 10);
        getProgress().update((int) progress);
    }

    public void updateScore(int val) {
        getScore().update(getScore().getAmount() + val);
    }
}