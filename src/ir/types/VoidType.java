package ir.types;

public class VoidType extends Type{
    public VoidType() {
        super(false);
    }

    @Override
    public String toString() {
        return "void";
    }
}
