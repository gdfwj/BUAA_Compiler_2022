package ir.types;

public class PointerType extends Type{
    private Type points;
    public PointerType(Type points) {
        super(false);
        this.points = points;
    }

    public Type getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return points+"*";
    }

    @Override
    public int getSize() {
        return 4;
    }

    public int getPointsSize() {
        return points.getSize();
    }
}
