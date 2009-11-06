/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.shaker.tools.factory;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 mars 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class CPModelFactory {

    VariableFactory vf = new VariableFactory();
    ConstraintFactory cf = new ConstraintFactory();
    OperatorFactory of = new OperatorFactory();
    MetaConstraintFactory mcf = new MetaConstraintFactory();

    //********************************************
    //********* METACONSTRAINTS ******************
    //********************************************
    /**
     * Declare specific metaconstraints to use
     * @param mcs metaconstraint types
     */
    public void uses(MetaConstraintFactory.MC... mcs){
        mcf.scopes(mcs);
    }

    /**
     * Force use of metacontraints
     */
    public void includesMetaconstraints(){
        mcf.scopes(MetaConstraintFactory.MC.values());
    }

    //********************************************
    //************* CONSTRAINTS ******************
    //********************************************
    /**
     * Declare specific constraints to use
     * @param cs constraint types
     */
    public void uses(ConstraintFactory.C... cs){
        cf.scopes(cs);
    }

    //********************************************
    //*************** OPERATORS ******************
    //********************************************
    /**
     * Declare specific operators to use
     * @param os operator types
     */
    public void uses(OperatorFactory.O... os){
        of.scopes(os);
    }

    /**
     * Force use of operators
     */
    public void includesOperators(){
        of.scopes(OperatorFactory.O.values());
    }

    //********************************************
    //**************** VARIABLES *****************
    //********************************************

    /**
     * Declare specific variables to use
     * @param vs variable types
     */
    public void uses(VariableFactory.V... vs){
        vf.scopes(vs);
    }

    /**
     * Define the pool of variables to use
     * @param vars variables
     */
    public void defines(IntegerVariable... vars){
        vf.defines(vars);
    }

    /**
     * Limit the number of variables created to nb
     * @param nb max number of created variables
     */
    public void limits(int nb){
        vf.limits(nb);
    }

    /**
     * Set the maximum domain size
     * @param size domain size
     */
    public void domain(int size){
        vf.domainesize(size);
    }

    //********************************************
    //************** PARAMATERS ******************
    //********************************************

    /**
     * Declare a specific depth for expressions
     * default = 2
     * @param d max depth
     */
    public void depth(int d){
        of.depth(d);
    }


    /**
     * Initialize data structures
     */
    private void init(){
        // declare dependencies
        mcf.depends(cf);
        cf.depends(of, vf);
        of.depends(vf, cf);

        // If no metaconstraints must be used...
        if(mcf.scope.size()==0){
            mcf.scope.add(MetaConstraintFactory.MC.NONE);
        }
        // If no operators must be used...
        if(of.scope.size()==0){
            of.scope.add(OperatorFactory.O.NONE);
        }
    }


    /**
     * Create a random model
     * @param r random
     * @return a CPModel
     */
    public CPModel model(Random r){
        init();
        CPModel m = new CPModel();
        Constraint c = mcf.make(r);
        m.addConstraint(c);
        return m;
    }

}
