package grammar;

import ir.BasicBlock;
import ir.MyModule;
import ir.instructions.CalculateInst;
import ir.instructions.Tag;
import ir.values.Value;
import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class LOrExp {
    private final LOrExp insideLOrExp;
    private final LAndExp insideLAndExp;

    public LOrExp() {
        ArrayList<LAndExp> temp = new ArrayList<>();
        temp.add(new LAndExp());
        while (((Item) Sym.getInstance().peek().context).id.equals("OR")) {
            Sym.getInstance().step();
            temp.add(new LAndExp());
        }
        if (temp.size() == 1) {
            insideLAndExp = temp.get(0);
            insideLOrExp = null;
        } else {
            insideLAndExp = temp.get(temp.size() - 1);
            temp.remove(temp.size() - 1);
            insideLOrExp = new LOrExp(temp);
        }
    }

    public LOrExp(ArrayList<LAndExp> temp) {
        if (temp.size() == 1) {
            insideLAndExp = temp.get(0);
            insideLOrExp = null;
        } else {
            insideLAndExp = temp.get(temp.size() - 1);
            temp.remove(temp.size() - 1);
            insideLOrExp = new LOrExp(temp);
        }
    }

    public void output() {
        if (insideLOrExp == null) {
            insideLAndExp.output();
            System.out.println("<LOrExp>");
        } else {
            insideLOrExp.output();
            System.out.println("OR ||");
            insideLAndExp.output();
            System.out.println("<LOrExp>");
        }
    }

    public void visit(BasicBlock ifTrueBlock, BasicBlock ifFalseBlock) {
        if (insideLOrExp == null) { // fixed 短路求值
            insideLAndExp.visit(ifTrueBlock, ifFalseBlock); // 如果正确就跳到正确位置，否则跳到错误位置
        } else {
            BasicBlock nextOr = new BasicBlock("");
            insideLOrExp.visit(ifTrueBlock, nextOr); // 如果正确就跳到正确位置(短路)，如果错误就跳到下一条or位置
            MyModule.getInstance().addBasicBlock(nextOr);
            insideLAndExp.visit(ifTrueBlock, ifFalseBlock); // 最后一块，同理
        }
    }
}
