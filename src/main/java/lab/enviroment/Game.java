package lab.enviroment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lab.GhostLoader;
import lab.entity.Ghost;
import lab.entity.Pacman;
import lab.entity.WorldEntity;
import lab.enums.BlockState;

import java.util.ArrayList;

public class Game {

    private final double width;
    private final double height;
    private final ArrayList<WorldEntity> entities;
    private String name;
    private final Grid grid;
    private final GhostLoader ghostLoader;

    public Game(double width, double height, String level) {

        //Window specifications
        this.width = width;
        this.height = height;

        // Grid
        this.grid = new Grid(this);

        // Ghost loader
        this.ghostLoader = new GhostLoader( this, level);
        this.ghostLoader.load();

        // Entities
        this.entities = new ArrayList<WorldEntity>();
        this.entities.add(new Pacman(this));
        this.entities.addAll(ghostLoader.createGhosts());
    }

    public void draw(GraphicsContext gc) {
        gc.save();

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Grid
        this.grid.draw(gc);

        // Entities
        for (WorldEntity entity : entities) {
            entity.draw(gc);
        }

        gc.restore();
    }

    public void simulate(double deltaT) {

        //Ghost collisions and simulating
        for (WorldEntity entity : entities) {
            entity.simulate(deltaT);

            if (entity instanceof Pacman pacman) {
                for (WorldEntity ghost : entities) {
                    if (ghost instanceof Ghost ghost1 && ghost1.getBoundingBox().contains(pacman.getCenterPoint())) {
                        pacman.hit(grid);
                    }
                }
            }

            for (GridBlock gridBlock : grid.getBlocks()) {
                if (entity instanceof Ghost ghost) {
                    if ((gridBlock.getState().equals(BlockState.FILLED) || gridBlock.getState().equals(BlockState.WALL)) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                        ghost.hit(grid);
                        return;
                    }

                    if (gridBlock.getState().equals(BlockState.PATH) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                        getPacman().hit(grid);
                    }
                }
            }

            if (entity instanceof Ghost ghost) {
                if (getPacman().getProgress().getAmount() > 20) {
                    ghost.spawnInky();
                }
            }
        }
    }

    public double getHeight() {
        return this.height;
    }

    public double getWidth() {
        return this.width;
    }

    public Grid getGrid() {
        return grid;
    }

    public Pacman getPacman() {
        for (WorldEntity entity : entities) {
            if (entity instanceof Pacman pacman) {
                return pacman;
            }
        }

        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<WorldEntity> getGhosts() {
        ArrayList<WorldEntity> tmp = new ArrayList<>();
        for (WorldEntity entity : entities) {
            if (entity instanceof Ghost ghost) {
                tmp.add(ghost);
            }
        }

        return tmp;
    }
}
