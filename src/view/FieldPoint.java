package view;


/**
 * Position on Field
 * (0, 0) on the left-up connor, x is added by going down, y is added by going right.
 */
public class FieldPoint {
    private final int x;
    private final int y;

    public FieldPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", x, y);
    }
}
