package choco.cp.solver.constraints.global.flow;

// import choco.integer.IntVar;  // diam non : je conserve encore 

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// // import ice.Ice;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 1:30:20 PM
 * To change this template use Options | File Templates.
 */
public class FlowConstraint extends AbstractIntSConstraint
{

// Quand cette contraint sera intégrée à choco, on pourra utiliser ceci,
// comme pour la contrainte AbstractBipartiteGraph.java
// (pour le test du moteur de propagation de choco)
protected static Logger logger = Logger.getLogger("choco.prop.const");

protected int[] indices;
protected IntDomainVar flowVar; // diam
protected FlowResEdge[] linkVarIdxToEdge;
protected FlowNode[] flowGraph;
protected PreFlowNodesSet excessFlowNodes;
protected int sourceIdx;
protected int sinkIdx;

protected static final int FLOWDUMMYSOURCE = 0;
protected static final int FLOWREALNODE = 1;
protected final int FLOWDUMMYSINK;
protected final int FLOWREALSINK;
protected Solver solver;

public FlowConstraint(Solver s,
                      SCapaEdge[][] graph,
                      IntDomainVar flowVar) {
        this(s, graph, 0, graph.length - 1, flowVar,null);
}

public FlowConstraint(Solver s,
                      SCapaEdge[][] graph,
                      int soIdx,
                      int siIdx,
                      IntDomainVar flowVar) {
        this(s,graph,soIdx,siIdx,flowVar,null);
}


public FlowConstraint(Solver s,
                      SCapaEdge[][] graph,
                      int soIdx,
                      int siIdx,
                      IntDomainVar flowVar,IntDomainVar cVar) {
        super(ConstraintEvent.LOW,makeVarArray(graph,flowVar,cVar));
        // initLoggers();

        this.flowVar = flowVar;

        this.solver =s;
        this.flowGraph = new FlowNode[graph.length + 3];
        for (int i = 0; i < flowGraph.length; i++) {
                flowGraph[i] = new FlowNode(i, s.getEnvironment().makeInt(0), s.getEnvironment().makeInt(0));
        }

        FLOWDUMMYSINK = flowGraph.length - 1;
        FLOWREALSINK = flowGraph.length - 2;

        this.excessFlowNodes = new PreFlowNodesSet();

        this.sourceIdx = soIdx + 1;
        this.sinkIdx = siIdx + 1;

        List<FlowResEdge> edgeWithVars = new ArrayList<FlowResEdge>();
        int currentVarIdx = 0;

        for (int i = 0; i < graph.length; i++) {
                FlowNode node = this.flowGraph[i + 1];
                SCapaEdge[] edgeList = graph[i];

                if ((i == siIdx) && (edgeList.length != 0)) {
                        if (logger.isLoggable(Level.INFO))
                                // // Logger.getLogger("Ice")
                                // //       .warning("Node " + i + " is the sink, outgoing edges are ignored.");
                                logger.warning("Node " + i + " is the sink, outgoing edges are ignored.");
                        edgeList = null;
                }

                // Creation des arcs
                assert edgeList != null;
                for (int j = 0; j < edgeList.length; j++) {
                        if (edgeList[j].dest == soIdx) {
                                if (logger.isLoggable(Level.INFO))
                                        logger.warning("Edges to the source " + i + " are ignored.");
                        } else {
                                FlowResEdge edge = makeEdge(edgeList[j], node);
                                if (edge != null) node.addNextEdge(edge);
                                else if (logger.isLoggable(Level.INFO))
                                        logger.warning("The capacity " + edgeList[j].capa +
                                                " is neither an integer nor an IntDomainVar !");
                        }
                }

                // Initialisation des arcs
                for (int j = 0; j < node.getNextEdge().size(); j++) {
                        FlowResEdge edge = (FlowResEdge) node.getNextEdge().get(j);
                        edge.getJ().addPrevEdge(edge);
                        if (edge.getLu_ij() != null) {
                                edgeWithVars.add(edge);
                                edge.setVarIdx(currentVarIdx++);
                                ((FlowNode) edge.getI()).incB(-edge.getLu_ij().getInf());
                                ((FlowNode) edge.getJ()).incB(edge.getLu_ij().getInf());
                        }
                }
        }

        // Ajout du nouveau puits pour associer un arc a la variable de flot.
        FlowNode realSink = this.flowGraph[FLOWREALSINK];
        FlowNode initSink = this.flowGraph[this.sinkIdx];
        FlowResEdge edge = new FlowResEdge(s.getEnvironment(), initSink, realSink, flowVar);
        initSink.addNextEdge(edge);
        realSink.addPrevEdge(edge);
        edgeWithVars.add(edge);
        edge.setVarIdx(currentVarIdx++);
        ((FlowNode) edge.getI()).incB(-edge.getLu_ij().getInf());
        ((FlowNode) edge.getJ()).incB(edge.getLu_ij().getInf());

        // Fin de l'initialisation de la contrainte
        this.linkVarIdxToEdge = new FlowResEdge[edgeWithVars.size()];
        this.linkVarIdxToEdge = (FlowResEdge[]) edgeWithVars.toArray(this.linkVarIdxToEdge);
        this.indices = new int[this.getNbVars()];
}

private static IntDomainVar[] makeVarArray(SCapaEdge[][] graph,IntDomainVar... vars)
{
        ArrayList<IntDomainVar> vs = new ArrayList<IntDomainVar>();
        for (int i = 0 ; i < graph.length ; i++)
        {
                for (int j = 0 ;j < graph[i].length ; j++)
                {
                        SCapaEdge ce =  graph[i][j];
                        if (ce != null && ce.capa != null)
                        {
                                vs.add(ce.capa);
                        }
                }
        }
        for (IntDomainVar v : vars)
                if (v!=null) vs.add(v);
        return vs.toArray(new IntDomainVar[vs.size()]);
}

protected FlowResEdge makeEdge(SCapaEdge edge, FlowNode node) {
        if (edge.capa.isInstantiated())
                return new FlowResEdge(solver.getEnvironment(),
                        node, this.flowGraph[edge.dest + 1],
                        edge.capa.getVal());
        else if (edge.capa instanceof IntDomainVar)
                return new FlowResEdge(solver.getEnvironment(),
                        node, this.flowGraph[edge.dest + 1],
                        edge.capa);
        else return null;
}

// Dans Ice.java :
//    private static Logger logger = Logger.getLogger("choco.ice");
//    ...
//    public static boolean isLoggable(Level level) {
//        return logger.isLoggable(level);
//    }
// Equivalent à
//    public static boolean isLoggable(Level level) {
//        return Logger.getLogger("choco.ice").isLoggable(level);
//    }
//
// private static void initLoggers() {
//   if (this.logger.isLoggable(Level.INFO)) this.logger.setLevel(Level.SEVERE);
// }

public String toString() {
        return ("N:" + arrayString(this.flowGraph) + " C:" + this.flowVar);
}

public int getFlow(int i) {
        FlowResEdge edge = this.linkVarIdxToEdge[i];
        return edge.getFlow();
        /*if (edge.getLu_ij() != null) {
         return edge.getL_ij() + Math.max(0, edge.getU_ij() - edge.getL_ij() - edge.getR_ij());
       } else {
         return edge.getU_ij() - edge.getR_ij();
       } */
}

public int getGlobalFlow() {
        FlowResEdge edge = this.linkVarIdxToEdge[this.getNbVars() - 1];
        return edge.getL_ij() + Math.max(0, edge.getU_ij() - edge.getL_ij() - edge.getR_ij());
}



// ============== API JChoco ================

// -- Reseau de contraintes --

/*public int assignIndices(AbstractCompositeConstraint root, int i) {
 int j = i;
 for (int k = 0; k < getNbVars(); k++) {
   j++;
   connectVar((AbstractVar) this.linkVarIdxToEdge[k].getLu_ij(), j);
   setConstraintIndex(k, this.linkVarIdxToEdge[k].getLu_ij().getNbConstraints());
 }
 return j;
} */
/* public int assignIndices(AbstractCompositeConstraint root, int i, boolean dynamicAddition) {
int j=i;
for (int k=0; k<getNbVars(); k++) {
j++;
int cidx = root.connectVar((AbstractVar) this.linkVarIdxToEdge[k].getLu_ij(), j, dynamicAddition);
setConstraintIndex(k, cidx);
}
return j;
}    */

/*public void setConstraintIndex(int i, int idx) {
this.indices[i] = idx;
}

public int getConstraintIdx(int i) {
return this.indices[i];
}

public int getNbVars() {
return this.linkVarIdxToEdge.length;
}

public Var getVar(int i) {
return this.getIntVar(i);
}

public void setVar(int i, Var v) {
//To change body of implemented methods use File | Settings | File Templates.
}             */

public IntDomainVar getIntVar(int i) {
        return this.linkVarIdxToEdge[i].getLu_ij();
}

/*public boolean isActive() {
 for (int i = 0; i < this.getNbVars(); i++) {
   try {
     if (this.getIntVar(i).isActive(this.getConstraintIdx(i)))
       return true;
   } catch (Exception e) {
     if (this.logger.isLoggable(Level.INFO)) this.logger.warning("FlowConstraint.isActive: Out of bound exception !!");
   }
 }
 return false;
} */


// -- Propagation --

public void awake() throws ContradictionException
{
        if (this.logger.isLoggable(Level.INFO)) this.logger.info("First Propagation on flow constraint " + this);
        for (int i = 0; i < linkVarIdxToEdge.length; i++) {
                FlowResEdge edge = linkVarIdxToEdge[i];
                if (edge.getLu_ij().getInf() < 0) {
                        edge.getLu_ij().updateInf(0, this.indices[edge.getVarIdx()]);
                }
        }
        this.awakeInitialFlowConservation();
        this.awakeInitialPreFlow();
}

public void propagate() throws ContradictionException {
        if (this.logger.isLoggable(Level.INFO)) this.logger.info("Try to propagate on flow constraint " + this);
        this.awakeFeasiblePreFlow();
        if (this.getGlobalFlow() != this.flowVar.getSup()) {
                // Si on n'est pas sur que le flot max atteint est toujours valide, on verifie !
                this.awakeMaxPreFlow();
        }
}

public void awakeOnInst(int idx) throws ContradictionException {
        this.awakePreFlowOnInst(this.linkVarIdxToEdge[idx].getLu_ij(), idx);
        this.awakeNodeFlowConservationOnInst(this.linkVarIdxToEdge[idx]);
}

public void awakeOnInf(int idx) throws ContradictionException {
        this.awakePreFlowOnInf(this.linkVarIdxToEdge[idx].getLu_ij(), idx);
        this.awakeNodeFlowConservationOnInf(this.linkVarIdxToEdge[idx]);
}

public void awakeOnSup(int idx) throws ContradictionException {
        this.awakePreFlowOnSup(this.linkVarIdxToEdge[idx].getLu_ij(), idx);
        this.awakeNodeFlowConservationOnSup(this.linkVarIdxToEdge[idx]);
}

public void updatePreFlowInf(IntDomainVar v, int value, int constIdx, FlowResEdge e) throws ContradictionException {
        v.updateInf(value,this,false);
        e.updateEdgeInf(v);
        this.awakeNodeFlowConservationOnInf(e);
}

public void updatePreFlowSup(IntDomainVar v, int value, int constIdx, FlowResEdge e) throws ContradictionException {
        v.updateSup(value, this,false);
        e.updateEdgeSup(v);
        this.awakeNodeFlowConservationOnSup(e);
}

public void updateFlowVarInf(IntDomainVar v, int value, int varIdx, boolean isPreFlowActive) throws ContradictionException {
        v.updateInf(value, this,false);
        if (isPreFlowActive) {
                this.awakePreFlowOnInf(v, varIdx);
        }
}

public void updateFlowVarSup(IntDomainVar v, int value, int varIdx, boolean isPreFlowActive) throws ContradictionException {
        v.updateSup(value, this,false);
        if (isPreFlowActive) {
                this.awakePreFlowOnSup(v, varIdx);
        }
}

public boolean isSatisfied() {
        return false;
}


// ================= Propagation grace au preflow ! ==================

public void awakePreFlowOnInst(IntDomainVar v, int idx) {
        if (this.getFlow(idx) != v.getInf()) { // Flow different a la valeur de la variable
                if (this.getFlow(idx) > v.getInf()) { // Flow superieur a la valeur
                        if (!this.incrementalUpdateEdgeOnSup(idx)) {
                                this.constAwake(false);
                        }
                        this.linkVarIdxToEdge[idx].updateEdgeInf(v);
                } else { // Flow inferieur a la valeur
                        if (!this.incrementalUpdateEdgeOnInf(idx)) {
                                this.constAwake(false);
                        }
                        this.linkVarIdxToEdge[idx].updateEdgeSup(v);
                }
        } else { // Flow egal a la valeur de la variable
                this.linkVarIdxToEdge[idx].updateEdgeInst(v);
        }
}

public void awakePreFlowOnInf(IntDomainVar v, int idx) {
        if (this.getFlow(idx) < v.getInf()) { // Le flot atteint de verifie pas la capa minimale... => il faut verifier la faisabilite
                if (!this.incrementalUpdateEdgeOnInf(idx)) {
                        this.constAwake(false);
                }
        } else {
                this.linkVarIdxToEdge[idx].updateEdgeInf(v);
        }
}

// En fait jamais incremental...
public boolean incrementalUpdateEdgeOnInf(int varIdx) {
        FlowResEdge edge = this.linkVarIdxToEdge[varIdx];
        int gapToAdd = edge.getLu_ij().getInf() - this.getFlow(varIdx);
        ((FlowNode) edge.getI()).incB(-gapToAdd);
        ((FlowNode) edge.getJ()).incB(gapToAdd);
        edge.updateResEdge(gapToAdd);
        edge.updateEdgeInf(edge.getLu_ij());
        return false;
}

public void awakePreFlowOnSup(IntDomainVar v, int idx) {
        if (this.getFlow(idx) > v.getSup()) { // Le flot atteint de verifie pas la capa maximale... => il faut verifier la faisabilite
                if (!this.incrementalUpdateEdgeOnSup(idx)) {
                        this.constAwake(false);
                }
        } else {
                this.linkVarIdxToEdge[idx].updateEdgeSup(v);
        }
}

public boolean incrementalUpdateEdgeOnSup(int varIdx) {
        FlowResEdge edge = this.linkVarIdxToEdge[varIdx];
        int gapToAdd = edge.getLu_ij().getSup() - this.getFlow(varIdx);
        ((FlowNode) edge.getI()).incB(-gapToAdd);
        ((FlowNode) edge.getJ()).incB(gapToAdd);
        edge.updateResEdge(gapToAdd);
        edge.updateEdgeSup(edge.getLu_ij());
        return false;
}

public void awakeInitialPreFlow() throws ContradictionException {
        FlowNode sourceN = this.flowGraph[this.sourceIdx];
        FlowNode sinkN = this.flowGraph[FLOWREALSINK];
        FlowResEdge edge = FlowResEdge.makeFlowDummyResEdge(solver.getEnvironment(), sinkN, sourceN, 0); // 0 = infinity

        sinkN.addNextEdge(edge);
        sourceN.addPrevEdge(edge);

        this.awakeStaticPreFlow(true);
}

public void awakeStaticPreFlow() throws ContradictionException {
        this.awakeStaticPreFlow(false);
}

public void awakeStaticPreFlow(boolean firstTime) throws ContradictionException {
        this.initForFlowAlgo(firstTime);

        this.awakeFeasiblePreFlow(firstTime);
        this.awakeMinPreFlow();
        this.awakeMaxPreFlow(firstTime);
}

public void initForFlowAlgo(boolean first) {
        for (int i = FLOWREALNODE; i <= FLOWREALSINK; i++) {
                FlowNode node = this.flowGraph[i];
                node.setB(0);
                node.setExcess(0);

                List edgeList = node.getNextEdge();
                for (int j = 0; j < edgeList.size(); j++) {
                        FlowResEdge edge = (FlowResEdge) edgeList.get(j);
                        edge.initFlowResEdge();
                }
        }

        for (int i = FLOWREALNODE; i <= FLOWREALSINK; i++) {
                FlowNode node = this.flowGraph[i];
                List edgeList = node.getNextEdge();
                for (int j = 0; j < edgeList.size(); j++) {
                        FlowResEdge edge = (FlowResEdge) edgeList.get(j);
                        if (edge.getLu_ij() != null) {
                                ((FlowNode) edge.getI()).incB(-edge.getLu_ij().getInf());
                                ((FlowNode) edge.getJ()).incB(edge.getLu_ij().getInf());
                        }
                }
        }
}

public void awakeMaxPreFlow() throws ContradictionException {
        this.awakeMaxPreFlow(false);
}

public void awakeMaxPreFlow(boolean firstTime) throws ContradictionException {
        if (this.logger.isLoggable(Level.INFO)) this.logger.info("Awake Max Preflow");
        FlowNode sourceN = this.flowGraph[this.sourceIdx];
        FlowNode sinkN = this.flowGraph[FLOWREALSINK];
        int realMaxFlow = 0;

        /*for (int i = FLOWREALNODE; i <= FLOWREALSINK; i ++) {
       System.out.println("Node : " + this.flowGraph[i]);
       System.out.println("  Edges : " + this.flowGraph[i].getNextEdge());
   }     */


        if (sourceN.getExcess() != 0) {
                this.fail();
        }
        try {
                this.excessFlowNodes.initialMaxFlow(sourceN, sinkN);
        } catch (NullFlowException e) {
                this.fail();
        }

        if (sourceN.getExcess() > 0) {
                this.excessFlowNodes.removeFlowNode(sourceN);
        }
        if (sinkN.getExcess() > 0) {
                this.excessFlowNodes.removeFlowNode(sinkN);
        }

        sourceN.setExcess(0);
        sinkN.setExcess(0);

        realMaxFlow = this.getGlobalFlow();
        if (this.logger.isLoggable(Level.INFO)) this.logger.info("Max flow for " + this + " is " + realMaxFlow);

        this.reInitDistLabel();

        if (!this.flowVar.canBeInstantiatedTo(realMaxFlow)) {
                this.fail();
        } else {
                if (realMaxFlow < this.flowVar.getSup()) {
                        this.updatePreFlowSup(this.flowVar, realMaxFlow, this.indices[this.getNbVars() - 1], this.linkVarIdxToEdge[this.getNbVars() - 1]);

                        if (!firstTime) {
                                for (int varIdx = 0; varIdx < this.linkVarIdxToEdge.length; varIdx++) {
                                        FlowResEdge edge = this.linkVarIdxToEdge[varIdx];
                                        if (edge.getU_ij() > realMaxFlow) {
                                                this.updatePreFlowSup(edge.getLu_ij(), realMaxFlow, this.indices[varIdx], edge);
                                        }
                                }
                        }
                } else {
                        if (firstTime) {
                                for (int varIdx = 0; varIdx < this.linkVarIdxToEdge.length; varIdx++) {
                                        FlowResEdge edge = this.linkVarIdxToEdge[varIdx];
                                        if (edge.getU_ij() > realMaxFlow) {
                                                this.updatePreFlowSup(edge.getLu_ij(), realMaxFlow, this.indices[varIdx], edge);
                                        }
                                }
                        }
                }
        }
}

public void awakeMinPreFlow() throws ContradictionException {
        FlowNode sourceN = this.flowGraph[this.sourceIdx];
        FlowNode sinkN = this.flowGraph[FLOWREALSINK];
        int minFlow = this.getGlobalFlow();

        if (minFlow > 0) {
                this.excessFlowNodes.initialMinFlow(sourceN, sinkN);
                this.excessFlowNodes.removeFlowNode(sinkN);

                minFlow = this.getGlobalFlow();
                if (this.logger.isLoggable(Level.INFO)) this.logger.info("Min flow is " + minFlow);

                sinkN.setExcess(0);
                sourceN.setExcess(0);
                this.reInitDistLabel();

                if (minFlow > this.flowVar.getInf()) {
                        this.updatePreFlowInf(this.flowVar, minFlow, this.indices[this.getNbVars() - 1], this.linkVarIdxToEdge[this.getNbVars() - 1]);
                }
        } else {
                if (minFlow > this.flowVar.getInf()) {
                        this.updatePreFlowInf(this.flowVar, minFlow, this.indices[this.getNbVars() - 1], this.linkVarIdxToEdge[this.getNbVars() - 1]);
                }
        }
}

public void awakeFeasiblePreFlow() throws ContradictionException {
        this.awakeFeasiblePreFlow(false);
}

public void awakeFeasiblePreFlow(boolean firstTime) throws ContradictionException {
        if (this.logger.isLoggable(Level.INFO)) this.logger.info("Awake Feasible Preflow");
        FlowNode sourceN = this.flowGraph[this.sourceIdx];
        FlowNode sinkN = this.flowGraph[FLOWREALSINK];
        boolean needToCheck = false;

        needToCheck = this.updateFeasibleGraph(sourceN, sinkN);

        if (needToCheck) {
                FlowNode dummySource = this.flowGraph[FLOWDUMMYSOURCE];
                FlowNode dummySink = this.flowGraph[FLOWDUMMYSINK];

                if (firstTime) {
                        ((FlowResEdge) sinkN.getNextEdge().get(0)).initFlowResEdge(this.flowVar.getSup());
                } else {
                        ((FlowResEdge) sinkN.getNextEdge().get(0)).initFlowResEdge(this.flowVar.getSup() - sinkN.getB());
                        ((FlowResEdge) sinkN.getNextEdge().get(0)).updateResEdge(this.getGlobalFlow() - sinkN.getB());
                }

                try {
                        this.excessFlowNodes.initialMaxFlow(dummySource, dummySink);
                } catch (NullFlowException e) {
                        this.fail();
                }

                this.excessFlowNodes.removeFlowNode(dummySink);
                this.reInitDistLabel();

                int saturatingFlow = dummySource.maxTheoreticalNextFlow();
                if (saturatingFlow != dummySink.getExcess()) {
                        if (logger.isLoggable(Level.INFO)) logger.info("Excess : " + dummySink.getExcess() + "  vs saturating flow : " + saturatingFlow);
                        dummySink.setExcess(0);
                        dummySource.setExcess(0);

                        List edgeList = dummySink.getPrevEdge();
                        for (int i = 0; i < edgeList.size(); i++) {
                                FlowResEdge edge = (FlowResEdge) edgeList.get(i);
                                ((FlowNode) edge.getI()).setB(0);
                                edge.setR_ij(0);
                                edge.setR_ji(0);
                                edge.setU_ij(0);
                        }
                        edgeList = dummySource.getNextEdge();
                        for (int i = 0; i < edgeList.size(); i++) {
                                FlowResEdge edge = (FlowResEdge) edgeList.get(i);
                                ((FlowNode) edge.getJ()).setB(0);
                                edge.setR_ij(0);
                                edge.setR_ji(0);
                                edge.setU_ij(0);
                        }

                        this.fail();
                }

                List edgeList = dummySink.getPrevEdge();
                for (int i = 0; i < edgeList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) edgeList.get(i);
                        edge.setR_ij(0);
                        edge.setR_ji(0);
                        edge.setU_ij(0);
                        ((FlowNode) edge.getI()).setB(0);
                }
                edgeList = dummySource.getNextEdge();
                for (int i = 0; i < edgeList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) edgeList.get(i);
                        edge.setR_ij(0);
                        edge.setR_ji(0);
                        edge.setU_ij(0);
                        ((FlowNode) edge.getJ()).setB(0);
                }
                ((FlowResEdge) sinkN.getNextEdge().get(0)).setR_ij(0);
                ((FlowResEdge) sinkN.getNextEdge().get(0)).setR_ji(0);

