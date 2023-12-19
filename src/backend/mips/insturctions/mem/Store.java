package backend.mips.insturctions.mem;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.La;
import backend.mips.insturctions.MipsInst;
import backend.mips.reg.Imm;
import backend.mips.reg.Label;
import backend.mips.reg.VirtualReg;

public class Store extends MipsInst {
    Operand ptr;
    Operand src;
    Operand offset;
    boolean global = false;

    public Store(InstType type, MipsBBlock block, Operand ptr, Operand src) {
        super(type, block);
        this.ptr = ptr;
        this.src = src;
        this.offset = new Imm(0);
        if (src instanceof VirtualReg) {
            ((VirtualReg) src).addUses(this);
        }
        if (ptr instanceof VirtualReg) {
            ((VirtualReg) ptr).addUses(this);
        }
    }

    public Store(InstType type, MipsBBlock block, Operand ptr, Operand src, Operand offset) {
        super(type, block);
        this.ptr = ptr;
        this.src = src;
        this.offset = offset;
        if (src instanceof VirtualReg) {
            ((VirtualReg) src).addUses(this);
        }
        if (ptr instanceof VirtualReg) {
            ((VirtualReg) ptr).addUses(this);
        }
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Operand getPtr() {
        return ptr;
    }

    public Operand getSrc() {
        return src;
    }

    public Operand getOffset() {
        return offset;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (src instanceof VirtualReg) {
            sb.append(((VirtualReg) src).handleOverflowSrc(false));
        }
        if (ptr instanceof VirtualReg) {
            sb.append(((VirtualReg) ptr).handleOverflowSrc(true));
        }
        sb.append(getType() + " " + src + ", ");
        if (offset instanceof Imm && !global) {
            sb.append(((Imm) offset).getImm() + getBlock().getOverflowOffset());
        } else {
            sb.append(offset);
        }
        sb.append("(" + ptr + ")\n");
        return sb.toString();
    }
}
