package grammar;

import ir.BasicBlock;
import ir.values.Value;
import sym.Item;
import sym.Sym;

public class Cond {
    LOrExp lOrExp;

    public Cond() {
        lOrExp = new LOrExp();
    }

    public void output() {
        lOrExp.output();
        System.out.println("<Cond>");
    }

    public void visit(BasicBlock ifTrueBlock, BasicBlock ifFalseBlock) {
        lOrExp.visit(ifTrueBlock, ifFalseBlock);
    }
}
