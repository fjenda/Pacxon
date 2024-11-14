package lab.interfaces;

import javafx.geometry.Rectangle2D;

public interface Collisionable extends Drawable {
    Rectangle2D getBoundingBox();
    void hit();

}
