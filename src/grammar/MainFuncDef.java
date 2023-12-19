package grammar;
import ir.BasicBlock;
import ir.Function;
import ir.MyModule;
import ir.types.IntegerType;
import ir.types.Type;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

public class MainFuncDef {
    Block block;
    public MainFuncDef() {
        SymbolTable.getInstance().newBlock();
        SymbolTable.getInstance().isVoid=false;
        if(!((Item) Sym.getInstance().peek().context).id.equals("INTTK")){
            System.out.println("MainFuncDef error");
        }
        Sym.getInstance().step();
        if(!((Item) Sym.getInstance().peek().context).id.equals("MAINTK")){
            System.out.println("MainFuncDef error");
        }
        Sym.getInstance().step();
        if(!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")){
            System.out.println("MainFuncDef error");
        }
        Sym.getInstance().step();
        if(!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")){
            System.out.println("MainFuncDef error");
        }
        Sym.getInstance().step();
        block = new Block();
        SymbolTable.getInstance().leaveBlock();
        Sym.getInstance().checkReturn();
        SymbolTable.getInstance().isVoid=true;
    }

    public void output() {
        System.out.println("INTTK int");
        System.out.println("MAINTK main");
        System.out.println("LPARENT (");
        System.out.println("RPARENT )");
        block.output();
        System.out.println("<MainFuncDef>");
    }

    public void visit() {
        Type returnType = new IntegerType();
        Function function = new Function("main", returnType);
        IrSymbol.getInstance().addSymbol("main", function);
        MyModule.getInstance().addFunction(function); // 添加函数

        IrSymbol.getInstance().newBlock(); // 进入新的函数块
        BasicBlock bb = new BasicBlock("main");
        MyModule.getInstance().addBasicBlock(bb); // 进入新的基本块
        block.visit();
        IrSymbol.getInstance().leaveBlock(); // 离开函数块
    }
}
