package grammar;
import sym.Item;
import sym.Sym;
import java.util.ArrayList;

public class ConstInitVal {
    private final int type;
    private ConstInitVal firstInitVal;
    private final ArrayList<ConstInitVal> initVal = new ArrayList<>();
    private ConstExp insideExp;

    public ConstInitVal() {
        if (((Item) Sym.getInstance().peek().context).id.equals("LBRACE")) {
            type = 1;
            Sym.getInstance().step();
            if (!((Item) Sym.getInstance().peek().context).id.equals("RBRACE")) {
                firstInitVal = new ConstInitVal();
                while (!((Item) Sym.getInstance().peek().context).id.equals("RBRACE")) {
                    if (!((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
//                        System.out.println("ConstInitVal error");
                    }
                    Sym.getInstance().step();
                    initVal.add(new ConstInitVal());
                }
            }
            Sym.getInstance().step();
        } else {
            type = 2;
            insideExp = new ConstExp();
        }
    }

    public void output() {
        if (type == 1) {
            System.out.println("LBRACE {");
            if (firstInitVal != null) {
                firstInitVal.output();
            }
            for (ConstInitVal i : initVal) {
                System.out.println("COMMA ,");
                i.output();
            }
            System.out.println("RBRACE }");
        } else {
            insideExp.output();
        }
        System.out.println("<ConstInitVal>");
    }

    public ArrayList<Integer> getConstInitValue(){
        ArrayList<Integer> ans = new ArrayList<>();
        if(type==2) {
            ans.add(insideExp.evaluate());
            return ans;
        }
        ans.addAll(firstInitVal.getConstInitValue());
        for(ConstInitVal i:initVal){
            ans.addAll(i.getConstInitValue());
        }
        return ans;
    }
}
