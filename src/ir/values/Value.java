package ir.values;

import ir.Function;
import ir.GlobalVariable;
import ir.StringLiteral;
import ir.instructions.ConstantInteger;
import ir.instructions.MemInst;
import ir.types.Type;

import java.util.ArrayList;

public abstract class Value {
    private final String name;
    private final Type type;
    private final ArrayList<Value> uses;
    private final ArrayList<Value> operands;
    private final int count;
    private static int COUNT = 0;

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
        uses = new ArrayList<>();
        operands = new ArrayList<>();
        if (!(this instanceof ConstantInteger || this instanceof GlobalVariable || this instanceof Function || this instanceof MemInst.StoreInst)) {
            count = COUNT++;
        } else {
            count = -1;
        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Value> getUses() {
        return uses;
    }

    public void addUses(Value value) {
        uses.add(value);
    }

    public void addOperands(Value value) {
        operands.add(value);
    }

    public void replaceOperands(Value oldValue, Value newValue) {
        while (true) {
            int index = 0;
            int flag = 0;
            for (Value i : operands) {
                if (i == oldValue) {
                    operands.remove(i);
                    operands.add(index, newValue);
                    flag = 1;
                    break;
                }
                index++;
            }
            if(flag == 0) {
                break;
            }
        }
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public ArrayList<Value> getOperandsCopy() {
        return new ArrayList<>(operands);
    }

    public String getCount() {
        if (this instanceof GlobalVariable) {
            return "@" + name;
        } else if (this instanceof StringLiteral) {
            return "@_str_" + count;
        } else {
            return "%" + count;
        }
    }

    public String getMIPSCount() {
        if (this instanceof GlobalVariable) {
            return name;
        } else if (this instanceof StringLiteral) {
            return "str" + count;
        }
        System.exit(-1);
        return null;
    }

}