                dummySink.setExcess(0);
                dummySource.setExcess(0);
        }
}


public boolean updateFeasibleGraph(FlowNode sourceN, FlowNode sinkN) {
        FlowNode dummySource = this.flowGraph[FLOWDUMMYSOURCE];
        FlowNode dummySink = this.flowGraph[FLOWDUMMYSINK];
        boolean needToCheck = false;

        for (int i = FLOWREALNODE; i <= FLOWREALSINK; i++) {
                FlowNode node = this.flowGraph[i];

                if (node.getB() < 0) {
                        FlowResEdge dummyEdge = node.getDummyNextEdge();
                        if (dummyEdge != null) {
                                dummyEdge.initFlowResEdge(node.getB() * -1);
                        } else {
                                node.setDummyNextEdge(FlowResEdge.makeFlowDummyResEdge(solver.getEnvironment(),
                                        node, dummySink, (node.getB() * -1)));
                                node.addNextEdge(node.getDummyNextEdge());
                                dummySink.addPrevEdge(node.getDummyNextEdge());
                        }
                        needToCheck = true;
                } else if (node.getB() > 0) {
                        FlowResEdge dummyEdge = node.getDummyPrevEdge();
                        if (dummyEdge != null) {
                                dummyEdge.initFlowResEdge(node.getB());
                        } else {
                                node.setDummyPrevEdge(FlowResEdge.makeFlowDummyResEdge(solver.getEnvironment(),
                                        dummySource, node, node.getB()));
                                dummySource.addNextEdge(node.getDummyPrevEdge());
                                node.addPrevEdge(node.getDummyPrevEdge());
                        }
                        needToCheck = true;
                }
        }

        return needToCheck;
}


