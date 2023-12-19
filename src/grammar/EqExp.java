package grammar;

import ir.MyModule;
import ir.instructions.CalculateInst;
import ir.instructions.Tag;
import ir.values.Value;
import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class EqExp {
    private final EqExp insideEqExp;
    private final RelExp insideRelExp;
    private final Item insidePlusMinus;

    public EqExp() {
        ArrayList<RelExp> temp = new ArrayList<>();
        ArrayList<Item> sym = new ArrayList<>();
        temp.add(new RelExp());
        while (((Item) Sym.getInstance().peek().context).id.equals("NEQ") ||
                ((Item) Sym.getInstance().peek().context).id.equals("EQL")) {
            sym.add(Sym.getInstance().peek());
            Sym.getInstance().step();
            temp.add(new RelExp());
        }
        if (temp.size() == 1) {
            insideRelExp = temp.get(0);
            insideEqExp = null;
            insidePlusMinus = null;
        } else {
            insideRelExp = temp.get(temp.size() - 1);
            insidePlusMinus = sym.get(sym.size() - 1);
            sym.remove(sym.size() - 1);
            temp.remove(temp.size() - 1);
            insideEqExp = new EqExp(temp, sym);
        }
    }

    public EqExp(ArrayList<RelExp> temp, ArrayList<Item> sym) {
        if (temp.size() == 1) {
            insideRelExp = temp.get(0);
            insideEqExp = null;
            insidePlusMinus = null;
        } else {
            insideRelExp = temp.get(temp.size() - 1);
            insidePlusMinus = sym.get(sym.size() - 1);
            sym.remove(sym.size() - 1);
            temp.remove(temp.size() - 1);
            insideEqExp = new EqExp(temp, sym);
        }
    }

    public void output() {
        if (insideEqExp == null) {
            insideRelExp.output();
            System.out.println("<EqExp>");
        } else {
            insideEqExp.output();
            System.out.println(((Item) insidePlusMinus.context).id + " " + ((Item) insidePlusMinus.context).context);
            insideRelExp.output();
            System.out.println("<EqExp>");
        }
    }

    public Value visit() {
        if (insideEqExp == null) {
            return insideRelExp.visit();
        } else {
            Value x = insideEqExp.visit();
            Value y = insideRelExp.visit();
            CalculateInst.BinaryInst binaryInst;
            if (((Item) insidePlusMinus.context).id.equals("NEQ")) {
                binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Neq);
            } else {
                assert ((Item) insidePlusMinus.context).id.equals("EQL");
                binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Eql);
            }
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        }
    }
}
