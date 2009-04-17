/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.reducedGraph;

import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.StoredBitSetGraph;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.Solver;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Logger;


public class ReducedGraph {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

    protected boolean affiche = false;

    protected Solver solver;

    protected int nbVertices;

    protected StoredBitSetGraph graph;

    protected int time;
    protected int numComp;
    protected int[] composante;
    protected int[] prefix;
    protected LinkedList<Integer> listSuffix;
    protected Vector<StoredBitSet> CFC;
    protected StoredBitSet[] reducedGraph;

    /**
     * Constructor
     * @param solver
     * @param graph
     * @param graph
     */
    public ReducedGraph(Solver solver, StoredBitSetGraph graph) {
        this.solver = solver;
        this.graph = graph;
        // le nbre de sommet dans G_c
        this.nbVertices = graph.getGraphSize();
        // l'horloge
        this.time = 0;
        // numero de la composante courrante
        this.numComp = -1;
        // chaque sommet appartient a une composante
        this.composante = new int[nbVertices];
        for (int i = 0; i < nbVertices; i++) composante[i] = -1;
        // ordre prefixe et suffixes lors des parcours
        this.prefix = new int[nbVertices];
        for (int i = 0; i < nbVertices; i++) prefix[i] = 0;
        // liste des sommets tries par suffixes
        this.listSuffix = new LinkedList<Integer>();
        this.CFC = new Vector<StoredBitSet>(nbVertices);
        this.reducedGraph = new StoredBitSet[1];
        this.reducedGraph[0] = new StoredBitSet(solver.getEnvironment(), nbVertices);
    }

     /**
     * methode principale: calculer les cfc du graphe reduit et stocker les sommets dans la structure du reduit
     *
     */
    public void stronglyConnectedComponent() {
        // l'horloge
        time = 0;
        // numero de la composante courrante
        numComp = -1;
        // chaque sommet appartient a une composante
        composante = new int[nbVertices];
        for (int i = 0; i < nbVertices; i++) composante[i] = -1;
        // ordre prefixe et suffixes lors des parcours
        prefix = new int[nbVertices];
        for (int i = 0; i < nbVertices; i++) prefix[i] = 0;
        // liste des sommets tries par suffixes
        listSuffix = new LinkedList<Integer>();
        // recalcul des cfc
        CFC.removeAllElements();
        // debut du traitement
        if (affiche) LOGGER.info("====> Premier DFS:");
        for (int v = 0; v < nbVertices; v++) {
            if (prefix[v] == 0) {
                if (affiche) LOGGER.info("    On entre par " + v);
                listSuffix = dfs_suffix(v);
            }
        }
        if (affiche) LOGGER.info("Fin du premier DFS <====");
        // mise a jour des structures
        razStruct();
        // inversion de la matrice CGA
        Vector invCGA = inverse();
        if (affiche) LOGGER.info("====> Second DFS (inverse):");
        while (listSuffix.size() != 0) {
            int v = listSuffix.removeLast();
            if (prefix[v] == 0) {
                numComp++;
                if (affiche) LOGGER.info("    On entre par " + v);
                dfs_mark(v, invCGA);
            }
        }
        if (affiche) LOGGER.info("Fin du second DFS <====");
        // le tableau composante contient la liste des cfc, on effectue un traitement pour le mettre sous forme d'un vector
        for (int i = 0; i < numComp + 1; i++) {
            StoredBitSet contain = new StoredBitSet(solver.getEnvironment(), nbVertices);
            boolean add = false;
            for (int j = 0; j < nbVertices; j++) {
                if (composante[j] == i) {
                    if (affiche) LOGGER.info(j + " est dans la composante " + i);
                    add = true;
                    contain.set(j, true);
                }
            }
            if (add) CFC.addElement(contain);
        }
        if (affiche) LOGGER.info("nbre de cfc = " + CFC.size());
        buildCFCgraph();
    }

    public void buildCFCgraph() {
        // On contruit une vision du graphe reduit
        reducedGraph = new StoredBitSet[CFC.size()];
        for (int i = 0; i < CFC.size(); i++) {
            StoredBitSet contain = CFC.elementAt(i);
            reducedGraph[i] = new StoredBitSet(solver.getEnvironment(), CFC.size());
            BitSet successors_i = new BitSet(nbVertices);
            for (int j = contain.nextSetBit(0); j >= 0; j = contain.nextSetBit(j + 1)) {
                StoredBitSet succ_j = graph.getSuccessors(j);
                for (int k = succ_j.nextSetBit(0); k >= 0; k = succ_j.nextSetBit(k + 1)) {
                    if (k != j) {
                        successors_i.set(k, true);
                    }
                }
            }
            for (int j = 0; j < CFC.size(); j++) {
                if (j != i) {
                    StoredBitSet possible = CFC.elementAt(j);
                    for (int k = possible.nextSetBit(0); k >= 0; k = possible.nextSetBit(k + 1)) {
                        if (successors_i.get(k)) {
                            reducedGraph[i].set(j, true);
                        }
                    }
                }
            }
        }
    }

    // remise a zero de la struct
    public void razStruct() {
        // l'horloge
        time = 0;
        // ordre prefixe
        prefix = new int[nbVertices];
        for (int i = 0; i < nbVertices; i++) prefix[i] = 0;
    }

    // DFS sur CGAPoss
    public LinkedList<Integer> dfs_suffix(int v) {
        prefix[v] = time++;
        if (affiche) LOGGER.info("       On visite " + v);
        StoredBitSet dom = graph.getSuccessors(v);
        for (int j = dom.nextSetBit(0); j >= 0; j = dom.nextSetBit(j + 1)) {
            if (affiche) LOGGER.info("              on voudrait visiter " + j);
            if (prefix[j] == 0) dfs_suffix(j);
        }
        listSuffix.offer(v);
        return listSuffix;
    }

    // DFS sur invCGA
    public void dfs_mark(int v, Vector invCGA) {
        prefix[v] = time++;
        if (affiche) LOGGER.info("       On visite " + v);
        if (composante[v] == -1) composante[v] = numComp;
        BitSet listPossSucc = (BitSet) invCGA.elementAt(v);
        for (int j = listPossSucc.nextSetBit(0); j >= 0; j = listPossSucc.nextSetBit(j + 1)) {
            if (prefix[j] == 0) dfs_mark(j, invCGA);
        }
    }

    // inversion de la matrice
    public Vector inverse() {
        Vector<BitSet> invCGA = new Vector<BitSet>(nbVertices);
        for (int i = 0; i < nbVertices; i++) {
            BitSet contain = new BitSet(nbVertices);
            invCGA.addElement(contain);
        }
        for (int i = 0; i < nbVertices; i++) {
            StoredBitSet dom = graph.getSuccessors(i);
            for (int j = dom.nextSetBit(0); j >= 0; j = dom.nextSetBit(j + 1)) {
                invCGA.elementAt(j).set(i, true);
            }
        }
        return invCGA;
    }

    public Vector<StoredBitSet> getCFC() {
        return CFC;
    }
    
    public StoredBitSet[] getCFCgraph() {
        return reducedGraph;
    }

    public StoredBitSet getMergedVertices(int numCFC) {
        return CFC.elementAt(numCFC);
    }
}