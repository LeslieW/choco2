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

import choco.kernel.model.ModelException;
import noNamespace.*;

import java.util.HashMap;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class ExpressionParser {

    final HashMap<String, Object> items;

    public ExpressionParser(HashMap<String, Object> items) {
        this.items = items;
    }

    Object readExpression(ExprDocument.Expr e){
        // If it is ...
        // .. an id: return the object with that name
        if(e.isSetId()){
            return buildId(e.getId());
        }else
        // .. an bool lit: return a boolean
        if(e.isSetBoolLit()){
            return buildBoolLit(e.getBoolLit());
        }else
        if(e.isSetIntLit()){
            return buildIntLit(e.getIntLit());
        }else
        if(e.isSetFloatLit()){
            return buildFloatLit(e.getFloatLit());
        }else
        if(e.isSetSetLit()){
            return buildSetLit(e.getSetLit());
        }else
        if(e.isSetArrayLit()){
            return buildArrayLit(e.getArrayLit());
        }else
        if(e.isSetArrayAccess()){
        return buildArrayAccess(e.getArrayAccess());
        }else{
            throw new ModelException("EpxressionParser:readExpression: unknown type");
        }
    }

    /**
     * Return the object defined by the id
     * @param id an id
     * @return object
     */
    Object buildId(IdDocument.Id id) {
        Object item = items.get(id.getName().getStringValue());
        return item;
    }

    /**
     * Return an array of objects defined by the id
     * @param ids an array of id
     * @return object[]
     */
    Object[] buildId(IdDocument.Id[] ids) {
        Object[] itms = new Object[ids.length];
        for(int i = 0 ; i < ids.length; i++){
            itms[i] = buildId(ids[i]);
        }
        return itms;
    }

    /**
     * Build a int from a int lit
     * @param il int lit
     * @return int
     */
    int buildIntLit(IntLitDocument.IntLit il){
        if(il.isNil())throw new ModelException("ExpressionParser:buildIntLit: il is nil");
        int ivalue = Integer.parseInt(il.getValue().getStringValue());
        return ivalue;
    }

    /**
     * Build an array of int from int lits
     * @param ils int lits
     * @return int[]
     */
    int[] buildIntLit(IntLitDocument.IntLit[] ils){
        int[] itms = new int[ils.length];
        for (int i = 0; i < ils.length; i++) {
            itms[i] =  buildIntLit(ils[i]);
        }
        return itms;
    }

    /**
     * Build a boolean from a bool lit
     * @param bl bool lit
     * @return boolean
     */
    boolean buildBoolLit(BoolLitDocument.BoolLit bl){
        if(bl.isNil())throw new ModelException("ExpressionParser:buildBoolLit: bl is nil");
        boolean bvalue = bl.getValue().intValue()==0;
        return bvalue;
    }

    /**
     * Build an array of booleans from bool lits
     * @param bls array of bool lits
     * @return boolean[]
     */
    boolean[] buildBoolLit(BoolLitDocument.BoolLit[] bls){
        boolean[] itms = new boolean[bls.length];
        for (int i = 0; i < bls.length; i++) {
            itms[i] =  buildBoolLit(bls[i]);
        }
        return itms;
    }

    /**
     * Build a float from a float lit
     * @param fl float lit
     * @return float
     */
    double buildFloatLit(FloatLitDocument.FloatLit fl){
        if(fl.isNil())throw new ModelException("ExpressionParser:buildFloatLit: fl is nil");
        double fvalue = Double.parseDouble(fl.getValue().getStringValue());
        return fvalue;
    }

    /**
     * Build an array of floats from float lits
     * @param fls float lits
     * @return float[]
     */
    double[] buildFloatLit(FloatLitDocument.FloatLit[] fls){
        double[] itms = new double[fls.length];
        for (int i = 0; i < fls.length; i++) {
            itms[i] =  buildFloatLit(fls[i]);
        }
        return itms;
    }

    /**
     * Build a set of items
     * @param sl set lit
     * @return object
     */
    Object buildSetLit(SetLitDocument.SetLit sl){
        if(sl.isSetIntRange()){
            return buildIntRange(sl.getIntRange());
        }else if(sl.sizeOfIdArray()>0){
            return buildId(sl.getIdArray());
        }else if(sl.sizeOfIntLitArray()>0){
            return buildIntLit(sl.getIntLitArray());
        }else if(sl.sizeOfFloatLitArray()>0){
            return buildFloatLit(sl.getFloatLitArray());
        }else if(sl.sizeOfBoolLitArray()>0){
            return buildBoolLit(sl.getBoolLitArray());
        }
        throw new ModelException("ExpressionParser:buildSetLit: unknown type");
    }

    /**
     * Build a int variable.
     * @param ir xml information about int ranges
     * @return array of 2 values: lower bound and upper bound
     */
    private int[] buildIntRange(IntRangeDocument.IntRange ir) {
        // get the value of the literal
        if(ir.isNil()){
            throw new ModelException("ExpressionParser:buildIntRange: no value for intrange");
        }else{
            int lo = Integer.parseInt(ir.getLo().getStringValue());
            int hi = Integer.parseInt(ir.getHi().getStringValue());
            return new int[]{lo,hi};
        }
    }

    /**
     * Return the item contains in the array
     * @param aa array access definition
     * @return object
     */
    Object buildArrayAccess(ArrayAccessDocument.ArrayAccess aa){
        
        if(aa.sizeOfIdArray()==1){
            Object[] array = (Object[])buildId(aa.getIdArray(0));
            int ind = buildIntLit(aa.getIntLit());
            return array[ind];
        }else{
            throw new ModelException("ExpressionParser:buildArrayAccess: not yet implemented");
        }
    }

    /**
     * Build an array of item
     * @param al array of item
     * @return array
     */
    Object buildArrayLit(ArrayLitDocument.ArrayLit al){
        if(al.sizeOfIdArray()>0){
            Object[] s = new Object[al.sizeOfIdArray()];
            for(int i = 0; i < al.sizeOfIdArray(); i++){
                s[i] = buildId(al.getIdArray(i));
            }
            return s;
        }else
        if(al.sizeOfIntLitArray()>0){
            int[] ints = new int[al.sizeOfIntLitArray()];
            for(int i = 0; i < al.sizeOfIntLitArray(); i++){
                ints[i] = buildIntLit(al.getIntLitArray(i));
            }
            return ints;
        }else
        if(al.sizeOfBoolLitArray()>0){
            boolean[] bools = new boolean[al.sizeOfBoolLitArray()];
            for(int i = 0; i < al.sizeOfBoolLitArray(); i++){
                bools[i] = buildBoolLit(al.getBoolLitArray(i));
            }
            return bools;
        }else
        if(al.sizeOfFloatLitArray()>0){
            double[] floats = new double[al.sizeOfFloatLitArray()];
            for(int i = 0; i < al.sizeOfFloatLitArray(); i++){
                floats[i] = buildFloatLit(al.getFloatLitArray(i));
            }
            return floats;
        }else
        if(al.sizeOfSetLitArray()>0){
            Object[] sets = new Object[al.sizeOfSetLitArray()];
            for(int i = 0; i < al.sizeOfSetLitArray(); i++){
                sets[i] = buildSetLit(al.getSetLitArray(i));
            }
            return sets;
        }else
        if(al.sizeOfArrayAccessArray()>0){
            Object[] aaccess = new Object[al.sizeOfArrayAccessArray()];
            for(int i = 0; i < al.sizeOfArrayAccessArray(); i++){
                aaccess[i] = buildArrayAccess(al.getArrayAccessArray(i));
            }
            return aaccess;
        }else{
            throw new ModelException("EpressionParser:buildArrayLit: unknown type");
        }
    }

    Object getObject(String name){
        return items.get(name);
    }

}
