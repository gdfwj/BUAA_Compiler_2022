package ir.instructions;

import ir.GlobalVariable;
import ir.types.*;
import ir.values.Value;

import java.util.ArrayList;

public abstract class MemInst extends Instruction {
    public MemInst(String name, Tag tag, Type type) {
        super(name, tag, type);
    }

    public static class AllocaInst extends MemInst {
        private Type allocaType;

        public AllocaInst(String name, Type type) {
            super(name, Tag.Alloca, new PointerType(type));
            this.allocaType = type;
        }

        public Type getAllocaType() {
            return allocaType;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getCount()).append(" = alloca ");
            sb.append(allocaType).append("\n");
            return sb.toString();
        }
    }

    public static class LoadInst extends MemInst {
        private Value ptr;

        public LoadInst(Value ptr) {
            super("load", Tag.Load, new IntegerType());
            this.ptr = ptr;
            ptr.addUses(this);
            addOperands(ptr);
        }

        public Value getPtr() {
            return ptr;
        }

        @Override
        public void replaceOperands(Value oldValue, Value newValue) {
            super.replaceOperands(oldValue, newValue);
            assert ptr != oldValue;
        }

        @Override
        public String toString() {
            return getCount() + " = load i32, i32* " + ptr.getCount() + "\n";
        }
    }

    public static class StoreInst extends MemInst {
        private Value input;
        private Value ptr;

        public StoreInst(Value input, Value ptr) {
            super("store", Tag.Store, new VoidType());
            this.ptr = ptr;
            this.input = input;
            ptr.addUses(this);
            input.addUses(this);
            addOperands(input);
            addOperands(ptr);
        }

        public Value getPtr() {
            return ptr;
        }

        public Value getInput() {
            return input;
        }

        @Override
        public void replaceOperands(Value oldValue, Value newValue) {
            super.replaceOperands(oldValue, newValue);
            if (input == oldValue) {
                input = newValue;
            }
            assert ptr != oldValue;
        }

        @Override
        public String toString() {
            return "store " + input.getType() + " " + input.getCount() + " " + ptr.getType() + " " + ptr.getCount() + "\n";
        }
    }

    public static class GEPInst extends MemInst {
        private Value base;
        private boolean global;

        public GEPInst(Value base, ArrayList<Value> args) {
            super("GEP", Tag.GEP, new PointerType(getRetType(base, args)));
//            if (getRetType(base, args) instanceof ArrayType) {
//                args.add(new ConstantInteger(0));
//            }
            this.base = base;
            base.addUses(this);
            addOperands(base);
            for (Value i : args) {
                i.addUses(this);
                addOperands(i);
            }
        }

        public void setGlobal(boolean global) {
            this.global = global;
        }

        public boolean isGlobal() {
            return global;
        }

        public Value getBase() {
            return base;
        }

        private static Type getRetType(Value base, ArrayList<Value> args) {
            assert base.getType() instanceof PointerType;
            Type now = ((PointerType) base.getType()).getPoints();
            Value reserved;
            if (args.size() > 0 && now instanceof ArrayType) {
                reserved = args.get(0);
                args.remove(0);
            } else {
                reserved = null;
            }
            for (Value i : args) { // 每次取数组内容
                if (now instanceof ArrayType) {
                    now = ((ArrayType) now).getInside();
                } else {
                    assert now instanceof PointerType;
                    now = ((PointerType) now).getPoints();
                }
            }
            if (reserved != null) {
                args.add(0, reserved);
            }
            return now;
        }

        @Override
        public String toString() { // fixme
            StringBuilder sb = new StringBuilder();
            sb.append(getCount()).append(" = getelementptr ");
            Type type = base.getType();
            assert type instanceof PointerType;
            type = ((PointerType) type).getPoints();
            if (type instanceof ArrayType) {
                int dim1 = ((ArrayType) type).getDim();
                int dim2 = -1;
                if (((ArrayType) type).getInside() instanceof ArrayType) {
                    dim2 = ((ArrayType) ((ArrayType) type).getInside()).getDim();
                }
                if (dim2 == -1) { // 一维数组
                    sb.append("[").append(dim1).append(" x i32], ");
                    sb.append("[").append(dim1).append(" x i32]* ");
                } else {
                    sb.append("[").append(dim1).append(" x [").append(dim2).append(" x i32]], ");
                    sb.append("[").append(dim1).append(" x [").append(dim2).append(" x i32]]* ");
                }
                sb.append(base.getCount());
                for (Value value : getOperands()) {
                    sb.append(", ");
                    sb.append("i32 ").append(value.getCount());
                }
                sb.append("\n");
            } else if (type instanceof PointerType) {
                sb.append(base.getCount());
                for (Value value : getOperands()) {
                    sb.append(", ");
                    sb.append("i32 ").append(value.getCount());
                }
                sb.append("\n");
            } else {
                sb.append("i32, i32*, ").append(base.getCount()).append(", ").append(getOperands().get(1).getCount()).append("\n");
            }
            return sb.toString();
        }
    }
}
