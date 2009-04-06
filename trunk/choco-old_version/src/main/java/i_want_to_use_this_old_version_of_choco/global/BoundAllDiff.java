// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public class BoundAllDiff extends AbstractLargeIntConstraint {
    public boolean PROPAGATE_ON_INSTANTIATIONS = true;
    public boolean PROPAGATE_ON_BOUNDS = true;

    int[] t; // Tree links
    int[] d; // Diffs between critical capacities
    int[] h; // Hall interval links
    int[] bounds;

    int nbBounds;

    Interval[] intervals;
    Interval[] minsorted;
    Interval[] maxsorted;

    boolean infBoundModified = true;
    boolean supBoundModified = true;

    public BoundAllDiff(IntDomainVar[] vars, boolean global) {
        super(vars);
        int n = this.getNbVars();
        if (!global) {
            PROPAGATE_ON_BOUNDS = false;
        }

        t = new int[2 * n + 2];
        d = new int[2 * n + 2];
        h = new int[2 * n + 2];
        bounds = new int[2 * n + 2];

        intervals = new Interval[n];
        minsorted = new Interval[n];
        maxsorted = new Interval[n];

        for (int i = 0; i < vars.length; i++) {
            intervals[i] = new Interval();
            intervals[i].var = vars[i];
            intervals[i].idx = i;
            minsorted[i] = intervals[i];
            maxsorted[i] = intervals[i];
        }
    }

    protected void sortmin() {
        boolean sorted = false;
        int current = this.getNbVars() - 1;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < current; i++) {
                if (minsorted[i].var.getInf() > minsorted[i + 1].var.getInf()) {
                    Interval t = minsorted[i];
                    minsorted[i] = minsorted[i + 1];
                    minsorted[i + 1] = t;
                    sorted = false;
                }
            }
            current--;
        }
    }

    protected void sortmax() {
        boolean sorted = false;
        int current = 0;
        while (!sorted) {
            sorted = true;
            for (int i = this.getNbVars() - 1; i > current; i--) {
                if (maxsorted[i].var.getSup() < maxsorted[i - 1].var.getSup()) {
                    Interval t = maxsorted[i];
                    maxsorted[i] = maxsorted[i - 1];
                    maxsorted[i - 1] = t;
                    sorted = false;
                }
            }
            current++;
        }
    }

    protected void sortIt() {
        this.sortmin();
        this.sortmax();

        int min = minsorted[0].var.getInf();
        int max = maxsorted[0].var.getSup() + 1;
        int last = min - 2;
        int nb = 0;
        bounds[0] = last;

        int i = 0, j = 0;
        while (true) {
            if (i < this.getNbVars() && min <= max) {
                if (min != last)
                    bounds[++nb] = last = min;
                minsorted[i].minrank = nb;
                if (++i < this.getNbVars())
                    min = minsorted[i].var.getInf();
            } else {
                if (max != last)
                    bounds[++nb] = last = max;
                maxsorted[j].maxrank = nb;
                if (++j == this.getNbVars()) break;
                max = maxsorted[j].var.getSup() + 1;
            }
        }

        this.nbBounds = nb;
        bounds[nb + 1] = bounds[nb] + 2;
    }

    protected void pathset(int[] tab, int start, int end, int to) {
        int next = start;
        int prev = next;

        while (prev != end) {
            next = tab[prev];
            tab[prev] = to;
            prev = next;
        }
    }

    protected int pathmin(int[] tab, int i) {
        while (tab[i] < i) {
            i = tab[i];
        }
        return i;
    }

    protected int pathmax(int[] tab, int i) {
        while (tab[i] > i) {
            i = tab[i];
        }
        return i;
    }

    protected void filterLower() throws ContradictionException {
        for (int i = 1; i <= nbBounds + 1; i++) {
            t[i] = h[i] = i - 1;
            d[i] = bounds[i] - bounds[i - 1];
        }

        for (int i = 0; i < this.getNbVars(); i++) {
            int x = maxsorted[i].minrank;
            int y = maxsorted[i].maxrank;
            int z = pathmax(t, x + 1);
            int j = t[z];

            if (--d[z] == 0) {
                t[z] = z + 1;
                z = pathmax(t, t[z]);
                t[z] = j;
            }

            pathset(t, x + 1, z, z);

            if (d[z] < bounds[z] - bounds[y]) this.fail();

            if (h[x] > x) {
                int w = pathmax(h, h[x]);
                maxsorted[i].var.updateInf(bounds[w], this.getConstraintIdx(maxsorted[i].idx));
                pathset(h, x, w, w);
            }

            if (d[z] == bounds[z] - bounds[y]) {
                pathset(h, h[y], j - 1, y);
                h[y] = j - 1;
            }
        }
    }

    protected void filterUpper() throws ContradictionException {
        for (int i = 0; i <= nbBounds; i++) {
            t[i] = h[i] = i + 1;
            d[i] = bounds[i + 1] - bounds[i];
        }
        for (int i = this.getNbVars() - 1; i >= 0; i--) {
            int x = minsorted[i].maxrank;
            int y = minsorted[i].minrank;
            int z = pathmin(t, x - 1);
            int j = t[z];

            if (--d[z] == 0) {
                t[z] = z - 1;
                z = pathmin(t, t[z]);
                t[z] = j;
            }

            pathset(t, x - 1, z, z);

            if (d[z] < bounds[y] - bounds[z]) this.fail();

            if (h[x] < x) {
                int w = pathmin(h, h[x]);
                minsorted[i].var.updateSup(bounds[w] - 1, this.getConstraintIdx(minsorted[i].idx));
                pathset(h, x, w, w);
            }
            if (d[z] == bounds[y] - bounds[z]) {
                pathset(h, h[y], j + 1, y);
                h[y] = j + 1;
            }
        }
    }

    public void awake() throws ContradictionException {
        for (int i = 0; i < vars.length; i++) {
            if (vars[i].isInstantiated()) {
                for (int j = 0; j < vars.length; j++) {
                    if (i != j) {
                        vars[j].removeVal(vars[i].getVal(), -1);
                    }
                }
            }
        }

        propagate();
    }

    public void propagate() throws ContradictionException {
        if (infBoundModified || supBoundModified) {
            //System.out.println("Sort it !");
            sortIt();
            //System.out.println("Filter lower !");
            filterLower();
            //System.out.println("Filter upper");
            filterUpper();
            //System.out.println("Finished !");
            infBoundModified = false;
            supBoundModified = false;
        }
    }

    public void awakeOnInf(int i) throws ContradictionException {
        if (PROPAGATE_ON_BOUNDS) {
            infBoundModified = true;
            this.constAwake(false);
            for (int j = 0; j < vars.length; j++) {
                if (j != i && vars[j].isInstantiated()) {
                    if (vars[j].getVal() == vars[i].getInf())
                        vars[i].updateInf(vars[j].getVal() + 1, -1);
                }
            }
        }
    }

    public void awakeOnSup(int i) throws ContradictionException {
        if (PROPAGATE_ON_BOUNDS) {
            supBoundModified = true;
            this.constAwake(false);
            for (int j = 0; j < vars.length; j++) {
                if (j != i && vars[j].isInstantiated()) {
                    if (vars[j].getVal() == vars[i].getSup())
                        vars[i].updateSup(vars[j].getVal() - 1, -1);
                }
            }
        }
    }

    public void awakeOnInst(int i) throws ContradictionException {   // Propagation classique
        if (PROPAGATE_ON_INSTANTIATIONS) {
            infBoundModified = true;
            supBoundModified = true;
            this.constAwake(false);
            int val = vars[i].getVal();
            for (int j = 0; j < vars.length; j++) {
                if (j != i) {
                    this.getIntVar(j).removeVal(val, -1);
                }
            }
        }
    }

    public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    }

  public Boolean isEntailed() {
        throw new Error("isEntailed not yet implemented on package i_want_to_use_this_old_version_of_choco.global.BoundAlldiff");
    }

  /**
   * This method assumes that all variables are instantiated and checks if the values are consistent with the
   * constraint.
   * Here it checks that all variables have distinct values. It uses double for loops (Thus the complixity is in O(n^2).
   * @return true if values are different.
   */
    public boolean isSatisfied(int[] tuple) {
      for(int i = 0; i < vars.length; i++)
        for(int j = i+1; j < vars.length; j++) {
          if (tuple[i] == tuple[j]) return false;
        }
      return true;
    }

    public String pretty() {
      StringBuilder sb = new StringBuilder();
      sb.append("BoundAllDiff({");
      for (int i = 0; i < vars.length; i++) {
        if (i > 0) sb.append(", ");
        IntDomainVar var = vars[i];
        sb.append(var.pretty());
      }
      sb.append("})");
      return sb.toString();
    }

    private class Interval {
        int minrank, maxrank;
        IntDomainVar var;
        int idx;
    }
}
