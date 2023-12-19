package ir;

import ir.instructions.Instruction;
import ir.types.Type;
import ir.values.Value;

import java.util.ArrayList;

public class MyModule {
    private static final MyModule instance = new MyModule();
    private final ArrayList<GlobalVariable> globalVariables;
    private final ArrayList<StringLiteral> stringLiterals;
    private final ArrayList<Function> functions;
    private final ArrayList<Value> instructions;
    private final ArrayList<Integer> last;
    private int nowFunc;


    public static MyModule getInstance() {
        return instance;
    }

    private MyModule() {
        globalVariables = new ArrayList<>();
        functions = new ArrayList<>();
        instructions = new ArrayList<>();
        last = new ArrayList<>();
        stringLiterals = new ArrayList<>();
        nowFunc = -1;
    }

    public void addGlobalVariable(GlobalVariable input) {
        globalVariables.add(input);
    }

    public void addFunction(Function function) {
        functions.add(function);
        nowFunc++;
    }

    public void addBasicBlock(BasicBlock block) {
        functions.get(nowFunc).addBasicBlock(block);
    }

    public void addInst(Instruction ins) {
        functions.get(nowFunc).addInst(ins);
    }

    public void addStringLiteral(StringLiteral sl) {
        stringLiterals.add(sl);
    }

    public Function getNowFunction() {
        return functions.get(nowFunc);
    }

    public void generateMIPSCode() {
        System.out.println(".data");
        for(GlobalVariable i:globalVariables) {
            i.generateMIPSCode();
        }
        for(StringLiteral i:stringLiterals) {
            i.generateMIPSCode();
        }
    }

    public ArrayList<GlobalVariable> getGlobalVariables() {
        return globalVariables;
    }

    public ArrayList<StringLiteral> getStringLiterals() {
        return stringLiterals;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ArrayList<Value> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(GlobalVariable globalVariable:globalVariables) {
            sb.append(globalVariable);
        }
        for(StringLiteral stringLiteral:stringLiterals) {
            sb.append(stringLiteral);
        }
        for(Function function:functions) {
            sb.append(function);
        }
        return sb.toString();
    }
}
