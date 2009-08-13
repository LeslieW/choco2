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
 *                   N. Jussien    1999-2009      *
 **************************************************/
package parser.chocogen.mzn;

import choco.Choco;
import choco.kernel.model.ModelException;
import noNamespace.*;

import java.util.HashMap;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class VariablesParser {

    public enum VarType{ ARRAY, SET, INT, INT_RANGE, INT_SET, FLOAT, FLOAT_RANGE, BOOL, VAR, INT_LIT, UNKNOWN }

    public final HashMap<String, Object> items;

    final ExpressionParser ep;

    public VariablesParser(HashMap<String, Object> items, ExpressionParser ep) {
        this.items = items;
        this.ep = ep;
    }

    /**
     * Iterates over variables and parameters and feed the HashMap
     * @param flatzinc flatzinc document
     */
    void readParametersAndVariables(FlatzincDocument.Flatzinc flatzinc) {
        for(VarDeclDocument.VarDecl var : flatzinc.getVarDeclArray()){
            // name of the item
            buildVariable(var);
        }
    }


    /**
     * Build the variable
     * @param var xml informations
     */
    private void buildVariable(VarDeclDocument.VarDecl var) {
        String name = var.getId().getName().getStringValue();

        // The variable is an int type
        if(var.getType().isSetInt()){
            items.put(name, buildInt(var));
        }else
        if(var.getType().isSetBool()){
            items.put(name, buildBool(var));
        }else
        if(var.getType().isSetFloat()){
            items.put(name, buildFloat(var));
        }else
        if(var.getType().isSetIntRange()){
            items.put(name, buildIntRange(var.getType().getIntRange()));
        }else
        if(var.getType().isSetFloatRange()){
            items.put(name, buildFloatRange(var.getType().getFloatRange()));
        }else
        if(var.getType().isSetIntSet()){
            items.put(name, buildIntSet(var.getType().getIntSet()));
        }else
        if(var.getType().isSetVar()){
            buildVar(var.getType().getVar(), name);
        }else
        if(var.getType().isSetArray()){
            buildArray(var, name);
        }else
        if(var.getType().isSetSet()){
            buildSet(var, name);
//            throw new ModelException("Variables:parser:buildVariable:set not yet implemented");
        }

    }

    /**
     * Build a int.
     * @param var xml information
     * @return int
     */
    private int buildInt(VarDeclDocument.VarDecl var) {
        return ep.buildIntLit(var.getExpr().getIntLit());
    }

    /**
     * Build a bool.
     * @param var xml information
     * @return boolean
     */
    private boolean buildBool(VarDeclDocument.VarDecl var) {
        return ep.buildBoolLit(var.getExpr().getBoolLit());
    }

    /**
     * Build a float.
     * @param var xml information
     * @return double
     */
    private double buildFloat(VarDeclDocument.VarDecl var) {
        return ep.buildFloatLit(var.getExpr().getFloatLit());
    }

    /**
     * Build a int variable.
     * @param ir xml information about int ranges
     * @return array of 2 values: lower bound and upper bound
     */
    private int[] buildIntRange(IntRangeDocument.IntRange ir) {
        // get the value of the literal
        if(ir.isNil()){
            throw new ModelException("VariablesParser:buildIntRange: no value for intrange");
        }else{
            int lo = Integer.parseInt(ir.getLo().getStringValue());
            int hi = Integer.parseInt(ir.getHi().getStringValue());
            return new int[]{lo,hi};
        }
    }

    /**
     * Build a float variable.
     * @param fr xml information about float range
     * @return array of 2 values: lower bound and upper bound
     */
    private double[] buildFloatRange(FloatRangeDocument.FloatRange fr) {
        // get the value of the literal
        if(fr.isNil()){
            throw new ModelException("VariablesParser:buildFloatRange: no value for floatrange");
        }else{
            double lo = Double.parseDouble(fr.getLo().getStringValue());
            double hi = Double.parseDouble(fr.getHi().getStringValue());
            return new double[]{lo,hi};
        }
    }

    /**
     * Build a int variable from a set of value
     * @param is xml information about set of ints
     * @return int[]
     */
    private int[] buildIntSet(IntSetDocument.IntSet is) {
        // get the value of the literal
        if(is.isNil()){
            throw new ModelException("VariablesParser:buildIntSet: no value for intset");
        }else{
            IntLitDocument.IntLit[] ils = is.getIntLitArray();
            int[] values = new int[ils.length];
            for (int i = 0; i < ils.length; i++) {
                values[i] = Integer.parseInt(ils[i].getValue().getStringValue());
            }
            return values;
        }
    }

    /**
     * Build a variable.
     * @param var xml information
     * @param name name of the variable
     */
    private void buildVar(VarDocument.Var var, String name) {

        if(var.isSetIntRange()){
            int[] values  = buildIntRange(var.getIntRange());
            items.put(name, Choco.makeIntVar(name, values[0], values[1]));
            return;
        }else
        if(var.isSetIntSet()){
            int[] values  = buildIntSet(var.getIntSet());
            items.put(name, Choco.makeIntVar(name, values));
            return;
        }else
        if(var.isSetFloatRange()){
            double[] values  = buildFloatRange(var.getFloatRange());
            items.put(name, Choco.makeRealVar(name, values[0], values[1]));
            return;
        }if(var.isSetInt()){
            items.put(name, Choco.makeIntVar(name));
            return;
        }else
        if(var.isSetBool()){
            items.put(name, Choco.makeBooleanVar(name));
            return;
        }if(var.isSetSet()){
            items.put(name, Choco.makeIntVar(name));
            return;
        }else{
            throw new ModelException("VariablesParser:buildIntSet: not yet implemented");
        }
    }

    /**
     * Build an array item
     * @param var array item
     * @param name name of the parameter
     */
    private void buildArray(VarDeclDocument.VarDecl var, String name) {

        // Get the size of the first dimension
        int size = Integer.parseInt(var.getType().getArray().getIntLit().getValue().getStringValue());

        ArrayDocument.Array array = var.getType().getArray();
        // If it is an array of ...
        // ...integer (known range)
        if(array.isSetIntRange()){
            int[] values = buildIntRange(array.getIntRange());
            int[][] ints_array = new int[size][2];
            for(int i = 0; i < size; i++){
                ints_array[i] = values;
            }
            //TODO: return?
        }else
        // ... integer (set)
        if(array.isSetIntSet()){
            int[] values = buildIntSet(array.getIntSet());
            int[][] ints_array = new int[size][values.length];
            for(int i = 0; i < size; i++){
                ints_array[i] = values;
            }
            //TODO: return?
        }else
        if(array.isSetFloatRange()){
            double[] values = buildFloatRange(array.getFloatRange());
            double[][] ints_array = new double[size][2];
            for(int i = 0; i < size; i++){
                ints_array[i] = values;
            }
            //TODO: return?
        }if(array.isSetVar()){
            if(var.getExpr()!=null){
                Object o = ep.readExpression(var.getExpr());
                items.put(name, o);
            }else{
                Object[] its = new Object[size+1];
                for(int i = 1; i < size+1; i++){
                    buildVar(array.getVar(), name+"_"+i);
                    its[i] = items.get(name+"_"+i);
                }
                items.put(name, its);
            }

            return;
        }else{
            // We don't do anything for the moment
            throw new ModelException("VariablesParser:buildArray: not yet implemented");
        }
    }

    /**
     * Build a set of items
     * @param var
     */
    private void buildSet(VarDeclDocument.VarDecl var, String name){
        ExprDocument.Expr exp  =var.getExpr();
        if(exp!=null){
            Object o = ep.readExpression(exp);
            items.put(name, o);
        }


    }
}
