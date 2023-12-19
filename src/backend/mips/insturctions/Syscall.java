package backend.mips.insturctions;

import backend.mips.MipsBBlock;

public class Syscall extends MipsInst{
    public Syscall(MipsBBlock block) {
        super(InstType.Syscall, block);
    }

    @Override
    public String toString() {
        return "syscall\n";
    }
}
