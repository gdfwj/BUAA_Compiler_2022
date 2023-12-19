package grammar;
import ir.MyModule;
import ir.instructions.CalculateInst.BinaryInst;
import ir.instructions.Tag;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import java.util.ArrayList;

public class AddExp {
    private final AddExp insideAddExp;
    private final MulExp insideMulExp;
    private final Item insidePlusMinus;
    public AddExp() {
        ArrayList<MulExp> temp = new ArrayList<>();
        ArrayList<Item> sym = new ArrayList<>();
        temp.add(new MulExp());
        while(((Item)Sym.getInstance().peek().context).id.equals("PLUS")||
                ((Item)Sym.getInstance().peek().context).id.equals("MINU")) {
            sym.add(Sym.getInstance().peek());
            Sym.getInstance().step();
            temp.add(new MulExp());
        }
        if(temp.size()==1) {
            insideMulExp = temp.get(0);
            insideAddExp=null;
            insidePlusMinus = null;
        } else {
            insideMulExp = temp.get(temp.size()-1);
            insidePlusMinus = sym.get(sym.size()-1);
            sym.remove(sym.size()-1);
            temp.remove(temp.size()-1);
            insideAddExp=new AddExp(temp, sym);
        }
    }
    public AddExp(ArrayList<MulExp> temp, ArrayList<Item>sym) {
        if(temp.size()==1) {
            insideMulExp = temp.get(0);
            insideAddExp=null;
            insidePlusMinus = null;
        } else {
            insideMulExp = temp.get(temp.size()-1);
            insidePlusMinus = sym.get(sym.size()-1);
            sym.remove(sym.size()-1);
            temp.remove(temp.size()-1);
            insideAddExp=new AddExp(temp, sym);
        }
    }
    public void output() {
        if(insideAddExp==null) {
            insideMulExp.output();
            System.out.println("<AddExp>");
        } else {
            insideAddExp.output();
            System.out.println(((Item) insidePlusMinus.context).id + " " + ((Item) insidePlusMinus.context).context);
            insideMulExp.output();
            System.out.println("<AddExp>");
        }
    }

    public int evaluate() {
        if(insideAddExp==null) {
            return insideMulExp.evaluate();
        }
        if(((Item) insidePlusMinus.context).id.equals("PLUS")) {
            return insideAddExp.evaluate()+insideMulExp.evaluate();
        } else {
            return insideAddExp.evaluate()-insideMulExp.evaluate();
        }
    }

    public Value visit() {
        if(insideAddExp==null) {
            return insideMulExp.visit();
        }
        if(((Item) insidePlusMinus.context).id.equals("PLUS")) {
            Value x = insideAddExp.visit();
            Value y = insideMulExp.visit();
            BinaryInst binaryInst = new BinaryInst(x, y, Tag.Add);
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        } else {
            Value x = insideAddExp.visit();
            Value y = insideMulExp.visit();
            BinaryInst binaryInst = new BinaryInst(x, y, Tag.Sub);
            MyModule.getInstance().addInst(binaryInst);
            return binaryInst;
        }
    }
}
