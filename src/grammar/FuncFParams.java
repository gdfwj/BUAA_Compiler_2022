package grammar;
import ir.types.Type;
import sym.Item;
import sym.Sym;
import java.util.ArrayList;

public class FuncFParams {
    private final FuncFParam firstFuncFParam;
    private final ArrayList<FuncFParam> funcFParams = new ArrayList<>();
    public FuncFParams() {
        firstFuncFParam = new FuncFParam();
        while(((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
            Sym.getInstance().step();
            funcFParams.add(new FuncFParam());
        }
    }

    public void output() {
        firstFuncFParam.output();
        for(FuncFParam i: funcFParams) {
            System.out.println("COMMA ,");
            i.output();
        }
        System.out.println("<FuncFParams>");
    }

    public ArrayList<Type> getParams() {
        ArrayList<Type> res = new ArrayList<>();
        res.add(firstFuncFParam.getParam());
        for(FuncFParam i:funcFParams) {
            res.add(i.getParam());
        }
        return res;
    }

    public void visit() {
        firstFuncFParam.visit();
        for(FuncFParam i: funcFParams) {
            i.visit();
        }
    }
}
