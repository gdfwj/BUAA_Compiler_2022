package ir.passes.mips;

import backend.GenerateMIPSModel;
import backend.mips.MipsBBlock;
import backend.mips.MipsFunction;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.MipsBinary;
import backend.mips.insturctions.MipsInst;
import ir.Function;
import ir.instructions.Instruction;
import ir.passes.Pass;

public class MulDiv2SllSlr implements Pass.IRPass.MIPSPass {
    @Override
    public String getName() {
        return "MulDiv2SllSlr";
    }

    @Override
    public void run(GenerateMIPSModel generateMIPSModel) {
        for (MipsFunction i : generateMIPSModel.getFunctions()) {
            for (MipsBBlock j : i.getbBlocks()) {
                for (MipsInst k : j.getInsts()) {
                    if(k instanceof MipsBinary && k.getType()== InstType.Mul) {

                    }
                }
            }
        }
    }
}
