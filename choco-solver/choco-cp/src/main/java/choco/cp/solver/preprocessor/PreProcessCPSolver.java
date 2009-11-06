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
import choco.kernel.solver.propagation.VarEvent;
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

    protected HashSet<String> options;

    /**
     * Allow preprocessing on expressions to extract
     * intensional constraints
     */
    protected ExpressionDetector cleverExp;

    /**
     * Allow preprocessing on relations to extract
     * intensional constraints
     */
    protected RelationDetector cleverRel;

    /**
     * A component with simple rules for symetry breaking
     */
    protected SymBreaking symb;

    /**
     * Search component
     */
    protected PPSearch ppsearch;

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
    public int proptime;

    /**
     * Do we perform restart or not
     */
    public boolean restartMode = false;


    public PreProcessCPSolver() {
        super();
        init();
    }

    public PreProcessCPSolver(IEnvironment env) {
        super(env);
        init();
    }

    public void init() {
        this.cleverExp = new ExpressionDetector();
        this.cleverRel = new RelationDetector();
        this.symb = new SymBreaking();
        this.options = new HashSet<String>();
        this.mod2sol = new PPModelToCPSolver(this);
        this.ppsearch = new PPSearch();
    }

    public PPModelToCPSolver getMod2Sol() {
        return (PPModelToCPSolver) mod2sol;
    }

    public void addOption(String opt) {
        options.add(opt);
    }

    public void addOptions(String options) {
        if (options != null && !"".equals(options)) {
            String[] optionsStrings = options.split(" ");
            for (String optionsString : optionsStrings) {
                this.addOption(optionsString);
            }
        }
    }

    public void setAllProcessing() {
        options.add("bb:exp");
        options.add("bb:cliques");
        options.add("bb:exttoint");
        options.add("bb:disjunctive");
        options.add("bb:breaksym");
    }

    public void setRandomValueOrdering(int seed) {
        ppsearch.setRandomValueHeuristic(seed);
    }

    /**
     * Add an index to the variables to be able to map them easily
     * to nodes of the constraint graph
     * @param m model
     */
    public void associateIndexes(Model m) {
        Iterator it = m.getIntVarIterator();
        int cpt = 0;
        while (it.hasNext()) {
            IntegerVariable iv = (IntegerVariable) it.next();
            iv.setHook(cpt);
            cpt++;
        }
        it = m.getMultipleVarIterator();
        cpt = 0;
        while (it.hasNext()) {
            MultipleVariables iv = (MultipleVariables) it.next();
            if(iv instanceof TaskVariable){
                ((TaskVariable)iv).setHook(cpt);
                cpt++;
            }
        }
    }

    /**
     * read of the black box solver
     *
     * @param m model
     */
    public void read(Model m) {
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

        if (options.contains("bb:disjunctive"))
            detectDisjonctives(m);

        if (options.contains("bb:exp"))
            detectExpression(m);

        if (options.contains("bb:cliques"))
            detectCliques(m);

        mod2sol.readVariables(model);

        if (options.contains("bb:breaksym"))
            breakSymetries(m);

        getMod2Sol().readBBDecisionVariables();
        getMod2Sol().readConstraints(model);
    }

    /**
     * set a heuristic that automatically choose between impact and DomWdeg
     * @param s solver
     * @param inittime init time
     * @return true if the problem is still feasible
     */
    public boolean setVersatile(CPSolver s, int inittime) {
       return ppsearch.setVersatile(s,inittime);
    }

    /**
     * set the DomOverDeg heuristic
     *
     * @param s solver
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setDomOverDeg(CPSolver s) {
        return ppsearch.setDomOverDeg(s);
    }

    /**
     * set the DomOverWDeg heuristic
     *
     * @param s solver
     * @param inittime init time
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setDomOverWeg(CPSolver s, int inittime) {
       return ppsearch.setDomOverWeg(s, inittime);
    }


    /**
     * set the Impact heuristic
     * @param s solver
     * @param initialisationtime init time
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setImpact(CPSolver s, int initialisationtime) {
         return ppsearch.setImpact(s, initialisationtime);
    }

    /**
     * return true if contains at least one disjunctive
     * @return a boolean
     */
    public boolean isScheduling() {
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
    public boolean isBinaryExtensionnal() {
        if (model == null) {
            throw new SolverException("you must read the model before !");
        } else {
            if (model.getNbConstraintByType(ConstraintType.TABLE) == 0)
                return false;
            Iterator<Constraint> it = model.getConstraintIterator();
            while(it.hasNext()) {
                Constraint ct = it.next();
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

    public void breakSymetries(Model m) {
        symb.addSymBreakingConstraint((CPModel) m);
    }

    // ############################################################################################################
    // ######                   Expressions Detection                                            ###
    // ############################################################################################################

    public boolean isAValidExpression(Constraint ic) {
        return ic instanceof MetaConstraint ||
                (ic instanceof ComponentConstraint &&
                 (ic.getConstraintType() == ConstraintType.EQ ||
                 ic.getConstraintType() == ConstraintType.NEQ ||
                 ic.getConstraintType() == ConstraintType.LEQ ||
                 ic.getConstraintType() == ConstraintType.GEQ ||
                 ic.getConstraintType() == ConstraintType.GT ||
                 ic.getConstraintType() == ConstraintType.LT));
    }

    public void detectExpression(Model m) {
        Iterator<Constraint> it = m.getConstraintIterator();
        List<Constraint> neqToAdd = new LinkedList<Constraint>();
        while (it.hasNext()) {
            Constraint ic = it.next();
            if (!this.mapconstraints.containsKey(ic.getIndex()) && isAValidExpression(ic)) {
                ExpressionSConstraint c = new ExpressionSConstraint(getMod2Sol().buildNode(ic));
                c.setScope(this);
                getMod2Sol().storeExpressionSConstraint(ic, c);
                SConstraint intensional = cleverExp.getIntentionalConstraint(c, this);
                if (intensional != null) {
                    c.setKnownIntensionalConstraint(intensional);
                } else {
                    if (cleverExp.encompassDiff(c)) {
                       IntegerVariable[] vars = ((AbstractConstraint) ic).getIntVariableScope();
                       neqToAdd.add(Choco.neq(vars[0],vars[1]));
                    }
                }
            }
        }
        for (Constraint aNeqToAdd : neqToAdd) {
            m.addConstraint(aNeqToAdd);
        }
    }

    // ############################################################################################################
    // ######                   Cliques of NEQ Detection                                           ###
    // ############################################################################################################


    public void detectCliques(Model m) {
        CliqueDetector cdetect = new CliqueDetector((CPModel) m);
        if (cdetect.addAllNeqEdges()) {
            CliqueDetector.CliqueIterator it = cdetect.cliqueIterator();
            while (it.hasNext()) {
                IntegerVariable[] cl = it.next();
                if (cl.length > 2) {
                    m.addConstraint(allDifferent(cl));
                    symb.setMaxClique(cl);
                    it.remove();
                } else m.addConstraint(Choco.neq(cl[0],cl[1]));
            }
        }
    }

    // ############################################################################################################
    // ######                   Cliques of Disjonction                                            ###
    // ############################################################################################################


    public int[] getVarIndexes(IntegerVariable[] vs) {
        int[] idxs = new int[vs.length];
        for (int i = 0; i < idxs.length; i++) {
            idxs[i] = vs[i].getHook();
        }
        return idxs;
    }

    public void detectDisjonctives(Model m) {
        CliqueDetector cdetect = new CliqueDetector((CPModel) m);
        int[] durations = cdetect.addAllDisjunctiveEdges(cleverExp, this);
        if (durations != null) {
            BitSet[] precedenceAlreadyAdded = new BitSet[m.getNbIntVars()];
            for (int i = 0; i < m.getNbIntVars(); i++)
                precedenceAlreadyAdded[i] = new BitSet();

            CliqueDetector.CliqueIterator it = cdetect.cliqueIterator();
            while (it.hasNext()) {
                IntegerVariable[] cl = it.next();
                int[] idxs = getVarIndexes(cl);
                int[] dur = new int[cl.length];
                TaskVariable[] tasks = new TaskVariable[cl.length];
                for (int i = 0; i < cl.length; i++) {
                    dur[i] = durations[idxs[i]];
                    tasks[i] = Choco.makeTaskVar("", cl[i], constant(dur[i]));
                }
                //automatically add reified precedences to make branching easier
                for (int j = 0; j < cl.length; j++) {
                    for (int k = j + 1; k < cl.length; k++) {
                        if (!precedenceAlreadyAdded[idxs[j]].get(idxs[k])) {
                            IntegerVariable b = makeIntVar("" + (dur[j] + dur[k]), 0, 1);
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

    private void detectEqualitiesOnIntegers(Model m) {
        int n = m.getNbIntVars();
        ISparseMatrix matrix = new BooleanSparseMatrix(n);
        Iterator<Constraint> iteq = m.getConstraintByType(ConstraintType.EQ);
        Constraint c;
        // Run over equalities constraints, and create edges
        while(iteq.hasNext()){
            c = iteq.next();
            Variable v1 = c.getVariables()[0];
            Variable v2 = c.getVariables()[1];
            if(v1.getVariableType()== VariableType.INTEGER
                    && v2.getVariableType()== VariableType.INTEGER){
            	matrix.add(v1.getHook(), v2.getHook());
                this.mapconstraints.put(c.getIndex(), null);
            }
        }
        if(matrix.getNbElement()> 0){
            matrix.prepare();
            // Detect connex components
            int[] color = new int[n];
            Arrays.fill(color, -1);
            TIntObjectHashMap<Domain> domainByColor = new TIntObjectHashMap<Domain>();
            int k = -1;
            Domain dtmp = new Domain();
            Iterator<Long> it = matrix.iterator();
            while(it.hasNext()){
                long v = it.next();
                int i = (int)(v / n);
                int j = (int)(v % n);

                if (color[i]==-1){
                    k++;
                    color[i]=k;
                    domainByColor.put(k, new Domain(m.getIntVar(i)));
                }
                Domain d = domainByColor.get(color[i]);
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
                        domainByColor.put(k, new Domain(m.getIntVar(j)));
                    }
                }
            }
            IntDomainVar[] var = new IntDomainVar[k+1];
            IntegerVariable vtmp;
            for(int i = 0; i < n; i++){
                int col = color[i];
                if(col !=-1){
                    IntegerVariable v = m.getIntVar(i);
                    if(var[col] == null){
                        dtmp = domainByColor.get(col);
                        if(dtmp.values != null){
                            vtmp = new IntegerVariable(v.getName(), VariableType.INTEGER, dtmp.values);
                        }else{
                            vtmp = new IntegerVariable(v.getName(), VariableType.INTEGER, dtmp.low, dtmp.upp);
                        }
                        vtmp.addOptions(dtmp.options);
                        vtmp.findManager(model.properties);
                        var[col] = (IntDomainVar)mod2sol.readModelVariable(vtmp);
                    }
                    this.mapvariables.put(v.getIndex(), var[col]);
                }
            }
        }
    }

    private void detectEqualitiesOnTasks(Model m) {
        int n = m.getNbStoredMultipleVars();
        ISparseMatrix matrix = new BooleanSparseMatrix(n);
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
            int[] color = new int[n];
            Arrays.fill(color, -1);
            TIntObjectHashMap<TaskObjects> domainByColor = new TIntObjectHashMap<TaskObjects>();
            int k = -1;
            TaskObjects dtmp = new TaskObjects();
            Iterator<Long> it = matrix.iterator();
            while(it.hasNext()){
                long v = it.next();
                int i = (int)(v / n);
                int j = (int)(v % n);

                if (color[i]==-1){
                    k++;
                    color[i]=k;
                    domainByColor.put(k, new TaskObjects((TaskVariable)m.getStoredMultipleVar(i)));
                }
                TaskObjects d = domainByColor.get(color[i]);
                //backup
                d.merge((TaskVariable)m.getStoredMultipleVar(j));
                color[j] = color[i];
                domainByColor.put(color[i], d);
            }
            TaskVar[] var = new TaskVar[k+1];
            TaskVariable vtmp;
            for(int i = 0; i < n; i++){
                int col = color[i];
                if(col !=-1){
                    TaskVariable v = (TaskVariable)m.getStoredMultipleVar(i);
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



    private static class Domain{
        protected int low;
        protected int upp;
        // values is null if domain is bounded
        int[] values;
        HashSet<String> options;

        private Domain() {
            options = new HashSet<String>();
        }

        private Domain(IntegerVariable v) {
            this();
            low = v.getLowB();
            upp = v.getUppB();
            options = v.getOptions();
        }

        public void copy(Domain d){
            low = d.low;
            upp = d.upp;
            if(d.values != null){
                values = new int[d.values.length];
                System.arraycopy(d.values, 0, values, 0, values.length);
            }
            options = d.options;
        }

        private int[] enumVal(){
            if(values != null){
                if(values.length==2 && values[0]==values[1]){
                    return new int[]{values[0]};
                }
                return values;
            }else{
                int[] val = new int[upp-low+1];
                for(int i = 0; i < val.length; i++){
                    val[i] = low+i;
                }
                return val;
            }
        }

        /**
         * intersection of the current domain and v
         * @param v the variable to intersect with
         * @return true if the two domains intersect
         */
        public boolean intersection(IntegerVariable v){
            if(v.getValues() == null && this.values == null){
                this.low = Math.max(this.low, v.getLowB());
                this.upp = Math.min(this.upp, v.getUppB());
                if(low>upp){
                    return false;
                }
            }else{
                int[] val = new int[Math.min((this.upp-this.low+1), v.getDomainSize())];
                int size = 0;
                int[] ev1 = this.enumVal();
                int[] ev2 = v.enumVal();
                for (int anEv1 : ev1) {
                    for (int anEv2 : ev2) {
                        if (anEv1 == anEv2) {
                            val[size++] = anEv1;
                        }
                    }
                }
                //<cpru> bidouille...
                if(size>0){
                    values = new int[size];
                    System.arraycopy(val, 0, values, 0, size--);
                    this.low = values[0];
                    this.upp = values[size];
                }else{
                    return false;
                }
            }
            HashSet<String> tOptions = new HashSet<String>();
            if(v.getOptions().contains("cp:decision")
                    || options.contains("cp:decision")){
                tOptions.add("cp:decision");
            }
            if(v.getOptions().contains("cp:objective")
                    || options.contains("cp:objective")){
                tOptions.add("cp:objective");
            }
            // Type copy
            if(v.getOptions().contains("cp:binary")
                    || options.contains("cp:binary")){
                tOptions.add("cp:binary");
            }else if(v.getOptions().contains("cp:btree")
                    || options.contains("cp:btree")){
                tOptions.add("cp:btree");
            }else if(v.getOptions().contains("cp:enum")
                    || options.contains("cp:enum")){
                tOptions.add("cp:enum");
            }else if(v.getOptions().contains("cp:blist")
                    || options.contains("cp:blist")){
                tOptions.add("cp:blist");
            }else if(v.getOptions().contains("cp:link")
                    || options.contains("cp:link")){
                tOptions.add("cp:link");
            }else if(v.getOptions().contains("cp:bound")
                    || options.contains("cp:bound")){
                tOptions.add("cp:bound");
            }
            this.options = tOptions;
            return true;
        }

    }


    private static class TaskObjects{
        protected IntegerVariable start;
        protected IntegerVariable duration;
        protected IntegerVariable end;

        HashSet<String> options;

        private TaskObjects() {
            options = new HashSet<String>();
        }

        private TaskObjects(TaskVariable v) {
            this();
            start = v.start();
            duration = v.duration();
            end = v.end();
            options = v.getOptions();
        }

        public void merge(TaskVariable d){
            if(start  == null){
                start = d.start();
            }
            if(duration == null){
                duration = d.duration();
            }
            if(end == null){
                end = d.end();
            }
            HashSet<String> tOptions = new HashSet<String>();
            if(d.getOptions().contains("cp:decision")
                    || options.contains("cp:decision")){
                tOptions.add("cp:decision");
            }
            if(d.getOptions().contains("cp:objective")
                    || options.contains("cp:objective")){
                tOptions.add("cp:objective");
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
	public boolean rootNodeSingleton(boolean doSingleton, int timelimit) {
		if (!doSingleton) return true;
        boolean sched = isScheduling();
        int time = (int) System.currentTimeMillis();
        if (proptime <= 1500) {
			int nbvrem = 0;
		    int nbvtried = 0;
			int threshold = 100;
			worldPush();
			for (int i = 0; i < getNbIntVars(); i++) {
				IntDomainVar v = (IntDomainVar) getIntVar(i);
				DisposableIntIterator it = v.getDomain().getIterator();
				if ((sched && v.getDomainSize() == 2) || (!sched && v.hasEnumeratedDomain())) {
					while (it.hasNext()) {
						int val = it.next();
						nbvtried ++;
						boolean cont = false;
						worldPush();
						try {
							v.instantiate(val, VarEvent.NOCAUSE);
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