// ================= Propagation grace a la conservation du flot aux noeuds ! ==================

public void awakeNodeFlowConservationOnInf(FlowResEdge edge) throws ContradictionException {
        this.awakeFlowConservation(this.awakeNodeFlowConservationOnMaxOutMinIn((FlowNode) edge.getJ()),
                this.awakeNodeFlowConservationOnMaxInMinOut((FlowNode) edge.getI()));
}

public void awakeNodeFlowConservationOnSup(FlowResEdge edge) throws ContradictionException {
        this.awakeFlowConservation(this.awakeNodeFlowConservationOnMaxOutMinIn((FlowNode) edge.getI()),
                this.awakeNodeFlowConservationOnMaxInMinOut((FlowNode) edge.getJ()));
}

public void awakeNodeFlowConservationOnInst(FlowResEdge edge) throws ContradictionException {
        BitSet bsMaxOutMinIn = this.awakeNodeFlowConservationOnMaxOutMinIn((FlowNode) edge.getI());
        bsMaxOutMinIn.or(this.awakeNodeFlowConservationOnMaxOutMinIn((FlowNode) edge.getJ()));

        ((FlowNode) edge.getI()).setStaticCapa(0);
        ((FlowNode) edge.getJ()).setStaticCapa(0);
        BitSet bsMaxInMinOut = this.awakeNodeFlowConservationOnMaxInMinOut((FlowNode) edge.getI());
        bsMaxInMinOut.or(this.awakeNodeFlowConservationOnMaxInMinOut((FlowNode) edge.getJ()));

        this.awakeFlowConservation(bsMaxOutMinIn, bsMaxInMinOut);
}

