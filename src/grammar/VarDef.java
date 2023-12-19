package grammar;

import ir.GlobalVariable;
import ir.MyModule;
import ir.instructions.ConstantInteger;
import ir.instructions.MemInst;
import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.Type;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import java.util.ArrayList;

public class VarDef {
    private final Item insideIdent;
    private final ArrayList<ConstExp> constExps = new ArrayList<>();
    private InitVal insideInitVal;
    private Type type;
    private ArrayList<Integer> globalInitVal = null;

    public VarDef() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("IDENFR")) {
            System.out.println("VarDef error");
        }
        insideIdent = Sym.getInstance().peek();
        Sym.getInstance().step();
        while (((Item) Sym.getInstance().peek().context).id.equals("LBRACK")) {
            Sym.getInstance().step();
            constExps.add(new ConstExp());
            if (!((Item) Sym.getInstance().peek().context).id.equals("RBRACK")) {
                System.out.println(Sym.getInstance().peekLast().id + " k");
            } else {
                Sym.getInstance().step();
            }
        }
        int flag;
        if (constExps.isEmpty()) {
            this.type = new IntegerType();
        } else if (constExps.size() == 1) {
            this.type = new ArrayType(new IntegerType(), constExps.get(0).evaluate());
        } else {
            this.type = new ArrayType(new ArrayType(new IntegerType(), constExps.get(1).evaluate()), constExps.get(0).evaluate());
        }
        flag = SymbolTable.getInstance().addSymbol((String) ((Item) insideIdent.context).context, this.type);
        if (flag == -1) {
            System.out.println(insideIdent.id + " b");
        }
        if (((Item) Sym.getInstance().peek().context).id.equals("ASSIGN")) {
            Sym.getInstance().step();
            insideInitVal = new InitVal();
        }
        if (SymbolTable.getInstance().isGlobal && insideInitVal != null) {
            globalInitVal = insideInitVal.evaluate();
        }
    }

    public void output() {
        System.out.println(((Item) insideIdent.context).id + " " + ((Item) insideIdent.context).context);
        for (ConstExp i : constExps) {
            System.out.println("LBRACK [");
            i.output();
//            System.out.println(i.evaluate());
            System.out.println("RBRACK ]");
        }
        if (insideInitVal != null) {
            System.out.println("ASSIGN =");
            insideInitVal.output();
        }
        System.out.println("<VarDef>");
    }

    public void visit() {
        if (IrSymbol.getInstance().inGlobal) { // 全局变量
            GlobalVariable globalVariable = new GlobalVariable((String) ((Item) insideIdent.context).context, this.type, globalInitVal);
            IrSymbol.getInstance().addSymbol(globalVariable.getName(), globalVariable); // 加入符号表
            MyModule.getInstance().addGlobalVariable(globalVariable);
        } else { // 局部变量, alloca
            MemInst.AllocaInst allocaInst = new MemInst.AllocaInst((String) ((Item) insideIdent.context).context, this.type);
            IrSymbol.getInstance().addSymbol(allocaInst.getName(), allocaInst);
            MyModule.getInstance().addInst(allocaInst);
            ArrayList<Exp> initExps = null;
            if (insideInitVal != null) {
                initExps = insideInitVal.visit();
            }
            if (insideInitVal != null) { // 有赋初值
                if (this.type instanceof IntegerType) { // 不是数组
                    assert initExps.size() == 1;
                    Value initVal = initExps.get(0).visit();
                    MemInst.StoreInst storeInst = new MemInst.StoreInst(initVal, allocaInst);
                    MyModule.getInstance().addInst(storeInst);
                } else {
                    assert this.type instanceof ArrayType;
                    Type inside = ((ArrayType) this.type).getInside();
                    if (inside instanceof IntegerType) { //一维数组
                        int column = 0;
                        for (Exp i : initExps) {
                            Value initVal = i.visit();
                            if (initVal instanceof ConstantInteger && ((ConstantInteger) initVal).getValue() == 0) {
                            } else {
                                ArrayList<Value> temp = new ArrayList<>();
                                temp.add(new ConstantInteger(0));
                                temp.add(new ConstantInteger(column));
                                MemInst.GEPInst gepInst = new MemInst.GEPInst(allocaInst, temp);
                                MyModule.getInstance().addInst(gepInst);
                                MemInst.StoreInst storeInst = new MemInst.StoreInst(initVal, gepInst);
                                MyModule.getInstance().addInst(storeInst);
                            }
                            column++;
                        }
                    } else {
                        assert inside instanceof ArrayType;
                        int line = 0, column = 0;
                        for (Exp i : initExps) {
                            Value initVal = i.visit();
                            if (initVal instanceof ConstantInteger && ((ConstantInteger) initVal).getValue() == 0) {
                            } else {
                                ArrayList<Value> temp = new ArrayList<>();
                                temp.add(new ConstantInteger(0));
                                temp.add(new ConstantInteger(line));
                                temp.add(new ConstantInteger(column));
                                MemInst.GEPInst gepInst = new MemInst.GEPInst(allocaInst, temp);
                                MyModule.getInstance().addInst(gepInst);
                                MemInst.StoreInst storeInst = new MemInst.StoreInst(initVal, gepInst);
                                MyModule.getInstance().addInst(storeInst);
                            }
                            column++;
                            if (column == ((ArrayType) inside).getDim()) {
                                column = 0;
                                line++;
                            }
                        }
                    }
                }
            }
        }
    }
}
