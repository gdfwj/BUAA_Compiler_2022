package ir.instructions;

import ir.GlobalVariable;
import ir.types.IntegerType;
import ir.types.Type;
import ir.values.Value;

public abstract class CalculateInst extends Instruction {
    public CalculateInst(Tag tag, Type type) {
        super("", tag, type);
    }

    public static class BinaryInst extends CalculateInst {
        private Value op1;
        private Value op2;

        public BinaryInst(Value op1, Value op2, Tag tag) {
            super(tag, new IntegerType());
            this.op1 = op1;
            this.op2 = op2;
            op1.addUses(this);
            op2.addUses(this);
            addOperands(op1);
            addOperands(op2);
        }

        public Value getOp1() {
            return op1;
        }

        public Value getOp2() {
            return op2;
        }

        @Override
        public void replaceOperands(Value oldValue, Value newValue) {
            super.replaceOperands(oldValue, newValue);
            if(op1==oldValue) {
                op1=newValue;
            }
            if(op2==oldValue) {
                op2=newValue;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (getTag() == Tag.Add || getTag() == Tag.Sub || getTag() == Tag.Mul || getTag() == Tag.Div || getTag() == Tag.Mod) {
                sb.append(getCount()).append(" = ").append(getTag());
                sb.append(" i32 ");
            } else { // 短路求值不存在and，or计算
                sb.append(getCount()).append(" = icmp ").append(getTag());
                sb.append(" i1 ");
            }
            sb.append(op1.getCount());
            sb.append(", ");
            sb.append(op2.getCount());
            sb.append("\n");
            return sb.toString();
        }
    }

    public static class UnaryInst extends CalculateInst {
        private Value op;

        public UnaryInst(Value op, Tag tag) {
            super(tag, new IntegerType());
            this.op = op;
            addOperands(op);
            op.addUses(this);
        }

        public Value getOp() {
            return op;
        }

        @Override
        public void replaceOperands(Value oldValue, Value newValue) {
            super.replaceOperands(oldValue, newValue);
            if(op==oldValue) {
                op=newValue;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getCount()).append(" = ").append(getTag()).append(" i1 ");
            sb.append(op.getCount()).append("\n");
            return sb.toString();
        }
    }

}
