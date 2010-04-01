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
package choco.kernel.model.detector;

import choco.cp.common.util.detector.TaskMerger;
import choco.cp.model.CPModel;
import choco.kernel.common.util.objects.BooleanSparseMatrix;
import choco.kernel.common.util.objects.ISparseMatrix;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.scheduling.TaskVariable;
import gnu.trove.TIntObjectHashMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 * <p/>
 * A class detector to detect equalities between TaskVariable within a model.
 */
public final class TaskVariableEqualitiesDetector extends AbstractDetector {


    protected TaskVariableEqualitiesDetector(final CPModel model) {
        super(model);
    }

    /**
     * Apply the detection defined within the detector.
     */
    @Override
    public void apply() {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("TaskVariable equalities detection :");
        }
        final ISparseMatrix matrix = analyze();
        if (matrix.getNbElement() > 0) {
            change(matrix);
        }
    }

    /**
     * Analyze the current model and record equality constraints over
     * {@link choco.kernel.model.variables.scheduling.TaskVariable}.
     *
     * @return
     */
    private ISparseMatrix analyze() {
        final int nbStoredMultipleVars = model.getNbStoredMultipleVars();
        final ISparseMatrix matrix = new BooleanSparseMatrix(nbStoredMultipleVars);
        MultipleVariables m1, m2;
        /// Run over equalities constraints, and create edges
        for (int i = 0; i < nbStoredMultipleVars - 1; i++) {
            m1 = model.getStoredMultipleVar(i);
            if (m1 instanceof TaskVariable) {
                for (int j = i + 1; j < nbStoredMultipleVars; j++) {
                    m2 = model.getStoredMultipleVar(j);
                    if (m2 instanceof TaskVariable) {
                        if (m1.isEquivalentTo(m2)) {
                            matrix.add(m1.getHook(), m2.getHook());
                        }
                    }

                }
            }
        }
        return matrix;
    }

    private void change(final ISparseMatrix matrix) {
        final int nbStoredMultipleVars = model.getNbIntVars();

        matrix.prepare();

        final int[] color = new int[nbStoredMultipleVars];
        Arrays.fill(color, -1);
        final TIntObjectHashMap<TaskMerger> domainByColor = new TIntObjectHashMap<TaskMerger>();

        int nbDiffObject = detect(matrix, nbStoredMultipleVars, color, domainByColor);
        apply(nbDiffObject, nbStoredMultipleVars, color, domainByColor);
    }

    private int detect(final ISparseMatrix matrix, final int nbStoredMultipleVars, final int[] color,
                       final TIntObjectHashMap<TaskMerger> domainByColor) {
        int nb = -1;
        final Iterator<Long> it = matrix.iterator();
        while (it.hasNext()) {
            final long v = it.next();
            final int i = (int) (v / nbStoredMultipleVars);
            final int j = (int) (v % nbStoredMultipleVars);

            if (color[i] == -1) {
                nb++;
                color[i] = nb;
                domainByColor.put(nb, new TaskMerger((TaskVariable) model.getStoredMultipleVar(i)));
            }
            final TaskMerger d = domainByColor.get(color[i]);
            //backup
            d.merge((TaskVariable) model.getStoredMultipleVar(j));
            color[j] = color[i];
            domainByColor.put(color[i], d);
        }
        return nb;
    }

    private void apply(final int k, final int nbStoredMultipleVars, final int[] color,
                       final TIntObjectHashMap<TaskMerger> domainByColor) {
        final TaskVariable[] var = new TaskVariable[k + 1];
        TaskMerger dtmp;
        TaskVariable vtmp;
        for (int i = 0; i < nbStoredMultipleVars; i++) {
            final int col = color[i];
            if (col != -1) {
                final TaskVariable v = (TaskVariable) model.getStoredMultipleVar(i);
                if (var[col] == null) {
                    dtmp = domainByColor.get(col);
                    vtmp = new TaskVariable(v.getName(), dtmp.start, dtmp.duration, dtmp.end);
                    vtmp.addOptions(vtmp.getOptions());
                    var[col] = vtmp;
                    add(var[col]);
                }
                replaceBy(v, var[col]);
                delete(v);
            }
        }
    }
}
