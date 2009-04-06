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
package parser.chocogen;

import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.integer.extension.BinRelation;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.cp.solver.constraints.integer.bool.sat.ClauseStore;
import choco.Choco;
import parser.absconparseur.components.*;
import parser.absconparseur.tools.InstanceParser;

import java.util.*;

/*
 * User:    hcambaza
 * Date:    15 avr. 2008
 */
public class ChocoFactory {

	protected static float ratioHole = 0.7f;

	protected InstanceParser parser;

	//status of restart mode (false if extension problems)
	//true otherwise depending the initial propagation time
	protected Boolean restartMode = null;

    //The model we are building according to the parser
    protected Model m;


	protected RelationFactory relfactory;

    //can be redondant variables if they are linked by
	//an equality constraint
	protected IntegerVariable[] vars;

	protected List cstrs;

	public ChocoFactory(InstanceParser parser, Model m) {
		this.m = m;
		this.parser = parser;
		restartMode = null;
	}

	public Model getM() {
		return m;
	}


//	/**
//	 * @return Restart Search loop
//	 */
//	public SearchLoopWithRestart getSearchLoopWithRestart(CPSolver s) {
//		s.getSearchStrategy().limits.add(new FailLimit(s.getSearchStrategy(), Integer.MAX_VALUE));
//		return new SearchLoopWithRestart(s.getSearchStrategy(),
//				new SearchLoopWithRestart.RestartStrategy() {
//					int failLimit = Math.min(Math.max(s.getNbIntVars(), 200), 400);
//					double mult = 1.4d;
//
//					public boolean shouldRestart(AbstractGlobalSearchStrategy search) {
//						boolean shouldRestart = ((search.limits.get(2)).getNb() >= failLimit);
//						if (shouldRestart) {
//							failLimit *= mult;
//						}
//						return shouldRestart;
//					}
//				});
//	}



//******************************************************************//
//***************** Factories **************************************//
//******************************************************************//


	public void createVariables() {
		VariableFactory factory = new VariableFactory(m, parser);
		vars = new IntegerVariable[factory.getNbvar()];
		PVariable[] pvars = parser.getVariables();
		for (int i = 0; i < pvars.length; i++) {
			vars[i] = factory.makeVariable(pvars[i]);
		}
		m.addVariables(vars);
	}


	public void createRelations() {
		relfactory = new RelationFactory(m, parser);
		Iterator<PRelation> maprel = parser.getMapOfRelations().values().iterator();
		for (; maprel.hasNext();) {
			PRelation prel = maprel.next();
			if (relfactory.isSatDecomposable(prel)) {
               relfactory.makeClausesEncoding(prel);
            } else {
                if (prel.getArity() == 2) {
                    BinRelation brel = relfactory.makeBinRelation(prel);
                    prel.setBrel(brel);
                } else {
                    //DFA lrel = relfactory.makeDFA(prel);
                    //prel.setDfa(lrel);
                    LargeRelation lrel = relfactory.makeLargeRelation(prel);
                    prel.setLrel(lrel);
                }
            }
        }
	}

    public void createConstraints(boolean forceExp){
        ExtConstraintFactory extFact = new ExtConstraintFactory(m, parser);
        extFact.preAnalyse(relfactory);

        Map pcstr = parser.getMapOfConstraints();
		Iterator it = pcstr.keySet().iterator();
        cstrs = new ArrayList();
        String options = (forceExp?"cp:decomp":"");
        while (it.hasNext()) {
			PConstraint pc = (PConstraint) pcstr.get(it.next());
			makeModelConstraint(pc, options);
		}

    }

    public void makeModelConstraint(PConstraint pc, String options) {
		Constraint[] c = null;
        if (pc instanceof PExtensionConstraint) {
			c = ExtConstraintFactory.makeExtConstraint((PExtensionConstraint) pc);
		} else
		if (pc instanceof PIntensionConstraint) {
			ModelConstraintFactory mcf = new ModelConstraintFactory (m, parser);
			c = mcf.makeIntensionConstraint((PIntensionConstraint) pc);
		}else
		if (pc instanceof PGlobalConstraint) {
			GloConstraintFactory gf = new GloConstraintFactory(m, parser);
			c = gf.makeGlobalConstraint((PGlobalConstraint) pc);
		}
        if (c != null)
            m.addConstraints(options, c);
	}

}
