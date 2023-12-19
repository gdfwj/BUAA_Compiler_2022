package grammar;

import backend.mips.insturctions.mem.Load;
import ir.GlobalVariable;
import ir.MyModule;
import ir.instructions.ConstantInteger;
import ir.instructions.MemInst.GEPInst;
import ir.instructions.MemInst.AllocaInst;
import ir.instructions.MemInst.LoadInst;
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

public class LVal {
    private final ArrayList<Exp> insideExp = new ArrayList<>();
    private final Item insideId;

    public LVal() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("IDENFR")) {
            System.out.println("LVal1 error");
        }
        insideId = Sym.getInstance().peek();
        if (SymbolTable.getInstance().checkSymbol((String) ((Item) Sym.getInstance().peek().context).context) == null) {
            System.out.println(Sym.getInstance().peek().id + " c");
        }
        Sym.getInstance().step();
        while (((Item) Sym.getInstance().peek().context).id.equals("LBRACK")) {
            Sym.getInstance().step();
            insideExp.add(new Exp());
            if (!((Item) Sym.getInstance().peek().context).id.equals("RBRACK")) {
                System.out.println(Sym.getInstance().peekLast().id + " k");
            } else {
                Sym.getInstance().step();
            }
        }
    }

    public void output() {
        System.out.println(((Item) insideId.context).id + " " +
                ((Item) insideId.context).context);
        for (Exp i : insideExp) {
            System.out.println("LBRACK [");
            i.output();
            System.out.println("RBRACK ]");
        }
        System.out.println("<LVal>");
    }

    public Item getIdent() {
        return insideId;
    }

    public int constEvaluate() {
        Item out = SymbolTable.getInstance().checkConstSymbol((String) ((Item) insideId.context).context);
        ArrayList<Integer> list = (ArrayList<Integer>) out.context;
        int dim1 = (int) out.id;
        if (insideExp.size() == 0) {
            return list.get(0);
        } else if (insideExp.size() == 1) {
            return list.get(insideExp.get(0).evaluate());
        } else {
            return list.get(insideExp.get(0).evaluate() * dim1 + insideExp.get(1).evaluate());
        }
    }

    public Value visit(boolean inExp) {
        if (inExp) { // 在表达式右端
            String name = (String) ((Item) insideId.context).context;
            Value createInst = IrSymbol.getInstance().checkSymbol(name); // 找到alloca语句或者全局变量
            if (createInst instanceof AllocaInst) { // 局部变量
                if (((AllocaInst) createInst).getAllocaType() instanceof PointerType) { // 是指针
                    ArrayList<Value> args = new ArrayList<>();
                    for(Exp i:insideExp) { // 每次向内GEP
                        args.add(i.visit());
                    }
                    GEPInst gep = new GEPInst(createInst, args);
                    MyModule.getInstance().addInst(gep);
                    Type returnType = ((PointerType) gep.getType()).getPoints();
                    if (returnType instanceof IntegerType) { // 返回的是数组元素
                        LoadInst loadInst = new LoadInst(gep);
                        MyModule.getInstance().addInst(loadInst);
                        return loadInst;
                    } else { // 返回的是数组指针
                        return gep;
                    }
                } else if(((AllocaInst) createInst).getAllocaType() instanceof ArrayType) { // 数组, 是否是常量目前都需要访问内存
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(new ConstantInteger(0));
                    for (Exp i : insideExp) {
                        args.add(i.visit());
                    }
                    GEPInst gepInst = new GEPInst(createInst, args); // 找到当前地址指针
                    MyModule.getInstance().addInst(gepInst);
                    Type returnType = ((PointerType) gepInst.getType()).getPoints();
                    if (returnType instanceof IntegerType) { // 返回的是数组元素
                        LoadInst loadInst = new LoadInst(gepInst);
                        MyModule.getInstance().addInst(loadInst);
                        return loadInst;
                    } else { // 返回的是数组指针
                        return gepInst;
                    }
                } else { // 是普通变量
                    assert insideExp.isEmpty();
                    if (((AllocaInst) createInst).getAllocaType().isConst()) { // 常量，查表
                        return new ConstantInteger(((ArrayList<Integer>) IrSymbol.getInstance().checkConstSymbol(name).context).get(0));
                    } else { // 变量 找到指针，load
                        LoadInst loadInst = new LoadInst(createInst);
                        MyModule.getInstance().addInst(loadInst);
                        return loadInst;
                    }
                }
            } else { // 全局变量
                assert createInst instanceof GlobalVariable;
                if (!(((GlobalVariable) createInst).getAllocaType() instanceof ArrayType)) { // 不是数组
                    assert insideExp.isEmpty();
                    if (createInst.getType().isConst()) { // 常量，查表
                        return new ConstantInteger(((ArrayList<Integer>) IrSymbol.getInstance().checkConstSymbol(name).context).get(0));
                    } else { // 变量 找到指针，load
                        LoadInst loadInst = new LoadInst(createInst);
                        MyModule.getInstance().addInst(loadInst);
                        return loadInst;
                    }
                } else { // 数组
                    assert ((GlobalVariable) createInst).getAllocaType() instanceof ArrayType;
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(new ConstantInteger(0));
                    for (Exp i : insideExp) {
                        args.add(i.visit());
                    }
                    GEPInst gepInst = new GEPInst(createInst, args); // 找到当前地址指针
                    MyModule.getInstance().addInst(gepInst);
                    Type returnType = ((PointerType) gepInst.getType()).getPoints();
                    if (returnType instanceof IntegerType) { // 返回的是数组元素
                        LoadInst loadInst = new LoadInst(gepInst);
                        MyModule.getInstance().addInst(loadInst);
                        return loadInst;
                    } else { // 返回的是数组指针
                        return gepInst;
                    }
                }
            }
        } else { // fixed 在表8达式左端，写赋值语句的时候再写，返回左值指针
            String name = (String) ((Item) insideId.context).context;
            Value createInst = IrSymbol.getInstance().checkSymbol(name); // 找到alloca语句或者全局变量
            assert createInst.getType() instanceof PointerType; // 必须是声明语句(返回指针)
            assert !((PointerType) createInst.getType()).getPoints().isConst(); // 不能是const
            if (createInst instanceof AllocaInst) {

//                if (!(((AllocaInst) createInst).getAllocaType() instanceof ArrayType ||
//                        ((AllocaInst) createInst).getAllocaType() instanceof PointerType)) { // 不是数组
//                    return createInst;
//                } else { // 数组，返回相应元素指针
//                    ArrayList<Value> args = new ArrayList<>();
//                    args.add(new ConstantInteger(0));
//                    for (Exp i : insideExp) {
//                        args.add(i.visit());
//                    }
//                    GEPInst gepInst = new GEPInst(createInst, args);
//                    MyModule.getInstance().addInst(gepInst);
//                    return gepInst;
//                }
                if (((AllocaInst) createInst).getAllocaType() instanceof PointerType) { // 是指针
                    ArrayList<Value> args = new ArrayList<>();
                    for(Exp i:insideExp) { // 每次向内GEP
                        args.add(i.visit());
                    }
                    GEPInst gep = new GEPInst(createInst, args);
                    MyModule.getInstance().addInst(gep);
                    return gep;
                } else if(((AllocaInst) createInst).getAllocaType() instanceof ArrayType) { // 数组, 是否是常量目前都需要访问内存
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(new ConstantInteger(0));
                    for (Exp i : insideExp) {
                        args.add(i.visit());
                    }
                    GEPInst gepInst = new GEPInst(createInst, args); // 找到当前地址指针
                    MyModule.getInstance().addInst(gepInst);
                    return gepInst;
//                    Type returnType = ((PointerType) gepInst.getType()).getPoints();
//                    if (returnType instanceof IntegerType) { // 返回的是数组元素
//                        LoadInst loadInst = new LoadInst(gepInst);
//                        MyModule.getInstance().addInst(loadInst);
//                        return loadInst;
//                    } else { // 返回的是数组指针
//                        return gepInst;
//                    }
                } else { // 是普通变量
                    assert insideExp.isEmpty();
                    return createInst;
                }
            } else { // 全局变量
                assert createInst instanceof GlobalVariable;
                if (!(((GlobalVariable) createInst).getAllocaType() instanceof ArrayType)) { // 不是数组
                    return createInst;
                } else { // 数组，返回相应元素指针
                    assert ((GlobalVariable) createInst).getAllocaType() instanceof ArrayType;
                    ArrayList<Value> args = new ArrayList<>();
                    args.add(new ConstantInteger(0));
                    for (Exp i : insideExp) {
                        args.add(i.visit());
                    }
                    GEPInst gepInst = new GEPInst(createInst, args);
                    MyModule.getInstance().addInst(gepInst);
                    return gepInst;
                }
            }
        }
    }
}
