package backend.mips;

public class IdPool {
    private int id=0;

    public int allocId() {
        return ++id;
    }
}
