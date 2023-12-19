package grammar;

import sym.Item;
import sym.Sym;

public class FuncType {
    private final Item type;

    public FuncType() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("INTTK") &&
                !((Item) Sym.getInstance().peek().context).id.equals("VOIDTK")) {
//            System.out.println("FuncType error");
        }
        type = Sym.getInstance().peek();
        Sym.getInstance().step();
    }

    public void output() {
        System.out.println(((Item) type.context).id + " " + ((Item) type.context).context);
        System.out.println("<FuncType>");
    }

    public boolean isVoid() {
        return ((String) ((Item) type.context).id).equals(("VOIDTK"));
    }
}
