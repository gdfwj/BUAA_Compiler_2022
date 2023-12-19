package backend.mips.insturctions.branch;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.MipsInst;
import backend.mips.reg.VirtualReg;

public class Branch extends MipsInst {
    Operand lhs;
    Operand rhs;
    MipsBBlock jumpBlock;

    public Branch(InstType type, MipsBBlock now, Operand lhs, Operand rhs, MipsBBlock jumpBlock) {
        super(type, now);
        this.lhs = lhs;
        this.rhs = rhs;
        this.jumpBlock = jumpBlock;
        if(lhs instanceof VirtualReg) {
            ((VirtualReg) lhs).addUses(this);
        }
        if(rhs instanceof VirtualReg) {
            ((VirtualReg) rhs).addUses(this);
        }
    }

    public Operand getLhs() {
        return lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    public MipsBBlock getJumpBlock() {
        return jumpBlock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(lhs instanceof VirtualReg){
            sb.append(((VirtualReg) lhs).handleOverflowSrc(false));
        }
        if(rhs instanceof VirtualReg){
            sb.append(((VirtualReg) rhs).handleOverflowSrc(true));
        }
        if (rhs != null) {
            return sb.toString()+getType() + " " + lhs + ", " + rhs + ", " + getJumpBlock().getName() + "\n";
        } else {
            return sb.toString()+getType() + " " + lhs + ", " + getJumpBlock().getName() + "\n";
        }
    }
}
