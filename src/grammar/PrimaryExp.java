package grammar;

import ir.values.Value;
import sym.Item;
import sym.Sym;

public class PrimaryExp {
    private Exp insideExp = null;
    private LVal insideLVal = null;
    private Number insideNumber = null;

    public PrimaryExp() {
        if (((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
            Sym.getInstance().step();
            insideExp = new Exp();
            if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
                System.out.println("PrimaryExp error");
            }
            Sym.getInstance().step();
        } else if (((Item) Sym.getInstance().peek().context).id.equals("IDENFR")) {
            insideLVal = new LVal();
        } else {
            insideNumber = new Number();
        }
    }

    public void output() {
        if (insideExp != null) {
            System.out.println("LPARENT (");
            insideExp.output();
            System.out.println("RPARENT )");
        } else if (insideLVal != null) {
            insideLVal.output();
        } else if (insideNumber != null) {
            insideNumber.output();
        }
        System.out.println("<PrimaryExp>");
    }

    public int evaluate() {
        if (insideExp != null) {
            return insideExp.evaluate();
        } else if (insideLVal != null) {
            return insideLVal.constEvaluate();
        } else {
            return insideNumber.evaluate();
        }
    }

    public Value visit() {
        if (insideExp != null) {
            return insideExp.visit();
        } else if (insideLVal != null) {
            return insideLVal.visit(true);
        } else {
            return insideNumber.visit();
        }
    }
}
