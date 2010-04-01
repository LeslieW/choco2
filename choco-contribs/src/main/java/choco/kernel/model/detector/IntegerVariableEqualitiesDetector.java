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

import choco.Choco;
import choco.cp.common.util.detector.DomainMerger;
import choco.cp.model.CPModel;
import choco.kernel.common.util.objects.BooleanSparseMatrix;
import choco.kernel.common.util.objects.ISparseMatrix;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;
import gnu.trove.TIntObjectHashMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * A class detector to detect equalities between IntegerVariable within a model.
 */
public final class IntegerVariableEqualitiesDetector extends AbstractDetector{


    protected IntegerVariableEqualitiesDetector(final CPModel model) {
        super(model);
    }

    /**
     * Apply the detection defined within the detector.
     */
    @Override
    public void apply() {
        if(LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("IntegerVariable equalities detection :");
        }
        final ISparseMatrix matrix = analyze();
        if(matrix.getNbElement()> 0){
            change(matrix);
        }
    }

    /**
     * Analyze the current model and record equality constraints over {@link IntegerVariable} or
     * {@link choco.kernel.model.variables.integer.IntegerConstantVariable}.
     * @return
     */
    private ISparseMatrix analyze(){
        final int nbIntVars = model.getNbIntVars();
        final ISparseMatrix matrix = new BooleanSparseMatrix(nbIntVars);
        final Iterator<Constraint> iteq = model.getConstraintByType(ConstraintType.EQ);
        Constraint c;
        // Run over equalities constraints, and create edges
        while(iteq.hasNext()){
            c = iteq.next();
            final Variable v1 = c.getVariables()[0];
            final Variable v2 = c.getVariables()[1];
            if(v1.getVariableType()== VariableType.INTEGER
                    && v2.getVariableType()== VariableType.INTEGER){
            	matrix.add(v1.getHook(), v2.getHook());
                delete(c);
            }
        }
        return matrix;
    }

    private void change(final ISparseMatrix matrix){
        final int nbIntVars = model.getNbIntVars();

        matrix.prepare();

        final int[] color = new int[nbIntVars];
        Arrays.fill(color, -1);
        final TIntObjectHashMap<DomainMerger> domainByColor = new TIntObjectHashMap<DomainMerger>();

        int nbDiffObject = detect(matrix, nbIntVars,color, domainByColor);
        apply(nbDiffObject, nbIntVars, color, domainByColor);
    }

    private int detect(final ISparseMatrix matrix, final int nbIntVars, final int[] color,
                      final TIntObjectHashMap<DomainMerger> domainByColor){
        int nb = -1;
        DomainMerger dtmp = new DomainMerger();
        final Iterator<Long> it = matrix.iterator();
        while(it.hasNext()){
            final long v = it.next();
            final int i = (int)(v / nbIntVars);
            final int j = (int)(v % nbIntVars);

            if (color[i]==-1){
                nb++;
                color[i]=nb;
                domainByColor.put(nb, new DomainMerger(model.getIntVar(i)));
            }
            final DomainMerger d = domainByColor.get(color[i]);
            //backup
            dtmp.copy(d);
            if(d.intersection(model.getIntVar(j))){
                color[j] = color[i];
                domainByColor.put(color[i], d);
            }else{
                add(Choco.eq(model.getIntVar(i), model.getIntVar(j)));
                //rollback
                d.copy(dtmp);
                if (color[j]==-1){
                    nb++;
                    color[j]=nb;
                    domainByColor.put(nb, new DomainMerger(model.getIntVar(j)));
                }
            }
        }
        return nb;
    }
    
    private void apply(final int k, final int nbIntVars, final int[] color,
                       final TIntObjectHashMap<DomainMerger> domainByColor){
        IntegerVariable vtmp;
        DomainMerger dtmp;
        final IntegerVariable[] var = new IntegerVariable[k+1];
        for(int i = 0; i < nbIntVars; i++){
            final int col = color[i];
            if(col !=-1){
                final IntegerVariable v = model.getIntVar(i);
                if(var[col] == null){
                    dtmp = domainByColor.get(col);
                    if(dtmp.values != null){
                        vtmp = new IntegerVariable(v.getName(), dtmp.values);
                    }else{
                        vtmp = new IntegerVariable(v.getName(), dtmp.low, dtmp.upp);
                    }
                    vtmp.addOptions(dtmp.optionsSet);
                    var[col] = vtmp;
                    add(vtmp);
                }
                replaceBy(v, var[col]);
                delete(v);
            }
        }
    }
}
