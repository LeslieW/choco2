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
package choco.kernel.model.constraints;

import java.util.HashMap;
import java.util.Map;

import choco.kernel.model.ModelException;
import choco.kernel.model.variables.VariableManager;
/**
 * Handle all object's managers referenced by property name.
 * The class ensures that there exists at most one instance of each manager.
 * @author Arnaud Malapert</br> 
 * @since 9 févr. 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
		
public final class ManagerFactory {

	private static final Map<String, VariableManager<?>> VM_MAP = new HashMap<String, VariableManager<?>>();

	private static final Map<String, ExpressionManager> EM_MAP = new HashMap<String, ExpressionManager>();

	private static final Map<String, ConstraintManager<?>> CM_MAP = new HashMap<String, ConstraintManager<?>>();

	private static final String ERROR_MSG="Cant load manager by reflection: ";
	
	private static Object loadManager(String name) {
		//We get it by reflection !
		try {
			return Class.forName(name).newInstance();
		} catch (ClassNotFoundException e) {
			throw new ModelException(ERROR_MSG+name);
		} catch (InstantiationException e) {
			throw new ModelException(ERROR_MSG+name);
		} catch (IllegalAccessException e) {
			throw new ModelException(ERROR_MSG+name);
		}
	}

	public static VariableManager<?> loadVariableManager(String name) {
		VariableManager<?> vm = VM_MAP.get(name);
		if( vm == null) {
			vm = (VariableManager<?>) loadManager(name);
			VM_MAP.put(name, vm);
		}
		return vm;
	}

	public static ExpressionManager loadExpressionManager(String name) {
		ExpressionManager em = EM_MAP.get(name);
		if( em == null) {
			em = (ExpressionManager) loadManager(name);
			EM_MAP.put(name, em);
		}
		return em;
	}

	public static ConstraintManager<?> loadConstraintManager(String name) {
		ConstraintManager<?> cm = CM_MAP.get(name);
		if( cm == null) {
			cm = (ConstraintManager<?>) loadManager(name);
			CM_MAP.put(name, cm);
		}
		return cm;	
	}
	
	public static void clear() {
		VM_MAP.clear();
		EM_MAP.clear();
		CM_MAP.clear();
	}
}
