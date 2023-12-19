package grammar;
import sym.Item;
import sym.Sym;
public class BlockItem {
    private Decl decl;
    private Stmt stmt;
    public BlockItem() {
        if(((Item) Sym.getInstance().peek().context).id.equals("CONSTTK") ||
                ((Item) Sym.getInstance().peek().context).id.equals("INTTK")) {
            decl = new Decl();
        } else {
            stmt = new Stmt();
        }
    }

    public void output() {
        if(decl!=null) {
            decl.output();
        } else {
            stmt.output();
        }
    }

    public void visit() {
        if(decl!=null) {
            decl.visit();
        } else {
            stmt.visit();
        }
    }
}
