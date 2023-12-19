package grammar;
import sym.Item;
import sym.Sym;
public class UnaryOp {
    private Item inside;

    public UnaryOp() {
        if (((Item) Sym.getInstance().peek().context).id.equals("PLUS") ||
                ((Item) Sym.getInstance().peek().context).id.equals("MINU") ||
                ((Item) Sym.getInstance().peek().context).id.equals("NOT")) {
            inside = Sym.getInstance().peek();
        } else {
            System.out.println("UnaryOp error");
        }
        Sym.getInstance().step();
    }

    public void output() {
        System.out.println(((Item) inside.context).id + " " + ((Item) inside.context).context);
        System.out.println("<UnaryOp>");
    }

    public String whenEvaluate() {
        return (String) ((Item) inside.context).context;
    }
}
