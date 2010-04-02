package choco.cp.solver.constraints.global.flow;



import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 14 févr. 2005
 * Time: 16:00:40
 */
public class FlowConstraintWithCost extends FlowConstraint {

    protected IntDomainVar costVar;
    protected int direction; // 0:minim & maxim, -1=only minim +1=only maxim

    public FlowConstraintWithCost(Solver s, SCostCapaEdge[][] graph, IntDomainVar flowVar, IntDomainVar costVar) {
        this(s, graph, 0, graph.length - 1, flowVar, costVar);
    }

    public FlowConstraintWithCost(Solver s, SCostCapaEdge[][] graph, IntDomainVar flowVar, IntDomainVar costVar, int direction) {
        this(s, graph, 0, graph.length - 1, flowVar, costVar, direction);
    }

    public FlowConstraintWithCost(Solver s, SCostCapaEdge[][] graph, int soIdx, int siIdx, IntDomainVar flowVar, IntDomainVar costVar) {
        this(s, graph, soIdx, siIdx, flowVar, costVar, 0);
    }

    public FlowConstraintWithCost(Solver s,  SCostCapaEdge[][] graph, int soIdx, int siIdx, IntDomainVar flowVar, IntDomainVar costVar, int direction) {
        super(s, graph, soIdx, siIdx, flowVar,costVar);
        this.costVar = costVar;
        this.direction = direction;
        this.excessFlowNodes.setNodes(flowGraph);
        this.makeRedundantConstraint();
    }

    /**
     * Builds a redondant constraint to improve the filtering algorithm. A linear combinaison constraint
     * is used to evaluating minimal and maximal cost of the flow.
     */
    public void makeRedundantConstraint() {
        int[] coeffs = new int[this.getNbVars() - 1];
        IntDomainVar[] vars = new IntDomainVar[this.getNbVars() - 1];
        for (int i = 0; i < linkVarIdxToEdge.length; i++) {
            FlowResEdge edge = linkVarIdxToEdge[i];
            coeffs[i] = edge.getCost();
            vars[i] = edge.getLu_ij();
        }

        SConstraint cst = solver.eq(solver.scalar(coeffs, vars), costVar);
        solver.post(cst);
    }

    /**
     * Redefines the edge factory to add cost information.
     *
     * @param edge Description of the edge.
     * @param node Outgoing vertex of the edge.
     * @return
     */
    protected FlowResEdge makeEdge(SCapaEdge edge, FlowNode node) {
        FlowResEdge ret = super.makeEdge(edge, node);
        if (ret != null)
            ret.setCost(((SCostCapaEdge) edge).cost);
        return ret;
    }

    /**
     * @return the index of the global flow variable
     */
    public int getFlowVarIdx() {
        return this.getNbVars() - 2;
    }

    /**
     * @return the index of the cost variable of the constraint
     */
    public int getCostVarIdx() {
        return this.getNbVars() - 1;
    }

    /**
     * @return the number of variables (capacities on edges and cost variable)
     */




    /**
     * @param i
     * @return the ith (integer) variable of the constraint
     */
    public IntDomainVar getIntVar(int i) {
        if (i < this.linkVarIdxToEdge.length)
            return this.linkVarIdxToEdge[i].getLu_ij();
        else
            return this.costVar;
    }

    /**
     * Returns the current global flow in the support.
     *
     * @return
     */
    public int getGlobalFlow() {
        FlowResEdge edge = this.linkVarIdxToEdge[this.getFlowVarIdx()];
        return edge.getL_ij() + Math.max(0, edge.getU_ij() - edge.getL_ij() - edge.getR_ij());
    }

    /**
     * First propagation of the constraint
     */
    public void awake() throws ContradictionException
    { // useless...
        super.awake();
        //this.awakeMaxCostMaxFlow();
    }

    /**
     * Constraint-based propagation.
     */
    public void propagate() throws ContradictionException {
        this.awakeFeasiblePreFlow();
        if (this.getGlobalFlow() != this.flowVar.getSup() && direction >= 0) {
            this.awakeMaxPreFlow();
            this.awakeMaxCostMaxFlow();
        } else {
            if (direction <= 0) this.awakeMinCostMinFlow();
            if (direction >= 0) this.awakeMaxCostMaxFlow();
        }
    }

    public void awakeOnInst(int idx) throws ContradictionException {
        if (idx != getNbVars() - 1) super.awakeOnInst(idx);
    }

    public void awakeOnInf(int idx) throws ContradictionException {
        if (idx != getNbVars() - 1) super.awakeOnInf(idx);
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx != getNbVars() - 1) super.awakeOnSup(idx);
    }

    public void awakeStaticPreFlow(boolean firstTime) throws ContradictionException {
        this.initForFlowAlgo(firstTime);
        this.awakeFeasiblePreFlow(firstTime);
        if (direction <= 0) {
            this.awakeMinPreFlow();
            this.awakeMinCostMinFlow();
        }
        if (direction >= 0) {
            this.awakeMaxPreFlow(firstTime);
            this.awakeMaxCostMaxFlow();
        }
    }

    public void awakeMinCostMinFlow() throws ContradictionException {
        int newMin = computeMinCostFlow(this.costVar.getSup() + 1);
        if (newMin > this.costVar.getSup())
            this.fail();
        else {
            this.costVar.updateInf(newMin, this.getConstraintIdx(this.getCostVarIdx()));
        }
    }

    public void awakeMaxCostMaxFlow() throws ContradictionException {
        int newMax = computeMaxCostFlow(this.costVar.getSup() + 1);
        if (newMax < this.costVar.getInf())
            this.fail();
        else {
            this.costVar.updateSup(newMax, this.getConstraintIdx(this.getCostVarIdx()));
        }
    }

    public int computeMinCostFlow(float maxVal) {
        this.excessFlowNodes.initCost();
        this.excessFlowNodes.costScalingMin(maxVal, this.flowGraph.length - 2);
        return this.costFlow();
    }

    public int computeMaxCostFlow(float maxVal) {
        this.excessFlowNodes.initCost();
        this.excessFlowNodes.costScalingMax(maxVal, this.flowGraph.length - 2);
        return this.costFlow();
    }

    public int costFlow() {
        int cost = 0;
        for (int i = 0; i < flowGraph.length; i++) {
            FlowNode node = flowGraph[i];
            for (int j = 0; j < node.getNextEdge().size(); j++) {
                FlowResEdge edge = (FlowResEdge) node.getNextEdge().get(j);
                cost += edge.getCost() * edge.getFlow();
            }
        }
        return cost;
    }
}
