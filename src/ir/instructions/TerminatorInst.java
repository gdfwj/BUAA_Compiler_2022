package ir.instructions;

import ir.BasicBlock;
import ir.Function;
import ir.GlobalVariable;
import ir.types.FunctionType;
import ir.types.Type;
import ir.types.VoidType;
import ir.values.Value;

import java.util.ArrayList;

public abstract class TerminatorInst extends Instruction {
    public TerminatorInst(String name, Tag tag, Type type) {
        super(name, tag, type);
    }

    public static class BranchInst extends TerminatorInst {
        public BranchInst(Value cond, BasicBlock ifTrue, BasicBlock ifFalse) {
            super("br", Tag.Br, new VoidType());
            addOperands(cond);
            cond.addUses(this);
            addOperands(ifTrue);
            ifTrue.addUses(this);
            addOperands(ifFalse);
            ifFalse.addUses(this);
        }

        public BranchInst(BasicBlock unConditionBlock) {
            super("br", Tag.Br, new VoidType());
            unConditionBlock.addUses(this);
            addOperands(unConditionBlock);
        }

        @Override
        public String toString() {
            if (getOperands().size() == 1) {
                return "br label " + getOperands().get(0).getCount() + "\n";
            } else {
                return String.format("br i1 %s,label %s, label %s\n", getOperands().get(0).getCount(), getOperands().get(1).getCount(), getOperands().get(2).getCount());
            }
        }
    }

    public static class CallInst extends TerminatorInst {
        public CallInst(Function function, ArrayList<Value> args) {
            super("call", Tag.Call, ((FunctionType) function.getType()).getRetType());
            addOperands(function);
            function.addUses(this);
            for (Value i : args) {
                addOperands(i);
                i.addUses(this);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getCount()).append(" = call ");
            if (getType() instanceof VoidType) {
                sb.append("void @");
            } else {
                sb.append("i32 @");
            }
            sb.append(getOperands().get(0).getName());
            sb.append("(");
            if (getOperands().size() > 1) {
                for (Value i : getOperands()) {
                    if (i instanceof Function) {
                        continue;
                    }
                    sb.append(i.getType()).append(" ");
                    sb.append(i.getCount()).append(", ");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(")\n");
            return sb.toString();
        }
    }

    public static class RetInst extends TerminatorInst {
        public RetInst(Value val) {
            super("ret", Tag.Ret, new VoidType());
            if (val != null) {
                addOperands(val);
                val.addUses(this);
            }
        }

        @Override
        public String toString() {
            if(getOperands().size()==0) {
                return "ret\n";
            }
            if (getOperands().get(0) instanceof GlobalVariable) {
                return "ret i32 " + getOperands().get(0).getCount()+"\n";
            } else {
                return "ret i32 " + getOperands().get(0).getCount()+"\n";
            }
        }
    }
}
