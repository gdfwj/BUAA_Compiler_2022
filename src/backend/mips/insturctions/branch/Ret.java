package backend.mips.insturctions.branch;

import backend.mips.MipsBBlock;
import backend.mips.MipsFunction;
import backend.mips.insturctions.InstType;
import backend.mips.insturctions.MipsInst;
import ir.instructions.MemInst;

public class Ret extends MipsInst {
    public Ret(InstType type, MipsBBlock block) {
        super(type, block);
    }

    @Override
    public String toString() {
//        if(getBlock().getContainsFunction().isMain()) {
//            return "li $v0, 10\nsysycall\n";
//        }
//        return "j " + getBlock().getContainsFunction().getRetBlockName()+"\n";
        StringBuilder sb = new StringBuilder();
        MipsFunction function = getBlock().getContainsFunction();
        if (function.isMain()) {
            sb.append("li $v0, 10\n");
            sb.append("sysycall\n");
        } else {
            int allocaSize = 0;
            for (MemInst.AllocaInst i : function.getAllocaList()) {
                allocaSize += i.getAllocaType().getSize();
            }
            int paraStackSize = function.getParas().size() - 3;
            sb.append("addi $sp, $sp, ").append(function.getOverflowOffset()).append("\n"); // 恢复溢出寄存器
            sb.append("addi $sp, $sp, ").append(allocaSize).append("\n");
//            for (int i = 0; i <= 7; i++) { // 取出s0到s7，反着存
//                sb.append("lw $s").append(i).append(", ").append((i + 1) * 4).append("($sp)").append("\n");
//            }
//            sb.append("addi $sp, $sp, 32\n");
            for (int i = 0; i <= 7; i++) { // 取出t0到t7，反着存
                sb.append("lw $t").append(i).append(", ").append((i + 1) * 4).append("($sp)").append("\n");
            }
            sb.append("lw $ra, 36($sp)\n");
            sb.append("addi $sp, $sp, 36\n");
//            if (paraStackSize > 0) { 已经在调用者回收
//                sb.append("addi $sp, $sp, ").append(4 * paraStackSize).append("\n");
//            }
            sb.append("jr $ra\n");
        }
        return sb.toString();
    }
}
