package grammar;

import ir.Function;
import ir.MyModule;
import ir.instructions.CalculateInst.BinaryInst;
import ir.instructions.CalculateInst.UnaryInst;
import ir.instructions.ConstantInteger;
import ir.instructions.Tag;
import ir.instructions.TerminatorInst.CallInst;
import ir.types.Type;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import ir.types.ArrayType;
import java.util.ArrayList;

public class UnaryExp {
    private final int type;
    private PrimaryExp insidePrimaryExp;
    private FuncRParams insideFuncRParams;
    private UnaryOp insideUnaryOp;
    private Item insideIdent;
    private UnaryExp insideUnaryExp;

    public UnaryExp() {
        if (((Item) Sym.getInstance().peek().context).id.equals("LPARENT") ||
                (((Item) Sym.getInstance().peek().context).id.equals("IDENFR") &&
                        !((Item) Sym.getInstance().peekNext().context).id.equals("LPARENT")) ||
                ((Item) Sym.getInstance().peek().context).id.equals("INTCON")) {
            type = 1;
            insidePrimaryExp = new PrimaryExp();
        } else if (((Item) Sym.getInstance().peek().context).id.equals("IDENFR") &&
                ((Item) Sym.getInstance().peekNext().context).id.equals("LPARENT")) {
            type = 2;
            insideIdent = Sym.getInstance().peek();
            if (SymbolTable.getInstance().checkSymbol((String) ((Item) Sym.getInstance().peek().context).context) == null) {
                System.out.println(Sym.getInstance().peek().id + " c");
            }
            Sym.getInstance().step();
            if (!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
                System.out.println("UnaryExp error");
            }
            Sym.getInstance().step();
            if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")
                    && !((Item) Sym.getInstance().peek().context).context.equals(";")) {
                insideFuncRParams = new FuncRParams();
            }
            if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
                System.out.println(Sym.getInstance().peekLast().id + " j");
            } else {
                Sym.getInstance().step();
            }
            if (insideFuncRParams != null) {
                checkWrongDE(SymbolTable.getInstance().getFuncParas((String) ((Item) insideIdent.context).context),
                        insideFuncRParams.getParams(), (int) insideIdent.id);
            } else {
                checkWrongDE(SymbolTable.getInstance().getFuncParas((String) ((Item) insideIdent.context).context),
                        new ArrayList<>(), (int) insideIdent.id);
            }
        } else {
            type = 3;
            insideUnaryOp = new UnaryOp();
            insideUnaryExp = new UnaryExp();
        }
    }

    public void output() {
        if (type == 1) {
            insidePrimaryExp.output();
        } else if (type == 2) {
            System.out.println(((Item) insideIdent.context).id + " " + ((Item) insideIdent.context).context);
            System.out.println("LPARENT (");
            if (insideFuncRParams != null) {
                insideFuncRParams.output();
            }
            System.out.println("RPARENT )");
        } else {
            insideUnaryOp.output();
            insideUnaryExp.output();
        }
        System.out.println("<UnaryExp>");
    }

    private void checkWrongDE(ArrayList<Type> a, ArrayList<Type> b, int line) {
        if (a == null || b==null || b.contains(null)) {
            return;
        }
        if (a.size() != b.size()) {
            System.out.println(line + " d");
            return;
        }
        for (int i = 0; i < a.size(); i++) {
            Object t1 = a.get(i);
            Object t2 = b.get(i);
            if (!t1.getClass().equals(t2.getClass())) {
                System.out.println(line + " e");
                return;
            }
            if(t1 instanceof ArrayType) {
                if(!((ArrayType) t1).getInside().getClass().equals(((ArrayType) t2).getInside().getClass())) {
                    System.out.println(line + " e");
                    return;
                }
            }
        }
    }

    public int evaluate() {
        if(type==1) {
            return insidePrimaryExp.evaluate();
        }
        if(insideUnaryOp.whenEvaluate().equals("+")) {
            return insideUnaryExp.evaluate();
        } else {
            return -insideUnaryExp.evaluate();
        }
    }

    public Value visit() {
        if (type == 1) {
            return insidePrimaryExp.visit();
        } else if (type == 2) {
            ArrayList<Value> args = new ArrayList<>();
            if (insideFuncRParams != null) {
                args.addAll(insideFuncRParams.visit());
            }
            CallInst callInst = new CallInst((Function) IrSymbol.getInstance().checkSymbol((String) ((Item) insideIdent.context).context)
                    , args);
            MyModule.getInstance().addInst(callInst);
            return callInst;
        } else {
            String op = insideUnaryOp.whenEvaluate();
            if(op.equals("+")) {
                return insideUnaryExp.visit();
            }
            else if(op.equals("-")) {
                BinaryInst binaryInst = new BinaryInst(new ConstantInteger(0), insideUnaryExp.visit(), Tag.Sub);
                MyModule.getInstance().addInst(binaryInst);
                return binaryInst;
            } else {
                UnaryInst unaryInst = new UnaryInst(insideUnaryExp.visit(), Tag.Not);
                MyModule.getInstance().addInst(unaryInst);
                return unaryInst;
            }
        }
    }
}
