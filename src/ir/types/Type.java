package ir.types;

public abstract class Type {
    private boolean isConst;
    public Type(boolean isConst) {
        this.isConst = isConst;
    }

    public boolean isConst() {
        return isConst;
    }

    public int getSize() {
        assert false;
        return -1;
    }
}
