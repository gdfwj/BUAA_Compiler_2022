package grammar;

import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import java.util.ArrayList;

public class CompUnit {
    private final ArrayList<Decl> decls = new ArrayList<>();
    private final ArrayList<FuncDef> funcDefs = new ArrayList<>();
    private final MainFuncDef mainFuncDef;

    public CompUnit() {
        while ((((Item) Sym.getInstance().peek().context).id.equals("INTTK") &&
                ((Item) Sym.getInstance().peekNext().context).id.equals("IDENFR") &&
                (!((Item) Sym.getInstance().peekNext2().context).id.equals("LPARENT"))) ||
                ((Item) Sym.getInstance().peek().context).id.equals("CONSTTK")) {
            decls.add(new Decl());
        }
        SymbolTable.getInstance().isGlobal = false;
        while ((((Item) Sym.getInstance().peek().context).id.equals("INTTK") &&
                ((Item) Sym.getInstance().peekNext().context).id.equals("IDENFR")) ||
                ((Item) Sym.getInstance().peek().context).id.equals("VOIDTK")) {
            funcDefs.add(new FuncDef());
        }
        mainFuncDef = new MainFuncDef();
    }

    public void output() {
        for (Decl i : decls) {
            i.output();
        }
        for (FuncDef i : funcDefs) {
            i.output();
        }
        mainFuncDef.output();
        System.out.println("<CompUnit>");
    }

    public void visit() {
        for (Decl i : decls) {
            i.visit();
        }
        IrSymbol.getInstance().inGlobal = false;
        for (FuncDef i : funcDefs) {
            i.visit();
        }
        mainFuncDef.visit();
    }
}
