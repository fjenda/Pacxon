package lab.enums;

public enum Direction {
    LEFT, RIGHT, UP, DOWN;

    private static final Direction[] vals = values();

    public Direction next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
}
