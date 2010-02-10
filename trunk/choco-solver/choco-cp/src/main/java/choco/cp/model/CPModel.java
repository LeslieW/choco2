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

package choco.cp.model;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.objects.DeterministicIndicedList;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.IOptions;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.ComponentConstraintWithSubConstraints;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.set.SetExpressionVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TLongHashSet;
import gnu.trove.TLongObjectIterator;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;


/**
 * A model is a global structure containing variables bound by listeners
 * as well as solutions or strategy parameters
 */
public class CPModel implements Model {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

    /**
     * Precision of the search for a real model.
     */
    protected double precision = 1.0e-6;
    /**
     * Minimal width reduction between two propagations.
     */
    protected double reduction = 0.99;

    /**
     * All the constraint of the model
     */
    protected DeterministicIndicedList<Constraint> constraints;

    /**
     * All the search intVars in the model.
     */
    protected DeterministicIndicedList<IntegerVariable> intVars;

    protected int nbBoolVar;
    /**
     * All the set intVars in the model.
     */
    protected DeterministicIndicedList<SetVariable> setVars;
    /**
     * All the float vars in the model.
     */
    protected DeterministicIndicedList<RealVariable> floatVars;

    /**
     * All the constant vars in the model
     */
    protected DeterministicIndicedList<Variable> constantVars;
    /**
     * All the search intVars in the model.
     */
    protected DeterministicIndicedList<IntegerExpressionVariable> expVars;

    protected DeterministicIndicedList<MultipleVariables> storedMultipleVariables;

    /**
     * Map that gives for type of contraints, a list of contraints of that type
     */
    private EnumMap<ConstraintType, TIntHashSet> constraintsByType;

    /**
     * Maximization / Minimization model
     */
    protected boolean doMaximize;

    /**
     * Decomposed expression
     */
    protected Boolean defDecExp;

    protected ComponentConstraintWithSubConstraints clausesStore = null;

    /**
     * Properties file
     */
    public final Properties properties;

    /**
     * Constructor.
     * Create srtuctures for a model of constraint programming.
     */
    public CPModel() {
        this(32,32,32,32,32,32,32);
    }

