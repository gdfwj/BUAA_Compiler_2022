package grammar;

import ir.MyModule;
import ir.instructions.CalculateInst;
import ir.instructions.Tag;
import ir.values.Value;
import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class MulExp {
    private final UnaryExp insideUnaryExp;
    private final MulExp insideMulExp;
    private final Item insidePlusMinus;

    public MulExp() {
        ArrayList<UnaryExp> temp = new ArrayList<>();
        ArrayList<Item> sym = new ArrayList<>();
        temp.add(new UnaryExp());
        while (((Item) Sym.getInstance().peek().context).id.equals("MULT") ||
                ((Item) Sym.getInstance().peek().context).id.equals("DIV") ||
                ((Item) Sym.getInstance().peek().context).id.equals("MOD")) {
            sym.add(Sym.getInstance().peek());
            Sym.getInstance().step();
            temp.add(new UnaryExp());
        }
        if (temp.size() == 1) {
            insideUnaryExp = temp.get(0);
            insideMulExp = null;
            insidePlusMinus = null;
        } else {
            insideUnaryExp = temp.get(temp.size() - 1);
            insidePlusMinus = sym.get(sym.size() - 1);
            sym.remove(sym.size() - 1);
            temp.remove(temp.size() - 1);
            insideMulExp = new MulExp(temp, sym);
        }
    }

    public MulExp(ArrayList<UnaryExp> temp, ArrayList<Item> sym) {
        if (temp.size() == 1) {
            insideUnaryExp = temp.get(0);
            insideMulExp = null;
            insidePlusMinus = null;
        } else {
            insideUnaryExp = temp.get(temp.size() - 1);
            insidePlusMinus = sym.get(sym.size() - 1);
            sym.remove(sym.size() - 1);
            temp.remove(temp.size() - 1);
            insideMulExp = new MulExp(temp, sym);
        }
    }

    public void output() {
        if (insideMulExp == null) {
            insideUnaryExp.output();
            System.out.println("<MulExp>");
        } else {
            insideMulExp.output();
            System.out.println(((Item) insidePlusMinus.context).id + " " + ((Item) insidePlusMinus.context).context);
            insideUnaryExp.output();
            System.out.println("<MulExp>");
        }
    }

    public int evaluate() {
        if (insideMulExp == null) {
            return insideUnaryExp.evaluate();
        }
        if (((Item) insidePlusMinus.context).context.equals("*")) {
            return insideMulExp.evaluate() * insideUnaryExp.evaluate();
        }
        if (((Item) insidePlusMinus.context).context.equals("/")) {
            return insideMulExp.evaluate() / insideUnaryExp.evaluate();
        } else {
            return insideMulExp.evaluate() % insideUnaryExp.evaluate();
        }
    }

    public Value visit() {
        if (insideMulExp == null) {
            return insideUnaryExp.visit();
        }
        if (((Item) insidePlusMinus.context).context.equals("*")) {
            Value x = insideMulExp.visit();
            Value y = insideUnaryExp.visit();
            CalculateInst.BinaryInst binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Mul);
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        }
        if (((Item) insidePlusMinus.context).context.equals("/")) {
            Value x = insideMulExp.visit();
            Value y = insideUnaryExp.visit();
            CalculateInst.BinaryInst binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Div);
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        } else {
            Value x = insideMulExp.visit();
            Value y = insideUnaryExp.visit();
            CalculateInst.BinaryInst binaryInst = new CalculateInst.BinaryInst(x, y, Tag.Mod);
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        }
    }
}
