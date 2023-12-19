package grammar;
import sym.Item;
import sym.Sym;
public class Decl {
    private ConstDecl insideConstDecl;
    private VarDecl insideVarDecl;

    public Decl() {
        if (((Item) Sym.getInstance().peek().context).id.equals("INTTK")) {
            insideVarDecl = new VarDecl();
        } else if (((Item) Sym.getInstance().peek().context).id.equals("CONSTTK")) {
            insideConstDecl = new ConstDecl();
        } else {
            System.out.println("Decl error");
        }
    }

    public void output() {
        if (insideVarDecl != null) {
            insideVarDecl.output();
        } else {
            insideConstDecl.output();
        }
    }

    public void visit() {
        if (insideVarDecl != null) {
            insideVarDecl.visit();
        } else {
            insideConstDecl.visit();
        }
    }
}
