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

import choco.Choco;
import static choco.Choco.*;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.automaton.DFA;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.constraints.integer.extension.TuplesTable;
import parser.absconparseur.components.PConstraint;
import parser.absconparseur.components.PExtensionConstraint;
import parser.absconparseur.components.PRelation;
import parser.absconparseur.components.PVariable;
import parser.absconparseur.tools.InstanceParser;

import java.util.Iterator;
import java.util.List;


/**
 *  The factory to create Extensionnal constraints
 **/
public class ExtConstraintFactory extends ObjectFactory {

    public ExtConstraintFactory(Model m, InstanceParser parser) {
		super(m, parser);
    }

	// add the neq into the difference graph
	public void preAnalyse(RelationFactory relfact) {
		Iterator<PConstraint> it = parser.getMapOfConstraints().values().iterator();
		while (it.hasNext()) {
			PConstraint pec = it.next();
			if (pec instanceof PExtensionConstraint) {
				if (pec.getArity() == 2) {
					relfact.detectIntensionConstraint((PExtensionConstraint)pec);
				}
			}
		}
	}

	public static Constraint[] makeExtConstraint(PExtensionConstraint pec) {
		Constraint[] extct = new Constraint[1];
		PRelation prel = pec.getRelation();
		if (prel.getNbTuples() == 0) {
			if (prel.getSemantics().equals("supports"))
				extct[0] = FALSE;
			else extct[0] = TRUE;
			return extct;
		} else {
            if (prel.getSatEncoding() != null) {
                PVariable[] sc = pec.getScope();
                List<XmlClause> lcls = prel.getSatEncoding();
                extct = new Constraint[lcls.size()];
                for (int i = 0; i < lcls.size(); i++) {
                    XmlClause xcl = lcls.get(i);
                    IntegerVariable[] pos = new IntegerVariable[xcl.poslits.length];
                    IntegerVariable[] neg = new IntegerVariable[xcl.neglits.length];
                    for (int k = 0; k < pos.length; k++) {
                        pos[k] = sc[xcl.poslits[k]].getChocovar();
                    }
                    for (int k = 0; k < neg.length; k++) {
                        neg[k] = sc[xcl.neglits[k]].getChocovar();
                    }
                    extct[i] = Choco.clause(pos,neg);
                }
                return extct;
            } else {
                if (pec.getArity() == 2) {
                    PVariable[] sc = pec.getScope();
                    ModelConstraintFactory.ConstExp ctexp = pec.getIntensionCts();
                    if (ctexp != null) {
                        switch (ctexp) {
                            case eq:
                                extct[0] = eq(sc[0].getChocovar(), sc[1].getChocovar());
                                break;
                            case ne:
                                extct[0] = neq(sc[0].getChocovar(), sc[1].getChocovar());
                                break;
                            default:
                                return null;
                        }
                    } else {
                        extct[0] = relationPairAC(sc[0].getChocovar(), sc[1].getChocovar(), prel.getBrel());
                    }
                } else {
                    PVariable[] sc = pec.getScope();
                    IntegerVariable[] intvars = new IntegerVariable[sc.length];
                    for (int i = 0; i < intvars.length; i++) {
                        intvars[i] = sc[i].getChocovar();
                    }
                    LargeRelation lrel = prel.getLrel();
                    DFA dfa = prel.getDfa();
                    if (lrel != null) {
                        if (prel.getLrel() instanceof TuplesTable)
                            extct[0] = relationTupleAC("cp:ac32", intvars, prel.getLrel());
                        else
                            extct[0] = relationTupleAC("cp:ac" + XmlModel.getAcAlgo(), intvars, prel.getLrel());
                    } else {
                        extct[0] = regular(dfa, intvars);
                    }
                }
            }
        }
		return extct;
	}

    

}
