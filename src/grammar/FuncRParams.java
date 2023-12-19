package grammar;
import ir.types.Type;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import java.util.ArrayList;

public class FuncRParams {
    private final Exp insideFirstExp;
    private final Type firstType;
    private final ArrayList<Exp> insideExps = new ArrayList<>();
    private final ArrayList<Type> types = new ArrayList<>();
    public FuncRParams() {
        firstType = Sym.getInstance().getExpType();
        insideFirstExp = new Exp();
        while(((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
            Sym.getInstance().step();
            types.add(Sym.getInstance().getExpType());
            insideExps.add(new Exp());
        }
    }
    public void output() {
        insideFirstExp.output();
        for(Exp i:insideExps) {
            System.out.println("COMMA ,");
            i.output();
        }
        System.out.println("<FuncRParams>");
    }

    public ArrayList<Type> getParams() {
        ArrayList<Type> res = new ArrayList<>();
        res.add(firstType);
        res.addAll(types);
        return res;
    }

    public ArrayList<Value> visit() {
        ArrayList<Value> ans = new ArrayList<>();
        ans.add(insideFirstExp.visit());
        for(Exp i:insideExps) {
            ans.add(i.visit());
        }
        return ans;
    }
}
