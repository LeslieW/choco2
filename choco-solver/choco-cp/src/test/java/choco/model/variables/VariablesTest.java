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
package choco.model.variables;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import static junit.framework.Assert.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.text.MessageFormat.format;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Arnaud Malapert
 *
 */
public class VariablesTest {
    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	private CPModel model;

	private void test(Variable toRemove,int nbVars) {
		LOGGER.info(model.pretty()+"\n");
		model.removeVariable(toRemove);
		test(0, nbVars);
		
	}
	
	private void test(Constraint toRemove, int nbConstraints, int nbConstants, int nbVars) {
		LOGGER.info(model.pretty()+"\n");
		model.removeConstraint(toRemove);
		test(nbConstraints, nbVars);
		assertEquals("nb constants",nbConstants, model.getNbConstantVars());
		
	}
	
	private void test(int nbConstraints, int nbVars) {
		LOGGER.info(model.pretty()+"\n");
		assertEquals("nb remaining constraints",nbConstraints, model.getNbConstraints());
		assertEquals("nb remaining variables ",nbVars, model.getNbTotVars());
	}

	@Before
	public void initialize() {
		model=new CPModel();
	}

	@Test
	public void testRemoveInt() {
		IntegerVariable[] v=makeIntVarArray("v",2, 0, 2);
        model.addVariables("cp:bound", v);
        model.addConstraint(neq(v[0], v[1]));
		test(v[0],0);
	}

	@Test
	public void testRemoveSet() {
		SetVariable[] v=makeSetVarArray("v",2, 0, 2);
        model.addVariables("cp:bound", v);
        model.addConstraint(neq(v[0], v[1]));
		test(v[0],0);
	}

	@Test
	public void testEnumVarWithRedundantValues() {
		int[] values = new int[] {3,5,7,3,5,9,10, 10};
		int[] res = new int[] {3,5,7,9,10};
		Assert.assertArrayEquals("sort and remove redundat values ", res, ArrayUtils.getNonRedundantSortedValues(values));
		final CPModel m = new CPModel();
		final IntegerVariable v = Choco.makeIntVar("v", values);
		m.addVariable(v);
		final CPSolver s = new CPSolver();
		s.read(m);
		IntDomainVar var = s.getVar(v);
		try {
			assertEquals("Enum Domain size",5, var.getDomainSize());
			var.updateInf(6, IntVarEvent.NOCAUSE);
			assertEquals("Enum Domain size",3, var.getDomainSize());
			var.updateSup(8, IntVarEvent.NOCAUSE);
			assertEquals("Enum Domain size",1, var.getDomainSize());
			assertTrue("Enum Inst. Domain", var.isInstantiatedTo(7));
		} catch (ContradictionException e) {
			Assert.fail("inconsitent integer enum domain");
		}
		
	}
	
	@Test
	public void testRemoveReal() {
		SetVariable[] v=makeSetVarArray("v",2, 0, 2);
        model.addVariables("cp:bound", v);
        model.addConstraint(Choco.neq(v[0], v[1]));
		test(v[0],0);
	}

	private final IntegerVariable[] bools = Choco.makeBooleanVarArray("b",3);
	
	private final IntegerVariable one = Choco.constant(1);
	
	private final Constraint[] cstr = { Choco.eq(bools[0], one), Choco.eq(bools[1], one),Choco.allDifferent(bools)}; 
	
	@Test
	public void testRemoveConstraint1() {
		model.addConstraints(cstr[0],cstr[1]);
		test(2,2);
		test(cstr[1], 1, 1, 1); //quick fix : forbid to remove constants
		test(cstr[0], 0, 1, 0); 
	}
	
	@Test
	public void testRemoveConstraint2() {
		model.addConstraints(cstr);
		test(3,3);
		test(cstr[2], 2, 1, 2);
		test(cstr[1], 1, 1, 1); //quick fix : forbid to remove constants
		test(cstr[0], 0, 1, 0);
	}

	@Test
	public void testDecisionOptions(){
		Model m = new CPModel();
		SetVariable s1 = makeSetVar("set1", 0, 10);
		SetVariable s2 = makeSetVar("set2", 5, 20);
		s1.getCard().addOption("cp:no_decision");
		m.addVariables(s1, s2,constant(2), constant("test", 1));
		CPSolver solver = new CPSolver();
		solver.read(m);
		List<IntDomainVar> l =solver.getIntDecisionVars();
		LOGGER.info(format("{0}", l));
		assertEquals("only one int decision var",1,l.size());
		assertEquals("number of integer constants",2,solver.getNbIntConstants());
		assertTrue("check decision var",l.contains(solver.getVar(s2.getCard())));
	}
	
	private void checkOptions(Set<String> options, int nb, int length) {
		assertEquals("Nb Options", nb, options.size());
		for (String str : options) {
			assertEquals("Option lenght: |"+str+"|", length, str.length());
		}
	}
	@Test
	public void testOptions() {
		final IntegerVariable v = Choco.makeBooleanVar("b", "   foo","   bar","foo   ","fou");
		checkOptions(v.getOptions(), 3, 3);
		final SetVariable s = Choco.makeSetVar("s", 0, 18, "foo","    bar","   bar","    foo    ");
		checkOptions(s.getOptions(), 2, 3);
		
	}

    @Test
    public void testMultipleModel(){
        Model m1 = new CPModel();
        Model m2 = new CPModel();
        IntegerVariable v1 = Choco.makeIntVar("v1", 0,10);
        IntegerVariable v2 = Choco.makeIntVar("v2", 0,10);
        Constraint c1 = Choco.eq(v1,v2);
        Constraint c2 = Choco.neq(v1,v2);

        m1.addConstraint(c1);
        m2.addConstraint(c2);

        Assert.assertEquals("Nb constraint for v1", 1, v1.getNbConstraint(m1));
        Assert.assertEquals("Nb constraint for v1", 1, v1.getNbConstraint(m2));

        Iterator it = v1.getConstraintIterator(m1);
       assertTrue("iter has next", it.hasNext());
       assertEquals("iter next ", c1, it.next());
       assertFalse("iter next ", it.hasNext());

        it = v1.getConstraintIterator(m2);
       assertTrue("iter has next", it.hasNext());
       assertEquals("iter next ", c2, it.next());
       assertFalse("iter next ", it.hasNext());

        m1.removeConstraint(c1);

       assertEquals("", 0, v1.getNbConstraint(m1));
       assertEquals("", 1, v1.getNbConstraint(m2));

        it = v1.getConstraintIterator(m1);
       assertFalse("iter next ", it.hasNext());

        it = v1.getConstraintIterator(m2);
       assertTrue("iter has next", it.hasNext());
       assertEquals("iter next ", c2, it.next());
       assertFalse("iter next ", it.hasNext());


    }
}