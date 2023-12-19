package symbol;

import ir.types.Type;
import sym.Item;
import sym.Sym;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final ArrayList<HashMap<String, Type>> symbolTables;
    private final ArrayList<Integer> uppers;
    private int now;
    private final HashMap<String, ArrayList<Type>> func;
    private final ArrayList<HashMap<String, Item>> constSymbols;
    private final ArrayList<HashMap<String, Integer>> varArraySymbols;
    public boolean isVoid;
    public boolean isLoop;
    public int loopCount;
    public boolean isGlobal;

    private static final SymbolTable instance = new SymbolTable();

    public static SymbolTable getInstance() {
        return instance;
    }

    private SymbolTable() {
        symbolTables = new ArrayList<>();
        constSymbols = new ArrayList<>();
        uppers = new ArrayList<>();
        uppers.add(-1);
        symbolTables.add(new HashMap<>());
        constSymbols.add(new HashMap<>());
        varArraySymbols = new ArrayList<>();
        varArraySymbols.add(new HashMap<>());
        now = 0;
        func = new HashMap<>();
        isVoid = true;
        isLoop = false;
        loopCount = 0;
        isGlobal=true;
    }

    public int addSymbol(String name, Type type) {
        if (symbolTables.get(now).get(name) != null) {
            return -1;
        }
        symbolTables.get(now).put(name, type);
        return 0;
    }

    public int addConstSymbol(String name, ArrayList<Integer> list, int dim1) {
        if (constSymbols.get(now).get(name) != null) {
            return -1;
        }
        constSymbols.get(now).put(name, new Item(list, dim1));
        return 0;
    }

    public int addVarArraySymbol(String name, int dim1) {
        if (varArraySymbols.get(now).get(name) != null) {
            return -1;
        }
        varArraySymbols.get(now).put(name, dim1);
        return 0;
    }

    public void newBlock() {
        symbolTables.add(new HashMap<>());
        constSymbols.add(new HashMap<>());
        varArraySymbols.add(new HashMap<>());
        uppers.add(now);
        now = symbolTables.size() - 1;
    }

    public void leaveBlock() {
        now = uppers.get(now);
    }

    public Type checkSymbol(String name) {
        int checkNow = now;
        while (checkNow != -1) {
            if (symbolTables.get(checkNow).get(name) != null) {
                return symbolTables.get(checkNow).get(name);
            }
            checkNow = uppers.get(checkNow);
        }
        return null;
    }

    public Item checkConstSymbol(String name) {
        int checkNow = now;
        while (checkNow != -1) {
            if (constSymbols.get(checkNow).get(name) != null) {
                return constSymbols.get(checkNow).get(name);
            }
            checkNow = uppers.get(checkNow);
        }
        return null;
    }

    public int checkVarArraySymbolDim1(String name) {
        int checkNow = now;
        while (checkNow != -1) {
            if (varArraySymbols.get(checkNow).get(name) != null) {
                return varArraySymbols.get(checkNow).get(name);
            }
            checkNow = uppers.get(checkNow);
        }
        return -1;
    }

    public void addFunc(String name, ArrayList<Type> paras) {
        func.computeIfAbsent(name, k -> new ArrayList<>(paras));
    }

    public ArrayList<Type> getFuncParas(String name) {
        return func.get(name);
    }

    public void intoLoop() {
        isLoop = true;
        loopCount++;
    }

    public void leaveLoop() {
        loopCount--;
        if (loopCount == 0) {
            isLoop = false;
        }
    }

}