public void awakeFlowConservation(BitSet toUpdateOnMaxInMinOut, BitSet toUpdateOnMaxOutMinIn) throws ContradictionException {
        this.awakeFlowConservation(toUpdateOnMaxInMinOut, toUpdateOnMaxOutMinIn, true);
}

public void awakeFlowConservation(BitSet toUpdateOnMaxInMinOut, BitSet toUpdateOnMaxOutMinIn, boolean isPreFlowActive) throws ContradictionException {
        while ((toUpdateOnMaxInMinOut.cardinality() > 0) || (toUpdateOnMaxOutMinIn.cardinality() > 0)) {
                while (toUpdateOnMaxInMinOut.cardinality() > 0) {
                        // TODO : verifier si l'ordre de Claire etait important !!
                        // (mais bon a priori non :)
                        int crtIdx = toUpdateOnMaxInMinOut.nextSetBit(0);
                        toUpdateOnMaxInMinOut.clear(crtIdx);

                        toUpdateOnMaxOutMinIn.or(awakeNodeFlowConservationOnMaxInMinOut(this.flowGraph[crtIdx], isPreFlowActive));
                }
                while (toUpdateOnMaxOutMinIn.cardinality() > 0) {
                        int crtIdx = toUpdateOnMaxOutMinIn.nextSetBit(0);
                        toUpdateOnMaxOutMinIn.clear(crtIdx);
                        toUpdateOnMaxInMinOut.or(awakeNodeFlowConservationOnMaxOutMinIn(this.flowGraph[crtIdx], isPreFlowActive));
                }
        }
}

