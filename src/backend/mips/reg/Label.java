package backend.mips.reg;

import backend.mips.Operand;

public class Label extends Operand {
    private String label;
    public Label(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
