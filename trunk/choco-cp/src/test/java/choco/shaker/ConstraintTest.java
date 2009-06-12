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
import static choco.Choco.distanceEQ;
import static choco.Choco.reifiedIntConstraint;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.DeterministicIndicedList;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.shaker.tools.factory.CPModelFactory;
import choco.shaker.tools.factory.MetaConstraintFactory;
import choco.shaker.tools.factory.OperatorFactory;
import choco.shaker.tools.factory.VariableFactory;
import org.junit.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
    Solver s;
    static Random random;
    int seed;
    boolean print=false;

    @Before
    public void before(){
        m = new CPModel();
        s = new CPSolver();
        random = new Random();
    }

    @After
    public void after(){
        m = null;
        s = null;
    }


    @Test
    @Ignore
    public void testConstraint() {

//        print = true;
        for (int i = 24; i < 1000; i++) {
            seed = i;
            System.out.println("seed:"+seed);
            Random r = new Random(seed);
            CPModelFactory mf = new CPModelFactory();

            mf.limits(10);
            mf.depth(0);
            mf.uses(VariableFactory.V.ENUMVAR, VariableFactory.V.BOUNDVAR,
                    VariableFactory.V.BLISTVAR, VariableFactory.V.BTREEVAR,
                    VariableFactory.V.LINKVAR, VariableFactory.V.BOOLVAR,
                    VariableFactory.V.CST);
            mf.uses(MetaConstraintFactory.MC.NONE);
            mf.uses(OperatorFactory.O.NONE);
            m = mf.model(r);
            try{
            checker(r);
            }catch (UnsupportedOperationException e){
                LOGGER.severe(MessageFormat.format("seed:{0} : {1}", seed, e.getMessage()));
            }
        }
    }


    @Test
    public void test1(){
        m  = new CPModel();
        IntegerVariable[] vars = new IntegerVariable[4];
        vars[0] = Choco.makeBooleanVar("b1");
        vars[1] = Choco.makeBooleanVar("b2");
        vars[2] = Choco.makeIntVar("v3", 4,6, "cp:enum");
        vars[3] = Choco.makeIntVar("v4", 9,9, "cp:enum");

        m.addConstraint(reifiedIntConstraint(vars[0], distanceEQ(vars[1], vars[2], vars[3], -2)));

        checker(new Random(37));
    }


    /**
     * Generate s tuple and check if this tuple satifies or not the problem.
     * Then, shake and check CHOCO to ensure it leads to the correct conclusion (solution or not).
     * @param r
     */
    private void checker(Random r) {
        s = new CPSolver();
        s.read(m);

        DeterministicIndicedList<IntDomainVar> vars = new DeterministicIndicedList<IntDomainVar>(IntDomainVar.class, s.getNbIntVars());
        ArrayList<AbstractIntSConstraint> cstrs = new ArrayList<AbstractIntSConstraint>(s.getNbIntConstraints());
        init(vars);

        // Simule an instanciation of eeach variable
        int[] values = pickValues(vars, r);
        boolean satisfied = satisifies(vars, cstrs, values);

        // If the constraints are satisfied with this tuple,
        // we ensure that CHOCO find this solution
        if(satisfied){
            goesToSolution(vars, cstrs, values, r);
        }else
        // We ensure CHOCO goes to a fail
        {
            goesToFail(vars, cstrs, values, r);
        }
    }



    /**
     * Generate random events that lead to instanciation based on values array
     * and propagate them to ensure CHOCO finds a solution
     * @param vars
     * @param cstrs
     * @param values
     * @param r
     */
    private void goesToSolution(DeterministicIndicedList<IntDomainVar> vars, ArrayList<AbstractIntSConstraint> cstrs,
                         int[] values, Random r) {
        while(!fullyInstanciated(vars)) {
                try {
                    AbstractIntSConstraint sc = cstrs.get(r.nextInt(cstrs.size()));
                    int v = r.nextInt(sc.getNbVars());
                    generateEvent((IntDomainVar)sc.getVar(v), values[vars.get((IntDomainVar)sc.getVar(v))], r);
                    s.propagate();
                    stillInstanciable(vars, values);
                } catch (ContradictionException e) {
                    Assert.fail("(seed:" + seed + ") unexpected behaviour in propagation...\n"+e.getMessage()+"\n" + s.pretty());
                }
            }
            Assert.assertEquals("(seed:"+seed+") not satisfied", true, s.checkSolution(false));
    }

    /**
     * Generate random events taht lead to instanciation based on values array
     * and propagate them to ensure CHOCO does not find a solution
     * @param vars
     * @param cstrs
     * @param values
     * @param r
     */
    private void goesToFail(DeterministicIndicedList<IntDomainVar> vars, ArrayList<AbstractIntSConstraint> cstrs,
                         int[] values, Random r) {
        int loop = 10;
            while(loop>0) {
                s.worldPush();
                int subloop = vars.size()*10;
                while(subloop>0){
                    try {
                        AbstractIntSConstraint sc = cstrs.get(r.nextInt(cstrs.size()));
                        int v = r.nextInt(sc.getNbVars());
                        generateEvent((IntDomainVar)sc.getVar(v), values[vars.get((IntDomainVar)sc.getVar(v))], r);
                        s.propagate();
                        stillInstanciable(vars, values);
                        if(fullyInstanciated(vars)){
                            if(s.checkSolution(false)){
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


    /**
     * Initialise structure
     * @param vars
     * @return
     */
    private void init(DeterministicIndicedList<IntDomainVar> vars){
        Iterator<SConstraint> it = s.getIntConstraintIterator();
        while(it.hasNext()){
            SConstraint c = it.next();
            for(int v = 0; v < c.getNbVars(); v++){
                IntDomainVar var = (IntDomainVar)c.getVar(v);
                vars.add(var);
            }
        }
    }

    /**
     *  Determine whether the tuple generated staisfies the constraints
     * @param vars
     * @param cstrs
     * @param values
     * @return
     */
    private boolean satisifies(DeterministicIndicedList<IntDomainVar> vars, ArrayList<AbstractIntSConstraint> cstrs,
                           int[] values){
        // Check if constraints are satisfied
        Iterator<SConstraint> it = s.getIntConstraintIterator();
        boolean satisfied = true;
        while(it.hasNext()){
            AbstractIntSConstraint c = (AbstractIntSConstraint)it.next();
            cstrs.add(c);
            int[] tuple = new int[c.getNbVars()];
            for(int i = 0; i < c.getNbVars(); i++){
                tuple[i] = values[vars.get((IntDomainVar)c.getVar(i))];
            }
            satisfied &= c.isSatisfied(tuple);
        }
        return satisfied;
    }


    /**
     * Check wether at least one variable can be instanciated to the decided value anymore
     * @param vars
     * @param values
     * @throws ContradictionException
     */
    private void stillInstanciable(DeterministicIndicedList<IntDomainVar> vars, int[] values) throws ContradictionException{
        Iterator<IntDomainVar> itv = vars.iterator();
        IntDomainVar v;
        while(itv.hasNext()){
            v = itv.next();
            if(!v.canBeInstantiatedTo(values[vars.get(v)])){
                throw new ContradictionException("stillInstanciable", -1);
            }
        }
    }


    /**
     * Check wether every variables of the solver are instanciated
     * @param vars
     * @return
     */
    private boolean fullyInstanciated(DeterministicIndicedList<IntDomainVar> vars){
        Iterator<IntDomainVar> itv = vars.iterator();
        while(itv.hasNext()){
            if(!itv.next().isInstantiated()){
                return false;
            }
        }
        return true;
    }

    /**
     * Generate an event based on the variables of the solver
     * @param var
     * @param value
     * @param r
     * @throws ContradictionException
     */
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
     * @param vars
     * @param r
     * @return
     */
    private int[] pickValues(DeterministicIndicedList<IntDomainVar> vars, Random r) {
        int[] tuple = new int[vars.size()];
        for (int i = 0; i < tuple.length; i++) {
            IntDomainVar var = vars.get(i);
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
