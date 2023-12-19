package backend.mips;

import backend.mips.insturctions.MipsInst;
import backend.mips.reg.VirtualReg;
import ir.Function;
import ir.instructions.MemInst;
import ir.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsFunction {
    private ArrayList<MipsBBlock> bBlocks = new ArrayList<>();
    private ArrayList<Function.Arg> paras = new ArrayList<>();
    private String name;
    private boolean isMain;
    private int stackSize;
    private HashMap<MemInst.AllocaInst, Integer> allocaMap = new HashMap<>();
    private ArrayList<MemInst.AllocaInst> allocaList = new ArrayList<>();
    private String retBlockName;

    private ArrayList<MipsBBlock> outBlocks = new ArrayList<>();
    private MipsBBlock inBlock;

    private ArrayList<VirtualReg> overflowReg = new ArrayList<>();

    private int overflowOffset = 0;

    private final HashMap<MipsBBlock, ArrayList<MipsBBlock>> forwardFlow = new HashMap<>();
    private final HashMap<MipsBBlock, ArrayList<MipsBBlock>> backwardFlow = new HashMap<>();
    int now = -1;

    public MipsFunction(String name, ArrayList<Function.Arg> paras) {
        this.name = name;
        this.paras = paras;
        isMain = name.equals("main");
        retBlockName = name+"_____ret";
    }

    public String getRetBlockName() {
        return retBlockName;
    }

    public HashMap<MipsBBlock, ArrayList<MipsBBlock>> getBackwardFlow() {
        return backwardFlow;
    }

    public void setInBlock(MipsBBlock inBlock) {
        this.inBlock = inBlock;
    }

    public MipsBBlock getInBlock() {
        return inBlock;
    }

    public ArrayList<MipsBBlock> getOutBlocks() {
        return outBlocks;
    }

    public HashMap<MipsBBlock, ArrayList<MipsBBlock>> getForwardFlow() {
        return forwardFlow;
    }

    public void addBBlock(MipsBBlock block) {
        bBlocks.add(block);
        now++;
    }

    public ArrayList<MemInst.AllocaInst> getAllocaList() {
        return allocaList;
    }

    public void addOverflowReg(VirtualReg i) {
        overflowReg.add(i);
        overflowOffset+=4;
    }

    public ArrayList<Function.Arg> getParas() {
        return paras;
    }

    public boolean isMain() {
        return isMain;
    }

    public String getName() {
        return name;
    }

    public void addInst(MipsInst inst) {
        bBlocks.get(now).addInst(inst);
    }

    public void setAllocaMap(HashMap<MemInst.AllocaInst, Integer> allocaMap) {
        this.allocaMap = allocaMap;
        stackSize = allocaMap.size() * 4;
    }

    public int getAllocaOffset(MemInst.AllocaInst inst) {
        return allocaMap.get(inst);
    }

    public void setAllocaList(ArrayList<MemInst.AllocaInst> allocaList) {
        this.allocaList = allocaList;
    }

    public ArrayList<VirtualReg> getOverflowReg() {
        return overflowReg;
    }

    public int getOverflowOffset() {
        return overflowOffset;
    }

    public ArrayList<MipsBBlock> getbBlocks() {
        return bBlocks;
    }

    @Override
    public String toString() {
        for(MipsBBlock block:bBlocks) {
            block.setOverflowOffset(overflowOffset); // 设置溢出造成的偏移
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":").append("\n");
//        sb.append("overflow: ").append(overflowOffset).append("\n");
        if (!name.equals("main")) {
            sb.append("move $v1, $sp\n"); // 存储之前的sp
            sb.append("subiu $sp, $sp, 36\n"); // 分配t0-t7和ra的空间
            for (int i = 0; i <= 7; i++) { // 存放t0到t7，反着存
                sb.append("sw $t").append(i).append(", ").append((i + 1) * 4).append("($sp)").append("\n");
            }
            sb.append("sw $ra, 36($sp)\n");
            for (int i = 0; i < allocaList.size(); i++) { // 传参和栈空间初始化
                int countStack = 0;
                if (i < paras.size()) { // 传参
                    if (i < 3) { // 寄存器传参
                        sb.append("sw $a").append(i).append(", 0($sp)\n");
                        sb.append("subiu $sp, $sp, 4\n");
                    } else { // 内存传参
                        int offset = (paras.size() - 3) * 4 - (i - 3) * 4;
                        assert offset > 0;
                        sb.append("lw $t0, ").append(offset).append("($v1)\n");
                        sb.append("sw $t0, ").append("0($sp)\n");
                        sb.append("subiu $sp, $sp, 4\n");
                    }
                } else { // 初始化(不需要
                    countStack += allocaList.get(i).getAllocaType().getSize();
                }
                if (countStack > 0) {
                    sb.append("subiu $sp, $sp, ").append(countStack).append("\n");
                }
            }
        } else {
            int countStack = 0;
            for (MemInst.AllocaInst inst : allocaList) {
                countStack += inst.getAllocaType().getSize();
            }
            sb.append("subiu $sp, $sp ").append(countStack).append("\n");
        }
        sb.append("subiu $sp, $sp ").append(overflowOffset).append("\n"); // 为溢出寄存器分配空间
        for (MipsBBlock block : bBlocks) {
            sb.append(block);
        }
        if (name.equals("main")) {
            sb.append("li $v0, 10\n");
            sb.append("syscall\n");
        } //else {
//            sb.append(retBlockName+":\n");
//            MipsFunction function = this;
//            if (function.isMain()) {
//                sb.append("li $v0, 10\n");
//                sb.append("sysycall\n");
//            } else {
//                int allocaSize = 0;
//                for (MemInst.AllocaInst i : function.getAllocaList()) {
//                    allocaSize += i.getAllocaType().getSize();
//                }
//                int paraStackSize = function.getParas().size() - 3;
//                sb.append("addi $sp, $sp, ").append(function.getOverflowOffset()).append("\n"); // 恢复溢出寄存器
//                sb.append("addi $sp, $sp, ").append(allocaSize).append("\n");
////            for (int i = 0; i <= 7; i++) { // 取出s0到s7，反着存
////                sb.append("lw $s").append(i).append(", ").append((i + 1) * 4).append("($sp)").append("\n");
////            }
////            sb.append("addi $sp, $sp, 32\n");
//                for (int i = 0; i <= 7; i++) { // 取出t0到t7，反着存
//                    sb.append("lw $t").append(i).append(", ").append((i + 1) * 4).append("($sp)").append("\n");
//                }
//                sb.append("lw $ra, 36($sp)\n");
//                sb.append("addi $sp, $sp, 36\n");
////            if (paraStackSize > 0) { 已经在调用者回收
////                sb.append("addi $sp, $sp, ").append(4 * paraStackSize).append("\n");
////            }
//                sb.append("jr $ra\n");
//            }
//            return sb.toString();
//        }
        return sb.toString();
    }
}
