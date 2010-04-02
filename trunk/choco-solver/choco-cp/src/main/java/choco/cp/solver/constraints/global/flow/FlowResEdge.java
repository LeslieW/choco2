package choco.cp.solver.constraints.global.flow;

import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.variables.integer.IntDomainVar;

// import choco.integer.IntVar;

// diam 27/01/2008 remplace Environment par IEnvironment sinon la version
// cvs de choco plante !
// import choco.mem.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 1:37:51 PM
 * To change this template use Options | File Templates.
 */
public class FlowResEdge extends PreFlowResEdge{
    private IStateInt l_ij;
    private IStateInt u_ij;
    private int varIdx;
    private IntDomainVar lu_ij;

    public int getL_ij() {
        return l_ij.get();
    }

    public void setL_ij(int l_ij) {
        this.l_ij.set(l_ij);
    }

    public int getU_ij() {
        return u_ij.get();
    }

    public void setU_ij(int u_ij) {
        this.u_ij.set(u_ij);
    }

    public IntDomainVar getLu_ij() {
        return lu_ij;
    }

    public int getVarIdx() {
        return varIdx;
    }

    public void setVarIdx(int varIdx) {
        this.varIdx = varIdx;
    }

    public static FlowResEdge makeFlowDummyResEdge(IEnvironment env, PreFlowNode i, PreFlowNode j, int capa) {
        FlowResEdge edge = new FlowResEdge(env, i, j, capa);
        return edge;
    }

    public FlowResEdge(IEnvironment env, PreFlowNode i, PreFlowNode j, int capa){
        super(i, j, env.makeInt(0), env.makeInt(0));
        this.lu_ij = null;
        this.l_ij = env.makeInt(0);
        this.u_ij = env.makeInt(0);
      this.r_ij.set(capa);
      this.u_ij.set(capa);
    }

    public FlowResEdge(IEnvironment env, PreFlowNode i, PreFlowNode j, IntDomainVar capa) {
        super(i,j, env.makeInt(capa.getSup() - capa.getInf()), env.makeInt(0));
        this.lu_ij = capa;
        this.l_ij = env.makeInt(capa.getInf());
        this.u_ij = env.makeInt(capa.getSup());
    }

    public String toString() {
        return (this.i.getLabel() + "->" + this.j.getLabel() + "(r_ij:" + this.r_ij.get() + ",r_ji:" + this.r_ji.get() +
                (this.lu_ij!= null ? ",lu_ij:" + this.lu_ij + "[" + this.lu_ij.getInf() + "->" + this.lu_ij.getSup() + "])":
                "," + this.l_ij.get() + "~>" + this.u_ij.get() + ")"));
    }

    public void updateEdgeInst(IntDomainVar v) {
        this.r_ij.set(this.r_ij.get() + v.getSup() - this.u_ij.get());
        this.r_ji.set(this.r_ji.get() + this.l_ij.get() - v.getInf());
        this.u_ij.set(v.getInf());
        this.l_ij.set(this.u_ij.get());
    }

    public void updateEdgeInf(IntDomainVar v) {
        this.r_ji.set(this.r_ji.get() + this.l_ij.get() - v.getInf());
        this.l_ij.set(v.getInf());
    }

    public void updateEdgeSup(IntDomainVar v) {
        this.r_ij.set(this.r_ij.get() + v.getSup() - this.u_ij.get());
        this.u_ij.set(v.getSup());
    }

    public void initFlowResEdge() {
        if (this.lu_ij != null) this.initFlowResEdge(this.lu_ij);
        else this.initFlowResEdge(this.u_ij.get());
    }

    public void initFlowResEdge(IntDomainVar v) {
        this.u_ij.set(v.getSup()); this.l_ij.set(v.getInf());
        this.r_ij.set(this.u_ij.get() - this.l_ij.get());
        this.r_ji.set(0);
    }

    public void initFlowResEdge(int v) {
        this.u_ij.set(v); this.l_ij.set(0);
        this.r_ij.set(this.u_ij.get() - this.l_ij.get());
        this.r_ji.set(0);
    }

  public int getFlow() {
    if (this.getLu_ij() != null) {
      return this.getL_ij() + Math.max(0, this.getU_ij() - this.getL_ij() - this.getR_ij());
    } else {
      return this.getU_ij() - this.getR_ij();
    }
  }
}
