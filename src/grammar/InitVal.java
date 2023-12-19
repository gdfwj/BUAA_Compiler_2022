package grammar;
import sym.Item;
import sym.Sym;
import symbol.SymbolTable;

import java.util.ArrayList;

public class InitVal {
    private final int type;
    private InitVal firstInitVal;
    private final ArrayList<InitVal> initVal = new ArrayList<>();
    private Exp insideExp;

    public InitVal() {
        if (((Item) Sym.getInstance().peek().context).id.equals("LBRACE")) {
            type = 1;
            Sym.getInstance().step();
            if (!((Item) Sym.getInstance().peek().context).id.equals("RBRACE")) {
                firstInitVal = new InitVal();
                while (!((Item) Sym.getInstance().peek().context).id.equals("RBRACE")) {
                    if (!((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
//                        System.out.println("InitVal error");
                    }
                    Sym.getInstance().step();
                    initVal.add(new InitVal());
                }
            }
            Sym.getInstance().step();
        } else {
            type = 2;
            insideExp = new Exp();
        }
    }

    public void output() {
        if (type == 1) {
            System.out.println("LBRACE {");
            if (firstInitVal != null) {
                firstInitVal.output();
            }
            for (InitVal i : initVal) {
                System.out.println("COMMA ,");
                i.output();
            }
            System.out.println("RBRACE }");
        } else {
            insideExp.output();
        }
        System.out.println("<InitVal>");
    }

    public ArrayList<Exp> visit() {
        ArrayList<Exp> ans = new ArrayList<>();
        if(type==1) {
            ans.addAll(firstInitVal.visit());
            for(InitVal i:initVal) {
                ans.addAll(i.visit());
            }
        } else {
            ans.add(insideExp);
        }
        return ans;
    }

    public ArrayList<Integer> evaluate() {
        assert SymbolTable.getInstance().isGlobal;
        ArrayList<Integer> ans = new ArrayList<>();
        if(type==2) {
            ans.add(insideExp.evaluate());
            return ans;
        }
        ans.addAll(firstInitVal.evaluate());
        for(InitVal i:initVal){
            ans.addAll(i.evaluate());
        }
        return ans;
    }
}
