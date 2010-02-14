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
public final class ConstantFactory {

    protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

    private final static TIntObjectHashMap<IntegerConstantVariable> INTEGER_MAP = new TIntObjectHashMap<IntegerConstantVariable>();
    private final static HashMap<int[], SetConstantVariable> SET_MAP = new HashMap<int[], SetConstantVariable>();
    private final static TDoubleObjectHashMap<RealConstantVariable> REAL_MAP = new TDoubleObjectHashMap<RealConstantVariable>();

    
    
    protected ConstantFactory() {
		super();
    }

	/**
     * Create if necessary and return the IntegerConstantVariable that
     * corresponds to the value
     * @param value int constant value
     * @return IntegerConstantVariable
     */
    public static IntegerConstantVariable getConstant(int value){
        if(INTEGER_MAP.get(0)!=null
                &&INTEGER_MAP.get(0).getValue() !=0){
            LOGGER.severe("$$$$$$$$$$$$$ ALARM $$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.exit(-1589);
        }
        if(!INTEGER_MAP.containsKey(value)){
            INTEGER_MAP.put(value, new IntegerConstantVariable(value));
        }
        return INTEGER_MAP.get(value);
    }

    /**
     * Create if necessary and return the SetConstantVariable that
     * corresponds to arrays of value
     * @param values set constant values
     * @return SetConstantVariable
     */
    public static SetConstantVariable getConstant(int[] values){
        if(!SET_MAP.containsKey(values)){
            SET_MAP.put(values, new SetConstantVariable(getConstant(values.length), values));
        }
        return SET_MAP.get(values);
    }

    /**
     * Create if necessary and return the RealConstantVariable that
     * corresponds to the value
     * @param value double real value
     * @return RealConstantVariable
     */
    public static RealConstantVariable getConstant(double value){
        if(!REAL_MAP.containsKey(value)){
            REAL_MAP.put(value, new RealConstantVariable(value));
        }
        return REAL_MAP.get(value);
    }

}
