package ir.types;

public class IntegerType extends Type{
    private final boolean isConst;
    public IntegerType() {
        super(false);
        isConst=false;
    }
    public IntegerType(boolean isConst) {
        super(isConst);
        this.isConst = isConst;
    }

    public boolean isConst() {
        return isConst;
    }

    @Override
    public String toString() {
        return "i32";
    }

    @Override
    public int getSize() {
        return 4;
    }
}
