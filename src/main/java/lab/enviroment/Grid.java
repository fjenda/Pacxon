package lab.enviroment;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import lab.enums.BlockState;
import lab.interfaces.Drawable;

import java.util.ArrayList;

public class Grid extends Enviroment implements Drawable {
    private final ArrayList<GridBlock> blocks;
    private boolean isGridVisible;
    private long switchCooldown = 0L;
    private long currentTime = 0L;
    private final int rows;
    private final int cols;
    private int all;

    public Grid(Game game) {
        super(game, null, null, false);

        int blockSize = 20;
        this.cols = (int) (game.getWidth() / blockSize);
        this.rows = (int) ((game.getHeight() - 50) / blockSize);

        this.blocks = new ArrayList<>();

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Point2D tmpPos = new Point2D(i * blockSize, 50 + j * blockSize);
                this.blocks.add(new GridBlock(game, tmpPos));
            }
        }

        //EDGES
        for (GridBlock block : blocks) {
            if (block.position.getX() == 0 ||
                block.position.getX() == game.getWidth() - 20 ||
                block.position.getY() == 50 ||
                block.position.getY() == game.getHeight() - 20) {
                block.setState(BlockState.WALL);
            }
        }

        this.all = 0;

        for (GridBlock block : blocks) {
            if (block.getState().equals(BlockState.EMPTY)) {
                this.all++;
            }
        }
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.save();

        for (GridBlock block : blocks) {
            block.draw(gc);
        }

        gc.restore();
    }

    public void showGrid() {
        currentTime = System.currentTimeMillis();
        if (currentTime - switchCooldown < 75) {
            return;
        }
        switchCooldown = currentTime;

        this.isGridVisible = !this.isGridVisible;

        for (GridBlock block : blocks) {
            block.setBlockVisible(isGridVisible);
        }
    }

    public ArrayList<GridBlock> getBlocks() {
        return this.blocks;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getAll() {
        return all;
    }
}
