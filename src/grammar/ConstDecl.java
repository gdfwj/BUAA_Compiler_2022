package grammar;

import sym.Item;
import sym.Sym;

import java.util.ArrayList;

public class ConstDecl {
    private final ConstDef constDef;
    private final ArrayList<ConstDef> moreConstDef = new ArrayList<>();

    public ConstDecl() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("CONSTTK")) {
            System.out.println("ConstDecl error");
        }
        Sym.getInstance().step();
        if (!((Item) Sym.getInstance().peek().context).id.equals("INTTK")) {
            System.out.println("ConstDecl error");
        }
        Sym.getInstance().step();
        constDef = new ConstDef();
        while (((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
            Sym.getInstance().step();
            moreConstDef.add(new ConstDef());
        }
        if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
            System.out.println(Sym.getInstance().peekLast().id + " i");
        } else {
            Sym.getInstance().step();
        }
    }

    public void output() {
        System.out.println("CONSTTK const");
        System.out.println("INTTK int");
        constDef.output();
        for (ConstDef i : moreConstDef) {
            System.out.println("COMMA ,");
            i.output();
        }
        System.out.println("SEMICN ;");
        System.out.println("<ConstDecl>");
    }

    public void visit() {
        constDef.visit();
        for (ConstDef i : moreConstDef) {
            i.visit();
        }
    }
}
