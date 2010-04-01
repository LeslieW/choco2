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
package choco.cp.solver.preprocessor;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.common.util.detector.DomainMerger;
import choco.cp.common.util.detector.TaskMerger;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.preprocessor.detectors.CliqueDetector;
import choco.cp.solver.preprocessor.detectors.ExpressionDetector;
import choco.cp.solver.preprocessor.detectors.RelationDetector;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.objects.BooleanSparseMatrix;
import choco.kernel.common.util.objects.ISparseMatrix;
import choco.kernel.memory.IEnvironment;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.*;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import gnu.trove.TIntObjectHashMap;

import java.util.*;

/*
 *
 * User:    charles
 * Date:    19 août 2008
 *
 * Black box solver
 */
public class PreProcessCPSolver extends CPSolver {

    /**
     * Allow preprocessing on expressions to extract
     * intensional constraints
     */
    private ExpressionDetector cleverExp;

    /**
     * Allow preprocessing on relations to extract
     * intensional constraints
     */
    private RelationDetector cleverRel;

    /**
     * A component with simple rules for symetry breaking
     */
    private SymBreaking symb;

    /**
     * Search component
     */
    private PPSearch ppsearch;

    /**
     * The ratio of holes within domains to which
     * decision are performed to switch from BC to AC
     */
    public static float ratioHole = 0.7f;

    /**
     * initial propagation time (to decide whether to perform or not
     * singleton and restarts). If propagation is too heavy, we will avoid
     * restarting
     */
    private int proptime;

    /**
     * Do we perform restart or not
     */
    public boolean restartMode = false;


    public PreProcessCPSolver(String... options) {
        super(options);
        init();
    }

    public PreProcessCPSolver(final IEnvironment env, String ... options) {
        super(env, options);
        init();
    }

    void init() {
        this.cleverExp = new ExpressionDetector();
        this.cleverRel = new RelationDetector();
        this.symb = new SymBreaking();
        this.mod2sol = new PPModelToCPSolver(this);
        this.ppsearch = new PPSearch();
    }

    public PPModelToCPSolver getMod2Sol() {
        return (PPModelToCPSolver) mod2sol;
    }

    public void addOption(final String opt) {
        optionsSet.add(opt);
    }


    void setAllProcessing() {
        optionsSet.add("bb:exp");
        optionsSet.add("bb:cliques");
        optionsSet.add("bb:exttoint");
        optionsSet.add("bb:disjunctive");
        optionsSet.add("bb:breaksym");
    }

    public void setRandomValueOrdering(final int seed) {
        ppsearch.setRandomValueHeuristic(seed);
    }

    /**
     * Add an index to the variables to be able to map them easily
     * to nodes of the constraint graph
     * @param m model
     */
    private static void associateIndexes(final Model m) {
        Iterator it = m.getIntVarIterator();
        int cpt = 0;
        while (it.hasNext()) {
            final IntegerVariable iv = (IntegerVariable) it.next();
            iv.setHook(cpt);
            cpt++;
        }
        it = m.getMultipleVarIterator();
        cpt = 0;
        while (it.hasNext()) {
            final MultipleVariables iv = (MultipleVariables) it.next();
            if(iv instanceof TaskVariable){
                iv.setHook(cpt);
                cpt++;
            }
        }
    }

    /**
     * read of the black box solver
     *
     * @param m model
     */
    public void read(final Model m) {
        this.model = (CPModel) m;
        ppsearch.setModel(model);

        super.initReading();

        setAllProcessing();

        associateIndexes(m);

        detectEqualitiesOnIntegers(m);
        mod2sol.readIntegerVariables(model);

        mod2sol.readRealVariables(model);
        mod2sol.readSetVariables(model);
        mod2sol.readConstants(model);

        detectEqualitiesOnTasks(m);
        mod2sol.readMultipleVariables(model);
        mod2sol.readParameters(model);

        if (optionsSet.contains("bb:disjunctive"))
            detectDisjonctives(m);

        if (optionsSet.contains("bb:exp"))
            detectExpression(m);

        if (optionsSet.contains("bb:cliques"))
            detectCliques(m);

        mod2sol.readVariables(model);

        if (optionsSet.contains("bb:breaksym"))
            breakSymetries(m);

        getMod2Sol().readBBDecisionVariables();
        getMod2Sol().readConstraints(model, !optionsSet.contains(CPOptions.S_MULTIPLE_READINGS));
    }

    /**
     * set a heuristic that automatically choose between impact and DomWdeg
     * @param s solver
     * @param inittime init time
     * @return true if the problem is still feasible
     */
    public boolean setVersatile(final CPSolver s, final int inittime) {
       return ppsearch.setVersatile(s,inittime);
    }

