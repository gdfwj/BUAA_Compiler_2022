package ir.passes.mips;

import ir.BasicBlock;
import ir.Function;
import ir.MyModule;
import ir.instructions.*;
import ir.passes.Pass;

public class Div2Mul implements Pass.IRPass {
    @Override
    public void run(MyModule module) {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                for (Instruction instruction : block.getInstructions()) {
//                    if (instruction instanceof CalculateInst.BinaryInst && instruction.getTag() == Tag.Div
//                            && ((CalculateInst.BinaryInst) instruction).getOp2() instanceof ConstantInteger) {
//                        int c = ((ConstantInteger) ((CalculateInst.BinaryInst) instruction).getOp2()).getValue();
//                        int k = -1;
//                        for (int temp = c; temp > 0; temp = temp / 2) {
//                            k++;
//                        }
//                        int m = (2<<(32+k))/c;
//                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Div2Mul";
    }
}
