package backend.mips;

import backend.mips.insturctions.MipsInst;

import java.util.ArrayList;

public class MipsBBlock {
    private ArrayList<MipsInst> insts = new ArrayList<>();
    private MipsFunction containsFunction;
    private String name;
    private int overflowOffset;

    public MipsBBlock(String name, MipsFunction function) {
        this.name = name;
        this.containsFunction = function;
    }

    public int getOverflowOffset() {
        return overflowOffset;
    }

    public MipsFunction getContainsFunction() {
        return containsFunction;
    }

    public void setOverflowOffset(int overflowOffset) {
        this.overflowOffset = overflowOffset;
    }

    public String getName() {
        return name;
    }

    public void addInst(MipsInst inst) {
        insts.add(inst);
    }

    public ArrayList<MipsInst> getInsts() {
        return insts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        for(MipsInst i:insts) {
            sb.append(i);
        }
        return sb.toString();
    }
}