    /**
     * set the DomOverDeg heuristic
     *
     * @param s solver
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setDomOverDeg(final CPSolver s) {
        return ppsearch.setDomOverDeg(s);
    }

    /**
     * set the DomOverWDeg heuristic
     *
     * @param s solver
     * @param inittime init time
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setDomOverWeg(final CPSolver s, final int inittime) {
       return ppsearch.setDomOverWeg(s, inittime);
    }


    /**
     * set the Impact heuristic
     * @param s solver
     * @param initialisationtime init time
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setImpact(final CPSolver s, final int initialisationtime) {
         return ppsearch.setImpact(s, initialisationtime);
    }

    /**
     * return true if contains at least one disjunctive
     * @return a boolean
     */
    boolean isScheduling() {
        if (model == null) {
            throw new SolverException("you must read the model before !");
        } else return ppsearch.isScheduling();
    }

    /**
     * return true if contains at least one extensional constraints
     *
     * @return a boolean
     */
    public boolean isExtensionnal() {
        if (model == null) {
            throw new SolverException("you must read the model before !");
        } else return model.getNbConstraintByType(ConstraintType.TABLE) > 0;
    }

    /**
     * return true if contains at least one extensional constraints
     *
     * @return a boolean
     */
    boolean isBinaryExtensionnal() {
        if (model == null) {
            throw new SolverException("you must read the model before !");
        } else {
            if (model.getNbConstraintByType(ConstraintType.TABLE) == 0)
                return false;
            final Iterator<Constraint> it = model.getConstraintIterator();
            while(it.hasNext()) {
                final Constraint ct = it.next();
                if (ct.getNbVars() > 2) return false;
            }
            return true;
        }
    }

    public PPSearch getBBSearch() {
        return ppsearch;
    }

    // ############################################################################################################
    // ######                   Symetry breaking                                            ###
    // ############################################################################################################

    void breakSymetries(final Model m) {
        symb.addSymBreakingConstraint((CPModel) m);
    }

    // ############################################################################################################
    // ######                   Expressions Detection                                            ###
    // ############################################################################################################

    private static boolean isAValidExpression(final Constraint ic) {
        return ic instanceof MetaConstraint ||
                (ic instanceof ComponentConstraint &&
                 (ic.getConstraintType() == ConstraintType.EQ ||
                 ic.getConstraintType() == ConstraintType.NEQ ||
                 ic.getConstraintType() == ConstraintType.LEQ ||
                 ic.getConstraintType() == ConstraintType.GEQ ||
                 ic.getConstraintType() == ConstraintType.GT ||
                 ic.getConstraintType() == ConstraintType.LT));
    }

    void detectExpression(final Model m) {
        final Iterator<Constraint> it = m.getConstraintIterator();
        final List<Constraint> neqToAdd = new LinkedList<Constraint>();
        while (it.hasNext()) {
            final Constraint ic = it.next();
            if (!this.mapconstraints.containsKey(ic.getIndex()) && isAValidExpression(ic)) {
                final ExpressionSConstraint c = new ExpressionSConstraint(getMod2Sol().buildNode(ic));
                c.setScope(this);
                getMod2Sol().storeExpressionSConstraint(ic, c);
                final SConstraint intensional = ExpressionDetector.getIntentionalConstraint(c, this);
                if (intensional != null) {
                    c.setKnownIntensionalConstraint(intensional);
                } else {
                    if (ExpressionDetector.encompassDiff(c)) {
                       final IntegerVariable[] vars = ((AbstractConstraint) ic).getIntVariableScope();
                       neqToAdd.add(Choco.neq(vars[0],vars[1]));
                    }
                }
            }
        }
        for (final Constraint aNeqToAdd : neqToAdd) {
            m.addConstraint(aNeqToAdd);
        }
    }

    // ############################################################################################################
    // ######                   Cliques of NEQ Detection                                           ###
    // ############################################################################################################


    void detectCliques(final Model m) {
        final CliqueDetector cdetect = new CliqueDetector((CPModel) m);
        if (cdetect.addAllNeqEdges()) {
            final CliqueDetector.CliqueIterator it = cdetect.cliqueIterator();
            while (it.hasNext()) {
                final IntegerVariable[] cl = it.next();
                if (cl.length > 2) {
                    m.addConstraint(CPOptions.C_ALLDIFFERENT_BC, allDifferent(cl));
                    symb.setMaxClique(cl);
                    it.remove();
                } else m.addConstraint(Choco.neq(cl[0],cl[1]));
            }
        }
    }

    // ############################################################################################################
    // ######                   Cliques of Disjonction                                            ###
    // ############################################################################################################


