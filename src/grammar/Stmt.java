package grammar;

import ir.BasicBlock;
import ir.MyModule;
import ir.StringLiteral;
import ir.instructions.IOInst;
import ir.instructions.MemInst;
import ir.instructions.TerminatorInst;
import ir.instructions.TerminatorInst.RetInst;
import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.Type;
import ir.values.Value;
import sym.Item;
import sym.Sym;
import symbol.IrSymbol;
import symbol.SymbolTable;

import java.util.ArrayList;

public class Stmt {
    private final int type;
    private Item formatString1;
    private final ArrayList<Exp> exps1 = new ArrayList<>();
    private Exp exp2;
    private Item reserved3;
    private Cond cond4;
    private Stmt stmt4;
    private Cond cond5;
    private Stmt stmt51;
    private Stmt stmt52;
    private Block block6;
    private LVal lVal7;
    private Exp exp7;
    private Exp exp8;

    public Stmt() {
        switch ((String) (((Item) Sym.getInstance().peek().context).id)) {
            case "PRINTFTK":
                type = 1;
                int line = (int) Sym.getInstance().peek().id;
                Sym.getInstance().step();
                if (!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
                    System.out.println("Stmt error");
                }
                Sym.getInstance().step();
                if (!((Item) Sym.getInstance().peek().context).id.equals("STRCON")) {
                    System.out.println("Stmt error");
                }
                formatString1 = Sym.getInstance().peek();
                int countFormat = testWrongA((String) ((Item) Sym.getInstance().peek().context).context);
                if (countFormat == -1) {
                    System.out.println(Sym.getInstance().peek().id + " a");
                }
                Sym.getInstance().step();
                while (((Item) Sym.getInstance().peek().context).id.equals("COMMA")) {
                    Sym.getInstance().step();
                    exps1.add(new Exp());
                }
                if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
                    System.out.println(Sym.getInstance().peekLast().id + " j");
                } else {
                    Sym.getInstance().step();
                }
                if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                    System.out.println(Sym.getInstance().peekLast().id + " i");
                } else {
                    Sym.getInstance().step();
                }
                if (exps1.size() != countFormat && countFormat != -1) {
                    System.out.println(line + " l");
                }
                break;
            case "RETURNTK":
                type = 2;
                Sym.getInstance().step();
                if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                    exp2 = new Exp();
                }
                if (exp2 != null && SymbolTable.getInstance().isVoid) {
                    System.out.println(Sym.getInstance().peek().id + " f");
                }
                if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                    System.out.println(Sym.getInstance().peekLast().id + " i");
                } else {
                    Sym.getInstance().step();
                }
                break;
            case "BREAKTK":
            case "CONTINUETK":
                type = 3;
                reserved3 = Sym.getInstance().peek();
                Sym.getInstance().step();
                if (!SymbolTable.getInstance().isLoop) {
                    System.out.println(reserved3.id + " m");
                }
                if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                    System.out.println(Sym.getInstance().peekLast().id + " i");
                } else {
                    Sym.getInstance().step();
                }
                break;
            case "WHILETK":
                type = 4;
                Sym.getInstance().step();
                if (!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
                    System.out.println("Stmt error");
                }
                Sym.getInstance().step();
                cond4 = new Cond();
                if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
                    System.out.println(Sym.getInstance().peekLast().id + " j");
                } else {
                    Sym.getInstance().step();
                }
                SymbolTable.getInstance().intoLoop();
                stmt4 = new Stmt();
                SymbolTable.getInstance().leaveLoop();
                break;
            case "IFTK":
                type = 5;
                Sym.getInstance().step();
                if (!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
                    System.out.println("Stmt error");
                }
                Sym.getInstance().step();
                cond5 = new Cond();
                if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
                    System.out.println(Sym.getInstance().peekLast().id + " j");
                } else {
                    Sym.getInstance().step();
                }
                stmt51 = new Stmt();
                if (((Item) Sym.getInstance().peek().context).id.equals("ELSETK")) {
                    Sym.getInstance().step();
                    stmt52 = new Stmt();
                }
                break;
            case "LBRACE":
                type = 6;
                SymbolTable.getInstance().newBlock();
                block6 = new Block();
                SymbolTable.getInstance().leaveBlock();
                break;
            default:
                if (Sym.getInstance().peekAssign()) {
                    type = 7;
                    lVal7 = new LVal();
                    if (!((Item) Sym.getInstance().peek().context).id.equals("ASSIGN")) {
                        System.out.println("Stmt1 error");
                    }
                    Type type = SymbolTable.getInstance().checkSymbol((String) ((Item) lVal7.getIdent().context).context);
                    if ((type instanceof ArrayType && ((ArrayType) type).isConst()) || (type instanceof IntegerType && ((IntegerType) type).isConst())) {
                        System.out.println(lVal7.getIdent().id + " h");
                    }
                    Sym.getInstance().step();
                    if (((Item) Sym.getInstance().peek().context).id.equals("GETINTTK")) {
                        Sym.getInstance().step();
                        if (!((Item) Sym.getInstance().peek().context).id.equals("LPARENT")) {
                            System.out.println("Stmt2 error");
                        }
                        Sym.getInstance().step();
                        if (!((Item) Sym.getInstance().peek().context).id.equals("RPARENT")) {
                            System.out.println(Sym.getInstance().peekLast().id + " j");
                        } else {
                            Sym.getInstance().step();
                        }
                        if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                            System.out.println(Sym.getInstance().peekLast().id + " i");
                        } else {
                            Sym.getInstance().step();
                        }
                    } else {
                        exp7 = new Exp();
                        if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                            System.out.println(Sym.getInstance().peekLast().id + " i");
                        } else {
                            Sym.getInstance().step();
                        }
                    }
                } else {
                    type = 8;
                    if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                        exp8 = new Exp();
                    }
                    if (!((Item) Sym.getInstance().peek().context).id.equals("SEMICN")) {
                        System.out.println(Sym.getInstance().peekLast().id + " i");
                    } else {
                        Sym.getInstance().step();
                    }
                }
        }
    }

    public void output() {
        switch (type) {
            case 1:
                System.out.println("PRINTFTK printf");
                System.out.println("LPARENT (");
                System.out.println(((Item) formatString1.context).id + " " + ((Item) formatString1.context).context);
                for (Exp i : exps1) {
                    System.out.println("COMMA ,");
                    i.output();
                }
                System.out.println("RPARENT )");
                System.out.println("SEMICN ;");
                break;
            case 2:
                System.out.println("RETURNTK return");
                if (exp2 != null) {
                    exp2.output();
                }
                System.out.println("SEMICN ;");
                break;
            case 3:
                System.out.println(((Item) reserved3.context).id + " " + ((Item) reserved3.context).context);
                System.out.println("SEMICN ;");
                break;
            case 4:
                System.out.println("WHILETK while");
                System.out.println("LPARENT (");
                cond4.output();
                System.out.println("RPARENT )");
                stmt4.output();
                break;
            case 5:
                System.out.println("IFTK if");
                System.out.println("LPARENT (");
                cond5.output();
                System.out.println("RPARENT )");
                stmt51.output();
                if (stmt52 != null) {
                    System.out.println("ELSETK else");
                    stmt52.output();
                }
                break;
            case 6:
                block6.output();
                break;
            case 7:
                lVal7.output();
                System.out.println("ASSIGN =");
                if (exp7 == null) {
                    System.out.println("GETINTTK getint");
                    System.out.println("LPARENT (");
                    System.out.println("RPARENT )");
                    System.out.println("SEMICN ;");
                } else {
                    exp7.output();
                    System.out.println("SEMICN ;");
                }
                break;
            case 8:
                if (exp8 != null) {
                    exp8.output();
                }
                System.out.println("SEMICN ;");
        }
        System.out.println("<Stmt>");
    }

    private int testWrongA(String test) {
        int count = 0;
        for (int i = 1; i < test.length() - 1; i++) {
            if (!(test.charAt(i) == 32 || test.charAt(i) == 33 ||
                    (test.charAt(i) >= 40 && test.charAt(i) <= 126 && test.charAt(i) != 92))) {
                if (test.charAt(i) != '%' && test.charAt(i) != '\\') {
                    return -1;
                } else if (test.charAt(i) == '\\') {
                    if (i + 1 > test.length()) {
                        return -1;
                    }
                    if (test.charAt(i + 1) != 'n') {
                        return -1;
                    }
                } else {
                    if (i + 1 > test.length()) {
                        return -1;
                    }
                    if (test.charAt(i + 1) != 'd') {
                        return -1;
                    }
                    count++;
                }
            }
        }
        return count;
    }

    public void visit() {
        switch (type) {
            case 1: { // fixed printf
                String f = (String) ((Item) formatString1.context).context;
                generatePrintf(f.substring(1, f.length() - 1), exps1);
                break;
            }
            case 2: {
                if (exp2 != null) {
                    Value retValue = exp2.visit();
                    RetInst retInst = new RetInst(retValue);
                    MyModule.getInstance().addInst(retInst);
                } else {
                    RetInst retInst = new RetInst(null);
                    MyModule.getInstance().addInst(retInst);
                }
                BasicBlock bb = new BasicBlock(""); // 从下一行开始是一个新的基本块
                MyModule.getInstance().addBasicBlock(bb);
                break;
            }
            case 3: {
                TerminatorInst.BranchInst branchInst;
                if (((Item) reserved3.context).context.equals("break")) {
                    branchInst = new TerminatorInst.BranchInst(IrSymbol.getInstance().getNowEndBlock());
                    MyModule.getInstance().addInst(branchInst);
                    BasicBlock newBB = new BasicBlock(""); // 其下一条语句为另一个基本块
                    MyModule.getInstance().addBasicBlock(newBB); // 加入下一个基本块 // 不需更改当前循环基本块
                } else {
                    assert ((Item) reserved3.context).context.equals("continue");
                    branchInst = new TerminatorInst.BranchInst(IrSymbol.getInstance().getNowCircleBlock());
                    MyModule.getInstance().addInst(branchInst);
                    BasicBlock newBB = new BasicBlock(""); // 其下一条语句为另一个基本块
                    MyModule.getInstance().addBasicBlock(newBB); // 加入下一个基本块
                }
                break;
            }
            case 4: {
                BasicBlock condBlock = new BasicBlock("");
                MyModule.getInstance().addBasicBlock(condBlock); // 判断块，之后的循环尾和continue都跳转到判断块
                BasicBlock inBasicBlock = new BasicBlock("");
                BasicBlock outBasicBlock = new BasicBlock("");
                IrSymbol.getInstance().addLoopBasicBlock(condBlock, outBasicBlock);
                cond4.visit(inBasicBlock, outBasicBlock);
                MyModule.getInstance().addBasicBlock(inBasicBlock); // 接下来的语句在循环的基本块
                stmt4.visit();
                MyModule.getInstance().addInst(new TerminatorInst.BranchInst(condBlock));
                IrSymbol.getInstance().leaveBasicBlockCircle();
                MyModule.getInstance().addBasicBlock(outBasicBlock); // 循环内的语句结束后进入循环后的基本块
                break;
            }
            case 5:
                BasicBlock ifTrueBlock = new BasicBlock("");
                BasicBlock ifFalseBlock = new BasicBlock("");
                BasicBlock overBlock = new BasicBlock("");
                cond5.visit(ifTrueBlock, ifFalseBlock); // 直接在内部跳转逻辑完成，短路求值，不需要and和or指令
                MyModule.getInstance().addBasicBlock(ifTrueBlock); // 在每个trueBlock后面都要加一个跳转到结束的overBlock
                stmt51.visit();
                TerminatorInst.BranchInst branchInst = new TerminatorInst.BranchInst(overBlock);
                MyModule.getInstance().addInst(branchInst);
                MyModule.getInstance().addBasicBlock(ifFalseBlock);
                if (stmt52 != null) {
                    stmt52.visit();
                }
                MyModule.getInstance().addBasicBlock(overBlock);
                break;
            case 6: {
                IrSymbol.getInstance().newBlock();
                block6.visit();
                IrSymbol.getInstance().leaveBlock();
                break;
            }
            case 7: {
                if (exp7 == null) { // fixed getint
                    IOInst.GetIntInst getIntInst = new IOInst.GetIntInst();
                    MyModule.getInstance().addInst(getIntInst);
                    Value ptr = lVal7.visit(false);
                    MemInst.StoreInst storeInst = new MemInst.StoreInst(getIntInst, ptr);
                    MyModule.getInstance().addInst(storeInst);
                } else {
                    Value value = exp7.visit();
                    Value ptr = lVal7.visit(false);
                    MemInst.StoreInst storeInst = new MemInst.StoreInst(value, ptr);
                    MyModule.getInstance().addInst(storeInst);
                }
                break;
            }
            case 8: {
                if (exp8 != null) {
                    exp8.visit();
                }
                break;
            }
        }
    }

    private void generatePrintf(String formatString, ArrayList<Exp> exps) {
        int start = 0; // 标记当前读取位置
        ArrayList<Exp> overExps = new ArrayList<>();
        for(Exp i:exps) {
            overExps.add(0, i);
        }
        ArrayList<Value> returns = new ArrayList<>();
        for(Exp i:overExps) {
            returns.add(0, i.visit());
        }
        int count=0;
        for (Exp i : exps) {
            int position = formatString.indexOf('%', start);
            if (position == start + 1) { // 使用putchar即可
                IOInst.PutCharInst putCharInst = new IOInst.PutCharInst(formatString.charAt(start));
                MyModule.getInstance().addInst(putCharInst);
            } else if (position > start + 1) { // 使用putstring(当position==start时不用处理)
                StringLiteral stringLiteral = new StringLiteral("", formatString.substring(start, position)); // 新增初始化string
                MyModule.getInstance().addStringLiteral(stringLiteral); // 添加
                IOInst.PutStringInst putStringInst = new IOInst.PutStringInst(stringLiteral); // 新增格式字符输出
                MyModule.getInstance().addInst(putStringInst);
            } // 输出完格式字符
            Value outputInt = returns.get(count);
            IOInst.PutIntInst putIntInst = new IOInst.PutIntInst(outputInt); // 输出exp
            MyModule.getInstance().addInst(putIntInst);
            start = position + 2; // start找到%d之后的位置
            count++;
        } //
        int position = formatString.length(); // 最后输出一次最后一个%d之后的字符串
        if (position == start + 1) { // 使用putchar即可
            IOInst.PutCharInst putCharInst = new IOInst.PutCharInst(formatString.charAt(start));
            MyModule.getInstance().addInst(putCharInst);
        } else if (position > start + 1) { // 使用putstring(当position==start时不用处理)
            StringLiteral stringLiteral = new StringLiteral("", formatString.substring(start, position)); // 新增初始化string
            MyModule.getInstance().addStringLiteral(stringLiteral); // 添加
            IOInst.PutStringInst putStringInst = new IOInst.PutStringInst(stringLiteral); // 新增格式字符输出
            MyModule.getInstance().addInst(putStringInst);
        } // 输出完格式字符
    }
}
