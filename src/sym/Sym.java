package sym;

import java.io.*;
import java.util.ArrayList;

import ir.types.*;
import symbol.SymbolTable;

public class Sym {
    private final ArrayList<Item> reserved = new ArrayList<>();
    private final static Sym instance = new Sym();
    private ArrayList<Item> symList = new ArrayList<>();
    private int now = 0;
    private Item temp;

    public static Sym getInstance() {
        return instance;
    }

    private Sym() {
        reserved.add(new Item("main", "MAINTK"));
        reserved.add(new Item("const", "CONSTTK"));
        reserved.add(new Item("int", "INTTK"));
        reserved.add(new Item("break", "BREAKTK"));
        reserved.add(new Item("continue", "CONTINUETK"));
        reserved.add(new Item("if", "IFTK"));
        reserved.add(new Item("else", "ELSETK"));
        reserved.add(new Item("while", "WHILETK"));
        reserved.add(new Item("getint", "GETINTTK"));
        reserved.add(new Item("printf", "PRINTFTK"));
        reserved.add(new Item("return", "RETURNTK"));
        reserved.add(new Item("void", "VOIDTK"));
    }

    public ArrayList<Item> parse() {
        ArrayList<Item> output = new ArrayList<>();
        File source = new File("./testfile.txt");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(source));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        StringBuilder allLine = new StringBuilder();
        while (true) {
            try {
                if (null == (line = bufferedReader.readLine())) {
                    break;
                } else {
                    allLine.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String nowLine = allLine.toString();
        int lineNum = 1;
        int len = nowLine.length();
        for (int i = 0; i < len; i++) {
            StringBuilder sb = new StringBuilder();
            char now = nowLine.charAt(i);
            if (now == '\n') {
                lineNum++;
            } else if (Character.isLetter(now) || now == '_') {
                while (Character.isLetter(now) || Character.isDigit(now) || now == '_') {
                    sb.append(now);
                    i++;
                    now = nowLine.charAt(i);
                }
                i--;
                String res = reserver(sb.toString());
                output.add(new Item(new Item(sb.toString(), res), lineNum));
            } else if (Character.isDigit(now)) {
                while (Character.isDigit(now)) {
                    sb.append(now);
                    i++;
                    now = nowLine.charAt(i);
                }
                i--;
                output.add(new Item(new Item(sb.toString(), "INTCON"), lineNum));
            } else if (now == '"') {
                do {
                    sb.append(now);
                    i++;
                    now = nowLine.charAt(i);
                } while (now != '"');
                sb.append('"');
                output.add(new Item(new Item(sb.toString(), "STRCON"), lineNum));
            } else if (now == '!') {
                i++;
                now = nowLine.charAt(i);
                if (now == '=') {
                    output.add(new Item(new Item("!=", "NEQ"), lineNum));
                } else {
                    i--;
                    output.add(new Item(new Item("!", "NOT"), lineNum));
                }
            } else if (now == '&') {
                i++;
                if (nowLine.charAt(i) == '&') {
                    output.add(new Item(new Item("&&", "AND"), lineNum));
                }
            } else if (now == '|') {
                i++;
                if (nowLine.charAt(i) == '|') {
                    output.add(new Item(new Item("||", "OR"), lineNum));
                }
            } else if (now == '+') {
                output.add(new Item(new Item("+", "PLUS"), lineNum));
            } else if (now == '-') {
                output.add(new Item(new Item("-", "MINU"), lineNum));
            } else if (now == '*') {
                output.add(new Item(new Item("*", "MULT"), lineNum));
            } else if (now == '/') {
                i++;
                now = nowLine.charAt(i);
                if (now == '*') {
                    i++;
                    while (true) {
                        if (nowLine.charAt(i) == '\n') {
                            lineNum++;
                        }
                        if (nowLine.charAt(i) == '*' && nowLine.charAt(i + 1) == '/') {
                            break;
                        }
                        i++;
                    }
                    i++;
                } else if (now == '/') {
                    i++;
                    while (nowLine.charAt(i) != '\n') {
                        i++;
                    }
                    lineNum++;
                } else {
                    i--;
                    output.add(new Item(new Item("/", "DIV"), lineNum));
                }
            } else if (now == '%') {
                output.add(new Item(new Item("%", "MOD"), lineNum));
            } else if (now == '<') {
                i++;
                now = nowLine.charAt(i);
                if (now == '=') {
                    output.add(new Item(new Item("<=", "LEQ"), lineNum));
                } else {
                    i--;
                    output.add(new Item(new Item("<", "LSS"), lineNum));
                }
            } else if (now == '>') {
                i++;
                now = nowLine.charAt(i);
                if (now == '=') {
                    output.add(new Item(new Item(">=", "GEQ"), lineNum));
                } else {
                    i--;
                    output.add(new Item(new Item(">", "GRE"), lineNum));
                }
            } else if (now == '=') {
                i++;
                now = nowLine.charAt(i);
                if (now == '=') {
                    output.add(new Item(new Item("==", "EQL"), lineNum));
                } else {
                    i--;
                    output.add(new Item(new Item("=", "ASSIGN"), lineNum));
                }
            } else if (now == ';') {
                output.add(new Item(new Item(";", "SEMICN"), lineNum));
            } else if (now == ',') {
                output.add(new Item(new Item(",", "COMMA"), lineNum));
            } else if (now == '(') {
                output.add(new Item(new Item("(", "LPARENT"), lineNum));
            } else if (now == ')') {
                output.add(new Item(new Item(")", "RPARENT"), lineNum));
            } else if (now == '[') {
                output.add(new Item(new Item("[", "LBRACK"), lineNum));
            } else if (now == ']') {
                output.add(new Item(new Item("]", "RBRACK"), lineNum));
            } else if (now == '{') {
                output.add(new Item(new Item("{", "LBRACE"), lineNum));
            } else if (now == '}') {
                output.add(new Item(new Item("}", "RBRACE"), lineNum));
            } else if (now != '\t' && now != ' ') {
//                System.out.println("error");
            }
        }
        symList = output;
        return output;
    }

    private String reserver(String sb) {
        for (Item i : reserved) {
            if (i.context.equals(sb)) {
                return (String) i.id;
            }
        }
        return "IDENFR";
    }

    public void step() {
        if (now == symList.size()) {
            temp = null;//new Item(new Item(" ", " "), " ");
            now++;
        } else {
            temp = symList.get(now++);
        }
    }

    public Item peek() {
        return temp;
    }

    public Item peekNext() {
        if (now == symList.size())
            return null;
        return symList.get(now);
    }

    public Item peekNext2() {
        if (now >= symList.size() - 1)
            return null;
        return symList.get(now + 1);
    }

    public Item peekLast() {
        return symList.get(now - 2);
    }

    public boolean peekAssign() {
        if (((Item) temp.context).id.equals("SEMICN")) {
            return false;
        }
        for (int i = now; i < symList.size(); i++) {
            if (((Item) symList.get(i).context).id.equals("SEMICN")) {
                return false;
            }
            if (((Item) symList.get(i).context).id.equals("ASSIGN")) {
                return true;
            }
        }
        return true;
    }

    public Type getExpType() {
        for (int i = now - 1; ; i++) {
            if (((Item) symList.get(i).context).id.equals("INTCON")) {
                return new IntegerType();
            } else if (((Item) symList.get(i).context).id.equals("IDENFR")) {
                Type type = SymbolTable.getInstance().checkSymbol((String) ((Item) symList.get(i).context).context);
                if (type == null) {
                    return null;
                } else if (type instanceof FunctionType && ((FunctionType) type).getRetType() instanceof IntegerType) {
                    return new IntegerType();
                } else if (type instanceof FunctionType && ((FunctionType) type).getRetType() instanceof VoidType) {
                    return new VoidType();
                } else if (type instanceof IntegerType) {
                    return new IntegerType();
                } else if (type instanceof ArrayType && ((ArrayType) type).getInside() instanceof IntegerType) {
                    if (((Item) symList.get(i + 1).context).context.equals("[")) {
                        return new IntegerType();
                    } else {
                        return new ArrayType(new IntegerType(), ((ArrayType) type).getDim());
                    }
                } else if (type instanceof ArrayType && ((ArrayType) type).getInside() instanceof ArrayType){
                    if (((Item) symList.get(i + 1).context).context.equals(",")
                            || ((Item) symList.get(i + 1).context).context.equals(")")) {
                        return new ArrayType(new ArrayType(new IntegerType(),
                                ((ArrayType) ((ArrayType) type).getInside()).getDim()), ((ArrayType) type).getDim());
                    } else {
                        int count = 0;
                        int numOfLB = 0;
                        while (!(((Item) symList.get(i).context).context.equals(",")
                                || ((Item) symList.get(i).context).context.equals(")"))) {
                            if (((Item) symList.get(i).context).context.equals("[")) {
                                count++;
                                if (count == 1) {
                                    numOfLB++;
                                }
                            } else if (((Item) symList.get(i).context).context.equals("]")) {
                                count--;
                            }
                            i++;
                        }
                        if (numOfLB == 0) {
                            return new ArrayType(new ArrayType(new IntegerType(),
                                    ((ArrayType) ((ArrayType) type).getInside()).getDim()), ((ArrayType) type).getDim());
                        } else if (numOfLB == 1) {
                            return new ArrayType(new IntegerType(), ((ArrayType) ((ArrayType) type).getInside()).getDim());
                        } else {
                            return new IntegerType();
                        }
                    }
                }
            }
        }
    }

    public void checkReturn() {
        int count = 0;
        for (int i = now - 2; ; i--) {
            if (count == 2 || ((Item) symList.get(i).context).context.equals("{")) {
                break;
            }
            if (((Item) symList.get(i).context).context.equals(";")) {
                count++;
            }
            if (((Item) symList.get(i).context).id.equals("RETURNTK") &&
                    !((Item) symList.get(i + 1).context).context.equals(";")) {
                return;
            }
        }
        System.out.println(symList.get(now - 2).id + " g");
    }
}
