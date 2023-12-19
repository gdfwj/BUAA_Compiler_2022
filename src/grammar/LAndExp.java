package grammar;

import ir.BasicBlock;
import ir.MyModule;
import ir.instructions.CalculateInst;
import ir.instructions.Tag;
import ir.instructions.TerminatorInst;
import ir.values.Value;
import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class LAndExp {
    private final EqExp insideEqExp;
    private final LAndExp insideLAndExp;

    public LAndExp() {
        ArrayList<EqExp> temp = new ArrayList<>();
        temp.add(new EqExp());
        while (((Item) Sym.getInstance().peek().context).id.equals("AND")) {
            Sym.getInstance().step();
            temp.add(new EqExp());
        }
        if (temp.size() == 1) {
            insideEqExp = temp.get(0);
            insideLAndExp = null;
        } else {
            insideEqExp = temp.get(temp.size() - 1);
            temp.remove(temp.size() - 1);
            insideLAndExp = new LAndExp(temp);
        }
    }

    public LAndExp(ArrayList<EqExp> temp) {
        if (temp.size() == 1) {
            insideEqExp = temp.get(0);
            insideLAndExp = null;
        } else {
            insideEqExp = temp.get(temp.size() - 1);
            temp.remove(temp.size() - 1);
            insideLAndExp = new LAndExp(temp);
        }
    }

    public void output() {
        if (insideLAndExp == null) {
            insideEqExp.output();
            System.out.println("<LAndExp>");
        } else {
            insideLAndExp.output();
            System.out.println("AND &&");
            insideEqExp.output();
            System.out.println("<LAndExp>");
        }
    }

    public void visit(BasicBlock ifTrueBlock, BasicBlock ifFalseBlock) {
        if (insideLAndExp == null) {
            Value cond = insideEqExp.visit();
            TerminatorInst.BranchInst branchInst = new TerminatorInst.BranchInst(cond, ifTrueBlock, ifFalseBlock);
            MyModule.getInstance().addInst(branchInst);
        } else {
            BasicBlock nextAnd = new BasicBlock("");
            insideLAndExp.visit(nextAnd, ifFalseBlock); // 如果是true的就跳到下一个and连接语句，如果是false的就直接跳到下一个Or连接语句
            MyModule.getInstance().addBasicBlock(nextAnd);
            Value cond = insideEqExp.visit();
            TerminatorInst.BranchInst branchInst = new TerminatorInst.BranchInst(cond, ifTrueBlock, ifFalseBlock); // 最后一条语句，如果为true则当前Or为真，可跳到最外层ifTrue
            MyModule.getInstance().addInst(branchInst);
        }
    }
}