    private static int[] getVarIndexes(final IntegerVariable[] vs) {
        final int[] idxs = new int[vs.length];
        for (int i = 0; i < idxs.length; i++) {
            idxs[i] = vs[i].getHook();
        }
        return idxs;
    }

    void detectDisjonctives(final Model m) {
        final CliqueDetector cdetect = new CliqueDetector((CPModel) m);
        final int[] durations = cdetect.addAllDisjunctiveEdges(cleverExp, this);
        if (durations != null) {
            final BitSet[] precedenceAlreadyAdded = new BitSet[m.getNbIntVars()];
            for (int i = 0; i < m.getNbIntVars(); i++){
                precedenceAlreadyAdded[i] = new BitSet();
            }

            final CliqueDetector.CliqueIterator it = cdetect.cliqueIterator();
            while (it.hasNext()) {
                final IntegerVariable[] cl = it.next();
                final int[] idxs = getVarIndexes(cl);
                final int[] dur = new int[cl.length];
                final TaskVariable[] tasks = new TaskVariable[cl.length];
                for (int i = 0; i < cl.length; i++) {
                    dur[i] = durations[idxs[i]];
                    tasks[i] = Choco.makeTaskVar("", cl[i], constant(dur[i]));
                }
                //automatically add reified precedences to make branching easier
                for (int j = 0; j < cl.length; j++) {
                    for (int k = j + 1; k < cl.length; k++) {
                        if (!precedenceAlreadyAdded[idxs[j]].get(idxs[k])) {
                            final IntegerVariable b = makeIntVar(String.format("%d", (dur[j] + dur[k])), 0, 1);
                            m.addConstraint(Choco.precedenceDisjoint(cl[j], dur[j], cl[k], dur[k], b));
                            precedenceAlreadyAdded[idxs[j]].set(idxs[k]);
                            precedenceAlreadyAdded[idxs[k]].set(idxs[j]);
                        }
                    }
                }
                m.addConstraint(Choco.disjunctive(tasks));
                //delete the disjunctions
                it.remove();
            }
        }
    }

    // ############################################################################################################
    // ######                   Merge equalities                                             ###
    // ############################################################################################################

    private void detectEqualitiesOnIntegers(final Model m) {
        final int n = m.getNbIntVars();
        final ISparseMatrix matrix = new BooleanSparseMatrix(n);
        final Iterator<Constraint> iteq = m.getConstraintByType(ConstraintType.EQ);
        Constraint c;
        // Run over equalities constraints, and create edges
        while(iteq.hasNext()){
            c = iteq.next();
            final Variable v1 = c.getVariables()[0];
            final Variable v2 = c.getVariables()[1];
            if(v1.getVariableType()== VariableType.INTEGER
                    && v2.getVariableType()== VariableType.INTEGER){
            	matrix.add(v1.getHook(), v2.getHook());
                this.mapconstraints.put(c.getIndex(), null);
            }
        }
        if(matrix.getNbElement()> 0){
            matrix.prepare();
            // Detect connex components
            final int[] color = new int[n];
            Arrays.fill(color, -1);
            final TIntObjectHashMap<DomainMerger> domainByColor = new TIntObjectHashMap<DomainMerger>();
            int k = -1;
            DomainMerger dtmp = new DomainMerger();
            final Iterator<Long> it = matrix.iterator();
            while(it.hasNext()){
                final long v = it.next();
                final int i = (int)(v / n);
                final int j = (int)(v % n);

                if (color[i]==-1){
                    k++;
                    color[i]=k;
                    domainByColor.put(k, new DomainMerger(m.getIntVar(i)));
                }
                final DomainMerger d = domainByColor.get(color[i]);
                //backup
                dtmp.copy(d);
                if(d.intersection(m.getIntVar(j))){
                    color[j] = color[i];
                    domainByColor.put(color[i], d);
                }else{
                    m.addConstraint(Choco.eq(m.getIntVar(i), m.getIntVar(j)));
                    //rollback
                    d.copy(dtmp);
                    if (color[j]==-1){
                        k++;
                        color[j]=k;
                        domainByColor.put(k, new DomainMerger(m.getIntVar(j)));
                    }
                }
            }
            final IntDomainVar[] var = new IntDomainVar[k+1];
            IntegerVariable vtmp;
            for(int i = 0; i < n; i++){
                final int col = color[i];
                if(col !=-1){
                    final IntegerVariable v = m.getIntVar(i);
                    if(var[col] == null){
                        dtmp = domainByColor.get(col);
                        if(dtmp.values != null){
                            vtmp = new IntegerVariable(v.getName(), dtmp.values);
                        }else{
                            vtmp = new IntegerVariable(v.getName(), dtmp.low, dtmp.upp);
                        }
                        vtmp.addOptions(dtmp.optionsSet);
                        vtmp.findManager(model.properties);
                        var[col] = (IntDomainVar)mod2sol.readModelVariable(vtmp);
                    }
                    this.mapvariables.put(v.getIndex(), var[col]);
                }
            }
        }
    }

