package ir.passes;

import backend.GenerateMIPSModel;
import ir.MyModule;
import ir.passes.mips.Div2Mul;
import ir.passes.mips.Mem2Reg;

import java.util.ArrayList;

public class PassModule {
    private static final PassModule INSTANCE = new PassModule();
    private final ArrayList<Pass.IRPass> irPasses = new ArrayList<>();
    private final ArrayList<Pass.MIPSPass> mipsPasses = new ArrayList<>();

    public static PassModule getInstance() {
        return INSTANCE;
    }

    private PassModule() {
        irPasses.add(new Mem2Reg());
//        irPasses.add(new Div2Mul());
    }

    public void run1() {
        for (Pass.IRPass i : irPasses) {
            i.run(MyModule.getInstance());
        }
    }

    public void run2() {
        for (Pass.MIPSPass i : mipsPasses) {
            i.run(GenerateMIPSModel.getInstance());
        }
    }
}
