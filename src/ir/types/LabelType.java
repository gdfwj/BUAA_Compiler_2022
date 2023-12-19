package ir.types;

public class LabelType extends Type {
    private final int handler;
    private static int HANDLER = 0;

    public LabelType() {
        super(false);
        handler = HANDLER++;
    }

    public int getHandler() {
        return handler;
    }
}