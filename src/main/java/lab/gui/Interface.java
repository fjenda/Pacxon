package lab.gui;

import javafx.scene.canvas.GraphicsContext;
import lab.interfaces.Drawable;
import lab.enviroment.Game;

public abstract class Interface implements Drawable {
    protected final Game game;
    protected int amount;
    public Interface(Game game, int amount) {
        this.game = game;
        this.amount = amount;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        drawInternal(gc);

        gc.restore();
    }

    public abstract void drawInternal(GraphicsContext gc);

    public void update(int amount) {
        updateInternal(amount);
    }
    public abstract void updateInternal(int amount);

    public int getAmount() {
        return this.amount;
    }


}
