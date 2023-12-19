package grammar;
import ir.instructions.ConstantInteger;
import ir.values.Value;
import sym.Item;
import sym.Sym;
public class Number {
    private String inside;

    public Number() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("INTCON")) {
            System.out.println("Number error");
        } else {
            inside = ((String) ((Item) Sym.getInstance().peek().context).context);
            Sym.getInstance().step();
        }
    }

    public void output() {
        System.out.println("INTCON " + inside);
        System.out.println("<Number>");
    }

    public int evaluate(){
        return Integer.parseInt(inside);
    }

    public Value visit() {
        return new ConstantInteger(evaluate());
    }
}
