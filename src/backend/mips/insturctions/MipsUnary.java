package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.VirtualReg;

public class MipsUnary extends MipsInst {
    Operand dst;

    public MipsUnary(InstType type, MipsBBlock block, Operand dst) {
        super(type, block);
        this.dst = dst;
        if(dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
    }

    public Operand getDst() {
        return dst;
    }

    @Override
    public String toString() {
        String dstString="";
        if(dst instanceof VirtualReg) {
            dstString = ((VirtualReg) dst).handleOverflowDst();
        }
        return type + " " + dst + "\n"+dstString;
    }
}
