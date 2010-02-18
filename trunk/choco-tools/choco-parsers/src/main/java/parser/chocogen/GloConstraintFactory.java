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
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import gnu.trove.TIntObjectHashMap;
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
		for (IntegerVariable var : vars) {
			int span = var.getUppB() - var.getLowB() + 1;
			if (var.getDomainSize() > maxdszise) {
				maxdszise = var.getDomainSize();
			}
			if (var.getDomainSize() > 1) nbnoninstvar++;
			if (ChocoFactory.ratioHole * span > var.getDomainSize()) {
				holes = true;
			}
		}

		if (vars.length <= 3) {
			return allDifferent("cp:clique", vars);
		} else if (holes || (maxdszise <= 30 &&
				(vars.length <= 10 || (nbnoninstvar < vars.length && nbnoninstvar < 20)))) {
			return allDifferent("cp:ac", vars);
		} else
			return allDifferent("cp:bc", vars);
	}

	public static Constraint buildGcc(IntegerVariable[] vars, Integer[] values, IntegerVariable[] noccurrences) {
		int maxdszise = 0;
		boolean holes = false;
		int nbnoninstvar = 0;
		int[] low = new int[noccurrences.length];
		int[] up =  new int[noccurrences.length];
		boolean constant = true;
		int noc = 0;
		while(constant && noc < noccurrences.length){
			constant = noccurrences[noc].isConstant();
			low[noc] = noccurrences[noc].getLowB();
			up[noc] = noccurrences[noc++].getUppB();
		}
		if(constant){
			for (IntegerVariable var : vars) {
				int span = var.getUppB() - var.getLowB() + 1;
				if (var.getDomainSize() > maxdszise) {
					maxdszise = var.getDomainSize();
				}
				if (var.getDomainSize() > 1) nbnoninstvar++;
				if (ChocoFactory.ratioHole * span > var.getDomainSize()) {
					holes = true;
				}
			}
			if (holes || (maxdszise <= 30 &&
					(vars.length <= 10 || (nbnoninstvar < vars.length && nbnoninstvar < 20)))) {
				return globalCardinality("cp:ac", vars, low, up, values[0]);
			}else{
				return globalCardinality("cp:bc", vars, low, up, values[0]);
			}
		}
		else
			return globalCardinality(vars, noccurrences, values[0]);
	}


	public Constraint[] makeAlldDifferent(PAllDifferent pgc) {
		IntegerVariable[] vars = new IntegerVariable[pgc.getScope().length];
		for (int i = 0; i < pgc.getScope().length; i++) {
			vars[i] = pgc.getScope()[i].getChocovar();
		}
		return new Constraint[]{buildAllDiff(vars)};

	}

	public Constraint[] makeCumulative(PCumulative pc) {
		//FIXME the manager is already dealing with disjunctive extraction
		int n = pc.getTasks().length;
		if (pc.getLimit() == 1) {
			TaskVariable[] tasks = new TaskVariable[n];
			IntegerVariable start;
			IntegerVariable end;
			int duration;
			for (int i = 0; i < n; i++) {
				PTask t = pc.getTasks()[i];
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
			PTask t = pc.getTasks()[i];
			start = ((PVariable) t.getOrigin()).getChocovar();
			duration = constant((Integer) t.getDuration());
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

	public Constraint[] makeElement(PElement pe) {
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
		throw new ModelException("Unknown global constraint");
	}


	public Constraint[] makeWeightedSum(PWeightedSum pws) {
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
		default :throw new ModelException("Unknown weighted sum operator");
		}
	}

	public Constraint[] makeDisjunctive(PDisjunctive pd) {
		int n = pd.getTasks().length;
		TaskVariable[] tasks = new TaskVariable[n];
		IntegerVariable start, duration, end;
		for (int i = 0; i < n; i++) {
			PTask t = pd.getTasks()[i];
			if(t.getOrigin() instanceof PVariable){
				start = ((PVariable) t.getOrigin()).getChocovar();
			}else {
				start = constant((Integer)t.getOrigin());
			}
			if(t.getDuration() instanceof PVariable){
				duration = ((PVariable) t.getDuration()).getChocovar();
			}else {
				duration = constant((Integer)t.getDuration());
			}
			end = makeIntVar("end_" + i, start.getLowB() + duration.getLowB(), start.getUppB() + duration.getUppB());

			tasks[i] = new TaskVariable("t", start, end, duration);
		}
		return new Constraint[]{disjunctive(tasks)};
	}

	public Constraint[] makeGlobalCardinality(PGlobalCardinality pgcc) {
		IntegerVariable[] vars = new IntegerVariable[pgcc.offset];
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for(int v = 0; v < pgcc.offset; v++){
			if(pgcc.table[v] instanceof PVariable){
				vars[v] = ((PVariable)pgcc.table[v]).getChocovar();
			}else{
				vars[v] = constant((Integer)pgcc.table[v]);
			}
			min = Math.min(min, vars[v].getLowB());
			max = Math.max(max, vars[v].getUppB());
		}
		TIntObjectHashMap<Object> counts = new TIntObjectHashMap<Object>();
		for(int i = pgcc.offset; i < pgcc.table.length; i+=2){
			counts.put((Integer)pgcc.table[i], pgcc.table[i+1]);
		}

		IntegerVariable[] noccurrences = new IntegerVariable[max-min+1];
		Integer[] values = new Integer[max-min+1];
		for(int i = min; i <= max; i++){
			values[i-min] = i;
			if(counts.containsKey(i)){
				if(counts.get(i) instanceof PVariable){
					noccurrences[i-min] = ((PVariable)counts.get(i)).getChocovar();
				}else{
					noccurrences[i-min] = constant((Integer)counts.get(i));    
				}
			}else{
				noccurrences[i-min] = constant(0);
			}
		}
		return new Constraint[]{buildGcc(vars, values, noccurrences)};
	}

	public Constraint[] makeLexLess(PLexLess pll) {
		IntegerVariable[] vars1 = new IntegerVariable[pll.offset];
		IntegerVariable[] vars2 = new IntegerVariable[pll.table.length - pll.offset];
		for(int i = 0; i < pll.table.length; i++){
			Object v = pll.table[i];
			if(i < pll.offset){
				if(v instanceof PVariable){
					vars1[i] = ((PVariable)v).getChocovar();
				}else{
					vars1[i] = constant((Integer)v);
				}
			}else{
				if(v instanceof PVariable){
					vars2[i - pll.offset] = ((PVariable)v).getChocovar();
				}else{
					vars1[i - pll.offset] = constant((Integer)v);
				}
			}
		}
		return new Constraint[]{lex(vars1, vars2)};
	}

	public Constraint[] makeLexLessEq(PLexLessEq plle) {
		IntegerVariable[] vars1 = new IntegerVariable[plle.offset];
		IntegerVariable[] vars2 = new IntegerVariable[plle.table.length - plle.offset];
		for(int i = 0; i < plle.table.length; i++){
			Object v = plle.table[i];
			if(i < plle.offset){
				if(v instanceof PVariable){
					vars1[i] = ((PVariable)v).getChocovar();
				}else{
					vars1[i] = constant((Integer)v);
				}
			}else{
				if(v instanceof PVariable){
					vars2[i - plle.offset] = ((PVariable)v).getChocovar();
				}else{
					vars1[i - plle.offset] = constant((Integer)v);
				}
			}
		}
		return new Constraint[]{lexeq(vars1, vars2)};
	}
	
	public Constraint[] makeGlobalConstraint(PGlobalConstraint pgc) {
		if (pgc instanceof PAllDifferent) {
			return makeAlldDifferent((PAllDifferent) pgc);
		} else if (pgc instanceof PCumulative) {
			return makeCumulative( (PCumulative) pgc);
		} else if (pgc instanceof PElement) {
			return makeElement((PElement) pgc);
		}else if (pgc instanceof PWeightedSum) {
			return makeWeightedSum((PWeightedSum) pgc);
		} else if(pgc instanceof PDisjunctive){
			return makeDisjunctive( (PDisjunctive)pgc);
		}else if(pgc instanceof PGlobalCardinality){
			return makeGlobalCardinality((PGlobalCardinality)pgc);
		}else if(pgc instanceof PLexLess){
			return makeLexLess( (PLexLess)pgc);
		}else if(pgc instanceof PLexLessEq){
			return makeLexLessEq( (PLexLessEq)pgc);
		} 
		throw new ModelException("Unknown global constraint");
	}

}
