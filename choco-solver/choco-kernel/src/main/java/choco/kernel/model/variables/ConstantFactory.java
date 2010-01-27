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
package choco.kernel.model.variables;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import gnu.trove.TDoubleObjectHashMap;
import gnu.trove.TIntObjectHashMap;

import java.util.HashMap;
import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 12 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public class ConstantFactory {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

    static TIntObjectHashMap<IntegerConstantVariable> integerMap = new TIntObjectHashMap<IntegerConstantVariable>();
    static HashMap<int[], SetConstantVariable> setMap = new HashMap<int[], SetConstantVariable>();
    static TDoubleObjectHashMap<RealConstantVariable> realMap = new TDoubleObjectHashMap<RealConstantVariable>();

    /**
     * Create if necessary and return the IntegerConstantVariable that
     * corresponds to the value
     * @param value int constant value
     * @return IntegerConstantVariable
     */
    public static IntegerConstantVariable getConstant(int value){
        if(integerMap.get(0)!=null
                &&integerMap.get(0).getValue() !=0){
            LOGGER.severe("$$$$$$$$$$$$$ ALARM $$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.exit(-1589);
        }
        if(!integerMap.containsKey(value)){
            integerMap.put(value, new IntegerConstantVariable(value));
        }
        return integerMap.get(value);
    }

    /**
     * Create if necessary and return the SetConstantVariable that
     * corresponds to arrays of value
     * @param values set constant values
     * @return SetConstantVariable
     */
    public static SetConstantVariable getConstant(int[] values){
        if(!setMap.containsKey(values)){
            setMap.put(values, new SetConstantVariable(getConstant(values.length), values));
        }
        return setMap.get(values);
    }

    /**
     * Create if necessary and return the RealConstantVariable that
     * corresponds to the value
     * @param value double real value
     * @return RealConstantVariable
     */
    public static RealConstantVariable getConstant(double value){
        if(!realMap.containsKey(value)){
            realMap.put(value, new RealConstantVariable(value));
        }
        return realMap.get(value);
    }

}
