package ir;

import ir.instructions.Instruction;
import ir.types.FunctionType;
import ir.types.Type;
import ir.values.Value;

import java.util.ArrayList;

public class Function extends Value {
    private ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private ArrayList<Arg> args = new ArrayList<>();
    private Type retType;
    private int now;

    public Function(String name, Type type) {
        super(name, new FunctionType(null, type));
        retType = type;
        now = -1;
    }

    public void addBasicBlock(BasicBlock block) {
        basicBlocks.add(block);
        now++;
    }

    public Type getRetType() {
        return retType;
    }

    public void addInst(Instruction inst) {
        basicBlocks.get(now).addInst(inst);
    }

    public Value addArgs(Type arg) {
        Arg argIn = new Arg("", arg);
        args.add(argIn);
        return argIn;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(retType).append(" @").append(getName()).append("(");
        for(Arg i:args) {
            sb.append(i).append(", ");
        }
        sb.append(") {\n");
        for (BasicBlock bb : basicBlocks) {
            sb.append(bb);
        }
        sb.append("}\n");
        return sb.toString();
    }

    public ArrayList<Arg> getArgs() {
        return args;
    }

    public static class Arg extends Instruction {
        public Arg(String name, Type type) {
            super(name, null, type);
        }

        @Override
        public String toString() {
            return getType()+" "+getCount();
        }
    }
}
