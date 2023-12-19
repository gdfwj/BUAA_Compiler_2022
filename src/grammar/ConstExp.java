package grammar;
import sym.Item;
import sym.Sym;
public class ConstExp {
    private final AddExp insideAddExp;
    public ConstExp() {
        insideAddExp = new AddExp();
    }
    public void output() {
        insideAddExp.output();
        System.out.println("<ConstExp>");
    }

    public int evaluate() {
        return insideAddExp.evaluate();
    }
}
