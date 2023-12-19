package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.VirtualReg;

public class Move extends MipsInst{
    Operand dst;
    Operand src;

    public Move(InstType type, MipsBBlock block, Operand dst, Operand src) {
        super(type, block);
        this.dst = dst;
        this.src = src;
        if(dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
        if(src instanceof VirtualReg) {
            ((VirtualReg) src).addUses(this);
        }
    }

    public Operand getSrc() {
        return src;
    }

    public Operand getDst() {
        return dst;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(src instanceof VirtualReg) {
            sb.append(((VirtualReg) src).handleOverflowSrc(false));
        }
        sb.append("move " + dst + ", " + src + "\n");
        if(dst instanceof VirtualReg) {
            sb.append(((VirtualReg) dst).handleOverflowDst());
        }
        return sb.toString();
    }
}
