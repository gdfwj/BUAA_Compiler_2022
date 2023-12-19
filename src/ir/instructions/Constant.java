package ir.instructions;

import ir.types.Type;
import ir.values.Value;

public class Constant extends Value {
    public Constant(String name, Type type) {
        super(name, type);
    }
}
