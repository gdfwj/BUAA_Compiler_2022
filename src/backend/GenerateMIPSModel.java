package backend;

import backend.mips.IdPool;
import backend.mips.MipsBBlock;
import backend.mips.MipsFunction;
import backend.mips.Operand;
import backend.mips.insturctions.*;
import backend.mips.insturctions.branch.Branch;
import backend.mips.insturctions.branch.Call;
import backend.mips.insturctions.branch.Jump;
import backend.mips.insturctions.branch.Ret;
import backend.mips.insturctions.mem.Load;
import backend.mips.insturctions.mem.Store;
import backend.mips.reg.*;
import ir.*;
import ir.instructions.*;
import ir.types.ArrayType;
import ir.types.PointerType;
import ir.types.Type;
import ir.types.VoidType;
import ir.values.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class GenerateMIPSModel {
    private final static GenerateMIPSModel instance = new GenerateMIPSModel();
    private final HashMap<GlobalVariable, String> globalVariable2Label = new HashMap<>();
    private final HashMap<StringLiteral, String> stringLiteral2Label = new HashMap<>();
    private final HashMap<Value, Operand> irMap = new HashMap<>(); // 存储ir返回值对应的虚拟寄存器
    private final HashMap<Function, MipsFunction> functionMap = new HashMap<>();
    private final HashMap<BasicBlock, MipsBBlock> blockMap = new HashMap<>();
    private final ArrayList<MipsFunction> functions = new ArrayList<>();
    private MipsFunction nowF;
    private MipsBBlock nowB;
    private boolean inCompare = false;
    private Value compareStore;
    private final MyModule module = MyModule.getInstance();

    private GenerateMIPSModel() {
    }


    public static GenerateMIPSModel getInstance() {
        return instance;
    }

    public void generate() {
        for (GlobalVariable i : module.getGlobalVariables()) {
            globalVariable2Label.put(i, i.getMIPSCount());
        }
        for (StringLiteral i : module.getStringLiterals()) {
            stringLiteral2Label.put(i, i.getMIPSCount());
        }
        for (Function i : module.getFunctions()) { // 每个函数
            boolean isVoid = i.getRetType() instanceof VoidType; // 如果是void需要手动在尾部添加调用返回
            IdPool bBlockId = new IdPool(); // 给基本块编号
            nowF = new MipsFunction(i.getName(), i.getArgs());
            functions.add(nowF);
            functionMap.put(i, nowF);
            handleAlloca(i); // 分配栈空间
            for (BasicBlock j : i.getBasicBlocks()) { // 预分配基本块，同时构建流图
                nowB = new MipsBBlock(i.getName() + "__________" + bBlockId.allocId(), nowF);
                blockMap.put(j, nowB);
                nowF.addBBlock(nowB);
            }
//            createFlowGraph(i);
            for (BasicBlock j : i.getBasicBlocks()) { // 每个基本块
                nowB = blockMap.get(j);
                for (Instruction k : j.getInstructions()) { // 遍历所有指令
                    if (!(!inCompare || k instanceof TerminatorInst.BranchInst)) { // 计算出比较的结果
                        assert compareStore instanceof CalculateInst.BinaryInst;
                        Value l = ((CalculateInst.BinaryInst) compareStore).getOp1();
                        Value r = ((CalculateInst.BinaryInst) compareStore).getOp2();
                        Operand op1, op2;
                        if (l instanceof ConstantInteger) {
                            Li li = new Li(InstType.Li, nowB, new VirtualReg(), new Imm(((ConstantInteger) l).getValue()));
                            nowB.addInst(li);
                            op1 = li.getDst();
                        } else {
                            op1 = irMap.get(l);
                        }
                        if (r instanceof ConstantInteger) {
                            op2 = new Imm(((ConstantInteger) r).getValue());
                        } else {
                            op2 = irMap.get(r);
                        }
                        MipsBinary sub = new MipsBinary(InstType.Subu, nowB, new VirtualReg(), op1, op2);
                        nowB.addInst(sub);
                        Operand ans = sub.getDst();
                        if (((CalculateInst.BinaryInst) compareStore).getTag() == Tag.Eql) {
                            MipsBinary sltiu = new MipsBinary(InstType.Sltiu, nowB, new VirtualReg(), ans, new Imm(1));
                            nowB.addInst(sltiu);
                            irMap.put(compareStore, sltiu.getDst());
                        } else if (((CalculateInst.BinaryInst) compareStore).getTag() == Tag.Neq) {
                            MipsBinary sltiu = new MipsBinary(InstType.Sltiu, nowB, new VirtualReg(), ans, new Imm(1));
                            nowB.addInst(sltiu);
                            MipsBinary not = new MipsBinary(InstType.Xori, nowB, new VirtualReg(), sltiu.getDst(), new Imm(1));
                            nowB.addInst(not);
                            irMap.put(compareStore, not.getDst());
                        } else if (((CalculateInst.BinaryInst) compareStore).getTag() == Tag.Leq) {
                            MipsBinary slt = new MipsBinary(InstType.Slti, nowB, new VirtualReg(), ans, new Imm(1));
                            nowB.addInst(slt);
                            irMap.put(compareStore, slt.getDst());
                        } else if (((CalculateInst.BinaryInst) compareStore).getTag() == Tag.Lss) {
                            MipsBinary slt = new MipsBinary(InstType.Slti, nowB, new VirtualReg(), ans, new Imm(0));
                            nowB.addInst(slt);
                            irMap.put(compareStore, slt.getDst());
                        } else if (((CalculateInst.BinaryInst) compareStore).getTag() == Tag.Geq) {
                            MipsBinary slt = new MipsBinary(InstType.Slti, nowB, new VirtualReg(), ans, new Imm(0));
                            nowB.addInst(slt);
                            MipsBinary not = new MipsBinary(InstType.Xori, nowB, new VirtualReg(), slt.getDst(), new Imm(1));
                            nowB.addInst(not);
                            irMap.put(compareStore, not.getDst());
                        } else {
                            assert ((CalculateInst.BinaryInst) compareStore).getTag() == Tag.Gre;
                            MipsBinary slt = new MipsBinary(InstType.Slti, nowB, new VirtualReg(), ans, new Imm(1));
                            nowB.addInst(slt);
                            MipsBinary not = new MipsBinary(InstType.Xori, nowB, new VirtualReg(), slt.getDst(), new Imm(1));
                            nowB.addInst(not);
                            irMap.put(compareStore, not.getDst());
                        }
                        inCompare = false;
                    }
                    if (k instanceof MemInst.AllocaInst) { // 忽略Alloca，已经处理了
                        continue;
                    }
                    if (k instanceof MemInst.LoadInst) {
                        Value ptr = ((MemInst.LoadInst) k).getPtr();
                        if (ptr instanceof MemInst.AllocaInst) { // 直接去栈上找
                            Imm offset = new Imm(nowF.getAllocaOffset((MemInst.AllocaInst) ptr));
                            Load load = new Load(InstType.Lw, nowB, new PhysicalReg("$sp", 29), new VirtualReg(), offset);
                            nowB.addInst(load);
                            irMap.put(k, load.getDst());
                        } else if (ptr instanceof GlobalVariable) { // 去数据区找
                            Label label = new Label(globalVariable2Label.get(ptr));
                            Load load = new Load(InstType.Lw, nowB, new PhysicalReg("$zero", 0), new VirtualReg(), label);
                            nowB.addInst(load);
                            irMap.put(k, load.getDst());
                            load.setGlobal(true);
                        } else { // 计算得来，通过寄存器找
                            Operand src = irMap.get(ptr);
                            Load load = new Load(InstType.Lw, nowB, src, new VirtualReg());
                            nowB.addInst(load);
                            irMap.put(k, load.getDst());
                            if (ptr instanceof MemInst.GEPInst) {
                                load.setGlobal(true); // 从GEP而来的load不需要额外计算overflow
                            }
                        }
                    } else if (k instanceof MemInst.StoreInst) { // 没有返回值，不需要加入map
                        Value ptr = ((MemInst.StoreInst) k).getPtr();
                        Value input = ((MemInst.StoreInst) k).getInput();
                        Operand inputReg;
                        if (input instanceof Instruction) { // 计算结果
                            if (input instanceof Function.Arg) { // 传参指令，删除
                                continue;
                            }
                            inputReg = irMap.get(input);
                        } else { // 立即数
                            assert input instanceof ConstantInteger;
                            Li li = new Li(InstType.Li, nowB, new VirtualReg(), new Imm(((ConstantInteger) input).getValue()));
                            nowB.addInst(li);
                            inputReg = li.getDst();
                        }
                        if (ptr instanceof MemInst.AllocaInst) { // 直接去栈上找
                            Imm offset = new Imm(nowF.getAllocaOffset((MemInst.AllocaInst) ptr));
                            Store store = new Store(InstType.Sw, nowB, new PhysicalReg("$sp", 29), inputReg, offset);
                            nowB.addInst(store);
                        } else if (ptr instanceof GlobalVariable) { // 去数据区找
                            Label label = new Label(globalVariable2Label.get(ptr));
                            Store store = new Store(InstType.Sw, nowB, new PhysicalReg("$zero", 0), inputReg, label);
                            nowB.addInst(store);
                            store.setGlobal(true);
                        } else { // 计算得来，通过寄存器找
                            Store store = new Store(InstType.Sw, nowB, irMap.get(ptr), inputReg);
                            nowB.addInst(store);
                            if (ptr instanceof MemInst.GEPInst) {
                                store.setGlobal(true); // 从GEP而来的store不需要额外计算overflow
                            }
                        }
                    } else if (k instanceof MemInst.GEPInst) { // fixed 先跑通别的再说
                        ArrayList<Value> args = k.getOperandsCopy();
                        Value base = args.remove(0); // 去掉base
                        assert base.getType() instanceof PointerType;
                        Type baseType = ((PointerType) base.getType()).getPoints();
                        Operand lastOffset;
                        boolean global = false;
                        if (baseType instanceof ArrayType) { // 在栈上或全局变量 初始偏移量是sp+offset或label
                            if (base instanceof MemInst.AllocaInst) {
                                MipsBinary binary = new MipsBinary(InstType.Addiu, nowB, new VirtualReg(), new PhysicalReg("$sp", 29), new Imm(nowF.getAllocaOffset((MemInst.AllocaInst) base)));
                                nowB.addInst(binary);
                                binary.setNeedOverflow(true); // 在这里添加overflow偏移
                                lastOffset = binary.getDst();
                            } else {
                                assert base instanceof GlobalVariable;
                                La la = new La(nowB, new VirtualReg(), new Label(globalVariable2Label.get(base)));
                                nowB.addInst(la);
                                lastOffset = la.getDst();
                                global = true;
                            }
                        } else { // 指针传参，偏移量是地址值
                            Load load = new Load(InstType.Lw, nowB, new PhysicalReg("$sp", 29), new VirtualReg(), new Imm(nowF.getAllocaOffset((MemInst.AllocaInst) base)));
                            nowB.addInst(load);
                            lastOffset = load.getDst(); // 这里本来就有overflow偏移
                        }
                        if (baseType instanceof ArrayType) {
                            for (Value arg : args) {
                                Operand offset;
                                if (arg instanceof ConstantInteger) { // 是常数，化为Li
                                    if (((ConstantInteger) arg).getValue() == 0) { // 为0,等于不变
                                        if (baseType instanceof ArrayType) {
                                            baseType = ((ArrayType) baseType).getInside();
                                        } else {
                                            assert arg == args.get(args.size() - 1);
                                        }
                                        continue;
                                    }
                                    Li li = new Li(InstType.Li, nowB, new VirtualReg(), new Imm(((ConstantInteger) arg).getValue()));
                                    nowB.addInst(li);
                                    offset = li.getDst();
                                } else { // 是指令计算结果，直接取出dst
                                    offset = irMap.get(arg);
                                }
                                int count = 0;
                                int num = baseType.getSize();
                                while (num % 2 == 0) {
                                    num = num / 2;
                                    count++;
                                }
                                if (num != 1) {
                                    count = -1;
                                }
                                MipsInst calculateOffset;
                                if (count == -1) {
                                    calculateOffset = new MipsBinary(InstType.Mul, nowB, new VirtualReg(), offset, new Imm(baseType.getSize()));
                                    nowB.addInst(calculateOffset);
                                } else {
                                    calculateOffset = new BitInst(InstType.Sll, nowB, new VirtualReg(), offset, new Imm(count));
                                    nowB.addInst(calculateOffset);
                                }
                                MipsBinary add = new MipsBinary(InstType.Addu, nowB, new VirtualReg(), calculateOffset.getDst(), lastOffset);
                                nowB.addInst(add);
                                lastOffset = add.getDst();
                                if (baseType instanceof ArrayType) {
                                    baseType = ((ArrayType) baseType).getInside();
                                } else {
                                    assert arg == args.get(args.size() - 1);
                                }
                            }
                        } else {
                            assert baseType instanceof PointerType;
                            baseType = ((PointerType) baseType).getPoints();
                            for (Value arg : args) {
                                Operand offset;
                                if (arg instanceof ConstantInteger) { // 是常数，化为Li
                                    if (((ConstantInteger) arg).getValue() == 0) {
                                        if (baseType instanceof ArrayType) {
                                            baseType = ((ArrayType) baseType).getInside();
                                        } else {
                                            assert arg == args.get(args.size() - 1);
                                        }
                                        continue;
                                    }
                                    Li li = new Li(InstType.Li, nowB, new VirtualReg(), new Imm(((ConstantInteger) arg).getValue()));
                                    nowB.addInst(li);
                                    offset = li.getDst();
                                } else { // 是指令计算结果，直接取出dst
                                    offset = irMap.get(arg);
                                }
                                int count = 0;
                                int num = baseType.getSize();
                                while (num % 2 == 0) {
                                    num = num / 2;
                                    count++;
                                }
                                if (num != 1) {
                                    count = -1;
                                }
                                MipsInst calculateOffset;
                                if (count == -1) {
                                    calculateOffset = new MipsBinary(InstType.Mul, nowB, new VirtualReg(), offset, new Imm(baseType.getSize()));
                                    nowB.addInst(calculateOffset);
                                } else {
                                    calculateOffset = new BitInst(InstType.Sll, nowB, new VirtualReg(), offset, new Imm(count));
                                    nowB.addInst(calculateOffset);
                                }
                                MipsBinary add = new MipsBinary(InstType.Addu, nowB, new VirtualReg(), calculateOffset.getDst(), lastOffset);
                                nowB.addInst(add);
                                lastOffset = add.getDst();
                                if (baseType instanceof ArrayType) {
                                    baseType = ((ArrayType) baseType).getInside();
                                } else {
                                    assert arg == args.get(args.size() - 1);
                                }
                            }
                        }
                        ((MemInst.GEPInst) k).setGlobal(global);
                        irMap.put(k, lastOffset);
                    } else if (k instanceof CalculateInst.BinaryInst) {
                        if (k.getTag() == Tag.Add || k.getTag() == Tag.Sub || k.getTag() == Tag.Mul ||
                                k.getTag() == Tag.Div || k.getTag() == Tag.Mod) { // 两种情况：常数->立即数，计算结果->寄存器
                        /*
                        只考虑加减乘除取模运算，比较指令之后和跳转一起处理
                        */
                            Value op1 = ((CalculateInst.BinaryInst) k).getOp1();
                            Value op2 = ((CalculateInst.BinaryInst) k).getOp2();
                            Operand o1;
                            Operand o2;
                            if (op1 instanceof ConstantInteger) {
                                o1 = new Imm(((ConstantInteger) op1).getValue());
                            } else {
                                o1 = irMap.get(op1);
                            }
                            if (op2 instanceof ConstantInteger) {
                                o2 = new Imm(((ConstantInteger) op2).getValue());
                            } else {
                                o2 = irMap.get(op2);
                            }
                            if (o1 instanceof Imm) { // 操作数1是立即数
                                Li binary = new Li(InstType.Li, nowB, new VirtualReg(), o1);
                                nowB.addInst(binary);
                                o1 = binary.getDst();
                            }
//                            if (o2 instanceof Imm) { // 操作数2是立即数
//                                Li binary = new Li(InstType.Li, nowB, new VirtualReg(), o2);
//                                nowB.addInst(binary);
//                                o2 = binary.getDst();
//                            }
                            if (k.getTag() == Tag.Add) {
                                MipsBinary binary = new MipsBinary(InstType.Addu, nowB, new VirtualReg(), o1, o2);
                                nowB.addInst(binary);
                                irMap.put(k, binary.getDst());
                            } else if (k.getTag() == Tag.Sub) {
                                MipsBinary binary = new MipsBinary(InstType.Subu, nowB, new VirtualReg(), o1, o2);
                                nowB.addInst(binary);
                                irMap.put(k, binary.getDst());
                            } else if (k.getTag() == Tag.Mul) {
                                int count = -1;
                                if (o2 instanceof Imm) {
                                    count = 0;
                                    int num = ((Imm) o2).getImm();
                                    while (num % 2 == 0) {
                                        num = num / 2;
                                        count++;
                                    }
                                    if (num != 1) {
                                        count = -1;
                                    }
                                }
                                if (count == -1) { // 不能优化
                                    MipsBinary binary = new MipsBinary(InstType.Mul, nowB, new VirtualReg(), o1, o2);
                                    nowB.addInst(binary);
                                    irMap.put(k, binary.getDst());
                                } else {
                                    BitInst bitInst = new BitInst(InstType.Sll, nowB, new VirtualReg(), o1, new Imm(count));
                                    nowB.addInst(bitInst);
                                    irMap.put(k, bitInst.getDst());
                                }
                            } else if (k.getTag() == Tag.Div) {
                                int count = -1;
                                if (o2 instanceof Imm) {
                                    count = 0;
                                    int num = ((Imm) o2).getImm();
                                    while (num % 2 == 0) {
                                        num = num / 2;
                                        count++;
                                    }
                                    if (num != 1) {
                                        count = -1;
                                    }
                                }
                                if (count == -1) {
                                    if (o2 instanceof Imm) { // 操作数2是立即数
                                        Li binary = new Li(InstType.Li, nowB, new VirtualReg(), o2);
                                        nowB.addInst(binary);
                                        o2 = binary.getDst();
                                    }
                                    MipsBinary binary = new MipsBinary(InstType.Div, nowB, null, o1, o2);
                                    nowB.addInst(binary);
                                    MipsUnary unary = new MipsUnary(InstType.Mflo, nowB, new VirtualReg());
                                    nowB.addInst(unary);
                                    irMap.put(k, unary.getDst());
                                } else {
                                    BitInst bitInst = new BitInst(InstType.Srl, nowB, new VirtualReg(), o1, new Imm(count));
                                    nowB.addInst(bitInst);
                                    irMap.put(k, bitInst.getDst());
                                }
                            } else {
                                if (o2 instanceof Imm) { // 操作数2是立即数
                                    Li binary = new Li(InstType.Li, nowB, new VirtualReg(), o2);
                                    nowB.addInst(binary);
                                    o2 = binary.getDst();
                                }
                                assert k.getTag() == Tag.Mod;
                                MipsBinary binary = new MipsBinary(InstType.Div, nowB, null, o1, o2);
                                nowB.addInst(binary);
                                MipsUnary unary = new MipsUnary(InstType.Mfhi, nowB, new VirtualReg());
                                nowB.addInst(unary);
                                irMap.put(k, unary.getDst());
                            }
                        } else { // 考虑4种比较，和branch配套
                            inCompare = true;
                            compareStore = k;
                        }
                    } else if (k instanceof TerminatorInst.BranchInst) {
                        inCompare = false;
                        ArrayList<Value> operandOfk = k.getOperands();
                        if (operandOfk.size() == 1) { // 无条件跳转
                            MipsBBlock nextBlock = getNextBlock();
                            MipsBBlock block = blockMap.get((BasicBlock) operandOfk.get(0));
                            if (nextBlock == block) {
                                continue; // 下一个块就是跳转块，忽略
                            }
                            Jump jump = new Jump(InstType.J, nowB, block);
                            nowB.addInst(jump);
                        } else { // 条件跳转
                            Value cond = operandOfk.get(0);
                            BasicBlock ifTrueBlock = (BasicBlock) operandOfk.get(1);
                            MipsBBlock mTrueBlock = blockMap.get(ifTrueBlock);
                            BasicBlock ifFalseBlock = (BasicBlock) operandOfk.get(2);
                            MipsBBlock mFalseBlock = blockMap.get(ifFalseBlock);
                            if (cond instanceof CalculateInst.UnaryInst) { // not，已经计算出来
                                assert ((CalculateInst.UnaryInst) cond).getTag() == Tag.Not;
                                Operand operand = irMap.get(cond);
                                Branch branch = new Branch(InstType.Bne, nowB, operand, new PhysicalReg("$zero", 0), mTrueBlock);
                                nowB.addInst(branch);
                                if (!(mFalseBlock == getNextBlock())) { // 不一致需要跳转
                                    Jump jump = new Jump(InstType.J, nowB, mFalseBlock);
                                    nowB.addInst(jump);
                                }
                            } else if (cond instanceof ConstantInteger) { // 单独一个数
                                Li li = new Li(InstType.Li, nowB, new VirtualReg(), new Imm(((ConstantInteger) cond).getValue()));
                                nowB.addInst(li);
                                Branch branch = new Branch(InstType.Bne, nowB, li.getDst(), new PhysicalReg("$zero", 0), mTrueBlock);
                                nowB.addInst(branch);
                                if (!(mFalseBlock == getNextBlock())) { // 不一致需要跳转
                                    Jump jump = new Jump(InstType.J, nowB, mFalseBlock);
                                    nowB.addInst(jump);
                                }
                            } else if (!(cond instanceof CalculateInst.BinaryInst)) { // not，已经计算出来
                                Operand operand = irMap.get(cond);
                                Branch branch = new Branch(InstType.Bne, nowB, operand, new PhysicalReg("$zero", 0), mTrueBlock);
                                nowB.addInst(branch);
                                if (!(mFalseBlock == getNextBlock())) { // 不一致需要跳转
                                    Jump jump = new Jump(InstType.J, nowB, mFalseBlock);
                                    nowB.addInst(jump);
                                }
                            } else {
                                assert cond instanceof CalculateInst.BinaryInst;
                                Value op1 = ((CalculateInst.BinaryInst) cond).getOp1();
                                Value op2 = ((CalculateInst.BinaryInst) cond).getOp2();
                                Operand o1;
                                Operand o2;
                                if (op1 instanceof ConstantInteger) {
                                    o1 = new Imm(((ConstantInteger) op1).getValue());
                                } else {
                                    o1 = irMap.get(op1);
                                }
                                if (op2 instanceof ConstantInteger) {
                                    o2 = new Imm(((ConstantInteger) op2).getValue());
                                } else {
                                    o2 = irMap.get(op2);
                                }
                                if (o1 instanceof Imm) { // 操作数1是立即数
                                    Li binary = new Li(InstType.Li, nowB, new VirtualReg(), o1);
                                    nowB.addInst(binary);
                                    o1 = binary.getDst();
                                }
                                if (o2 instanceof Imm) { // 操作数2是立即数
                                    Li binary = new Li(InstType.Li, nowB, new VirtualReg(), o2);
                                    nowB.addInst(binary);
                                    o2 = binary.getDst();
                                }
                                if (((CalculateInst.BinaryInst) cond).getTag() == Tag.Neq) {
                                    Branch branch = new Branch(InstType.Bne, nowB, o1, o2, mTrueBlock);
                                    nowB.addInst(branch);
                                } else if (((CalculateInst.BinaryInst) cond).getTag() == Tag.Eql) {
                                    Branch branch = new Branch(InstType.Beq, nowB, o1, o2, mTrueBlock);
                                    nowB.addInst(branch);
                                } else {
                                    MipsBinary binary = new MipsBinary(InstType.Sub, nowB, new VirtualReg(), o1, o2);
                                    nowB.addInst(binary);
                                    if (((CalculateInst.BinaryInst) cond).getTag() == Tag.Leq) {
                                        Branch branch = new Branch(InstType.Blez, nowB, binary.getDst(), null, mTrueBlock);
                                        nowB.addInst(branch);
                                    } else if (((CalculateInst.BinaryInst) cond).getTag() == Tag.Lss) {
                                        Branch branch = new Branch(InstType.Bltz, nowB, binary.getDst(), null, mTrueBlock);
                                        nowB.addInst(branch);
                                    } else if (((CalculateInst.BinaryInst) cond).getTag() == Tag.Geq) {
                                        Branch branch = new Branch(InstType.Bgez, nowB, binary.getDst(), null, mTrueBlock);
                                        nowB.addInst(branch);
                                    } else if (((CalculateInst.BinaryInst) cond).getTag() == Tag.Gre) {
                                        Branch branch = new Branch(InstType.Bgtz, nowB, binary.getDst(), null, mTrueBlock);
                                        nowB.addInst(branch);
                                    } else { // 加减乘除指令
                                        Operand ansOfCal = irMap.get(cond);
                                        Branch branch = new Branch(InstType.Bne, nowB, ansOfCal, new PhysicalReg("$zero", 0), mTrueBlock);
                                        nowB.addInst(branch);
                                    }
                                }
                                if (!(mFalseBlock == getNextBlock())) { // 不一致需要跳转
                                    Jump jump = new Jump(InstType.J, nowB, mFalseBlock);
                                    nowB.addInst(jump);
                                }
                            }
                        }
                    } else if (k instanceof CalculateInst.UnaryInst) { // 只有not
                        assert k.getTag() == Tag.Not;
                        Value op = ((CalculateInst.UnaryInst) k).getOp();
                        Operand src;
                        if (op instanceof ConstantInteger) {
                            src = new Imm(((ConstantInteger) op).getValue());
                        } else {
                            src = irMap.get(op);
                        }
                        if (src instanceof Imm) { // 转换成li
                            Li li = new Li(InstType.Li, nowB, new VirtualReg(), src);
                            nowB.addInst(li);
                            src = li.getDst();
                        }
                        MipsBinary binary = new MipsBinary(InstType.Sltu, nowB, new VirtualReg(), new PhysicalReg("$zero", 0), src);
                        nowB.addInst(binary);
                        MipsBinary binary1 = new MipsBinary(InstType.Xor, nowB, new VirtualReg(), binary.getDst(), new Imm(1));
                        nowB.addInst(binary1);
                        irMap.put(k, binary1.getDst());
                    } else if (k instanceof TerminatorInst.CallInst) { // 直接完成函数传参
                        ArrayList<Value> operandOfk = k.getOperands();
                        Function function = (Function) operandOfk.get(0);
                        ArrayList<Value> paras = new ArrayList<>();
                        for (Value ii : operandOfk) {
                            if (ii instanceof Function) {
                                continue;
                            }
                            paras.add(ii);
                        }
                        int stackSub = 0;
                        for (int count = 0; count < paras.size(); count++) {
                            if (count < 3) { // 放入传参寄存器a0-a2
                                if (count == 0) {
                                    if (paras.get(count) instanceof ConstantInteger) {
                                        Li li = new Li(InstType.Mov, nowB, new PhysicalReg("$a0",
                                                RegNameMap.getInstance().getRegNum("$a0")), new Imm(((ConstantInteger) paras.get(count)).getValue()));
                                        nowB.addInst(li);
                                    } else {
                                        Move move = new Move(InstType.Mov, nowB, new PhysicalReg("$a0",
                                                RegNameMap.getInstance().getRegNum("$a0")), irMap.get(paras.get(count)));
                                        nowB.addInst(move);
                                    }
                                } else if (count == 1) {
                                    if (paras.get(count) instanceof ConstantInteger) {
                                        Li li = new Li(InstType.Li, nowB, new PhysicalReg("$a1",
                                                RegNameMap.getInstance().getRegNum("$a1")), new Imm(((ConstantInteger) paras.get(count)).getValue()));
                                        nowB.addInst(li);
                                    } else {
                                        Move move = new Move(InstType.Mov, nowB, new PhysicalReg("$a1",
                                                RegNameMap.getInstance().getRegNum("$a1")), irMap.get(paras.get(count)));
                                        nowB.addInst(move);
                                    }
                                } else if (count == 2) {
                                    if (paras.get(count) instanceof ConstantInteger) {
                                        Li li = new Li(InstType.Li, nowB, new PhysicalReg("$a2",
                                                RegNameMap.getInstance().getRegNum("$a2")), new Imm(((ConstantInteger) paras.get(count)).getValue()));
                                        nowB.addInst(li);
                                    } else {
                                        Move move = new Move(InstType.Mov, nowB, new PhysicalReg("$a2",
                                                RegNameMap.getInstance().getRegNum("$a2")), irMap.get(paras.get(count)));
                                        nowB.addInst(move);
                                    }
                                }
                            } else {
                                if (paras.get(count) instanceof ConstantInteger) {
                                    Li li = new Li(InstType.Li, nowB, new VirtualReg(), new Imm(((ConstantInteger) paras.get(count)).getValue()));
                                    nowB.addInst(li);
                                    Store store = new Store(InstType.Sw, nowB, new PhysicalReg("$sp", 29), li.getDst(), new Imm(-stackSub));
                                    nowB.addInst(store);
                                    store.setGlobal(true);
                                    stackSub += 4;
                                } else {
                                    Store store = new Store(InstType.Sw, nowB, new PhysicalReg("$sp", 29), irMap.get(paras.get(count)), new Imm(-stackSub));
                                    nowB.addInst(store);
                                    store.setGlobal(true);
                                    stackSub += 4;
                                }
                            }
                        }
                        if (stackSub > 0) {
                            nowB.addInst(new MipsBinary(InstType.Sub, nowB, new PhysicalReg("$sp", 29), new PhysicalReg("$sp", 29), new Imm(stackSub)));
                        }
                        Call call = new Call(InstType.Jal, nowB, functionMap.get(function));
                        nowB.addInst(call);
                        int count = paras.size() - 3;
                        if (count > 0) {
                            nowB.addInst(new MipsBinary(InstType.Add, nowB, new PhysicalReg("$sp", 29), new PhysicalReg("$sp", 29), new Imm(4 * count)));
                        }
                        Move move = new Move(InstType.Mov, nowB, new VirtualReg(), new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")));
                        nowB.addInst(move);
                        irMap.put(k, move.getDst());
                    } else if (k instanceof IOInst.GetIntInst) { // 输入
                        nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")), new Imm(5)));
                        nowB.addInst(new Syscall(nowB));
                        irMap.put(k, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")));
                    } else if (k instanceof IOInst.PutIntInst) {
                        Value output = k.getOperands().get(0);
                        if (output instanceof ConstantInteger) {
                            nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$a0", RegNameMap.getInstance().getRegNum("$a0")), new Imm(((ConstantInteger) output).getValue())));
                            nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")), new Imm(1)));
                            nowB.addInst(new Syscall(nowB));
                        } else {
                            nowB.addInst(new Move(InstType.Mov, nowB, new PhysicalReg("$a0", RegNameMap.getInstance().getRegNum("$a0")), irMap.get(output)));
                            nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")), new Imm(1)));
                            nowB.addInst(new Syscall(nowB));
                        }
                    } else if (k instanceof IOInst.PutCharInst) {
                        int output = ((IOInst.PutCharInst) k).getOut();
                        nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$a0", RegNameMap.getInstance().getRegNum("$a0")), new Imm(output)));
                        nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")), new Imm(11)));
                        nowB.addInst(new Syscall(nowB));
                    } else if (k instanceof IOInst.PutStringInst) {
                        Value ptr = k.getOperands().get(0);
                        assert ptr instanceof StringLiteral;
                        La la = new La(nowB, new PhysicalReg("$a0", RegNameMap.getInstance().getRegNum("$a0")), new Label(ptr.getMIPSCount()));
                        nowB.addInst(la);
                        nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")), new Imm(4)));
                        nowB.addInst(new Syscall(nowB));
                    } else if (k instanceof TerminatorInst.RetInst) {
                        if (nowF.isMain()) {
                            nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$v0")), new Imm(10)));
                            nowB.addInst(new Syscall(nowB));
                        } else {
                            if (k.getOperands().isEmpty()) {
                                nowB.addInst(new Ret(InstType.Jal, nowB));
                            } else {
                                Value output = k.getOperands().get(0);
                                if (output instanceof ConstantInteger) {
                                    nowB.addInst(new Li(InstType.Li, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$a0")), new Imm(((ConstantInteger) output).getValue())));
                                    nowB.addInst(new Ret(InstType.Jal, nowB));
                                } else {
                                    nowB.addInst(new Move(InstType.Mov, nowB, new PhysicalReg("$v0", RegNameMap.getInstance().getRegNum("$a0")), irMap.get(output)));
                                    nowB.addInst(new Ret(InstType.Jal, nowB));
                                }
                            }
                        }
                    } else {
                        assert false;
                    }
                }
                mapVirtualRegister();
            }
            if (isVoid) {
                nowF.addInst(new Ret(InstType.Jal, nowB));
            }
        }
    }

    private void mapVirtualRegister() { // fixme 当前只支持块内
        ArrayList<MipsInst> blockInsts = new ArrayList<>();
        for (MipsInst i : nowB.getInsts()) { // 反向插入
            blockInsts.add(0, i);
        }
        HashSet<VirtualReg> livingVariables = new HashSet<>();
        ArrayList<VirtualReg> defs = new ArrayList<>();
        for (MipsInst now : blockInsts) {
            ArrayList<VirtualReg> uses = new ArrayList<>();
            VirtualReg def = getUseAndDef(now, uses);
            if (def != null) {
                livingVariables.remove(def); // 将自身标记为不活跃
                for (VirtualReg livingReg : livingVariables) { // 与和目前活跃的建立冲突图
                    def.addNode(livingReg);
                    livingReg.addNode(def);
                }
                defs.add(def); // 添加当前到defs，用于后续分析图
            }
            livingVariables.addAll(uses); // 将自己标记为活跃
        }
        assert livingVariables.isEmpty(); // 应该所有的def都被清除
        int num = 0;
        int overflow = 4;
        ArrayList<VirtualReg> mappingList = new ArrayList<>();
        while (!defs.isEmpty()) { // 开始映射
            VirtualReg mapping = null;
            for (VirtualReg i : defs) {
                if (i.getGraphNodeSize() < 8) { // 分配t0-t7
                    mapping = i;
                    break;
                }
            }
            if (mapping != null) { // 还可以分配
                mappingList.add(mapping);
                mapping.setMappedOut(true);
                defs.remove(mapping);
            } else { // 溢出寄存器，放在栈上，alloca之后，从图中删除
                VirtualReg overflowReg = defs.get(0);
                nowF.addOverflowReg(overflowReg);
                for (VirtualReg i : overflowReg.getGraphNode()) { // 删除结点
                    i.deleteNode(overflowReg);
                }
                defs.remove(overflowReg);
                overflowReg.setOffset(overflow);
                overflow += 4;
            }
        }
        Collections.reverse(mappingList);
        for (VirtualReg i : mappingList) { // 将可映射的映射
            i.setMappedOut(false);
            HashSet<Integer> ok = new HashSet<>();
            ok.add(0);
            ok.add(1);
            ok.add(2);
            ok.add(3);
            ok.add(4);
            ok.add(5);
            ok.add(6);
            ok.add(7);
            i.conflict(ok);
            i.mapTo(ok.iterator().next());
        }
    }

    private VirtualReg getUseAndDef(MipsInst now, ArrayList<VirtualReg> uses) { // 把所有的use写到uses，定义返回
        if (now instanceof Branch) {
            if (((Branch) now).getRhs() instanceof VirtualReg) {
                uses.add(((VirtualReg) ((Branch) now).getRhs()));
            }
            if (((Branch) now).getLhs() instanceof VirtualReg) {
                uses.add(((VirtualReg) ((Branch) now).getLhs()));
            }
        } else if (now instanceof Call || now instanceof Ret || now instanceof Syscall) {
            return null;
        } else if (now instanceof Jump) {
            if (((Jump) now).getJumpReg() instanceof VirtualReg) {
                uses.add((VirtualReg) ((Jump) now).getJumpReg());
            }
        } else if (now instanceof Load) {
            if (((Load) now).getPtr() instanceof VirtualReg) {
                uses.add((VirtualReg) ((Load) now).getPtr());
            }
            if (((Load) now).getDst() instanceof VirtualReg) {
                return (VirtualReg) ((Load) now).getDst();
            }
        } else if (now instanceof Store) {
            if (((Store) now).getPtr() instanceof VirtualReg) {
                uses.add((VirtualReg) ((Store) now).getPtr());
            }
            if (((Store) now).getSrc() instanceof VirtualReg) {
                uses.add((VirtualReg) ((Store) now).getSrc());
            }
        } else if (now instanceof La) {
            if (((La) now).getDst() instanceof VirtualReg) {
                return (VirtualReg) ((La) now).getDst();
            }
        } else if (now instanceof Li) {
            if (((Li) now).getDst() instanceof VirtualReg) {
                return (VirtualReg) ((Li) now).getDst();
            }
        } else if (now instanceof MipsBinary) {
            if (((MipsBinary) now).getRhs() instanceof VirtualReg) {
                uses.add((VirtualReg) ((MipsBinary) now).getRhs());
            }
            if (((MipsBinary) now).getLhs() instanceof VirtualReg) {
                uses.add((VirtualReg) ((MipsBinary) now).getLhs());
            }
            if (((MipsBinary) now).getDst() instanceof VirtualReg) {
                return (VirtualReg) ((MipsBinary) now).getDst();
            }
        } else if (now instanceof MipsUnary) {
            if (((MipsUnary) now).getDst() != null) {
                return (VirtualReg) ((MipsUnary) now).getDst();
            }
        } else if (now instanceof Move) {
            if (((Move) now).getSrc() instanceof VirtualReg) {
                uses.add((VirtualReg) ((Move) now).getSrc());
            }
            if (((Move) now).getDst() instanceof VirtualReg) {
                return (VirtualReg) ((Move) now).getDst();
            }
        } else if (now instanceof BitInst) {
            if (((BitInst) now).getSrc() instanceof VirtualReg) {
                uses.add((VirtualReg) ((BitInst) now).getSrc());
            }
            return (VirtualReg) ((BitInst) now).getDst();
        } else {
            System.out.println(now.getClass());
            assert false;
        }
        return null;
    }

    private void createFlowGraph(Function function) { // 构建流图
        for (BasicBlock bb : function.getBasicBlocks()) {
            nowF.getBackwardFlow().put(blockMap.get(bb), new ArrayList<>());
            nowF.getForwardFlow().put(blockMap.get(bb), new ArrayList<>());
        }
        for (BasicBlock bb : function.getBasicBlocks()) {
            Instruction last = bb.getInstructions().size() == 0 ? null : bb.getInstructions().get(bb.getInstructions().size() - 1);
            if (last instanceof TerminatorInst.RetInst) {
                nowF.getOutBlocks().add(blockMap.get(bb));
            } else if (last instanceof TerminatorInst.BranchInst) {
                if (last.getOperands().size() == 1) { // 无条件跳转
                    MipsBBlock up = blockMap.get(bb);
                    MipsBBlock down = blockMap.get((BasicBlock) last.getOperands().get(0));
                    nowF.getForwardFlow().get(up).add(down);
                    nowF.getBackwardFlow().get(down).add(up);
                } else { // 条件跳转
                    MipsBBlock up = blockMap.get(bb);
                    MipsBBlock down1 = blockMap.get((BasicBlock) last.getOperands().get(1));
                    MipsBBlock down2 = blockMap.get((BasicBlock) last.getOperands().get(2));
                    nowF.getForwardFlow().get(up).add(down1);
                    nowF.getBackwardFlow().get(down1).add(up);
                    nowF.getForwardFlow().get(up).add(down2);
                    nowF.getBackwardFlow().get(down2).add(up);
                }
            }
        }
    }

    private void handleAlloca(Function i) {
        ArrayList<MemInst.AllocaInst> allocaInsts = new ArrayList<>();
        for (BasicBlock j : i.getBasicBlocks()) {
            for (Instruction k : j.getInstructions()) { // 遍历第一遍找到所有alloca，分配栈空间
                if (k instanceof MemInst.AllocaInst) {
                    allocaInsts.add((MemInst.AllocaInst) k);
                }
            }
        }
        HashMap<MemInst.AllocaInst, Integer> allocaMap = new HashMap<>();
        int nowOffset = 0;
        for (MemInst.AllocaInst m : allocaInsts) {
            nowOffset += m.getAllocaType().getSize();
        }
        for (MemInst.AllocaInst m : allocaInsts) {
            nowOffset -= m.getAllocaType().getSize();
            allocaMap.put(m, nowOffset + 4);
        }
        assert nowOffset == 0;
        nowF.setAllocaMap(allocaMap);
        nowF.setAllocaList(allocaInsts);
    }

    private MipsBBlock getNextBlock() {
        return nowF.getbBlocks().get(nowF.getbBlocks().indexOf(nowB) + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        for (GlobalVariable globalVariable : globalVariable2Label.keySet()) {
            sb.append(globalVariable.generateMIPSCode());
        }
        for (StringLiteral stringLiteral : stringLiteral2Label.keySet()) {
            sb.append(stringLiteral.generateMIPSCode());
        }
        sb.append(".text\n");
        for (MipsFunction function : functions) {
            if (function.isMain()) {
                sb.append(function);
                break;
            }
        }
        for (MipsFunction function : functions) {
            if (!function.isMain()) {
                sb.append(function);
            }
        }
        return sb.toString();
    }

    public ArrayList<MipsFunction> getFunctions() {
        return functions;
    }
}