package backend.mips.insturctions.branch;

import backend.mips.MipsBBlock;
import backend.mips.MipsFunction;
import backend.mips.Operand;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.MipsInst;
import backend.mips.reg.VirtualReg;

public class Call extends MipsInst {
    MipsFunction function;

    public Call(InstType type, MipsBBlock block, MipsFunction function) {
        super(type, block);
        this.function = function;
    }

    @Override
    public String toString() {
        return "jal " + function.getName() + "\n";
    }
}
