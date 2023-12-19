package backend.mips.insturctions;

import backend.mips.MipsBBlock;
import backend.mips.Operand;
import backend.mips.reg.Label;
import backend.mips.reg.VirtualReg;

public class La extends MipsInst {
    Operand dst;
    Label label;

    public La(MipsBBlock block, Operand dst, Label label) {
        super(InstType.La, block);
        this.dst = dst;
        this.label = label;
        if (dst instanceof VirtualReg) {
            ((VirtualReg) dst).setDefInstr(this);
        }
    }

    public Operand getDst() {
        return dst;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public String toString() {
        if (dst instanceof VirtualReg) {
            return "la " + dst + ", " + label + "\n" + ((VirtualReg) dst).handleOverflowDst();
        }
        return "la " + dst + ", " + label + "\n";
    }
}
