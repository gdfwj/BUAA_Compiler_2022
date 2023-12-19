package backend.mips.reg;

import backend.mips.Operand;

public class Imm extends Operand {
    private Integer imm;
    public Imm(int imm) {
        this.imm = imm;
    }

    public Integer getImm() {
        return imm;
    }

    @Override
    public String toString() {
        return imm.toString();
    }
}
