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
package choco.kernel.model.variables;

import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ExpressionManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/*
 * User:    charles
 * Date: 8 août 2008
 */
public abstract class ComponentVariable extends AbstractVariable implements IComponentVariable{

    protected final Object parameters;
    protected String componentClass;
    protected String variableManager;
    protected String expressionManager;
    protected String name;
    protected Operator operator;
    protected ComponentVariable[] variables;
    protected final ArrayList<Constraint> constraints=new ArrayList<Constraint>();

    //null by default until it has been loaded
    protected VariableManager vm;
    protected ExpressionManager em;


    public ComponentVariable(final VariableType variableType, final Operator operator, final Object parameters, final String name, final ComponentVariable... vars) {
        super(variableType);
        build(name, vars);
        this.parameters = parameters;
        this.operator = operator;
    }

    public ComponentVariable(final VariableType variableType, final String operatorManager, final Object parameters, final String name, final ComponentVariable... vars) {
        super(variableType);
        build(name, vars);
        this.parameters = parameters;
        this.expressionManager = operatorManager;
    }

    public ComponentVariable(final VariableType variableType, final Class operatorClass, final Object parameters, final String name, final ComponentVariable... vars) {
        super(variableType);
        build(name, vars);
        this.expressionManager = operatorClass.getName();
        this.parameters = parameters;
    }

    private void build(final String name, final ComponentVariable... vars){
        this.name = name;
        this.variables = vars;
    }

    public String getComponentClass() {
        return variableManager;
    }

    public String getOperatorClass(){
        if(expressionManager!=null){
            return expressionManager;
        }
        return variableManager;
    }


    public Object getParameters() {
        return parameters;
    }

    public Variable[] getVariables() {
        return variables;
    }

    public final Variable getVariable(final int idx) {
        return variables[idx];
    }

    public int getNbVars() {
        return variables.length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void addConstraint(Constraint c) {
        if (variables == null) {
            constraints.add(c);
        } else {
            for(Variable v : variables){
                v.addConstraint(c);
            }
        }
    }

    public void removeConstraint(Constraint c) {
        if (variables == null) {
            constraints.remove(c);
        } else {
            for(Variable v : variables){
                v.removeConstraint(c);
            }
        }
    }

    @Deprecated
    public Iterator<Constraint> getConstraintIterator() {
        return new Iterator<Constraint>(){
            int n = 0;
            Iterator<Constraint> it = (variables !=null? variables[n].getConstraintIterator():constraints.iterator());

            public boolean hasNext() {
                if (it == null) {
                    return false;
                }
                while (n < variables.length && !it.hasNext()) {
                    n++;
                    if (n < variables.length) {
                        it = variables[n].getConstraintIterator();
                    }
                }
                return n < variables.length && it.hasNext();
            }

            public Constraint next() {
                return it.next();
            }

            public void remove() {
                it.remove();
            }
        };
    }

    public Iterator<Constraint> getConstraintIterator(final Model m) {
        return new Iterator<Constraint>(){
            int n = 0;
            Iterator<Constraint> it = (variables !=null? variables[n].getConstraintIterator(m):constraints.iterator());
            Constraint c = null;

            public boolean hasNext() {
                while(true){
                    if(it == null){
                        return false;
                    }else
                    if(it.hasNext()){
                        c = it.next();
                        if(Boolean.TRUE.equals(c.alreadyIn(m.getIndex()))){
                            return true;
                        }
                    }else
                    if(n < variables.length){
                        n++;
                        if (n < variables.length) {
                            it = variables[n].getConstraintIterator(m);
                        }
                    }else{
                        return false;
                    }
                }
            }

            public Constraint next() {
                return c;
            }

            public void remove() {
                it.remove();
            }
        };
    }


    public Iterator<Variable> getVariableIterator() {
       return ChocoUtil.iterator(extractVariables());
      }


    public Constraint[] getConstraints() {
        Constraint[] cstr = new Constraint[constraints.size()];
        constraints.toArray(cstr);
        return cstr;
    }

    public final Constraint getConstraint(final int idx) {
        return constraints.get(idx);
    }

    @Deprecated
    public int getNbConstraint() {
        if (variables == null) {
            return constraints.size();
        } else {
            int sum = 0;
            for(Variable v : variables){
                sum = v.getNbConstraint();
            }
            return sum;
        }
    }

    public int getNbConstraint(Model m) {
        int sum = 0;
        if (variables == null) {
            for(Constraint c: constraints){
                if(Boolean.TRUE.equals(c.alreadyIn(m.getIndex()))){
                    sum++;
                }
            }
            return sum;
        } else {
            for(Variable v : variables){
                sum = v.getNbConstraint(m);
            }
            return sum;
        }
    }


     public void findManager(Properties propertiesFile) {
         if (variableManager == null && !type.equals(VariableType.NONE)){
            variableManager = propertiesFile.getProperty(type.property);
         }
         if(expressionManager == null && !operator.equals(Operator.NONE)){
                expressionManager = propertiesFile.getProperty(operator.property);
         }
         if(variableManager == null && expressionManager == null){
             throw new ModelException("Can not find "+type.property+" or "
                     + operator.property+" in application.properties");
         }
     }

    public VariableManager getVm() {
            if (vm == null) {
                vm = (VariableManager)loadManager(getComponentClass());
            }
            return vm;
        }

        public Object loadManager(String manager) {
             //We get it by reflection !
            Class componentClass = null;
            try {
              componentClass = Class.forName(manager);
            } catch (ClassNotFoundException e) {
              LOGGER.severe("Component class could not be found: " + manager);
              System.exit(-1);
            }
            try {
              return componentClass.newInstance();
            } catch (InstantiationException e) {
              LOGGER.severe("Component class could not be instantiated: " + manager);
              System.exit(-1);
            } catch (IllegalAccessException e) {
              LOGGER.severe("Component class could not be accessed: " + manager);
              System.exit(-1);
            }
            return null;
        }


    public ExpressionManager getEm() {
        if (em == null) {
            em = (ExpressionManager)loadManager(getOperatorClass());
        }
        return em;
    }


    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    public String pretty() {
        if (variables == null) {
            return name;
        } else {
            StringBuffer st = new StringBuffer();
            for(Variable v : variables){
                st.append(v.pretty());
            }
            return st.toString();
        }
    }

}
