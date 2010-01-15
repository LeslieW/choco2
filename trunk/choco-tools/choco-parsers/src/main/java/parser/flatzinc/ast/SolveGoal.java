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
package parser.flatzinc.ast;

import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.MaxVal;
import choco.cp.solver.search.integer.valselector.MidVal;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.DomOverDeg;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.MostConstrained;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.search.set.MinDomSet;
import choco.cp.solver.search.set.MinEnv;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.StaticSetVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.search.integer.IntVarSelector;
import choco.kernel.solver.search.integer.ValIterator;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.search.set.SetValSelector;
import choco.kernel.solver.search.set.SetVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;
import parser.flatzinc.ast.expression.EAnnotation;
import parser.flatzinc.ast.expression.EIdentifier;
import parser.flatzinc.ast.expression.Expression;
import parser.flatzinc.parser.FZNParser;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

import static parser.flatzinc.parser.FZNParser.solver;

/*
* User : CPRUDHOM
* Mail : cprudhom(a)emn.fr
* Date : 12 janv. 2010
* Since : Choco 2.1.1
*
* Class for solve goals definition based on flatzinc-like objects.
*
* A solve goal is defined with:
* </br> 'solve annotations satisfy;'
* </br> or 'solve annotations maximize expression;'
* /br> or 'solve annotations minimize expression;' 
*/
public class SolveGoal {

    static Logger LOGGER = ChocoLogging.getParserLogger();

    public enum Solver {
        SATISFY, MINIMIZE, MAXIMIZE
    }

    public SolveGoal(List<EAnnotation> annotations, Solver type, Expression expr) {
        readAnnotations(annotations);
        solver.read(FZNParser.model);
        switch (type) {
            case SATISFY:
                solver.setFirstSolution(true);
                break;
            case MAXIMIZE:
                solver.setDoMaximize(true);
                Variable max = expr.intVarValue();
                solver.setObjective(solver.getVar(max));
                FZNParser.solver.setRestart(true);
                solver.setFirstSolution(false);
                break;
            case MINIMIZE:
                solver.setDoMaximize(false);
                Variable min = expr.intVarValue();
                solver.setObjective(solver.getVar(min));
                FZNParser.solver.setRestart(true);
                solver.setFirstSolution(false);

                break;
        }
        solver.generateSearchStrategy();
    }

    /**
     * Read and treat annotations. These are search strategy annotations.
     * @param annotations search strategy annotations
     */
    private void readAnnotations(List<EAnnotation> annotations) {
        for (EAnnotation ann : annotations) {
            // read search sequences
            if (ann.id.equals("seq_search")) {
                // read search annotation
                for (Expression e : ann.exps) {
                    if (e.getTypeOf().equals(Expression.EType.ANN)) {
                        readSearchAnnotation((EAnnotation) e);
                    } else {
                        LOGGER.severe(MessageFormat.format("SolveGoal#readAnnotations : unknown type \"{0}\"", e.getTypeOf()));
                    }
                }
            } else {
                if (ann.getTypeOf().equals(Expression.EType.ANN)) {
                        readSearchAnnotation(ann);
                    } else {
                        LOGGER.severe(MessageFormat.format("SolveGoal#readAnnotations : unknown type \"{0}\"", ann.getTypeOf()));
                    }
            }
        }
    }

    private static final String[] sannos = {"int_search", "bool_search", "set_search"};

    private static final String[] varchoiceannos = {
            "input_order", "first_fail", "anti_first_fail", "smallest",
            "largest", "occurence", "most_constrained", "max_regret"
    };

    private static final String[] assignmentannos = {
            "indomain_min", "in_domain_max", "in_domain_middle", "indomain_median", "indomain",
            "indomain_random", "indomain_split", "indomain_reverse_split", "indomain_interval"
    };

    private static final String[]strategyannos = {"complete"};

    /**
     * Read search annotation and build corresponding strategy
     * @param e {@link parser.flatzinc.ast.expression.EAnnotation}
     */
    private void readSearchAnnotation(EAnnotation e) {
        Expression[] exps = new Expression[e.exps.size()];
        e.exps.toArray(exps);

        // int_search or bool_search
        if (sannos[0].equals(e.id) || sannos[1].equals(e.id)) {
            IntegerVariable[] scope = exps[0].toIntVarArray();
            setIntSearchStrategy(solver.getVar(scope), (EIdentifier)exps[1], (EIdentifier)exps[2]);
        } else
            // set_search
            if (sannos[2].equals(e.id)) {
                SetVariable[] scope = exps[0].toSetVarArray();
                setSetSearchStrategy(solver.getVar(scope), (EIdentifier)exps[1], (EIdentifier)exps[2]);
            }
    }

