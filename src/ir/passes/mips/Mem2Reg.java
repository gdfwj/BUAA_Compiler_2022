package ir.passes.mips;

import backend.mips.MipsBBlock;
import backend.mips.insturctions.InstType;
import ir.BasicBlock;
import ir.Function;
import ir.GlobalVariable;
import ir.MyModule;
import ir.instructions.IOInst;
import ir.instructions.Instruction;
import ir.instructions.MemInst;
import ir.instructions.TerminatorInst;
import ir.passes.Pass;
import ir.values.Value;
import sym.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Mem2Reg implements Pass.IRPass {

    private HashMap<BasicBlock, ArrayList<BasicBlock>> forwardCFG; // 存储后缀
    private HashMap<BasicBlock, ArrayList<BasicBlock>> backwardCFG; // 存储后缀
    private HashMap<BasicBlock, HashSet<BasicBlock>> dom;
    private ArrayList<BasicBlock> outBlocks;

    @Override
    public String getName() {
        return "Mem2Reg";
    }

    @Override
    public void run(MyModule m) {
        for (Function function : m.getFunctions()) {
            setDom(function);
            for (BasicBlock bb : function.getBasicBlocks()) {
                promoteSingleBlockAlloca(bb);
            }
        }
    }

    public void promoteSingleBlockAlloca(BasicBlock bb) {
        for (int index = 0; index < bb.getInstructions().size(); index++) { // 将load替换成最近的store
            Instruction i = bb.getInstructions().get(index);
            if (i instanceof MemInst.LoadInst) {
                if (!(((MemInst.LoadInst) i).getPtr() instanceof MemInst.GEPInst)) {// 不能是数组
                    MemInst.StoreInst nearestStore = findStore(bb.getInstructions(), index, ((MemInst.LoadInst) i).getPtr());
                    if (nearestStore != null) { // 替换返回值 ，将load获得的值替换成store使用的值
                        for (Value value : i.getUses()) {
                            value.replaceOperands(i, nearestStore.getInput());
                        }
                        bb.getInstructions().remove(i);
                        index--;
                    }
                }
            }
        }
    }

    public MemInst.StoreInst findStore(ArrayList<Instruction> instructions, int index, Value alloca) {
        for (; index >= 0; index--) {
            Instruction now = instructions.get(index);
            if (now instanceof MemInst.StoreInst && ((MemInst.StoreInst) now).getPtr() == alloca
                    && !(((MemInst.StoreInst) now).getInput() instanceof Function.Arg)
                    && !(((MemInst.StoreInst) now).getInput() instanceof IOInst.GetIntInst)) { // 存储的地址是读取的地址，不能是传参
                return (MemInst.StoreInst) now;
            }
        }
        return null;
    }

    private void setDom(Function function) {
        dom = new HashMap<>();
        for (BasicBlock bb : function.getBasicBlocks()) {
            HashSet<BasicBlock> all = new HashSet<>(function.getBasicBlocks());
            dom.put(bb, all);
        }
        createFlowGraph(function);
        HashSet<BasicBlock> dom0 = new HashSet<>();
        dom0.add(function.getBasicBlocks().get(0));
        dom.put(function.getBasicBlocks().get(0), dom0); // 初始化所有支配
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 1; i < function.getBasicBlocks().size(); i++) {
                HashSet<BasicBlock> temp = new HashSet<>(dom.get(function.getBasicBlocks().get(i)));
                for (BasicBlock pred : backwardCFG.get(function.getBasicBlocks().get(i))) {
                    temp.retainAll(dom.get(pred));
                }
                temp.add(function.getBasicBlocks().get(i));
                if (!temp.equals(dom.get(function.getBasicBlocks().get(i)))) {
                    dom.put(function.getBasicBlocks().get(i), temp);
                }
            }
        }
    }

    private void createFlowGraph(Function function) { // 构建流图
        forwardCFG = new HashMap<>();
        backwardCFG = new HashMap<>();
        outBlocks = new ArrayList<>();
        for (BasicBlock bb : function.getBasicBlocks()) {
            forwardCFG.put(bb, new ArrayList<>());
            backwardCFG.put(bb, new ArrayList<>());
        }
        for (BasicBlock bb : function.getBasicBlocks()) {
            Instruction last = bb.getInstructions().size() == 0 ? null : bb.getInstructions().get(bb.getInstructions().size() - 1);
            if (last instanceof TerminatorInst.RetInst) {
                outBlocks.add(bb);
            } else if (last instanceof TerminatorInst.BranchInst) {
                if (last.getOperands().size() == 1) { // 无条件跳转
                    BasicBlock down = (BasicBlock) last.getOperands().get(0);
                    forwardCFG.get(bb).add(down);
                    backwardCFG.get(down).add(bb);
                } else { // 条件跳转
                    BasicBlock up = bb;
                    BasicBlock down1 = (BasicBlock) last.getOperands().get(1);
                    BasicBlock down2 = (BasicBlock) last.getOperands().get(2);
                    forwardCFG.get(up).add(down1);
                    backwardCFG.get(down1).add(up);
                    forwardCFG.get(up).add(down2);
                    backwardCFG.get(down2).add(up);
                }
            } else {
                if (function.getBasicBlocks().indexOf(bb) + 1 < function.getBasicBlocks().size()) {
                    BasicBlock down = function.getBasicBlocks().get(function.getBasicBlocks().indexOf(bb) + 1);
                    forwardCFG.get(bb).add(down);
                    backwardCFG.get(down).add(bb);
                } else {
                    function.getBasicBlocks().remove(bb);
                    break;
                }
            }
        }
    }
}