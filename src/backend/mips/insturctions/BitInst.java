package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.Imm;
import backend.mips.reg.VirtualReg;

public class BitInst extends MipsInst {
    Operand dst;
    Operand src;
    Operand imm;

    public BitInst(InstType type, MipsBBlock block, Operand dst, Operand src, Imm imm) {
        super(type, block);
        this.dst = dst;
        this.src = src;
        this.imm = imm;
    }

    public Operand getDst() {
        return dst;
    }

    public void setDst(Operand dst) {
        this.dst = dst;
    }

    public Operand getSrc() {
        return src;
    }

    public void setSrc(Operand src) {
        this.src = src;
    }

    public Operand getImm() {
        return imm;
    }

    public void setImm(Operand imm) {
        this.imm = imm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (src instanceof VirtualReg) {
            sb.append(((VirtualReg) src).handleOverflowSrc(false));
        }
        sb.append(type + " " + dst + ", " + src + ", " + imm + "\n");
        if(dst instanceof VirtualReg) {
            sb.append(((VirtualReg) dst).handleOverflowDst());
        }
        return sb.toString();
    }
}
