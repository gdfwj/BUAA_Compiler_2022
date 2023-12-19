package ir.instructions;

public enum Tag {
    Add("add"), Sub("sub"), Mul("mul"), Div("div"), Mod("mod"),
    Not("not"), Neq("ne"), And("and"), Or("or"), Leq("sle"), Lss("slt"), Geq("sge"), Gre("sgt"), Eql("eq"), // calculate
    Ret("return"), Call("call"), Br("br"), // control
    Alloca("alloca"), Load("load"), Store("store"), GEP("getelementptr"), // memory
    GetInt("getint"), PutInt("putint"),PutChar("putchar"), PutString("putstring"); // lib

    final String tagName;

    Tag(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return tagName;
    }
}
