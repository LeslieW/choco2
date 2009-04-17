/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.shaker;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.shaker.tools.factory.CPModelFactory;
import choco.shaker.tools.factory.VariableFactory;
import org.junit.*;

import java.util.Random;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ConstraintTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    Model m;
    Constraint c;
    Solver s;
    static Random random;
    int seed;
    boolean print=false;

    @Before
    public void before(){
        m = new CPModel();
        s = new CPSolver();
        c = null;
        random = new Random();
    }

    @After
    public void after(){
        m = null;
        s = null;
        c = null;
    }


    @Test
    public void testConstraint1(){
        IntegerVariable[] v = new IntegerVariable[5];
        v[0] = Choco.makeIntVar("v1", -8, 1,"cp:enum");
        v[1] = Choco.makeIntVar("v2", -4, 1,"cp:enum");
        v[2] = Choco.makeIntVar("v3", 2, 4,"cp:btree");
        v[3] = Choco.makeBooleanVar("v4");
        v[4] = Choco.constant("v5", 7);

        m.addConstraint(Choco.allDifferent(v));
        s.read(m);
        s.worldPush();
        try {
            s.propagate();
            s.getVar(v[1]).updateSup(0, -1);
            s.propagate();
            s.getVar(v[2]).updateInf(3, -1);
            s.propagate();
            s.getVar(v[1]).instantiate(0,-1);
            s.propagate();
        } catch (ContradictionException e) {
            Assert.fail("erreur");
        }
    }


    @Test
    @Ignore
    public void test1() {

//        print = true;
        for (int i = 0; i < 1000; i++) {
            seed = i;
            Random r = new Random(seed);
            CPModelFactory mf = new CPModelFactory();

            mf.bounds(10);
            mf.depth(0);
            mf.uses(VariableFactory.V.ENUMVAR, VariableFactory.V.BOUNDVAR,
                    VariableFactory.V.BLISTVAR, VariableFactory.V.BTREEVAR,
                    VariableFactory.V.LINKVAR, VariableFactory.V.BOOLVAR,
                    VariableFactory.V.CST);
//            mf.uses(ConstraintFactory.C.ALLDIFFERENT);
            m = mf.model(r);
            c = m.getConstraint(0);
//            IntIterator it = m.getIntConstraintIterator();
//            c = (Constraint)it.next();
            checker(r);
        }
    }

    private void checker(Random r) {
        s = new CPSolver();
        s.read(m);
        AbstractIntSConstraint sc = (AbstractIntSConstraint)s.getCstr(c);
        int[] values = pickValues(sc, r);
        boolean satisfied = sc.isSatisfied(values);
        // If the constraint is satisfied with this tuple,
        // we ensure that CHOCO find this solution
        if(satisfied){
            while(!sc.isCompletelyInstantiated()) {
                try {
                    int v = r.nextInt(sc.getNbVars());
                    generateEvent((IntDomainVar)sc.getVar(v), values[v], r);
                    s.propagate();
                    stillInstanciable(sc, values);
                } catch (ContradictionException e) {
                    Assert.fail("(seed:" + seed + ") unexpected behaviour in propagation...\n"+e.getMessage()+"\n" + s.pretty());
                }
            }
            Assert.assertEquals("(seed:"+seed+") not satisfied", true, sc.isSatisfied());
        }else
        // We ensure CHOCO goes to a fail
        {
            int loop = 10;
            while(loop>0) {
                s.worldPush();
                int subloop = sc.getNbVarNotInst()*10;
                while(subloop>0){
                    try {
                        int v = r.nextInt(sc.getNbVars());
                        generateEvent((IntDomainVar)sc.getVar(v), values[v], r);
                        s.propagate();
                        stillInstanciable(sc, values);
                        if(sc.isCompletelyInstantiated()){
                            if(sc.isSatisfied()){
                                    Assert.fail("(seed:" + seed + ") satisfied...\n" + s.pretty());
                            }
                        }
                        subloop--;
                    } catch (ContradictionException e) {
                        //can fail, it is an expected behaviour
                            subloop = 0;
                    }
                }
                loop--;
                s.worldPop();
            }
        }
    }

    private void stillInstanciable(AbstractIntSConstraint sc, int[] values) throws ContradictionException{
        for (int i = 0; i < sc.getNbVars(); i++) {
            IntDomainVar var = sc.getIntVar(i);
            if(!var.canBeInstantiatedTo(values[i]))throw new ContradictionException("stillInstanciable", -1);
        }
    }


    private void generateEvent(IntDomainVar var, int value, Random r) throws ContradictionException {
        int event = r.nextInt(4);
        int v;
        switch (event) {
            case 0: // INSTANTIATION
                if(print)LOGGER.info(var.getName()+" = "+value);
                var.instantiate(value, -1);
                break;
            case 1: // LOWER BOUND
                if(value > var.getInf()){
                    v = value - var.getInf();
                    v = value - r.nextInt(v);
                    if(print)LOGGER.info(var.getName()+" >= "+v);
                    var.updateInf(v, -1);
                }
                break;
            case 2: // UPPER BOUND
                if(value < var.getSup()){
                    v = var.getSup() - value;
                    v = value + r.nextInt(v);
                    if(print)LOGGER.info(var.getName()+" <= "+v);
                    var.updateSup(v, -1);
                }
                break;
            case 3: // REMOVAL
                if (var.getDomainSize() > 1) {
                    v = getRandomValue(var, r);
                    while (v == value) {
                        v = getRandomValue(var, r);
                    }
                    if(print)LOGGER.info(var.getName()+" != "+v);
                    var.removeVal(v, -1);
                }
                break;
        }
    }

    /**
     * Randomly create a tuple of value
     * @param sc
     * @param r
     * @return
     */
    private int[] pickValues(AbstractIntSConstraint sc, Random r) {
        int[] tuple = new int[sc.getNbVars()];
        for (int i = 0; i < tuple.length; i++) {
            IntDomainVar var = sc.getIntVar(i);
            tuple[i] = getRandomValue(var, r);
            if(print)LOGGER.info(var.getName()+":"+tuple[i]);
        }
        return tuple;
    }

    /**
     * Get a random value in the domain
     * BEWARE : do not use Domain#getRandomDomainValue() because can not
     * be replayed.
     * @param var
     * @param r
     * @return
     */
    private int getRandomValue(IntDomainVar var, Random r) {
        int v = var.getInf() + r.nextInt(var.getSup() - var.getInf() + 1);
        while (!var.canBeInstantiatedTo(v)) {
            v = var.getInf() + r.nextInt(var.getSup() - var.getInf() + 1);
        }
        return v;
    }



}