public void awakeInitialFlowConservation() throws ContradictionException {
        BitSet toUpdateNodesOnMaxInMinOut = new BitSet();
        BitSet toUpdateNodesOnMaxOutMinIn = new BitSet();

        BitSet mask = new BitSet();

        for (int i = FLOWREALNODE; i <= FLOWREALSINK; i++) {
                BitSet bs = this.awakeNodeFlowConservationOnMaxOutMinIn(this.flowGraph[i], false);
                bs.and(mask);
                toUpdateNodesOnMaxInMinOut.or(bs);

                mask.set(i);

                bs = this.awakeNodeFlowConservationOnMaxInMinOut(this.flowGraph[i], false);
                bs.and(mask);
                toUpdateNodesOnMaxOutMinIn.or(bs);
        }

        this.awakeFlowConservation(toUpdateNodesOnMaxInMinOut, toUpdateNodesOnMaxOutMinIn, false);
}

public BitSet awakeNodeFlowConservationOnMaxInMinOut(FlowNode node) throws ContradictionException {
        return awakeNodeFlowConservationOnMaxInMinOut(node, true);
}

public BitSet awakeNodeFlowConservationOnMaxInMinOut(FlowNode node, boolean isPreFlowActive) throws ContradictionException {
        BitSet toUpdateNodes = new BitSet();
        if (node.getStaticCapa() != 1) {
                int maxInMinOut = 0;
                boolean becomeStaticCapa = true;
                boolean change = false;

                List prevList;
                if (node.getLabel() == this.sourceIdx) {
                        prevList = this.flowGraph[FLOWREALSINK].getPrevEdge();
                } else {
                        prevList = node.getPrevEdge();
                }

                List nextList;
                if (node.getLabel() == FLOWREALSINK) {
                        nextList = this.flowGraph[this.sourceIdx].getNextEdge();
                } else {
                        nextList = node.getNextEdge();
                }

                // maxInMinOut value
                for (int i = 0; i < prevList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) prevList.get(i);
                        if (edge.getLu_ij() == null) {
                                maxInMinOut += edge.getU_ij();
                        } else {
                                maxInMinOut += edge.getLu_ij().getSup();
                        }
                }
                for (int i = 0; i < nextList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) nextList.get(i);
                        if (edge.getLu_ij() != null) {
                                maxInMinOut -= edge.getLu_ij().getInf();
                        }
                }

                // filtering
                for (int i = 0; i < prevList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) prevList.get(i);
                        if ((edge.getLu_ij() != null) && (!edge.getLu_ij().isInstantiated())) {
                                int newMin = edge.getLu_ij().getSup() - maxInMinOut;
                                if (edge.getLu_ij().getInf() < newMin) {
                                        change = true;
                                        this.updateFlowVarInf(edge.getLu_ij(), newMin, edge.getVarIdx(), isPreFlowActive);
                                        if (!edge.getLu_ij().isInstantiated()) {
                                                becomeStaticCapa = false;
                                        }
                                        toUpdateNodes.or(this.awakeNodeFlowConservationOnMaxInMinOut((FlowNode) edge.getI(), isPreFlowActive));
                                } else {
                                        becomeStaticCapa = false;
                                }
                        }
                }
                for (int i = 0; i < nextList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) nextList.get(i);
                        if ((edge.getLu_ij() != null) && (!edge.getLu_ij().isInstantiated())) {
                                int newMax = edge.getLu_ij().getInf() + maxInMinOut;
                                if (edge.getLu_ij().getSup() > newMax) {
                                        change = true;
                                        this.updateFlowVarSup(edge.getLu_ij(), newMax, edge.getVarIdx(), isPreFlowActive);
                                        if (!edge.getLu_ij().isInstantiated()) {
                                                becomeStaticCapa = false;
                                        }
                                        toUpdateNodes.or(this.awakeNodeFlowConservationOnMaxInMinOut((FlowNode) edge.getJ(), isPreFlowActive));
                                } else {
                                        becomeStaticCapa = false;
                                }
                        }
                }

                if (change && !becomeStaticCapa) {
                        toUpdateNodes.set(node.getLabel());
                }
                if (becomeStaticCapa) {
                        node.setStaticCapa(1);
                }
        }
        return toUpdateNodes;
}

