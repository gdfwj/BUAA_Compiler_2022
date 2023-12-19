package backend.mips.reg;

import backend.mips.IdPool;
import backend.mips.insturctions.MipsInst;

import java.util.ArrayList;
import java.util.HashSet;

public class VirtualReg extends Reg {
    private final String name;
    private static final IdPool vidPool = new IdPool();
    private MipsInst defInstr = null;
    private ArrayList<MipsInst> uses = new ArrayList<>();
    private int count;
    private PhysicalReg map = null;
    private int mapNum = -1;
    private ArrayList<VirtualReg> graphNode = new ArrayList<>();
    private int offset = -4;
    private boolean two = false;
    private boolean mappedOut = false;

    public VirtualReg() {
        this.count = vidPool.allocId();
        this.name = "$" + this.count;
    }

    public void setMappedOut(boolean i) {
        mappedOut = i;
    }

    public boolean isMappedOut() {
        return mappedOut;
    }

    public int getMapNum() {
        return mapNum;
    }

    public void conflict(HashSet<Integer> in) {
        for(VirtualReg i:graphNode) {
            if(!(i.isMappedOut())) {
                in.remove(i.getMapNum());
            }
        }
    }

    public void setDefInstr(MipsInst defInstr) {
        this.defInstr = defInstr;
    }

    public void addUses(MipsInst inst) {
        uses.add(inst);
    }

    public void addNode(VirtualReg i) {
        graphNode.add(i);
    }

    public void deleteNode(VirtualReg i) {
        graphNode.remove(i);
    }

    public ArrayList<VirtualReg> getGraphNode() {
        return graphNode;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void mapTo(int name) {
        this.map = new PhysicalReg("$t"+name, RegNameMap.getInstance().getRegNum("$t"+name));
        this.mapNum = name;
    }

    public int getGraphNodeSize() {
        int c=0;
        for(VirtualReg i:graphNode) {
            if(!i.isMappedOut()) {
                c++;
            }
        }
        return c;
    }

    public String handleOverflowSrc(boolean two) {
        this.two=two;
        if (offset < 0) { // 没有溢出，不用管
            return "";
        } else {
            if(two) {
                return "lw $s1, " + offset + "($sp)\n";
            }
            return "lw $s0, " + offset + "($sp)\n";
        }
    }

    public String handleOverflowDst() {
        if (offset < 0) {
            return "";
        } else {
            if(two){
                return"sw $s1, " + offset + "($sp)\n";
            }
            return "sw $s0, " + offset + "($sp)\n";
        }
    }


    public String toString() {
        if (map != null) {
            return map.toString();
        }
        if (offset > 0) { // 溢出
            if(two) {
                return "$s1";
            }
            return "$s0";
        }
        return "$$" + count;
    }
}
