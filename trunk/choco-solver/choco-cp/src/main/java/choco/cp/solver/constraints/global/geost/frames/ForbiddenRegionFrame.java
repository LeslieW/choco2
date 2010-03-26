package choco.cp.solver.constraints.global.geost.frames;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 4 févr. 2009
 * Time: 10:41:36
 * To change this template use File | Settings | File Templates.
 */
public final class ForbiddenRegionFrame extends Frame {
    public int q,D,s1,s2,o1,o2;

    public ForbiddenRegionFrame(int q_, int D_, int s1_, int s2_,int o1_, int o2_) {
        super();
        q=q_;D=D_;s1=s1_;s2=s2_;o1=o1_;o2=o2_;
    }

    public String toString() {
        return "q:"+q+";D:"+D+";s1:"+s1+";s2:"+s2+";o1:"+o1+";o2:"+o2;

    }
    
}
