package cards;

public class Vector {

    private int X;

    private int Y;

    public Vector(int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    @Override
    public String toString() {
        return X + "," + Y + ";";
    }
}
