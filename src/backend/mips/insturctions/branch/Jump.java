package backend.mips.insturctions.branch;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.MipsInst;
import backend.mips.reg.VirtualReg;

public class Jump extends MipsInst { // 只操作函数内部
    private MipsBBlock jumpTo;
    private Operand jumpReg;

    public Jump(InstType type, MipsBBlock now, MipsBBlock jumpTo) {
        super(type, now);
        this.jumpTo = jumpTo;
    }

    public Operand getJumpReg() {
        return jumpReg;
    }

    public Jump(InstType type, MipsBBlock now, Operand jumpReg) {
        super(type, now);
        this.jumpReg = jumpReg;
        if(jumpReg instanceof VirtualReg) {
            ((VirtualReg) jumpReg).addUses(this);
        }
    }

    @Override
    public String toString() {
        if (jumpReg == null) {
            return getType() + " " + jumpTo.getName() + "\n";
        } else {
            return getType() + " " + jumpReg+"\n";
        }
    }
}
