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
import java.util.List;

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
        this.entities.add(new BonusItem(this));
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
        winGame();
        gameOver();

        //Collisions and simulating
        for (WorldEntity entity : entities) {
            entity.simulate(deltaT);

            if (entity instanceof Pacman pacman) {
                for (Ghost ghost : entities.stream().filter(g -> g instanceof Ghost).map(g -> (Ghost) g).toList()) {
                    if (ghost.getBoundingBox().contains(pacman.getCenterPoint())) {
                        if (pacman.isPowered()) {
                            ghost.resetPosition();
                            pacman.getScore().update(200);
                        } else {
                            pacman.hit();
                        }
                    }
                }

                for (BonusItem bonusItem : entities.stream().filter(b -> b instanceof BonusItem).map(b -> (BonusItem) b).toList()) {
                    if (bonusItem.getBoundingBox().contains(pacman.getCenterPoint()) && !bonusItem.isEaten()) {
                        bonusItem.hit();
                        pacman.setPowered(true);
                    }
                }
            }


            if (entity instanceof Ghost ghost) {
                for (GridBlock gridBlock : grid.getBlocks().stream().filter(b -> b instanceof GridBlock).map(b -> (GridBlock) b).toList()) {
                    if (!ghost.getTexture().equals(GhostTexture.INKY)) {
                        if ((gridBlock.getState().equals(BlockState.FILLED) || gridBlock.getState().equals(BlockState.WALL)) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                            ghost.hit();
                            //return;
                        }
                    } else {
                        if (gridBlock.getState().equals(BlockState.WALL) || gridBlock.getState().equals(BlockState.EMPTY) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                            ghost.hit();
                            //return;
                        }
                    }

                    if (gridBlock.getState().equals(BlockState.PATH) && ghost.getBoundingBox().intersects(gridBlock.getBoundingBox())) {
                        if (!getPacman().isPowered()) {
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

    public List<WorldEntity> getGhosts() {
        return entities.stream().filter(e -> e instanceof Ghost).toList();
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
