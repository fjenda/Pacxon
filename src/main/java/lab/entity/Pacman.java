package lab.entity;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import lab.enums.BlockState;
import lab.enums.Direction;
import lab.enviroment.Grid;
import lab.enviroment.GridBlock;
import lab.gui.*;
import lab.interfaces.Collisionable;
import lab.enviroment.Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import static lab.Constants.PACMAN_SPRITE;

public class Pacman extends WorldEntity implements Collisionable {
    private Interface gui[];
    ArrayList<GridBlock> tmpBlocks = new ArrayList<>();
    private double angle = 180;
    private long switchCooldown = 0L;
    private long currentTime = 0L;

    public Pacman(Game game) {
        super(game, new Point2D(0, 50), new Point2D(20, 18));

        this.gui = new Interface[]{new Health(game), new Score(game), new Progress(game)};
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
        for (GridBlock block : tmpBlocks) {
            if (block.getState() == BlockState.TEMP) {
                block.setState(BlockState.EMPTY);
            }
        }
        tmpBlocks.clear();

        this.position = new Point2D(0, 50);
    }

    @Override
    public void hit(Grid grid) {
        resetPosition();
        for (Interface inter : gui) {
            if (inter instanceof Health health) {
                updateHealth();
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

        checkBlocks(game.getGrid());
    }

    public void checkBlocks(Grid grid) {
        for (GridBlock block : grid.getBlocks()) {
            if (block.getBoundingBox().contains(centerPoint) && (!block.getState().equals(BlockState.WALL) && !block.getState().equals(BlockState.FILLED))) {
                tmpBlocks.add(block);
                block.setState(BlockState.TEMP);
            }

            if (block.getBoundingBox().contains(centerPoint) && (block.getState().equals(BlockState.WALL) || block.getState().equals(BlockState.FILLED))) {
                if (!tmpBlocks.isEmpty()){
                    // SUPER-SHAPE
                    fillBlocks(tmpBlocks);
                }
            }
        }
    }

    public ArrayList<GridBlock> findCorners(ArrayList<GridBlock> tmpVisited) {
        ArrayList<GridBlock> tmp = new ArrayList<>();

        tmp.add(tmpVisited.get(0));

        for (int i = 2; i < tmpVisited.size(); i++) {
            if ((tmpVisited.get(i).getPosition().getX() > tmpVisited.get(i - 2).getPosition().getX() && tmpVisited.get(i).getPosition().getY() > tmpVisited.get(i - 2).getPosition().getY()) ||
               (tmpVisited.get(i).getPosition().getX() < tmpVisited.get(i - 2).getPosition().getX() && tmpVisited.get(i).getPosition().getY() < tmpVisited.get(i - 2).getPosition().getY()) ||
               (tmpVisited.get(i).getPosition().getX() < tmpVisited.get(i - 2).getPosition().getX() && tmpVisited.get(i).getPosition().getY() > tmpVisited.get(i - 2).getPosition().getY()) ||
               (tmpVisited.get(i).getPosition().getX() > tmpVisited.get(i - 2).getPosition().getX() && tmpVisited.get(i).getPosition().getY() < tmpVisited.get(i - 2).getPosition().getY())) {
                tmp.add(tmpVisited.get(i - 1));
            }
        }

        tmp.add(tmpVisited.get(tmpVisited.size() - 1));

        return tmp;
    }

    public void floodFill(GridBlock block) {
        Queue<GridBlock> queue = new LinkedList<>();

        queue.add(block);

        block.setState(BlockState.FILLED);

        while (!queue.isEmpty()) {
            GridBlock tmp = queue.peek();
            queue.remove();

            for (GridBlock gridBlock : game.getGrid().getBlocks()) {
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX(), tmp.getCenterPoint().getY() - 20) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.FILLED);
                    queue.add(gridBlock);
                }
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX(), tmp.getCenterPoint().getY() + 20) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.FILLED);
                    queue.add(gridBlock);
                }
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX() - 20, tmp.getCenterPoint().getY()) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.FILLED);
                    queue.add(gridBlock);
                }
                if (gridBlock.getBoundingBox().contains(tmp.getCenterPoint().getX() + 20, tmp.getCenterPoint().getY()) && gridBlock.getState().equals(BlockState.EMPTY)) {
                    gridBlock.setState(BlockState.FILLED);
                    queue.add(gridBlock);
                }
            }
        }
    }

    public void fillBlocks(ArrayList<GridBlock> tmpVisited) {
        for (GridBlock block : tmpVisited) {
            block.setState(BlockState.FILLED);
        }

        ArrayList<GridBlock> corners = findCorners(tmpVisited);
        Polygon polygon = new Polygon();

        for (GridBlock block : corners) {
            //block.setState(BlockState.FILLED);

            polygon.getPoints().add(block.getCenterPoint().getX());
            polygon.getPoints().add(block.getCenterPoint().getY());
        }

        for (GridBlock block : game.getGrid().getBlocks()) {
            if (polygon.contains(block.getPosition().getX(), block.getPosition().getY())) {
                if (block.getState().equals(BlockState.EMPTY)) {
                    floodFill(block);
                    return;
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
        for (GridBlock block : game.getGrid().getBlocks()) {
            if (block.getState().equals(BlockState.FILLED)) {
                count++;
            }
        }

        double progress = count / (game.getGrid().getAll() / 100.);

        getProgress().update((int) progress);
    }

    public boolean isBetween(Point2D leftTop, Point2D rightBottom, Point2D pos) {
        if (pos.getX() >= leftTop.getX() && pos.getX() <= rightBottom.getX()) {
            if (pos.getY() >= leftTop.getY() && pos.getY() <= rightBottom.getY()) {
                return true;
            }
        }
        return false;
    }
}