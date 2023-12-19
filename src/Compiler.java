import backend.GenerateMIPSModel;
import grammar.CompUnit;
import ir.MyModule;
import ir.passes.PassModule;
import sym.Item;
import sym.Sym;
import symbol.SymbolTable;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        try {
            PrintStream ps = new PrintStream("llvm_ir.txt");
            System.setOut(ps);
            ArrayList<Item> res = Sym.getInstance().parse();
            Sym.getInstance().step();
            CompUnit compUnit = new CompUnit();
//            compUnit.output();
            compUnit.visit();
//            System.out.println("declare i32 @getint()\ndeclare void @putint(i32)\ndeclare void @putch(i32)\ndeclare void @putstr(i8*)\n");
            PassModule.getInstance().run1();
            System.out.println(MyModule.getInstance());
//            MyModule.getInstance().generateMIPSCode();
            PrintStream ps2 = new PrintStream("mips.txt");
            System.setOut(ps2);
            GenerateMIPSModel.getInstance().generate();
            PassModule.getInstance().run2();
            System.out.println(GenerateMIPSModel.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
