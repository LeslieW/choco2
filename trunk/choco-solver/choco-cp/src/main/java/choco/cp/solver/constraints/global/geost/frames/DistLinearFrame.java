package choco.cp.solver.constraints.global.geost.frames;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 4 févr. 2009
 * Time: 10:41:36
 * To change this template use File | Settings | File Templates.
 */
public final class DistLinearFrame extends Frame {
    public int[] a;
    public int o1,b;


    public DistLinearFrame(int[] a, int o1, int b) {
        super();
        this.a=a;this.o1 = o1;this.b=b;
    }

    public String toString() {
        return "a:"+ Arrays.toString(a) +";o1:"+o1+";b:"+b;
    }

}