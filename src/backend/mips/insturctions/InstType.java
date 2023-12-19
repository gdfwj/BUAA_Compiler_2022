package backend.mips.insturctions;

public enum InstType {
    // 算数R
    Mov("mov"), Add("add"), Sub("sub"),Mult("mult"),Mul("mul"),Div("div"), Sll("sll"), Srl("srl"), And("and"), Or("or"), Xor("xor"), Nor("nor"), Sltu("sltu"),
    // 算数I
    Addi("addi"), Andi("andi"), Ori("ori"), Xori("xori"), Lui("lui"), Li("li"),
    Addu("addu"), Subu("subu"),Addiu("addiu"), Subiu("subiu"), Slti("slti"),
    // 分支
    Beq("beq"), Bne("bne"), Blez("blez"), Bgtz("bgtz"), Bltz("bltz"), Bgez("bgez"), Sltiu("sltiu"),
    // 跳转
    J("j"), Jal("jal"), Jalr("jalr"), Jr("jr"),
    // 传输(计算)
    Mfhi("mfhi"), Mflo("mflo"), Mthi("mthi"), Mtlo("mtlo"), La("la"),
    // 系统调用
    Syscall("syscall"),
    // 访存
    Sb("sb"), Sh("sh"), Sw("sw"), Lb("lb"), Lh("lh"), Lw("lw")
    ;

    private final String Inst;

    InstType(String Instr) {
        this.Inst = Instr;
    }

    @Override
    public String toString() {
        return Inst;
    }
}
