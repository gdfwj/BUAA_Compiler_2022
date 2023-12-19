package grammar;

import ir.GlobalVariable;
import ir.MyModule;
import ir.instructions.ConstantInteger;
import ir.instructions.MemInst;
import ir.instructions.MemInst.AllocaInst;
import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.Type;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import java.util.ArrayList;

public class ConstDef {
    private final Item ident;
    private final ArrayList<ConstExp> constExps = new ArrayList<>();
    private final ConstInitVal constInitVal;
    private final Type type;
    private final ArrayList<Integer> initValue;
    private int dim1;

    public ConstDef() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("IDENFR")) {
            System.out.println("ConstDef error");
        }
        ident = Sym.getInstance().peek();
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
        if (constExps.isEmpty()) {
            this.type = new IntegerType(true);
        } else if (constExps.size() == 1) {
            this.type = new ArrayType(new IntegerType(true), constExps.get(0).evaluate(), true);
        } else {
            this.type = new ArrayType(new ArrayType(new IntegerType(true), constExps.get(1).evaluate(), true), constExps.get(0).evaluate(), true);
        }
        int flag = SymbolTable.getInstance().addSymbol((String) ((Item) ident.context).context, this.type);
        if (flag == -1) {
            System.out.println(ident.id + " b");
        }
        if (!((Item) Sym.getInstance().peek().context).id.equals("ASSIGN")) {
            System.out.println("ConstDef error");
        }
        Sym.getInstance().step();
        constInitVal = new ConstInitVal();
        this.initValue = constInitVal.getConstInitValue();
        this.dim1=1;
        if(constExps.size()==2) {
            this.dim1=constExps.get(1).evaluate();
        }
        SymbolTable.getInstance().addConstSymbol((String) ((Item) ident.context).context, this.initValue, this.dim1);
    }

    public void output() {
        System.out.println(((Item) ident.context).id + " " + ((Item) ident.context).context);
        for (ConstExp i : constExps) {
            System.out.println("LBRACK [");
            i.output();
            System.out.println("RBRACK ]");
        }
        System.out.println("ASSIGN =");
        constInitVal.output();
        System.out.println("<ConstDef>");
    }

    public void visit() {
        if(IrSymbol.getInstance().inGlobal) { // 全局变量 fixme 忘了初始化
            GlobalVariable globalVariable = new GlobalVariable((String) ((Item) ident.context).context, this.type, true, this.initValue);
            IrSymbol.getInstance().addSymbol(globalVariable.getName(), globalVariable); // 加入符号表
            IrSymbol.getInstance().addConstSymbol(globalVariable.getName(), globalVariable.getConstInitValue(), this.dim1); // 加入const
            MyModule.getInstance().addGlobalVariable(globalVariable); // 需要添加指令 因为可能是数组
        } else { // 局部变量, alloca
            AllocaInst allocaInst = new AllocaInst((String) ((Item) ident.context).context, this.type);
            IrSymbol.getInstance().addSymbol(allocaInst.getName(), allocaInst);
            IrSymbol.getInstance().addConstSymbol(allocaInst.getName(), this.initValue, this.dim1); // 加入const
            MyModule.getInstance().addInst(allocaInst);
            if(this.type instanceof IntegerType) { // 不是数组
                MemInst.StoreInst storeInst = new MemInst.StoreInst(new ConstantInteger(this.initValue.get(0)), allocaInst);
                MyModule.getInstance().addInst(storeInst);
            }
            else{
                assert this.type instanceof ArrayType;
                Type inside = ((ArrayType) this.type).getInside();
                if(inside instanceof IntegerType) { // 一维数组
                    int count=0;
                    for(Integer i:initValue) { // 循环赋初值
                        ArrayList<Value> temp = new ArrayList<>();
                        temp.add(new ConstantInteger(0));
                        temp.add(new ConstantInteger(count));
                        MemInst.GEPInst gepInst = new MemInst.GEPInst(allocaInst, temp);
                        MyModule.getInstance().addInst(gepInst);
                        MemInst.StoreInst storeInst = new MemInst.StoreInst(new ConstantInteger(i), gepInst);
                        MyModule.getInstance().addInst(storeInst);
                        count++;
                    }
                } else {
                    assert inside instanceof ArrayType && ((ArrayType) inside).getInside() instanceof IntegerType;
                    int line=0,column =0;
                    for(Integer i:initValue) {
                        ArrayList<Value> temp = new ArrayList<>();
                        temp.add(new ConstantInteger(0));
                        temp.add(new ConstantInteger(line));
                        temp.add(new ConstantInteger(column));
                        MemInst.GEPInst gepInst = new MemInst.GEPInst(allocaInst, temp);
                        MyModule.getInstance().addInst(gepInst);
                        MemInst.StoreInst storeInst = new MemInst.StoreInst(new ConstantInteger(i), gepInst);
                        MyModule.getInstance().addInst(storeInst);
                        column++;
                        if(column==dim1) {
                            column=0;
                            line++;
                        }
                    }
                }
            }
        }
    }

}
