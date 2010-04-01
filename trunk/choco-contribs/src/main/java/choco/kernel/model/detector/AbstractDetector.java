/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.model.detector;

import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.IHook;
import choco.kernel.model.variables.Variable;
import gnu.trove.THashMap;
import gnu.trove.TLongObjectHashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * An abstract class to set the methods of a detector.
 * A detector analyzes a model and thanks to its analizis, it is allowed to strongly modified the model involved. 
 */
public abstract class AbstractDetector {

    /**
     * Logger
     */
    protected static final Logger LOGGER = ChocoLogging.getEngineLogger();

    /**
     * Model to analyse and transform.
     */
    protected final CPModel model;

    /**
     * Internal structure to store constraint addition instructions
     */
    protected TLongObjectHashMap<Constraint> constraintsToAdd;

    /**
     * Internal structure to store constraint deletion instructions
     */
    protected TLongObjectHashMap<Constraint> constraintsToDelete;

    /**
     * Internal structure to store variable addition instructions
     */
    protected TLongObjectHashMap<Variable> variablesToAdd;

    /**
     * Internal structure to store variable deletion instructions
     */
    protected TLongObjectHashMap<Variable> variablesToDelete;

    /**
     * Internal structure to store variable deletion instructions
     */
    protected THashMap<Variable, Variable> variablesToReplace;

    protected AbstractDetector(final CPModel model) {
        this.model = model;
        constraintsToAdd = new TLongObjectHashMap<Constraint>();
        constraintsToDelete = new TLongObjectHashMap<Constraint>();
        variablesToAdd = new TLongObjectHashMap<Variable>();
        variablesToDelete = new TLongObjectHashMap<Variable>();
        variablesToReplace = new THashMap<Variable, Variable>();
    }

    /**
     * Apply the detection defined within the detector.
     */
    public abstract void apply();

    /**
     * Add a constraint {@code c} to the model which is currently treated by the detector.<br/>
     * The addition is recorded but not done immediatly. It must be "commited" using {@link AbstractDetector#commit()}.
     * @param c contraint to add
     */
    protected final void add(Constraint c){
        constraintsToAdd.put(c.getIndex(), c);
    }

    /**
     * Delete a constraint {@code c} to the model which is currently treated by the detector.<br/>
     * The deletion is recorded but not done immediatly. It must be "commited" using {@link AbstractDetector#commit()}.
     * @param c contraint to delete
     */
    protected final void delete(Constraint c){
        constraintsToDelete.put(c.getIndex(), c);
    }

    /**
     * Remove deletion instruction on {@code c}.
     * @param c contraint to keep
     */
    protected final void keep(Constraint c){
        constraintsToDelete.remove(c.getIndex());
    }

    /**
     * Remove addition instruction on {@code c}.
     * @param c contraint to not add
     */
    protected final void forget(Constraint c){
        constraintsToAdd.remove(c.getIndex());
    }

    /**
     * Add a variable {@code v} to the model which is currently treated by the detector.<br/>
     * The addition is recorded but not done immediatly. It must be "commited" using {@link AbstractDetector#commit()}.
     * @param v variable to add
     */
    protected final void add(Variable v){
        variablesToAdd.put(v.getIndex(), v);
    }

    /**
     * Delete a variable {@code v} from the model which is currently treated by the detector.<br/>
     * The deletion is recorded but not done immediatly. It must be "commited" using {@link AbstractDetector#commit()}.
     * @param v variable to delete
     */
    protected final void delete(Variable v){
        variablesToDelete.put(v.getIndex(), v);
    }

    /**
     * Remove deletion instruction on {@code v}.
     * @param v variable to keep
     */
    protected final void keep(Variable v){
        variablesToDelete.remove(v.getIndex());
    }

    /**
     * Remove addition instruction on {@code v}.
     * @param v contraint to not add
     */
    protected final void forget(Variable v){
        variablesToAdd.remove(v.getIndex());
    }

    /**
     * Replace {@code outVar} by {@code inVar} in every constraint where {@code outVar} is involved.
     * @param outVar deleted variable
     * @param inVar the substitute
     */
    protected final void replaceBy(Variable outVar, Variable inVar){
        variablesToReplace.put(outVar, inVar);
    }

    /**
     * Send changes detected to the treated model.
     */
    public final void commit(){
        for(long l : variablesToAdd.keys()){
            final Variable v = variablesToAdd.get(l);
            model.addVariables(v);
            if(LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("..add variable : %s", v.pretty()));
            }
        }
        for(long l : constraintsToAdd.keys()){
            final Constraint c = constraintsToAdd.get(l);
            model.addConstraint(c);
            if(LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("..add constraint : %s", c.pretty()));
            }
        }
        for(long l : constraintsToDelete.keys()){
            final Constraint c = constraintsToDelete.get(l);
            model.removeConstraint(c);
            if(LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("..delete constraint : %s", c.pretty()));
            }
        }
        for(Variable outVar : variablesToReplace.keySet()){
            final Variable inVar = variablesToReplace.get(outVar);
            for(Constraint c : outVar.getConstraints()){
                c.replaceBy(outVar, inVar);
            }
            if( inVar.getHook() == IHook.NO_HOOK) {
                inVar.setHook(outVar.getHook());
            }
            if(LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("..%s replaced by : %s", outVar.getName(), inVar.getName()));
            }
        }
        for(long l : variablesToDelete.keys()){
            final Variable v = variablesToDelete.get(l);
            model.removeVariable(v);
            if(LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("..delete variable : %s", v.pretty()));
            }
        } 
    }

    /**
     * Remove all uncommited instructions.
     */
    public final void rollback(){
        constraintsToAdd.clear();
        constraintsToDelete.clear();
        variablesToAdd.clear();
        variablesToDelete.clear();
        variablesToReplace.clear();
    }

}
