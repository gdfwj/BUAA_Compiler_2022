package grammar;
import ir.values.Value;
import sym.Item;
import sym.Sym;
public class Exp {
    private final AddExp inside;

    public Exp() {
        inside = new AddExp();
    }

    public void output() {
        inside.output();
        System.out.println("<Exp>");
    }

    public int evaluate(){
        return inside.evaluate();
    }

    public Value visit() {
        return inside.visit();
    }
}
