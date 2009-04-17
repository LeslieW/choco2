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
package choco.cp.model.managers.constraints.integer;

import choco.Choco;
import static choco.Choco.makeLargeRelation;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.constraints.integer.extension.*;
import choco.cp.solver.variables.integer.BitSetIntDomain;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.extension.*;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;
import java.util.List;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 6, 2008
 * Since : Choco 2.0.0
 *
 */
public class TableManager extends IntConstraintManager {

    /**
     * @param solver
     * @param vars
     * @param parameters : a List<Object> which is intended to either a single element i.e the relation or
     *                   two elements : - a boolean indicating if the consistency relation is feasible or infeasible
     *                   - a list<int[]> or a int[][]
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, Variable[] vars, Object parameters, HashSet<String> options) {
        Object[] ps = (Object[]) parameters;
        IntegerVariable[] vars2 = new IntegerVariable[vars.length];
        System.arraycopy(vars,0,vars2,0,vars.length);
        IntDomainVar[] vs = solver.getVar(vars2);
        if (vs.length == 2
                && !options.contains("cp:ac2008") 
                && !(ps[1] instanceof LargeRelation)) { //ac binaire
            return buildBinaryTable(vs, parameters, options);
        } else { //ac naire
            return buildNaryTable(vs, parameters, options);

        }
    }

    //*********************************************//
    //*************** Binairy AC ******************//    
    //*********************************************//

    public SConstraint buildBinaryTable(IntDomainVar[] vs, Object parameters, HashSet<String> options) {
        Object[] ps = (Object[]) parameters;
        IntDomainVar v1 = vs[0];
        IntDomainVar v2 = vs[1];
        if (ps[1] instanceof BinRelation) {
            return buildBinaryTable(v1, v2, (BinRelation) ps[1], options);
        } else {
            boolean feas = (Boolean) ps[0];
            BinRelation binR = makePairAC(v1, v2, ps[1], feas, options);
            return buildBinaryTable(v1, v2, binR, options);
        }
    }

    public SConstraint buildBinaryTable(IntDomainVar v1, IntDomainVar v2, BinRelation binR, HashSet<String> options) {
        if (options.contains("cp:fc")) {
            return new FCBinSConstraint(v1,v2,binR);
        } else if (options.contains("cp:ac3")) {
            return new AC3BinSConstraint(v1, v2, binR);
        } else if (options.contains("cp:ac32")) {
            return new AC3rmBinSConstraint(v1, v2, binR);
        } else if (options.contains("cp:ac322")) {
            return new AC3rmBitBinSConstraint(v1, v2, (CouplesBitSetTable) binR);
        } else if (options.contains("cp:ac2001")) {
            return new AC2001BinSConstraint(v1, v2, binR);
        } else { //default choice
            if (binR instanceof CouplesBitSetTable && (v1.getDomain() instanceof BitSetIntDomain) && (v2.getDomain() instanceof BitSetIntDomain)) {
                return new AC3rmBitBinSConstraint(v1, v2, (CouplesBitSetTable) binR);
            } else {
                return new AC3rmBinSConstraint(v1, v2, binR);
            }
        }
    }

    private BinRelation makePairAC(IntDomainVar x, IntDomainVar y, Object mat, boolean feas, HashSet<String> options) {
        int[] min = new int[]{x.getInf(), y.getInf()};
        int[] max = new int[]{x.getSup(), y.getSup()};
        if (mat instanceof List)
            return Choco.makeBinRelation(min, max, (List<int[]>) mat, feas, options.contains("cp:ac322"));
        else if (mat instanceof boolean[][])
            return Choco.makeBinRelation(min, max, (boolean[][]) mat, feas, options.contains("cp:ac322"));
        else throw new ModelException("a relation should be given a List<int[]> or boolean[][]");
    }

    //*********************************************//
    //*************** Nary AC *********************//    
    //*********************************************//

    public SConstraint buildNaryTable(IntDomainVar[] vs, Object parameters, HashSet<String> options) {
        Object[] ps = (Object[]) parameters;
        if (ps[1] instanceof LargeRelation) {
            return buildNaryTable(vs, (LargeRelation) ps[1], options);
        } else {
            boolean feas = (Boolean) ps[0];
            LargeRelation rela = makeTupleAC(vs,(List<int[]>) ps[1],feas,options);
            return buildNaryTable(vs, rela, options);
        }
    }

    public SConstraint buildNaryTable(IntDomainVar[] vs, LargeRelation rela, HashSet<String> options) {
        if (options.contains("cp:fc")) {
            return new CspLargeSConstraint(vs, rela);
        } else {
            if (rela instanceof IterLargeRelation) {
                if (options.contains("cp:ac32")) {
                    return new GAC3rmPositiveLargeConstraint(vs, (IterTuplesTable) rela);
                } else if (options.contains("cp:ac2001")) {
                    return new GAC2001PositiveLargeConstraint(vs, (IterTuplesTable) rela);
                } else {
                    return new GAC3rmPositiveLargeConstraint(vs, (IterTuplesTable) rela);
                }
            } else {
                if (options.contains("cp:ac32")) {
                    return new GAC3rmLargeConstraint(vs, rela);
                } else if (options.contains("cp:ac2001")) {
                    return new GAC2001LargeSConstraint(vs, rela);
                } else if (options.contains("cp:ac2008") && rela instanceof TuplesList) {
                    return new GACstrPositiveLargeSConstraint(vs, rela);
                } else {
                    return new GAC3rmLargeConstraint(vs, rela); 
                }
            }
        }
    }


    /**
     * Create a constraint to enforce GAC on a list of feasible or infeasible tuples
     *
     * @param vs
     * @param tuples the list of tuples
     * @param feas   specify if the tuples are feasible or infeasible tuples
     * @return
     */
    private LargeRelation makeTupleAC(IntDomainVar[] vs, List<int[]> tuples, boolean feas, HashSet<String> options) {
        int[] min = new int[vs.length];
        int[] max = new int[vs.length];
        for (int i = 0; i < vs.length; i++) {
            min[i] = vs[i].getInf();
            max[i] = vs[i].getSup();
        }
        if (options.contains("cp:ac2008")) {
            return makeLargeRelation(min, max, tuples, feas, 2);
        } else if (options.contains("cp:fc")) {
            return makeLargeRelation(min, max, tuples, feas, 1);
        } else {
            return makeLargeRelation(min, max, tuples, feas);            
        }
    }

    //*********************************************//
    //*************** Favorite Variables **********//    
    //*********************************************//


    public int[] getFavoriteDomains(HashSet<String> options) {
        if (options.contains("cp:ac322")) {
            return new int[]{IntDomainVar.BITSET};
        } else {
            return new int[]{
                             IntDomainVar.BITSET,
                             IntDomainVar.BIPARTITELIST, //<hca>: todo reverse once blist done
                             IntDomainVar.BOUNDS};
        }
    }
}