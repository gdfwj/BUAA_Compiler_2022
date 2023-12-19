package ir.instructions;

import ir.GlobalVariable;
import ir.types.IntegerType;
import ir.types.Type;
import ir.types.VoidType;
import ir.values.Value;

public abstract class IOInst extends Instruction {
    public IOInst(String name, Tag tag, Type type) {
        super(name, tag, type);
    }

    public static class GetIntInst extends IOInst {
        public GetIntInst() {
            super("getint", Tag.GetInt, new IntegerType());
        }

        @Override
        public String toString() {
            return getCount() + " = call i32 @getint()\n";
        }
    }

    public static class PutIntInst extends IOInst {
        public PutIntInst(Value value) {
            super("putint", Tag.PutInt, new VoidType());
            value.addUses(this);
            addOperands(value);
        }

        public String toString() {
            return "call void @putint(i32 " + getOperands().get(0).getCount() + ")\n";
        }
    }

    public static class PutCharInst extends IOInst {
        private int out;
        private boolean isConstChar = false;

        public PutCharInst(char out) {
            super("putchar", Tag.PutChar, new VoidType());
            this.out = out;
            isConstChar = true;
        }

        public PutCharInst(Value value) {
            super("putchar", Tag.PutChar, new VoidType());
            value.addUses(this);
            addOperands(value);
        }

        public int getOut() {
            return out;
        }

        @Override
        public String toString() {
            if (isConstChar) {
                return "call void @putch(i32 " + out + ")\n";
            } else {
                return "call void @putch(i32 " + getOperands().get(0).getCount() + ")\n";
            }
        }
    }

    public static class PutStringInst extends IOInst {
        public PutStringInst(Value ptr) {
            super("putstring", Tag.PutString, new VoidType());
            ptr.addUses(this);
            addOperands(ptr);
        }

        @Override
        public String toString() {
            return "call void @putstr(i8* " + getOperands().get(0).getCount() + ")\n";
        }
    }
}