    private void detectEqualitiesOnTasks(final Model m) {
        final int n = m.getNbStoredMultipleVars();
        final ISparseMatrix matrix = new BooleanSparseMatrix(n);
        MultipleVariables m1, m2;
        // Run over equalities constraints, and create edges
        for(int i = 0; i < n-1; i++){
            m1 = m.getStoredMultipleVar(i);
            if(m1 instanceof TaskVariable){
                for(int j = i+1; j < n; j++){
                    m2 = m.getStoredMultipleVar(j);
                    if(m2 instanceof TaskVariable){
                        if(m1.isEquivalentTo(m2)){
                        	matrix.add(m1.getHook(), m2.getHook());
                        }
                    }

                }
            }
        }
        if(matrix.getNbElement()> 0){
            matrix.prepare();
            // Detect connex components
            final int[] color = new int[n];
            Arrays.fill(color, -1);
            final TIntObjectHashMap<TaskMerger> domainByColor = new TIntObjectHashMap<TaskMerger>();
            int k = -1;
            TaskMerger dtmp = new TaskMerger();
            final Iterator<Long> it = matrix.iterator();
            while(it.hasNext()){
                final long v = it.next();
                final int i = (int)(v / n);
                final int j = (int)(v % n);

                if (color[i]==-1){
                    k++;
                    color[i]=k;
                    domainByColor.put(k, new TaskMerger((TaskVariable)m.getStoredMultipleVar(i)));
                }
                final TaskMerger d = domainByColor.get(color[i]);
                //backup
                d.merge((TaskVariable)m.getStoredMultipleVar(j));
                color[j] = color[i];
                domainByColor.put(color[i], d);
            }
            final TaskVar[] var = new TaskVar[k+1];
            TaskVariable vtmp;
            for(int i = 0; i < n; i++){
                final int col = color[i];
                if(col !=-1){
                    final TaskVariable v = (TaskVariable)m.getStoredMultipleVar(i);
                    if(var[col] == null){
                        dtmp = domainByColor.get(col);
                        vtmp = new TaskVariable(v.getName(), dtmp.start, dtmp.duration, dtmp.end);
                        vtmp.addOptions(vtmp.getOptions());
                        vtmp.findManager(model.properties);
                        var[col] = (TaskVar)mod2sol.readModelVariable(vtmp);
                    }
                    this.mapvariables.put(v.getIndex(), var[col]);
                    v.resetHook();
                }
            }
        }
    }


//******************************************************************//
//***************** Root node Propagation tools ********************//
//******************************************************************//


    /**
     * Perform initial propagarion and measure its time
     * @return true if the problem is still consistent at this stage
     */
	public boolean initialPropagation() {
		proptime = (int) System.currentTimeMillis();
		try {
			propagate();
		} catch (ContradictionException e) {
			return false;
		}
		proptime = (int) System.currentTimeMillis() - proptime;
        restartMode = !isBinaryExtensionnal();
		return true;
	}

    /**
     * Apply a step of singleton consistency
     * @param timelimit the time limit to respect
     * @return boolean
     */
	public boolean rootNodeSingleton(final boolean doSingleton, final int timelimit) {
		if (!doSingleton) return true;
        final boolean sched = isScheduling();
        final int time = (int) System.currentTimeMillis();
        if (proptime <= 1500) {
			int nbvrem = 0;
		    int nbvtried = 0;
			final int threshold = 100;
			worldPush();
			for (int i = 0; i < getNbIntVars(); i++) {
				final IntDomainVar v = (IntDomainVar) getIntVar(i);
				final DisposableIntIterator it = v.getDomain().getIterator();
				if ((sched && v.getDomainSize() == 2) || (!sched && v.hasEnumeratedDomain())) {
					while (it.hasNext()) {
						final int val = it.next();
						nbvtried ++;
						boolean cont = false;
						worldPush();
						try {
							v.instantiate(val, null, true);
							propagate();
						} catch (ContradictionException e) {
							cont = true;
						}
						worldPop();
						if (cont) {
							try {
								nbvrem++;
								v.remVal(val);
								propagate();
							} catch (ContradictionException e) {
								return false;
							}
						}
                        //gardefou (limite de temps)
						if ((int) (System.currentTimeMillis() - time) > timelimit) {
                            break;
						}
						if (nbvtried == threshold && nbvrem == 0 && proptime >= 200) {
							break;
						}
					}
                    it.dispose();
				}
			}
		}
        return true;
	}
}
