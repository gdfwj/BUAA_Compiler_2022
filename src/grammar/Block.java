package grammar;
import sym.Item;
import sym.Sym;
import java.util.ArrayList;

public class Block {
    ArrayList<BlockItem> blockItems = new ArrayList<>();

    public Block() {
        if (!((Item) Sym.getInstance().peek().context).id.equals("LBRACE")) {
//            System.out.println("Block error");
        }
        Sym.getInstance().step();
        while (!((Item) Sym.getInstance().peek().context).id.equals("RBRACE")) {
            blockItems.add(new BlockItem());
        }
        Sym.getInstance().step();
    }

    public void output() {
        System.out.println("LBRACE {");
        for (BlockItem i : blockItems) {
            i.output();
        }
        System.out.println("RBRACE }");
        System.out.println("<Block>");
    }

    public void visit() {
        for (BlockItem i : blockItems) {
            i.visit();
        }
    }
}
