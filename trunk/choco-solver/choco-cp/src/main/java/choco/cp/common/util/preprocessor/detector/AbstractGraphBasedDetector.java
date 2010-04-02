/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.common.util.preprocessor.detector;

import choco.cp.common.util.preprocessor.AbstractDetector;
import choco.cp.common.util.preprocessor.graph.ArrayGraph;
import choco.cp.common.util.preprocessor.graph.MaxCliques;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.Iterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 2 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * A class dedicated to detect clique of differences or disjonctions
 * and state the corresponding global constraints
 */
public abstract class AbstractGraphBasedDetector extends AbstractDetector {

    /**
     * The graph of differences
     */
    protected ArrayGraph diffs;

    protected CliqueIterator itc;

    protected AbstractGraphBasedDetector(final CPModel model) {
        super(model);
        this.diffs = new ArrayGraph(model.getNbIntVars());
    }


    public void addEdge(Variable a, Variable b, Constraint c) {
        final int idxa = a.getHook();
        final int idxb = b.getHook();
        diffs.addEdge(idxa, idxb);
        diffs.storeEdge(c, idxa, idxb);
    }

    public void removeConstraint(int a, int b) {
        delete(diffs.getConstraintEdge(a, b));
    }

    //**************************************************************//
    //******************** Iterator on cliques ********************//
    //*************************************************************//


    /**
     * An iterator over all the cliques detected by the Bron and Kerbosh
     *
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
                c[j] = model.getIntVar(clique[idx][j]);
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
