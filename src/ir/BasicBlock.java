package ir;

import ir.instructions.Instruction;
import ir.types.LabelType;
import ir.values.Value;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions;

    public BasicBlock(String name) {
        super(name, new LabelType());
        instructions = new ArrayList<>();
    }

    public void addInst(Instruction inst) {
        instructions.add(inst);
    }

    public String getCount() {
        return "%" + ((LabelType) getType()).getHandler();
    }

    public String getMipsCount() {
        return Integer.toString(((LabelType) getType()).getHandler());
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(((LabelType) getType()).getHandler()).append(":\n");
        for (Instruction instruction : instructions) {
            sb.append("\t").append(instruction);
        }
        sb.append("\n");
        return sb.toString();
    }
}
