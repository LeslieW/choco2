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

import static choco.Choco.*;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import parser.absconparseur.components.*;
import parser.absconparseur.tools.InstanceParser;

/**
 * The factory for global constraints
 */
public class GloConstraintFactory extends ObjectFactory {

	public GloConstraintFactory(Model m, InstanceParser parser) {
		super(m, parser);
	}

	public static Constraint buildAllDiff(IntegerVariable[] vars) {
		int maxdszise = 0;
		boolean holes = false;
		int nbnoninstvar = 0;
		for (int i = 0; i < vars.length; i++) {
			int span = vars[i].getUppB() - vars[i].getLowB() + 1;
			if (vars[i].getDomainSize() > maxdszise) {
				maxdszise = vars[i].getDomainSize();
			}
			if (vars[i].getDomainSize() > 1) nbnoninstvar++;
			if (ChocoFactory.ratioHole * span > vars[i].getDomainSize()) {
				holes = true;
			}
		}

		//System.out.println("arity: " + vars.length + " ds: " + maxdszise);
		if (vars.length <= 3) {
			return allDifferent("cp:clique", vars);
		} else if (holes || (maxdszise <= 30 &&
				(vars.length <= 10 || (nbnoninstvar < vars.length && nbnoninstvar < 20)))) {
			return allDifferent("cp:ac", vars);
		} else
			return allDifferent("cp:bc", vars);
	}

	public Constraint[] makeGlobalConstraint(PGlobalConstraint pgc) {
		if (pgc instanceof PAllDifferent) {
			PAllDifferent pa = (PAllDifferent) pgc;
			IntegerVariable[] vars = new IntegerVariable[pa.getScope().length];
			for (int i = 0; i < pa.getScope().length; i++) {
				vars[i] = pa.getScope()[i].getChocovar();
			}
			return new Constraint[]{buildAllDiff(vars)};
		}
		if (pgc instanceof PCumulative) {
			PCumulative pc = (PCumulative) pgc;
			int n = pc.getTasks().length;
			if (pc.getLimit() == 1) {
                TaskVariable[] tasks = new TaskVariable[n];
				IntegerVariable start;
                IntegerVariable end;
				int duration;
				for (int i = 0; i < n; i++) {
					PCumulative.Task t = pc.getTasks()[i];
					start = ((PVariable) t.getOrigin()).getChocovar();
					duration = (Integer) t.getDuration();
                    if (t.getEnd() == null) {
                        end = makeIntVar("end_" + i, start.getLowB() + (Integer) t.getDuration(), start.getUppB() + (Integer) t.getDuration());
                    } else {
                        end = ((PVariable) t.getEnd()).getChocovar();
                    }
                    tasks[i] = new TaskVariable("t", start, end, constant(duration));
				}
				return new Constraint[]{disjunctive(tasks)};
			}
            TaskVariable[] taskvars = new TaskVariable[n];
			IntegerVariable start;
			IntegerVariable duration;
            IntegerVariable end;
			int[] heights = new int[n];
			for (int i = 0; i < n; i++) {
				PCumulative.Task t = pc.getTasks()[i];
                start = ((PVariable) t.getOrigin()).getChocovar();
                duration = constant("dur_" + i, (Integer) t.getDuration());
                heights[i] = (Integer) t.getHeight();
                if (t.getEnd() == null) {
                    end = makeIntVar("end_" + i, start.getLowB() + (Integer) t.getDuration(), start.getUppB() + (Integer) t.getDuration());
                } else {
                    end = ((PVariable) t.getEnd()).getChocovar();
                }
                taskvars[i] = new TaskVariable("", start, end, duration);
            }
			return new Constraint[]{cumulativeMax(taskvars, heights, pc.getLimit())};

		}
		if (pgc instanceof PElement) {
			PElement pe = (PElement)pgc;
            IntegerVariable v = null;
            if(pe.getValue() instanceof PVariable){
                v = ((PVariable)pe.getValue()).getChocovar();
            }else if(pe.getValue() instanceof Integer){
                v = makeIntVar("value", (Integer)pe.getValue(), (Integer)pe.getValue());
                m.addVariable("cp:bound", v);
            }
            if(pe.getTable().length>0 && pe.getTable()[0] instanceof PVariable){
                IntegerVariable[] vars = new IntegerVariable[pe.getTable().length];
                for (int i = 0; i < vars.length; i++) {
                    vars[i] = ((PVariable)pe.getTable()[i]).getChocovar();
                }
                int offset = -1;//-pe.getIndex().getDomain().getMinValue();
                return new Constraint[]{nth(pe.getIndex().getChocovar(), vars, v, offset)};
            }else if(pe.getTable().length>0 && pe.getTable()[0] instanceof Integer){
                int[] vars = new int[pe.getTable().length];
                for (int i = 0; i < vars.length; i++) {
                    vars[i] = (Integer)pe.getTable()[i];
                }
                int offset = -1;//- pe.getIndex().getDomain().getMinValue();
                return new Constraint[]{nth(pe.getIndex().getChocovar(), vars, v, offset)};
            }
		}
		if (pgc instanceof PWeightedSum) {
			PWeightedSum pws = (PWeightedSum) pgc;
			int[] coef = pws.getCoeffs();
			IntegerVariable[] vars = new IntegerVariable[pws.getScope().length];
			for (int i = 0; i < pws.getScope().length; i++) {
				vars[i] = pws.getScope()[i].getChocovar();
			}
			switch (pws.getOperator()) {
				case EQ:
					return new Constraint[]{eq(scalar(coef, vars), pws.getLimit())};
				case GE:
					return new Constraint[]{geq(scalar(coef, vars), pws.getLimit())};
				case GT:
					return new Constraint[]{gt(scalar(coef, vars), pws.getLimit())};
				case LE:
					return new Constraint[]{leq(scalar(coef, vars), pws.getLimit())};
				case LT:
					return new Constraint[]{lt(scalar(coef, vars), pws.getLimit())};
				case NE:
					return new Constraint[]{neq(scalar(coef, vars), pws.getLimit())};
			}
		}
		throw new Error("Unknown global constraint");
	}

}
