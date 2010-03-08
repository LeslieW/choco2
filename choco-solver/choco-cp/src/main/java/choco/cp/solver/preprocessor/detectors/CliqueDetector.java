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
package choco.cp.solver.preprocessor.detectors;

import choco.cp.model.CPModel;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.preprocessor.graph.ArrayGraph;
import choco.cp.solver.preprocessor.graph.MaxCliques;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.Arrays;
import java.util.Iterator;

/*
 * User:    hcambaza
 * Date:    19 août 2008
 *
 * A class dedicated to detect clique of differences or disjonctions
 * and state the corresponding global constraints
 */
public class CliqueDetector {

    /**
     * The graph of differences
     */
    protected ArrayGraph diffs;

    protected CliqueIterator itc;

    protected CPModel mod;

    public CliqueDetector(CPModel mod) {
        this.diffs = new ArrayGraph(mod.getNbIntVars());
        this.mod = mod;
    }

    public void addEdge(Variable a, Variable b, Constraint c) {
        final int idxa = a.getHook();
        final int idxb = b.getHook();
        diffs.addEdge(idxa,idxb);
        diffs.storeEdge(c,idxa,idxb);
    }

    public void removeConstraint(int a, int b) {
         diffs.deleteConstraintEdge(mod,a,b);
     }

    //*************************************************************//
    //******************** Deal with differences ******************//    
    //*************************************************************//

    /**
     * Build the constraint graph of differences
     * @return boolean
     */
    public boolean addAllNeqEdges() {
        Iterator<Constraint> itneq = mod.getConstraintByType(ConstraintType.NEQ);
        while(itneq.hasNext()) {
            Constraint neq = itneq.next();
            Variable[] vars = neq.getVariables();
            if (isRealBinaryNeq(vars)) { //the NEQ can take a constant...
                addEdge(neq.getVariables()[0],neq.getVariables()[1], neq);
            }
        }
//        itneq = mod.getConstraintByType(ConstraintType.ALLDIFFERENT);
//        while(itneq.hasNext()) {
//            Constraint allDiff = itneq.next();
//            Variable[] vars = allDiff.getVariables();
//            if (isRealNaryNeq(vars)) {
//                for(int i = 0; i < vars.length-1; i++){
//                    for(int j = i+1; j < vars.length; j++){
//                        addEdge(allDiff.getVariables()[i],allDiff.getVariables()[j], null);
//                    }
//                }
////                itneq.remove();
//            }
//        }
        return diffs.nbEdges > 0;
    }

    public boolean isRealBinaryNeq(Variable[] vars) {
        if (vars.length != 2) return false;
        for (Variable var : vars) {
            if (var.getVariableType() != VariableType.INTEGER)
                return false;
        }
        return true;
    }

    public boolean isRealNaryNeq(Variable[] vars) {
        for (Variable var : vars) {
            if (var.getVariableType() != VariableType.INTEGER)
                return false;
        }
        return true;
    }


    //*************************************************************//
    //******************** Deal with disjunctions *****************//
    //*************************************************************//

    /**
     * Build the constraint graph of disjonctions and compute
     * the duration of each task
     * @param ed expression detector
     * @param ppcs PreProcessCPSolver
     * @return int[]
     */
    public int[] addAllDisjunctiveEdges(ExpressionDetector ed, PreProcessCPSolver ppcs) {
        Iterator<Constraint> it = mod.getConstraintIterator();
        int[] durations = new int[mod.getNbIntVars()];
        Arrays.fill(durations,-1);
        boolean b = false;
        while (it.hasNext()) {
            Constraint ic = it.next();
            if (ic instanceof MetaConstraint) {
                ExpressionSConstraint es = new ExpressionSConstraint(ppcs.getMod2Sol().buildNode(ic));
                ExpressionDetector.SimplePrecedence sp = ed.getPrecedenceConstraint(es);
                if (sp != null) {
                    b = true;
                    addEdge(sp.v1, sp.v2, ic);
                    int idx1 = sp.v1.getHook();
                    int idx2 = sp.v2.getHook();
                    if (durations[idx1] == -1)
                        durations[idx1] = sp.d1;
                    else if (durations[idx1] != sp.d1) {
                        return null; // the same task do not have always the same duration so give up
                    }
                    if (durations[idx2] == -1)
                        durations[idx2] = sp.d2;
                    else if (durations[idx2] != sp.d2) {
                        return null; // the same task do not have always the same duration so give up
                    }
                }
            }
        }
        if (b) return durations;
        else return null;
    }

    //**************************************************************//
     //******************** Iterator on cliques ********************//
     //*************************************************************//


    /**
     * An iterator over all the cliques detected by the Bron and Kerbosh
     * @return CliqueIterator
     */
    public CliqueIterator cliqueIterator() {
       if (itc == null) {
           return new CliqueIterator();
       } else {
           itc.init();
           return itc;
       }
    }

    public class CliqueIterator implements Iterator<IntegerVariable[]> {

        protected int idx = 0;

        protected int[][] clique;

        public CliqueIterator() {
            MaxCliques mc = new MaxCliques(diffs);
            clique = mc.getMaxCliques();
        }

        public void init() {
            idx = 0;
        }

        public boolean hasNext() {
            return idx < clique.length;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public IntegerVariable[] next() {
            IntegerVariable[] c = new IntegerVariable[clique[idx].length];
            for (int j = 0; j < c.length; j++) {
                c[j] = mod.getIntVar(clique[idx][j]);                
            }
            idx++;
            return c;
        }


        public void remove() {
            int id = idx - 1;
            for (int j = 0; j < clique[id].length; j++) {
                for (int k = j + 1; k < clique[id].length; k++) {
                    diffs.remEdge(clique[id][j], clique[id][k]);
                    removeConstraint(clique[id][j],
                                     clique[id][k]);
                }
            }
        }
    }
}