public BitSet awakeNodeFlowConservationOnMaxOutMinIn(FlowNode node) throws ContradictionException {
        return awakeNodeFlowConservationOnMaxOutMinIn(node, true);
}

public BitSet awakeNodeFlowConservationOnMaxOutMinIn(FlowNode node, boolean isPreFlowActive) throws ContradictionException {
        BitSet toUpdateNodes = new BitSet();
        if (node.getStaticCapa() != 1) {
                int maxOutMinIn = 0;
                boolean becomeStaticCapa = true;
                boolean change = false;

                List prevList;
                if (node.getLabel() == this.sourceIdx) {
                        prevList = this.flowGraph[FLOWREALSINK].getPrevEdge();
                } else {
                        prevList = node.getPrevEdge();
                }

                List nextList;
                if (node.getLabel() == FLOWREALSINK) {
                        nextList = this.flowGraph[this.sourceIdx].getNextEdge();
                } else {
                        nextList = node.getNextEdge();
                }

                // maxOutMinIn value
                for (int i = 0; i < prevList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) prevList.get(i);
                        if (edge.getLu_ij() != null) {
                                maxOutMinIn -= edge.getLu_ij().getInf();
                        }
                }
                for (int i = 0; i < nextList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) nextList.get(i);
                        if (edge.getLu_ij() == null) {
                                maxOutMinIn += edge.getU_ij();
                        } else {
                                maxOutMinIn += edge.getLu_ij().getSup();
                        }
                }

                // filtering
                for (int i = 0; i < prevList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) prevList.get(i);
                        if ((edge.getLu_ij() != null) && (!edge.getLu_ij().isInstantiated())) {
                                int newMax = edge.getLu_ij().getInf() + maxOutMinIn;
                                if (edge.getLu_ij().getSup() > newMax) {
                                        change = true;
                                        this.updateFlowVarSup(edge.getLu_ij(), newMax, edge.getVarIdx(), isPreFlowActive);
                                        if (!edge.getLu_ij().isInstantiated()) {
                                                becomeStaticCapa = false;
                                        }
                                        toUpdateNodes.or(this.awakeNodeFlowConservationOnMaxOutMinIn((FlowNode) edge.getI(), isPreFlowActive));
                                } else {
                                        becomeStaticCapa = false;
                                }
                        }
                }
                for (int i = 0; i < nextList.size(); i++) {
                        FlowResEdge edge = (FlowResEdge) nextList.get(i);
                        if ((edge.getLu_ij() != null) && (!edge.getLu_ij().isInstantiated())) {
                                int newMin = edge.getLu_ij().getSup() - maxOutMinIn;
                                if (edge.getLu_ij().getInf() < newMin) {
                                        change = true;
                                        this.updateFlowVarInf(edge.getLu_ij(), newMin, edge.getVarIdx(), isPreFlowActive);
                                        if (!edge.getLu_ij().isInstantiated()) {
                                                becomeStaticCapa = false;
                                        }
                                        toUpdateNodes.or(this.awakeNodeFlowConservationOnMaxOutMinIn((FlowNode) edge.getJ(), isPreFlowActive));
                                } else {
                                        becomeStaticCapa = false;
                                }
                        }
                }

                if (change && !becomeStaticCapa) {
                        toUpdateNodes.set(node.getLabel());
                }
                if (becomeStaticCapa) {
                        node.setStaticCapa(1);
                }
        }
        return toUpdateNodes;
}

// ================== Tools =================

public final void reInitDistLabel() {
        for (int i = 0; i < flowGraph.length; i++) {
                FlowNode node = flowGraph[i];
                node.reInitDistLabel();
        }
}

public static String arrayString(Object[] array) {
        StringBuffer str = new StringBuffer();
        str.append("[");
        for (int i = 0; i < array.length - 1; i++) {
                Object o = array[i];
                str.append(o);
                str.append(",");
        }
        str.append(array[array.length - 1]);
        str.append("]");
        return str.toString();
}
}
