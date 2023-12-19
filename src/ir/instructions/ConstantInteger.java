package ir.instructions;

import ir.types.IntegerType;

public class ConstantInteger extends Constant{
    private final int value;
    public ConstantInteger(int value) {
        super(String.valueOf(value), new IntegerType(true));
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getCount() {
        return String.valueOf(value);
    }
}
