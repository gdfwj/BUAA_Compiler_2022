package grammar;

import ir.MyModule;
import ir.instructions.MemInst;
import ir.instructions.MemInst.AllocaInst;
import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.Type;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import java.util.ArrayList;

public class FuncFParam {
    private final Item ident;
    private final ArrayList<ConstExp> constExp = new ArrayList<>();
    private Type type;
    private int a = 0;

    public FuncFParam() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("INTTK")) {
            System.out.println("FuncFParam error");
        } else {
            Sym.getInstance().step();
        }
        ident = Sym.getInstance().peek();
        if (!((Item) Sym.getInstance().peek().context).id.equals("IDENFR")) {
            System.out.println("FuncFParam error");
        } else {
            Sym.getInstance().step();
        }
        if (((Item) Sym.getInstance().peek().context).id.equals("LBRACK")) {
            Sym.getInstance().step();
            if (!((Item) Sym.getInstance().peek().context).id.equals("RBRACK")) {
                System.out.println(Sym.getInstance().peekLast().id + " k");
            } else {
                Sym.getInstance().step();
            }
            a = 1;
            while (((Item) Sym.getInstance().peek().context).id.equals("LBRACK")) {
                Sym.getInstance().step();
                constExp.add(new ConstExp());
                if (!((Item) Sym.getInstance().peek().context).id.equals("RBRACK")) {
                    System.out.println(Sym.getInstance().peekLast().id + " k");
                } else {
                    Sym.getInstance().step();
                }
            }
        }
        int flag;
        if (a != 1) {
            this.type=new IntegerType();
        } else if (constExp.isEmpty()) {
            this.type=new ArrayType(new IntegerType(), 0);
        } else {
            this.type=new ArrayType(new ArrayType(new IntegerType(), constExp.get(0).evaluate()), 0);
        }
        flag = SymbolTable.getInstance().addSymbol((String) ((Item) ident.context).context, this.type);
        if (flag == -1) {
            System.out.println(ident.id + " b");
        }
    }

    public void output() {
        System.out.println("INTTK int");
        System.out.println(((Item) ident.context).id + " " + ((Item) ident.context).context);
        if (a == 1) {
            System.out.println("LBRACK [");
            System.out.println("RBRACK ]");
        }
        if (!constExp.isEmpty()) {
            for (ConstExp i : constExp) {
                System.out.println("LBRACK [");
                i.output();
                System.out.println("RBRACK ]");
            }
        }
        System.out.println("<FuncFParam>");
    }

    public Type getParam() {
        if (!constExp.isEmpty()) {
            return new ArrayType(new ArrayType(new IntegerType(), constExp.get(0).evaluate()), 0);
        } else if (a == 1) {
            return new ArrayType(new IntegerType(), 0);
        } else {
            return new IntegerType();
        }
    }

    public void visit() {
        change();
        Value pram = MyModule.getInstance().getNowFunction().addArgs(this.type);
        AllocaInst allocaInst = new AllocaInst((String) ((Item) ident.context).context, this.type);
        MyModule.getInstance().addInst(allocaInst);
        IrSymbol.getInstance().addSymbol(allocaInst.getName(), allocaInst);
        MemInst.StoreInst storeInst = new MemInst.StoreInst(pram, allocaInst);
        MyModule.getInstance().addInst(storeInst);
    }

    private void change() {
        if(this.type instanceof ArrayType) {
            this.type = new PointerType(((ArrayType) this.type).getInside());
        }
    }
}
