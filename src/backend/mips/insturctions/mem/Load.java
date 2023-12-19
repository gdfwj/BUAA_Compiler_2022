package backend.mips.insturctions.mem;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.MipsInst;
import backend.mips.reg.Imm;
import backend.mips.reg.Label;
import backend.mips.reg.VirtualReg;

public class Load extends MipsInst {
    Operand ptr;
    Operand dst;
    Operand offset;
    boolean fromGEP = false;

    public Load(InstType type, MipsBBlock block, Operand ptr, Operand dst) {
        super(type, block);
        this.ptr = ptr;
        this.dst = dst;
        offset = new Imm(0);
        if (dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
        if (ptr instanceof VirtualReg) {
            ((VirtualReg) ptr).addUses(this);
        }
    }

    public Load(InstType type, MipsBBlock block, Operand ptr, Operand dst, Operand offset) {
        super(type, block);
        this.ptr = ptr;
        this.dst = dst;
        this.offset = offset;
        if (dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
        if (ptr instanceof VirtualReg) {
            ((VirtualReg) ptr).addUses(this);
        }
    }

    public void setGlobal(boolean global) {
        this.fromGEP = global;
    }

    public Operand getPtr() {
        return ptr;
    }

    public Operand getDst() {
        return dst;
    }

    public Operand getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (ptr instanceof VirtualReg) {
            sb.append(((VirtualReg) ptr).handleOverflowSrc(false));
        }
        sb.append(getType() + " " + dst + ", ");
        if (offset instanceof Imm && !fromGEP) {
            sb.append(((Imm) offset).getImm() + getBlock().getOverflowOffset());
        } else{
            sb.append(offset);
        }
        sb.append("(" + ptr + ")\n");
        if (dst instanceof VirtualReg) {
            sb.append(((VirtualReg) dst).handleOverflowDst());
        }
        return sb.toString();
    }
}