    /**
     * Return the corresponding index of {@code v} in {@code vs}.
     * @param v value to search
     * @param vs pool of value
     * @return index of {@code v} in {@code vs}, -1 otherwise
     */
    private int index(String v, String[] vs){
        for(int i = 0; i < vs.length; i++){
            if(vs[i].equals(v))return i;
        }
        return -1;
    }

    /**
     * Read and apply (if fully recognize) the search strategy for integer variable scope.
     * @param scope solver int variable
     * @param exp {@link parser.flatzinc.ast.expression.EIdentifier} defining the variable choice
     * @param exp1 {@link parser.flatzinc.ast.expression.EIdentifier} defining the assignment choice
     */
    private void setIntSearchStrategy(IntDomainVar[] scope, EIdentifier exp, EIdentifier exp1) {
        IntVarSelector varSelector = null;
        switch (index(exp.value, varchoiceannos)){
            case 0:
                varSelector = new StaticVarOrder(scope);
                break;
            case 1:
                varSelector = new MinDomain(solver, scope);
                break;
            case 2:
                //TODO: implement MaxDomain
                return;
            case 3:
                //TODO: implements MinValueDomain
                return;
            case 4:
                //TODO: implements MaxValueDomain
                return;
            case 5:
                varSelector = new MostConstrained(solver, scope);
                break;
            case 6:
                varSelector = new DomOverDeg(solver, scope);
                break;
            case 7:
                //TODO: implements MaxRegret
                return;
            default: return;
        }
        ValSelector vals = null;
        ValIterator vali = null;
        switch (index(exp1.value, assignmentannos)){
            case 0:
                vals = new MinVal();
                break;
            case 1:
                vals = new MaxVal();
                break;
            case 2:
                //TODO: implements DomainClosestMiddle
                return;
            case 3:
                vals = new MidVal();
                break;
            case 4:
                vali = new IncreasingDomain();
                break;
            case 5:
                vals = new RandomIntValSelector();
                break;
            case 6:
                //TODO: implements DomainFirstHalf
                return;
            case 7:
                //TODO: implements DomainSecondtHalf
                return;
            case 8:
                //TODO: implements DomainInterval
                return;
            default : return;
        }
        if(vals != null){
            solver.setVarIntSelector(varSelector);
            solver.setValIntSelector(vals);
        }else if(vali != null){
            solver.setVarIntSelector(varSelector);
            solver.setValIntIterator(vali);
        }
    }


    /**
     * Read and apply (if fully recognize) the search strategy for set variable scope.
     * @param scope solver set variable
     * @param exp {@link parser.flatzinc.ast.expression.EIdentifier} defining the variable choice
     * @param exp1 {@link parser.flatzinc.ast.expression.EIdentifier} defining the assignment choice
     */
    private void setSetSearchStrategy(SetVar[] scope, EIdentifier exp, EIdentifier exp1) {
        SetVarSelector varSelector = null;
        switch (index(exp.value, varchoiceannos)){
            case 0:
                varSelector = new StaticSetVarOrder(scope);
                break;
            case 1:
                varSelector = new MinDomSet(solver, scope);
                break;
            case 2:
                //TODO: implement MaxDomSet
                return;
            case 3:
                //TODO: implements MinValueDomSet
                return;
            case 4:
                //TODO: implements MaxValueDomSet
                return;
            case 5:
                //TODO: implements MostConstrained
                return;
            case 6:
                //TODO: implements DomOverDeg
                return;
            case 7:
                //TODO: implements MaxRegret
                return;
            default: return;
        }
        SetValSelector vals = null;
        switch (index(exp1.value, assignmentannos)){
            case 0:
                vals = new MinEnv(solver);
                break;
            case 1:
                //TODO: implements MaxEnv
                return;
            case 2:
                //TODO: implements DomainClosestMiddle
                return;
            case 3:
                //TODO: implements MidEnv
                return;
            case 4:
                //TODO: implements MeddianEnv
                return;
            case 5:
                vals = new RandomSetValSelector();
                break;
            case 6:
                //TODO: implements DomainFirstHalf
                return;
            case 7:
                //TODO: implements DomainSecondtHalf
                return;
            case 8:
                //TODO: implements DomainInterval
                return;
            default : return;
        }
        if(vals != null){
            solver.setVarSetSelector(varSelector);
            solver.setValSetSelector(vals);
        }
    }


}
