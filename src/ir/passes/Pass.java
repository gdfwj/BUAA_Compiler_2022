package ir.passes;

import backend.GenerateMIPSModel;
import ir.MyModule;

public interface Pass {
    String getName();

    interface MIPSPass extends Pass {
        void run(GenerateMIPSModel generateMIPSModel);
    }

    interface IRPass extends Pass {
        void run(MyModule m);
    }
}
