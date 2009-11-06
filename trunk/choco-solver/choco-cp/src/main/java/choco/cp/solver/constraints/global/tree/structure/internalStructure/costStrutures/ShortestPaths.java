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
package choco.cp.solver.constraints.global.tree.structure.internalStructure.costStrutures;

import choco.cp.solver.constraints.global.tree.structure.internalStructure.graphStructures.graphViews.VarGraphView;
import choco.kernel.memory.IStateInt;



public class ShortestPaths {

    int size;

    protected int maxInt = 100000;
    protected VarGraphView inputGraph;
    protected IStateInt[][] travelTime;
    protected IStateInt[][] minTravelTime;

    public ShortestPaths(int nbNodes, IStateInt[][] travelTime, VarGraphView inputGraph, IStateInt[][] minTravelTime) {
        this.size = nbNodes;
        this.travelTime = travelTime;
        this.minTravelTime = minTravelTime;
        this.inputGraph = inputGraph;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                minTravelTime[i][j].set(travelTime[i][j].get());
            }
        }
    }

    /* Floyd algorithms : O(n^3)
    // Initializations
        Initialize the diagonal of the matrix V to 0, and the others to +infty
        for i:=1 � N
	        for each successor j of i
		        V[i,j]:=W[i,j];
    // compute iteratively the matrix V(k)
        for k := 1 � N
            for i := 1 � N
                for j := 1 � N
                    if (V[i,k]+V[k,j] < V[i,j]) then
                        V[i,j] := V[i,k] + V[k,j];
                    FS
                FP
            FP
        FP
    */
    public void computeMinPaths() {
        for (int i = 0; i < size; i++) {
            if (inputGraph.isFixedSucc(i)) {
                for (int j = 0; j < size; j++) {
                    if (j != inputGraph.getSure().getSuccessors(i).nextSetBit(0)) {
                        minTravelTime[i][j].set(maxInt);
                    } else  minTravelTime[i][j].set(travelTime[i][j].get());
                }
            } else {
                for (int j = 0; j < size; j++) minTravelTime[i][j].set(travelTime[i][j].get());
            }
        }
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (minTravelTime[i][k].get() + minTravelTime[k][j].get() < minTravelTime[i][j].get()) {
                        minTravelTime[i][j].set(minTravelTime[i][k].get() + minTravelTime[k][j].get());
                    }
                }
            }
        }
    }
}
