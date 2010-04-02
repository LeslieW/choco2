package choco.cp.solver.constraints.global.flow;


import choco.kernel.memory.IStateInt;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 1:39:37 PM
 * To change this template use Options | File Templates.
 */
public class FlowNode extends PreFlowNode{
    private FlowResEdge dummyNextEdge;
    private FlowResEdge dummyPrevEdge;
    private IStateInt b; // b = sum(l_(label,i)) - sum(l_(label,j))
    private IStateInt staticCapa; // Normalement c'est un booleen...

    public FlowNode(int label, IStateInt b, IStateInt staticCapa) {
        super(label);
        this.b = b;
        this.staticCapa = staticCapa;
    }

    public String toString() {
        return (this.label + "(d:" + this.distLabel + ",e:" + this.excess + ",b:" + this.b.get() + ")");
    }

    public FlowResEdge getDummyNextEdge() {
        return dummyNextEdge;
    }

    public void setDummyNextEdge(FlowResEdge dummyNextEdge) {
        this.dummyNextEdge = dummyNextEdge;
    }

    public int getStaticCapa() {
        return staticCapa.get();
    }

    public void setStaticCapa(int staticCapa) {
        this.staticCapa.set(staticCapa);
    }

    public int getB() {
        return b.get();
    }

    public void setB(int b) {
        this.b.set(b);
    }

    public void incB(int delta) {
        this.b.set(this.b.get() + delta);
    }

    public FlowResEdge getDummyPrevEdge() {
        return dummyPrevEdge;
    }

    public void setDummyPrevEdge(FlowResEdge dummyPrevEdge) {
        this.dummyPrevEdge = dummyPrevEdge;
    }

    public int maxTheoreticalNextFlow() {
        int val = 0;
        List edgeList = this.getNextEdge();
        for (int i = 0; i < edgeList.size(); i++) {
            FlowResEdge edge = (FlowResEdge) edgeList.get(i);
            val += edge.getU_ij();
        }
        return val;
    }
}
