package lab.enviroment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lab.GhostLoader;
import lab.entity.BonusItem;
import lab.entity.Ghost;
import lab.entity.Pacman;
import lab.entity.WorldEntity;
import lab.enums.BlockState;
import lab.enums.GhostTexture;
import lab.interfaces.GameListener;

import java.util.ArrayList;

public class Game {

    private final double width;
    private final double height;
    private final ArrayList<WorldEntity> entities;
    private String name;
    private final Grid grid;
    private GameListener gameListener = new EmptyGameListener();

    public Game(double width, double height, String level) {

        //Window specifications
        this.width = width;
        this.height = height;

        // Grid
        this.grid = new Grid(this);

        // Ghost loader
        GhostLoader ghostLoader = new GhostLoader(this, level);
        ghostLoader.load();

        // Entities
        this.entities = new ArrayList<>();
        this.entities.add(new Pacman(this));
        this.entities.addAll(ghostLoader.createGhosts());
        this.entities.add(new BonusItem(this));
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
        winGame();
        gameOver();

        //Ghost collisions and simulating
        for (WorldEntity entity : entities) {
            entity.simulate(deltaT);

            if (entity instanceof Pacman pacman) {
                for (WorldEntity ghost : entities) {
                    if (ghost instanceof Ghost ghost1 && ghost1.getBoundingBox().contains(pacman.getCenterPoint())) {
                        if (pacman.isPowered()) {
                            ghost1.resetPosition();
                            pacman.getScore().update(200);
                        } else {
                            pacman.hit();
                        }
                    }
                }

                for (WorldEntity bonus : entities) {
                    if (bonus instanceof BonusItem bonusItem && bonusItem.getBoundingBox().contains(pacman.getCenterPoint())) {
                        bonusItem.hit();
                        pacman.setPowered(true);
                    }
                }
            }

            for (Enviroment enviroment : grid.getBlocks()) {
                if (entity instanceof Ghost ghost && enviroment instanceof GridBlock gridBlock) {
                    if ((gridBlock.getState().equals(BlockState.FILLED) || gridBlock.getState().equals(BlockState.WALL)) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                        ghost.hit();
                        return;
                    }

                    if (gridBlock.getState().equals(BlockState.PATH) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                        if (getPacman().isPowered()) {
                            ghost.hit();
                        } else {
                            getPacman().hit();
                        }
                    }
                }
            }

            if (entity instanceof Ghost ghost) {
                if (getPacman().getProgress().getAmount() > 20) {
                    if (ghost.getTexture().equals(GhostTexture.INKY)) {
                        ghost.spawnInky();
                    }
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

    public String getName() {
        return this.name;
    }

    public void setGameListener(GameListener gameListener) {
    	this.gameListener = gameListener;
    }

    public void winGame() {
        if (this.getPacman().getProgress().getAmount() >= 80) {
            this.gameListener.gameOver();
        }
    }
    public void gameOver() {
        if (this.getPacman().getHealth().getAmount() == 0) {
            System.out.println("Game over");
            this.gameListener.gameOver();
        }
    }
}