    /**
     * Constructor.
     * Create srtuctures for a model of constraint programming.
     * @param nbCstrs estimated number of constraints
     * @param nbIntVars estimated number of integer variables
     * @param nbSetVars estimated number of set variables
     * @param nbRealVars estimated number of real variables
     * @param nbCsts estimated number of constants
     * @param nbExpVars estimated number of expression variables
     * @param nbMultVars estimated number of multiples variables
     */
    public CPModel(int nbCstrs, int nbIntVars, int nbSetVars, int nbRealVars, int nbCsts, int nbExpVars, int nbMultVars){
        intVars = new DeterministicIndicedList<IntegerVariable>(IntegerVariable.class, nbIntVars);
        setVars = new DeterministicIndicedList<SetVariable>(SetVariable.class, nbSetVars);
        floatVars = new DeterministicIndicedList<RealVariable>(RealVariable.class, nbRealVars);
        constantVars = new DeterministicIndicedList<Variable>(Variable.class, nbCsts);
        expVars = new DeterministicIndicedList<IntegerExpressionVariable>(IntegerExpressionVariable.class, nbExpVars);
        storedMultipleVariables = new DeterministicIndicedList<MultipleVariables>(MultipleVariables.class, nbMultVars);
        constraints = new DeterministicIndicedList<Constraint>(Constraint.class, nbCstrs);

        constraintsByType = new EnumMap<ConstraintType, TIntHashSet>(ConstraintType.class);
        properties = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream( "/application.properties" );
            properties.load(is);
        } catch (IOException e) {
            LOGGER.severe("Could not open application.properties");
        }
    }

    /**
     * Preprocessing that helps the garbage collector.
     */
    @Override
    public void freeMemory() {
        for(Iterator<IntegerVariable> it = intVars.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        intVars.clear();
        for(Iterator<SetVariable> it = setVars.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        setVars.clear();
        for(Iterator<RealVariable> it = floatVars.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        floatVars.clear();
        for(Iterator<Variable> it = constantVars.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        constantVars.clear();
        for(Iterator<IntegerExpressionVariable> it = expVars.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        expVars.clear();
        for(Iterator<MultipleVariables> it = storedMultipleVariables.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        storedMultipleVariables.clear();
        for(Iterator<Constraint> it = constraints.iterator();it.hasNext();){
            it.next().freeMemory();
        }
        constraints.clear();
        constraintsByType.clear();
        if(clausesStore!=null){
            clausesStore.freeMemory();
            clausesStore = null;
        }
    }

    public String pretty() {
        StringBuffer buf = new StringBuffer("Pb[" + getNbTotVars() + " vars, "+getNbStoredMultipleVars() + " multiple vars, " + getNbConstraints() + " cons]\n");
        buf.append(this.varsToString());
        buf.append(this.constraintsToString());
        return new String(buf);
    }

    public String varsToString() {
        StringBuffer buf = new StringBuffer("==== VARIABLES ====\n");
        buf.append(StringUtils.prettyOnePerLine(intVars.iterator()));
        buf.append(StringUtils.prettyOnePerLine(floatVars.iterator()));
        buf.append(StringUtils.prettyOnePerLine(setVars.iterator()));
        buf.append(StringUtils.prettyOnePerLine(constantVars.iterator()));

        buf.append("==== MULTIPLE VARIABLES ====\n");
        buf.append(StringUtils.prettyOnePerLine(storedMultipleVariables.iterator()));

        return new String(buf);
    }

    public String constraintsToString() {
        StringBuffer buf = new StringBuffer("==== CONSTRAINTS ====\n");
        buf.append(StringUtils.prettyOnePerLine(constraints.iterator()));
        return new String(buf);
    }


    public String solutionToString() {
        StringBuffer buf = new StringBuffer();
        buf.append(StringUtils.prettyOnePerLine(intVars.iterator()));
        buf.append(StringUtils.prettyOnePerLine(floatVars.iterator()));
        buf.append(StringUtils.prettyOnePerLine(setVars.iterator()));
        buf.append(StringUtils.prettyOnePerLine(constantVars.iterator()));
        return new String(buf);
    }


    @Deprecated
    public int getIntVarIndex(IntDomainVar c) {
        throw new ModelException("CPModel: ?");
    }

    @Deprecated
    public int getIntVarIndex(IntVar c) {
        throw new ModelException("CPModel: ?");
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getReduction() {
        return reduction;
    }

    public void setReduction(double reduction) {
        this.reduction = reduction;
    }

    public Boolean getDefaultExpressionDecomposition() {
        return defDecExp;
    }

    public void setDefaultExpressionDecomposition(Boolean defDecExp) {
        this.defDecExp = defDecExp;
    }

    /**
     * <i>Network management:</i>
     * Retrieve a variable by its index (all integer variables of
     * the model are numbered in sequence from 0 on)
     *
     * @param i index of the variable in the model
     */

    public final IntegerVariable getIntVar(int i) {
        return intVars.get(i);
    }

    /**
     * retrieving the total number of variables
     *
     * @return the total number of variables in the model
     */
    public final int getNbIntVars() {
        return intVars.size();
    }

    /**
     * Returns a real variable.
     *
     * @param i index of the variable
     * @return the i-th real variable
     */
    public final RealVariable getRealVar(int i) {
        return floatVars.get(i);
    }

    /**
     * Returns the number of variables modelling real numbers.
     */
    public final int getNbRealVars() {
        return floatVars.size();
    }

    /**
     * Returns a set variable.
     *
     * @param i index of the variable
     * @return the i-th real variable
     */
    public final SetVariable getSetVar(int i) {
        return setVars.get(i);
    }

    /**
     * Returns the number of variables modelling real numbers.
     */
    public final int getNbSetVars() {
        return setVars.size();
    }


    /**
     * @see choco.kernel.model.Model#getNbTotVars()
     */
    @Override
    public int getNbTotVars() {
        return getNbIntVars() + getNbRealVars() + getNbSetVars();
    }


    /**
     * Returns a constant variable.
     *
     * @param i index of the variable
     * @return the i-th real variable
     */
    public IntegerConstantVariable getConstantVar(int i) {
        return (IntegerConstantVariable) constantVars.get(i);
    }

    /**
     * Returns the number of variables modelling constant.
     */
    public int getNbConstantVars() {
        return constantVars.size();
    }


    @Override
	public int getNbStoredMultipleVars() {
    	return storedMultipleVariables.size();
	}


	@Override
	public MultipleVariables getStoredMultipleVar(int i) {
		return storedMultipleVariables.get(i);
	}


	/**
     * retrieving the total number of constraint
     *
     * @return the total number of constraints in the model
     */
    public final int getNbConstraints() {
        return constraints.size();
    }


    /**
     * <i>Network management:</i>
     * Retrieve a constraint by its index.
     *
     * @param i index of the constraint in the model
     */
    public final Constraint getConstraint(int i) {
//        return (Constraint)constraints.getValues()[i];
        return constraints.get(i);
    }

    /**
     * Return an iterator over the integer constraints of the model
     *
     * @return an iterator over the integer constraints of the model
     * @deprecated
     * @see choco.cp.model.CPModel#getConstraintIterator()
     */
    @Deprecated
    @Override
    public Iterator<Constraint> getIntConstraintIterator() {
        return getConstraintIterator();
    }

    /**
     * Return an iterator over the constraints of the model
     *
     * @return an iterator over the constraints of the model
     */
    @Override
    public Iterator<Constraint> getConstraintIterator() {
//        return new TroveIterator(constraints.iterator());
        return constraints.iterator();
    }


    public Iterator<Constraint> getConstraintByType(ConstraintType t) {
        final TIntHashSet hs = constraintsByType.get(t);
        if (hs != null) {
            return new Iterator<Constraint>(){
                TIntIterator it = hs.iterator();
                /**
                 * Returns <tt>true</tt> if the iteration has more elements. (In other
                 * words, returns <tt>true</tt> if <tt>next</tt> would return an element
                 * rather than throwing an exception.)
                 *
                 * @return <tt>true</tt> if the iterator has more elements.
                 */
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                /**
                 * Returns the next element in the iteration.
                 *
                 * @return the next element in the iteration.
                 * @throws java.util.NoSuchElementException
                 *          iteration has no more elements.
                 */
                @Override
                public Constraint next() {
                    return constraints.get(it.next());
                }

                /**
                 * Removes from the underlying collection the last element returned by the
                 * iterator (optional operation).  This method can be called only once per
                 * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
                 * the underlying collection is modified while the iteration is in
                 * progress in any way other than by calling this method.
                 *
                 * @throws UnsupportedOperationException if the <tt>remove</tt>
                 *                                       operation is not supported by this Iterator.
                 * @throws IllegalStateException         if the <tt>next</tt> method has not
                 *                                       yet been called, or the <tt>remove</tt> method has already
                 *                                       been called after the last call to the <tt>next</tt>
                 *                                       method.
                 */
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
		}
        return new EmptyIterator();
    }

    public int getNbConstraintByType(ConstraintType t) {
        TIntHashSet hs = constraintsByType.get(t);
        if (hs != null) {
			return hs.size();
		} else {
			return 0;
		}
    }

    @Override
    public <E extends IOptions> void addOption(String option, E... element) {
        for (E anElement : element) {
            anElement.addOption(option);
        }
    }

    /**
     * Add a variable to the model
     *
     * @param v a variable
     * <p/>
     *             This method use default options.
     *             But User can define its own one.
     *             See CPModel.addVariable(String options, Variable... tabv) for more details.
     */
    public void addVariable(Variable v) {
        this.addVariables("", v);
    }

    /**
     * Add one variable with options to the model
     *
     * @param options define options of the variables
     * <p/>
     *             This method use default options.
     *             But User can define its own one.
     *             See CPModel.addVariable(String options, Variable... tabv) for more details.
     * @param v       one or more variables
     */
    public void addVariable(String options, Variable v) {
        this.addVariables(options, v);
    }

    /**
     * Add one or more variables to the model
     *
     * @param v one or more variables
     * @see choco.kernel.model.Model#addVariables(choco.kernel.model.variables.Variable[])
     * @deprecated
     */
    @Deprecated
    public void addVariable(Variable... v) {
        this.addVariables("", v);
    }

    /**
     * Add one or more variables to the model with particular options
     *
     * @param options defines options of the variables
     * @param v       one or more variables
     * @see choco.kernel.model.Model#addVariables(String, choco.kernel.model.variables.Variable[])
     * @deprecated
     */
    @Deprecated
    public void addVariable(String options, Variable... v) {
        this.addVariables(options, v);
    }

    /**
     * Add variables to the model.
     *
     * @param tabv : variables to add
     *             <p/>
     *             This method use default options.
     *             But User can define its own one.
     *             See CPModel.addVariable(String options, Variable... tabv) for more details.
     */
    public void addVariables(Variable... tabv) {
        this.addVariables("", tabv);
    }


    /**
     * Add variables to CPModel.
     *
     * @param options : String that allows user to precise som parameters to the model concerning the variable tabv
     * @param tabv    : variables to add
     *                <p/>
     *                Options of CPModel must be prefix with "cp:".
     *                For IntegerVariable, available options are :
     *                <ul>
     *                <i> cp:enum to force Solver to create enumerated variables (default options if options is empty)</i>
     *                <i> cp:bound to force Solver to create bounded variables</i>
     *                <i> cp:btree to force Solver to create binary tree variables</i>
     *                <i> cp:link to force Solver to create linked list variables</i>
     *                </ul>
     *                <p/>
     *                For SetVariable, available options are :
     *                <ul>
     *                <i> cp:enum to force Solver to create set variables with enumerated caridinality (default options if options is empty)</i>
     *                <i> cp:bound to force Solver to create set variables with bounded cardinality</i>
     *                </ul>
     *                No options are available concerning Real variables.
     *                <p/>
     *                Options for decisionnal/undecisionnal variables
     *                <ul>
     *                <i>cp:decision to force variable to be a decisional one</i>
     *                <i>cp:no_decision to force variable to be removed from the pool of decisionnal variables</i>
     *                </ul>
     *                Options for optimization
     *                <ul>
     *                <i>cp:objective to define the variable to optimize
     *                </ul>
     */
    public void addVariables(String options, Variable... tabv) {
        Variable v;
        Iterator<Variable> it;
        for (Variable aTabv : tabv) {
            v = aTabv;
            if (options != null && !"".equals(options)) {
                String[] optionsStrings = options.split(" ");
                for (String optionsString : optionsStrings) {
                    v.addOption(optionsString);
                }
            }
            v.findManager(properties);
            switch (v.getVariableType()) {
                case INTEGER:
                    // It is necessary to detect boolean variable as soon as possible
                    if (((IntegerVariable) v).isBoolean()) {
                        nbBoolVar++;
                    }
                    //addVariable((IntegerVariable) v, intVars);
                    intVars.add((IntegerVariable)v);
                    break;
                case SET:
//                    addVariable((SetVariable) v, setVars);
                    setVars.add((SetVariable) v);
                    addVariable(options, ((SetVariable) v).getCard());
                    break;
                case REAL:
//                    addVariable((RealVariable) v, floatVars);
                    floatVars.add((RealVariable) v);
                    break;
                case CONSTANT_INTEGER:
                    // It is necessary to detect boolean variable as soon as possible
                    if (((IntegerConstantVariable) v).isBoolean()) {
                        nbBoolVar++;
                    }
//                    addVariable(v, constantVars);
                    constantVars.add(v);
                    break;
                case CONSTANT_DOUBLE:
//                    addVariable(v, constantVars);
                    constantVars.add(v);
                    break;
                case CONSTANT_SET:
//                    addVariable(v, constantVars);
                    constantVars.add(v);
                    addVariable(options, ((SetVariable) v).getCard());
                    break;
                case INTEGER_EXPRESSION:
                    IntegerExpressionVariable iev = (IntegerExpressionVariable) v;
                    it = iev.getVariableIterator();
                    while (it.hasNext()) {
                        this.addVariable(it.next());
                    }
                    break;
                case SET_EXPRESSION:
                    SetExpressionVariable sev = (SetExpressionVariable) v;
                    it = sev.getVariableIterator();
                    while (it.hasNext()) {
                        this.addVariable(it.next());
                    }
                    break;
                case REAL_EXPRESSION:
                    RealExpressionVariable rev = (RealExpressionVariable) v;
                    it = rev.getVariableIterator();
                    while (it.hasNext()) {
                        this.addVariable(it.next());
                    }
                    break;
                case MULTIPLE_VARIABLES:
                    MultipleVariables mv = (MultipleVariables) v;
                    it = mv.getVariableIterator();
                    while (it.hasNext()) {
                        this.addVariable(it.next());
                    }
                    if (mv.isStored()
                            && !storedMultipleVariables.contains(mv)) {
                        storedMultipleVariables.add(mv);
                    }
                    break;
                default:
                    throw new ModelException("unknown variable type :" + v.getVariableType());
            }
        }
    }

    /**
     * return the number of boolean variable (with binary domain) of the model
     * @return int
     */
    public int getNbBoolVar(){
        return nbBoolVar;
    }

    @Deprecated
    protected <E extends Variable> void removeVariable(E v, DeterministicIndicedList<E> vars) {
            vars.remove(v);
            Iterator<Constraint> it = v.getConstraintIterator(this);
            while (it.hasNext()) {
                Constraint c = it.next();
                it.remove();
                this.removeConstraint(c);
            }
    }



    protected <E extends Variable> void remVariable(E v) {
        Iterator<Variable> it;
        switch (v.getVariableType()) {
            case INTEGER:
                IntegerVariable vi = (IntegerVariable)v;
                intVars.remove(vi);
                break;
            case SET:
                SetVariable sv  = (SetVariable)v;
                intVars.remove(sv.getCard());
                setVars.remove(sv);
                break;
            case REAL:
                RealVariable rv = (RealVariable)v;
                floatVars.remove(rv);
                break;
            case CONSTANT_INTEGER:
            case CONSTANT_DOUBLE:
            case CONSTANT_SET:
                // a constant cannot be removed from a model
                return;
            case INTEGER_EXPRESSION:
                    IntegerExpressionVariable iev = (IntegerExpressionVariable) v;
                    it = iev.getVariableIterator();
                    while (it.hasNext()) {
                        this.removeVariable(it.next());
                    }
                    break;
                case SET_EXPRESSION:
                    SetExpressionVariable sev = (SetExpressionVariable) v;
                    it = sev.getVariableIterator();
                    while (it.hasNext()) {
                        this.removeVariable(it.next());
                    }
                    break;
                case REAL_EXPRESSION:
                    RealExpressionVariable rev = (RealExpressionVariable) v;
                    it = rev.getVariableIterator();
                    while (it.hasNext()) {
                        this.removeVariable(it.next());
                    }
                    break;
            case MULTIPLE_VARIABLES:
                MultipleVariables mv = (MultipleVariables) v;
                it = mv.getVariableIterator();
                while (it.hasNext()) {
                    this.removeVariable(it.next());
                }
                break;
            default:
                throw new ModelException("unknown variable type :" + v.getVariableType());
        }
    }

    public void remove(Object ob) {
        Constraint c;
        Variable v;
        // Liste des contraintes à supprimer
        TLongHashSet conSet = new TLongHashSet();
        // Liste des variables à supprimer
        TLongHashSet varSet  = new TLongHashSet();

        // Liste des contraintes à supprimer
        Queue<Constraint> conQueue  = new ArrayDeque<Constraint>();
        // Liste des variables à supprimer
        Queue<Variable> varQueue  = new ArrayDeque<Variable>();

        if(ob instanceof Constraint){
            c = (Constraint)ob;
            conQueue.add(c);
            Constraint clast = constraints.getLast();
            int id = constraints.remove(c);
            TIntHashSet hs = constraintsByType.get(c.getConstraintType());
            hs.remove(id);
            if(id!=-1 && clast!=null && c.getIndex()!= clast.getIndex()){
                hs = constraintsByType.get(clast.getConstraintType());
                hs.remove(constraints.size());
                hs.add(id);
            }
        }else if(ob instanceof Variable){
            v = (Variable)ob;
            varQueue.add(v);
            remVariable(v);
        }
        Iterator<Variable> itv;
        Iterator<Constraint> itc;
        while(!(varQueue.isEmpty() && conQueue.isEmpty())){
            if(!conQueue.isEmpty()){
                c = conQueue.remove();
                itv = c.getVariableIterator();
                while(itv.hasNext()){
                    v= itv.next();
                    v._removeConstraint(c);
                    if(v.getNbConstraint(this) == 0 && !varSet.contains(v.getIndex())){
                        remVariable(v);
                        varQueue.add(v);
                    }
                }
                conSet.add(c.getIndex());
            }
            if(!varQueue.isEmpty()){
                v = varQueue.remove();
                itc= v.getConstraintIterator(this);
                while(itc.hasNext()){
                    c = itc.next();
                    if(!conSet.contains(c.getIndex())){
                        conQueue.add(c);
                        Constraint clast = constraints.getLast();
                        int id = constraints.remove(c);
                        TIntHashSet hs = constraintsByType.get(c.getConstraintType());
                        hs.remove(id);
                        if(id!=-1 && clast!=null && c.getIndex()!= clast.getIndex()){
                            hs = constraintsByType.get(clast.getConstraintType());
                            hs.remove(constraints.size());
                            hs.add(id);
                        }
                    }
                }
                varSet.add(v.getIndex());
            }
        }
    }

    public void removeConstraint(Constraint c){
        this.remove(c);
    }

    /**
     * Remove one or more variables from the model
     * (also remove constraints linked to the variables)
     *
     * @param v variables to remove
     * @see choco.kernel.model.Model#removeVariables(choco.kernel.model.variables.Variable[])
     * @deprecated
     */
    @Deprecated
    public void removeVariable(Variable... v) {
        for (Variable aV : v) {
            this.remove(aV);
        }
    }

    /**
     * Remove one variable from the model
     * (also remove constraints linked to the variable)
     *
     * @param v the variable to remove
     */
    public void removeVariable(Variable v) {
        this.remove(v);
    }

    /**
     * Remove one or more variables from the model
     * (also remove constraints linked to the variables)
     *
     * @param v variables to remove
     */
    public void removeVariables(Variable... v) {
        for (Variable aV : v) {
            this.remove(aV);
        }
    }

    /**
     * Add constraint into constraintsByType collections
     *
     * @param t type of constraint
     * @param c the constraint to add
     */
    private void updateConstraintByType(ConstraintType t, Constraint c) {
        TIntHashSet hs = constraintsByType.get(t);
        if (hs == null) {
            hs = new TIntHashSet();
        }
        int id = constraints.get(c);
        if(!hs.contains(id)){
            hs.add(id);
        }
        constraintsByType.put(t, hs);
    }

    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     *
     * @param c one or more constraint
     * @see choco.kernel.model.Model#addConstraints(choco.kernel.model.constraints.Constraint[])
     * @deprecated
     */
    @Deprecated
    public void addConstraint(Constraint... c) {
        this.addConstraints("", c);
    }
    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     *
     * @param options defines options of the constraint
     * @param c       one or more constraint
     * @see choco.kernel.model.Model#addConstraints(choco.kernel.model.constraints.Constraint[])
     * @deprecated
     */
    @Deprecated
    public void addConstraint(String options, Constraint... c) {
        this.addConstraints(options, c);
    }

    /**
     * Add one constraint to the model.
     * Also add variables to the model if necessary.
     *
     * @param c one constraint
     */
    public void addConstraint(Constraint c) {
        this.addConstraints("", c);
    }

    /**
     * Add one or more constraint to the model.
     * Also add variables to the model if necessary.
     *
     * @param c one or more constraint
     */
    public void addConstraints(Constraint... c) {
        this.addConstraints("", c);
    }

    /**
     * Add one constraint to the model.
     * Also add variables to the model if necessary.
     *
     * @param options defines options of the constraint
     * @param c       one constraint
     */
    public void addConstraint(String options, Constraint c) {
        this.addConstraints(options, c);
    }


    /**
     * Add constraints to the model
     *
     * @param options : options of constraint
     * @param tabc    : constraints to add
     *                <p/>
     *                Options of CPModel must be prefixed with cp.
     *                The following options are available:
     *                <ul>
     *                <i>cp:decomp to force decomposition on particular expression constraint</i>
     *                </ul>
     */
    public void addConstraints(String options, Constraint... tabc) {
        Constraint c;
        for (Constraint aTabc : tabc) {
            c = aTabc;
            if (options != null && !"".equals(options)) {
                String[] optionsStrings = options.split(" ");
                for (String optionsString : optionsStrings) {
                    c.addOption(optionsString);
                }
            }
            c.findManager(properties);
            switch (c.getConstraintType()) {
                case CLAUSES:
                    storeClauses((ComponentConstraint) c);
                    break;
                default:
                    if (!constraints.contains(c)) {
                        constraints.add(c);
                    }
                    break;
            }

            updateConstraintByType(c.getConstraintType(), c);
            Iterator<Variable> it = c.getVariableIterator();
            while (it.hasNext()) {
                Variable v = it.next();
                if (v == null) {
                    LOGGER.severe("Adding null variable in the model !");
                }
                addVariable(v);
                if (v.getVariableType() != VariableType.CONSTANT_INTEGER
                        && v.getVariableType() != VariableType.CONSTANT_SET) {
                    v._addConstraint(c);
                }
            }
        }
    }

    /**
     * Data structure to deal with clauses
     * @param clause clause to add
     */
    private void storeClauses(ComponentConstraint clause){
        if(clausesStore==null){
            clausesStore = new ComponentConstraintWithSubConstraints(ConstraintType.CLAUSES, clause.getVariables(), null, clause);
            HashSet<String> opt = clause.getOptions();
            for (String anOpt : opt) {
                clausesStore.addOption(anOpt);
            }
            clausesStore.findManager(properties);
            constraints.add(clausesStore);

        }else{
            clausesStore.addElements(clause.getVariables(), new Constraint[]{clause});
        }
    }


    public Iterator<IntegerVariable> getIntVarIterator() {
        return intVars.iterator();
    }

    public Iterator<RealVariable> getRealVarIterator() {
        return floatVars.iterator();
    }

    public Iterator<SetVariable> getSetVarIterator() {
        return setVars.iterator();
    }

    public Iterator<Variable> getConstVarIterator() {
        return constantVars.iterator();
    }

    public Iterator<IntegerExpressionVariable> getExprVarIterator() {
        return expVars.iterator();
    }

    public Iterator<MultipleVariables> getMultipleVarIterator() {
        return storedMultipleVariables.iterator();
    }

    static class EmptyIterator implements Iterator<Constraint> {
        public boolean hasNext() {
            return false;
        }

        public Constraint next() {
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
    }
  }
    static class TroveIterator implements Iterator<Constraint>{
        private TLongObjectIterator<Constraint> iterator;

        TroveIterator(TLongObjectIterator<Constraint> tit) {
            this.iterator = tit;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Constraint next() {
            iterator.advance();
            return iterator.value();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    public boolean contains(Constraint c){
        return constraints.contains(c);
    }
}
