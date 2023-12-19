package grammar;

import ir.BasicBlock;
import ir.Function;
import ir.MyModule;
import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.types.Type;
import ir.types.VoidType;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import java.util.ArrayList;

public class FuncDef {
    private final FuncType funcType;
    private final Item ident;
    private FuncFParams funcFParams;
    private final Block block;

    public FuncDef() {
        funcType = new FuncType();
        if (!((Item) Sym.getInstance().peek().context).id.equals("IDENFR")) {
            System.out.println("FuncDef error");
        }
        ident = Sym.getInstance().peek();
        int flag;
        if (funcType.isVoid()) {
            flag = SymbolTable.getInstance().addSymbol((String) ((Item) ident.context).context, new FunctionType(null, new VoidType()));
        } else {
            flag = SymbolTable.getInstance().addSymbol((String) ((Item) ident.context).context, new FunctionType(null, new IntegerType()));
        }
        if (flag == -1) {
            System.out.println(ident.id + " b");
        }
        Sym.getInstance().step();
        SymbolTable.getInstance().newBlock();
        SymbolTable.getInstance().isVoid = funcType.isVoid();
        if (!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
            System.out.println("FuncDef error");
        }
        Sym.getInstance().step();
        if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT") &&
                !((Item) Sym.getInstance().peek().context).context.equals("{")) {
            funcFParams = new FuncFParams();
        }
        if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
            System.out.println(Sym.getInstance().peekLast().id + " j");
        } else {
            Sym.getInstance().step();
        }
        if (funcFParams != null) {
            SymbolTable.getInstance().addFunc((String) ((Item) ident.context).context, funcFParams.getParams());
        } else {
            SymbolTable.getInstance().addFunc((String) ((Item) ident.context).context, new ArrayList<>());
        }
        block = new Block();
        if (!SymbolTable.getInstance().isVoid) {
            Sym.getInstance().checkReturn();
        }
        SymbolTable.getInstance().leaveBlock();
    }

    public void output() {
        funcType.output();
        System.out.println(((Item) ident.context).id + " " + ((Item) ident.context).context);
        System.out.println("LPARENT (");
        if (funcFParams != null) {
            funcFParams.output();
        }
        System.out.println("RPARENT )");
        block.output();
        System.out.println("<FuncDef>");
    }

    public void visit() {
        Type returnType;
        if (funcType.isVoid()) {
            returnType = new VoidType();
        } else {
            returnType = new IntegerType();
        }
        Function function = new Function((String) ((Item) ident.context).context, returnType);
        IrSymbol.getInstance().addSymbol((String) ((Item) ident.context).context, function);
        MyModule.getInstance().addFunction(function); // 添加函数

        IrSymbol.getInstance().newBlock(); // 进入新的函数块
        BasicBlock bb = new BasicBlock((String) ((Item) ident.context).context);
        MyModule.getInstance().addBasicBlock(bb); // 进入新的基本块
        if (funcFParams != null) {
            funcFParams.visit(); // 声明形参
        }

        block.visit();
        IrSymbol.getInstance().leaveBlock(); // 离开函数块
    }
}
