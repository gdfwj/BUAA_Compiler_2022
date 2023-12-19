package ir;

import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.Type;
import ir.values.Value;
import sym.Sym;

import java.util.ArrayList;

public class GlobalVariable extends Value {
    private Type type;
    private boolean isConst;
    private final ArrayList<Integer> constInitValue;

    public GlobalVariable(String name, Type type, boolean isConst, ArrayList<Integer> constInitValue) {
        super(name, new PointerType(type));
        this.type = type;
        this.isConst = isConst;
        this.constInitValue = constInitValue;
    }

    public GlobalVariable(String name, Type type, ArrayList<Integer> constInitValue) {
        super(name, new PointerType(type));
        this.type = type;
        this.isConst = false;
        this.constInitValue = constInitValue;
    }

    public Type getAllocaType() {
        return type;
    }

    public boolean isConst() {
        return isConst;
    }

    public ArrayList<Integer> getConstInitValue() {
        return constInitValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(getName()).append(" = dso_local");
        if (isConst) {
            sb.append(" constant");
        } else {
            sb.append(" global");
        }
        Type now = getAllocaType();
        int dim1 = -1;
        int dim2 = -1;
        if (now instanceof ArrayType) {
            dim1 = ((ArrayType) now).getDim();
            now = ((ArrayType) now).getInside();
            if (now instanceof ArrayType) {
                dim2 = ((ArrayType) now).getDim();
                now = ((ArrayType) now).getInside();
                assert now instanceof IntegerType;
            }
        }
        if (dim1 == -1) { // 非数组变量
            sb.append(" i32");
            if (constInitValue != null) {
                sb.append(" ").append(constInitValue.get(0));
            } else {
                sb.append(" 0");
            }
        } else {
            if (dim2 == -1) { // 一维数组
                sb.append(" [").append(dim1).append(" x i32] ");
                if (constInitValue != null) {
                    sb.append("[");
                    for (Integer i : constInitValue) {
                        sb.append("i32 ").append(i).append(", ");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append("]");
                } else {
                    sb.append("zeroinitializer");
                }
            } else {
                sb.append(" [").append(dim1).append(" x [").append(dim2).append(" x i32]] ");
                if (constInitValue != null) {
                    sb.append("[");
                    for (Integer i : constInitValue) {
                        sb.append("i32 ").append(i).append(", ");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append("]");
                } else {
                    sb.append("zeroinitializer");
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public String generateMIPSCode() {
        Type now = getAllocaType();
        int dim1 = -1;
        int dim2 = -1;
        if (now instanceof ArrayType) {
            dim1 = ((ArrayType) now).getDim();
            now = ((ArrayType) now).getInside();
            if (now instanceof ArrayType) {
                dim2 = ((ArrayType) now).getDim();
                now = ((ArrayType) now).getInside();
                assert now instanceof IntegerType;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" : .word ");
        if (dim1 == -1) { // 非数组变量
            if (constInitValue != null) {
                sb.append(constInitValue.get(0));
            } else {
                sb.append("0");
            }
        } else {
            if (constInitValue != null) {
                for (Integer i : constInitValue) {
                    sb.append(i).append(", ");
                }
            } else {
                 if(dim2==-1) {
                     for(int i=0;i<dim1;i++) {
                         sb.append("0").append(", ");
                     }
                 } else {
                     for(int i=0;i<dim1*dim2;i++) {
                         sb.append("0").append(", ");
                     }
                 }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("\n");
        return sb.toString();
    }
}
