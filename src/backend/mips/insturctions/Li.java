package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.VirtualReg;

public class Li extends MipsInst {
    Operand dst;
    Operand imm;

    public Li(InstType type, MipsBBlock block, Operand dst, Operand imm) {
        super(type, block);
        this.dst = dst;
        this.imm = imm;
        if(dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
    }

    public Operand getDst() {
        return dst;
    }

    public Operand getImm() {
        return imm;
    }

    @Override
    public String toString() {
        if (dst instanceof VirtualReg) {
            return "li " + dst + ", " + imm + "\n" + ((VirtualReg) dst).handleOverflowDst();
        }
        return "li " + dst + ", " + imm + "\n";
    }
}
