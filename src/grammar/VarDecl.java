package grammar;

import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class VarDecl {
    private final VarDef insideVarDef;
    private final ArrayList<VarDef> moreVarDef = new ArrayList<>();

    public VarDecl() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("INTTK")) {
//            System.out.println("VarDecl error");
        }
        Sym.getInstance().step();
        insideVarDef = new VarDef();
        while (((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
            Sym.getInstance().step();
            moreVarDef.add(new VarDef());
        }
        if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
            System.out.println(Sym.getInstance().peekLast().id + " i");
        } else {
            Sym.getInstance().step();
        }
    }

    public void output() {
        System.out.println("INTTK int");
        insideVarDef.output();
        for (VarDef i : moreVarDef) {
            System.out.println("COMMA ,");
            i.output();
        }
        System.out.println("SEMICN ;");
        System.out.println("<VarDecl>");
    }

    public void visit() {
        insideVarDef.visit();
        for (VarDef i : moreVarDef) {
            i.visit();
        }
    }
}
