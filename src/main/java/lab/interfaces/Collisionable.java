package lab.interfaces;

import javafx.geometry.Rectangle2D;
import lab.enviroment.Grid;

public interface Collisionable extends Drawable {
    Rectangle2D getBoundingBox();
    default boolean intersects(Collisionable other) {
        return getBoundingBox().intersects(other.getBoundingBox());
    }

    void hit();

}
