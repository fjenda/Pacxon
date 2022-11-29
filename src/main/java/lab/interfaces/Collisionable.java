package lab.interfaces;

import javafx.geometry.Rectangle2D;

public interface Collisionable extends Drawable {
    Rectangle2D getBoundingBox();
    default boolean intersects(Collisionable other) {
        return getBoundingBox().intersects(other.getBoundingBox());
    }

    void hit();

}
