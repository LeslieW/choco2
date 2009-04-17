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
package samples.seminar.tsp;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.IntIterator;
import choco.kernel.memory.IStateBitSet;
import choco.kernel.memory.IStateInt;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class SubTourConstraint extends AbstractLargeIntSConstraint {

    public static class SubTourConstraintManager extends IntConstraintManager {
        public SConstraint makeConstraint(Solver solver, Variable[] variables, Object parameters, HashSet<String> options) {
            if(solver instanceof CPSolver){
                return new SubTourConstraint(solver.getVar((IntegerVariable[])variables));
            }

            return null;
        }
    }

    protected boolean debug = false;
    protected boolean filter = false;

    protected int n;

    protected IntDomainVar[] s;
    protected IStateBitSet[] inPath;
    protected IStateInt[] end;

    public SubTourConstraint(IntDomainVar[] s) {
        super(s);
        this.s = s;
        this.n = s.length;
        this.inPath = new IStateBitSet[n];
        this.end = new IStateInt[n];
        for (int i = 0; i < this.n; i++) {
            this.inPath[i] = s[i].getSolver().getEnvironment().makeBitSet(n);
            this.end[i] = s[i].getSolver().getEnvironment().makeInt(i);
        }
    }

    public void awake() throws ContradictionException {
        // on fait un parcours du graphe depuis le sommet origine d'index 0 et on met a jour inpath
        BitSet currentPath = new BitSet(n);
        BitSet reached = new BitSet(n);
        Queue<Integer> q = new LinkedList<Integer>();
        q.offer(0);
        reached.set(n - 1, true);
        while (!q.isEmpty()) {
            int u = q.poll();
            reached.set(u, true);
            currentPath.set(u, true);
            if (s[u].isInstantiated()) {
                int v = s[u].getVal();
                if (v != n - 1) q.offer(v);
            }
            if (q.isEmpty() && reached.cardinality() < n) {
                // currentPath contient un chemin sur et u est le dernier sommet
                for (int v = currentPath.nextSetBit(0); v >= 0; v = currentPath.nextSetBit(v + 1)) {
                    inPath[u].set(v, true);
                }
                // on commence un nouveau chemin
                currentPath.clear();
                int w = 0;
                do {
                    if (!reached.get(w)) q.offer(w);
                    else w++;
                } while (q.isEmpty());
            }
        }
        if (debug) LOGGER.info(this.showInPath());
        // on fait un parcours du graphe depuis le sommet fin d'index n-1 et on met a jour end
        reached.clear();
        q = new LinkedList<Integer>();
        q.offer(n - 1);
        reached.set(0, true);
        while (!q.isEmpty()) {
            int v = q.poll();
            reached.set(v, true);
            for (int u = 0; u < n; u++) {
                if (s[u].isInstantiatedTo(v)) {
                    end[u].set(end[v].get());
                    if (u != 0) q.offer(u);
                }
            }
            if (q.isEmpty() && reached.cardinality() < n) {
                int w = 0;
                do {
                    if (!reached.get(w)) q.offer(w);
                    else w++;
                } while (q.isEmpty());
            }
        }
        if (debug) LOGGER.info(this.showEnds());
        // on recupere le chemin sur partant de 0
        BitSet mainPath = new BitSet(n);
        q = new LinkedList<Integer>();
        q.offer(0);
        while (!q.isEmpty()) {
            int u = q.poll();
            mainPath.set(u, true);
            if (s[u].isInstantiated()) {
                int v = s[u].getVal();
                if (v != n - 1) q.offer(v);
            }
        }
        // on interdit que le dernier sommet d'un chemin sur puisse atteindre un sommet de ce meme chemin
        for (int u = 0; u < n; u++) {
            for (int v = inPath[u].nextSetBit(0); v >= 0; v = inPath[u].nextSetBit(v + 1)) {
                if (s[u].canBeInstantiatedTo(v)) {
                    if (filter) LOGGER.info("1- rem (" + u + "," + v + ")");
                    s[u].removeVal(v, cIndices[v]);
                }
            }
        }
        // on interdit de pouvoir atteindre le chemin sur partant de 0 depuis un quelconque sommet du graphe
        for (int u = 0; u < n; u++) {
            if (!mainPath.get(u)) {
                for (int v = mainPath.nextSetBit(0); v >= 0; v = mainPath.nextSetBit(v + 1)) {
                    if (end[u].get() != n-1) {
                        if (s[end[u].get()].canBeInstantiatedTo(v)) {
                            if (filter) LOGGER.info("2- rem (" + end[u].get() + "," + v + ")");
                            s[end[u].get()].removeVal(v, cIndices[end[u].get()]);
                        }
                    }
                }
            }
        }
    }

    public void propagate() throws ContradictionException {
    }

    public void awakeOnInst(int u) throws ContradictionException {
        if (u != n - 1) {
            int v = s[u].getVal();
            // on met a jour le chemin sur pouvant atteindre v
            for (int w = inPath[u].nextSetBit(0); w >= 0; w = inPath[u].nextSetBit(w + 1)) {
                inPath[v].set(w, true);
            }
            if (debug) LOGGER.info(this.showInPath());
            // on met a jour end[u]
            end[u].set(end[v].get());
            if (debug) LOGGER.info(this.showEnds());
            // filtrage : on interdit tout arc de end[v] vers un sommet w de inPath[u]
            for (int w = inPath[u].nextSetBit(0); w >= 0; w = inPath[u].nextSetBit(w + 1)) {
                if ((end[v].get() != n-1 || w != 0) &&  s[end[v].get()].canBeInstantiatedTo(w)) {
                    if (filter) LOGGER.info("3- rem (" + end[v].get() + "," + w + ")");
                    s[end[v].get()].removeVal(w, cIndices[end[v].get()]);
                }
            }
        }
    }

    public void awakeOnInf(int u) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnSup(int u) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnBounds(int u) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnRem(int u, int v) throws ContradictionException {
        this.constAwake(false);
    }

    public void awakeOnRemovals(int u, IntIterator deltaDomain) throws ContradictionException {
        this.constAwake(false);
    }

    public boolean isSatisfied() {
        return false;
    }

    private String showInPath() {
        String s = "";
        for (int i = 0; i < n; i++) {
            s += "inPath[" + i + "] = ";
            for (int j = inPath[i].nextSetBit(0); j >= 0; j = inPath[i].nextSetBit(j + 1)) {
                s += j + " ";
            }
            s += "\n";
        }
        return s;
    }

    private String showEnds() {
        String s = "";
        for (int i = 0; i < n; i++) {
            s += "end[" + i + "] = " + end[i].get() + "\n";
        }
        return s;
    }
}
