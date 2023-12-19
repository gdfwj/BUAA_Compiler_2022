package grammar;

import ir.MyModule;
import ir.instructions.CalculateInst;
import ir.instructions.Tag;
import ir.values.Value;
import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class RelExp {
    private final AddExp insideAddExp;
    private final RelExp insideRelExp;
    private final Item insidePlusMinus;

    public RelExp() {
        ArrayList<AddExp> temp = new ArrayList<>();
        ArrayList<Item> sym = new ArrayList<>();
        temp.add(new AddExp());
        while (((Item) Sym.getInstance().peek().context).id.equals("LEQ") ||
                ((Item) Sym.getInstance().peek().context).id.equals("LSS") ||
                ((Item) Sym.getInstance().peek().context).id.equals("GEQ") ||
                ((Item) Sym.getInstance().peek().context).id.equals("GRE")) {
            sym.add(Sym.getInstance().peek());
            Sym.getInstance().step();
            temp.add(new AddExp());
        }
        if (temp.size() == 1) {
            insideAddExp = temp.get(0);
            insideRelExp = null;
            insidePlusMinus = null;
        } else {
            insideAddExp = temp.get(temp.size() - 1);
            insidePlusMinus = sym.get(sym.size() - 1);
            sym.remove(sym.size() - 1);
            temp.remove(temp.size() - 1);
            insideRelExp = new RelExp(temp, sym);
        }
    }

    public RelExp(ArrayList<AddExp> temp, ArrayList<Item> sym) {
        if (temp.size() == 1) {
            insideAddExp = temp.get(0);
            insideRelExp = null;
            insidePlusMinus = null;
        } else {
            insideAddExp = temp.get(temp.size() - 1);
            insidePlusMinus = sym.get(sym.size() - 1);
            sym.remove(sym.size() - 1);
            temp.remove(temp.size() - 1);
            insideRelExp = new RelExp(temp, sym);
        }
    }

    public void output() {
        if (insideRelExp == null) {
            insideAddExp.output();
            System.out.println("<RelExp>");
        } else {
            insideRelExp.output();
            System.out.println(((Item) insidePlusMinus.context).id + " " + ((Item) insidePlusMinus.context).context);
            insideAddExp.output();
            System.out.println("<RelExp>");
        }
    }

    public Value visit() {
        if (insideRelExp == null) {
            return insideAddExp.visit();
        } else {
            Value x = insideRelExp.visit();
            Value y = insideAddExp.visit();
            CalculateInst.BinaryInst binaryInst;
            if (((Item) insidePlusMinus.context).id.equals("LEQ")) {
                binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Leq);
            } else if (((Item) insidePlusMinus.context).id.equals("LSS")) {
                binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Lss);
            } else if (((Item) insidePlusMinus.context).id.equals("GEQ")) {
                binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Geq);
            } else {
                assert ((Item) insidePlusMinus.context).id.equals("GRE");
                binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Gre);
            }
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        }
    }
}
