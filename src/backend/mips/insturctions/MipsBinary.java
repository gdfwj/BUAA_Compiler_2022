package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.Imm;
import backend.mips.reg.VirtualReg;

public class MipsBinary extends MipsInst {
    Operand dst;
    Operand lhs;
    Operand rhs;
    boolean needOverflow;

    public MipsBinary(InstType type, MipsBBlock block, Operand dst, Operand lhs, Operand rhs) {
        super(type, block);
        this.dst = dst;
        this.lhs = lhs;
        this.rhs = rhs;
        if (dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
        if (lhs instanceof VirtualReg) {
            ((VirtualReg) lhs).addUses(this);
        }
        if (rhs instanceof VirtualReg) {
            ((VirtualReg) rhs).addUses(this);
        }
    }

    public void setNeedOverflow(boolean needOverflow) {
        this.needOverflow = needOverflow;
    }

    public Operand getDst() {
        return dst;
    }

    public Operand getLhs() {
        return lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lhs instanceof VirtualReg) {
            sb.append(((VirtualReg) lhs).handleOverflowSrc(false));
        }
        if (rhs instanceof VirtualReg) {
            sb.append(((VirtualReg) rhs).handleOverflowSrc(true));
        }
        if(needOverflow) {
            assert rhs instanceof Imm;
            rhs = new Imm(((Imm) rhs).getImm()+ getBlock().getOverflowOffset());
        }
        if (dst != null) {
            sb.append(type + " " + dst + ", " + lhs + ", " + rhs + "\n");
        } else {
            assert type == InstType.Div;
            sb.append(type + " " + lhs + ", " + rhs + "\n");
        }
        if(dst instanceof VirtualReg) {
            sb.append(((VirtualReg) dst).handleOverflowDst());
        }
        return sb.toString();
    }
}
