package ir.types;

public class ArrayType extends Type {
    private Type inside;
    private int dim;
    private boolean isConst;

    public ArrayType(Type inside, int dim) {
        super(false);
        this.inside = inside;
        this.dim = dim;
        isConst = false;
    }

    public ArrayType(Type inside, int dim, boolean isConst) {
        super(isConst);
        this.inside = inside;
        this.dim = dim;
        this.isConst = isConst;
    }

    public Type getInside() {
        return inside;
    }

    public int getDim() {
        return dim;
    }

    public boolean isConst() {
        return isConst;
    }

    @Override
    public String toString() {
        return "[" + dim + " x " + inside + "]";
    }

    @Override
    public int getSize() {
        return inside.getSize() * dim;
    }
}
