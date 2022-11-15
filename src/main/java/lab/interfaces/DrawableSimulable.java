package lab.interfaces;

import lab.interfaces.Drawable;

public interface DrawableSimulable extends Drawable {
    void simulate(double deltaT);
}
