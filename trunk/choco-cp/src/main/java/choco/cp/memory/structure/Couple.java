package choco.cp.memory.structure;

public class Couple<C> {
    public C c;
    public int i;

    public void init(C c, int i) {
        this.c = c;
        this.i = i;
    }
}