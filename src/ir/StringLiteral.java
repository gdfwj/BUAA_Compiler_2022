package ir;

import ir.types.PointerType;
import ir.types.VoidType;
import ir.values.Value;

public class StringLiteral extends Value {
    private final String inside;

    public StringLiteral(String name, String inside) {
        super(name, new PointerType(new VoidType()));
        this.inside = inside;
    }

    public String getFormatString() {
        return inside;
    }

    @Override
    public String toString() {
        return getCount() + " = constant [" + (inside.length() + 1) + " x i8] c\"" +
                inside.replace("\\n", "\\0a") + "\\00\"\n";
    }

    public String generateMIPSCode() {
        return getMIPSCount() + ": .asciiz \"" + inside + "\"" + "\n";
    }
}
