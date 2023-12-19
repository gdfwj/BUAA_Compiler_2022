package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.PhysicalReg;
import backend.mips.reg.VirtualReg;

public class MipsInst {
    InstType type;
    MipsBBlock block;
    public MipsInst(InstType type, MipsBBlock block) {
        this.type = type;
        this.block = block;
    }

    public InstType getType() {
        return type;
    }

    public MipsBBlock getBlock() {
        return block;
    }

    public Operand getDst() {
        assert false;
        return new PhysicalReg("null", 111);
    }
}
