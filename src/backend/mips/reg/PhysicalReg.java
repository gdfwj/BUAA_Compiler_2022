package backend.mips.reg;

public class PhysicalReg extends Reg{
    private String name;
    private int id;
    public PhysicalReg(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
