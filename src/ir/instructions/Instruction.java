package ir.instructions;

import ir.types.Type;
import ir.values.Value;

public abstract class Instruction extends Value {
    private final Tag tag;
    private final Type type;


    public Instruction(String name, Tag tag, Type type){
        super(name, type);
        this.tag = tag;
        this.type = type;
    }

    public Tag getTag() {
        return tag;
    }

    public Type getType() {
        return type;
    }
}
