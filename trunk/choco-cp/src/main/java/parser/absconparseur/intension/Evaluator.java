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
package parser.absconparseur.intension;


import choco.kernel.common.logging.ChocoLogging;
import parser.absconparseur.ReflectionManager;
import parser.absconparseur.Toolkit;
import parser.absconparseur.intension.types.*;

import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Evaluator {

	protected final static Logger LOGGER = ChocoLogging.getParserLogger();

	private static Map<String, Class> classMap;

	private static Map<String, Integer> arityMap;

	private static Set<String> symmetricSet;

	private static Set<String> associativeSet;

	static {
		classMap = new HashMap<String, Class>();
		arityMap = new HashMap<String, Integer>();
		symmetricSet = new HashSet<String>();
		associativeSet = new HashSet<String>();

		Class[] classes = ReflectionManager.searchClassesInheritingFrom(Evaluator.class, Modifier.PUBLIC, Modifier.ABSTRACT);
		for (Class clazz : classes) {
			String className = Toolkit.getRelativeClassNameOf(clazz);
			String evaluatorToken = className.substring(0, 1).toLowerCase() + className.substring(1, className.lastIndexOf("Evaluator"));

			// LOGGER.info("evaluatorToken = " + evaluatorToken + " absoluteClassName = " + clazz.getName());
			classMap.put(evaluatorToken, clazz);

			int arity = -1;
			try {
				if (Arity0Type.class.isAssignableFrom(clazz))
					arity = 0;
				if (Arity1Type.class.isAssignableFrom(clazz))
					arity = 1;
				if (Arity2Type.class.isAssignableFrom(clazz))
					arity = 2;
				if (Arity3Type.class.isAssignableFrom(clazz))
					arity = 3;
				if (Arity4Type.class.isAssignableFrom(clazz))
					arity = 4;
				if (SymmetricType.class.isAssignableFrom(clazz))
					symmetricSet.add(evaluatorToken);
				if (AssociativeType.class.isAssignableFrom(clazz))
					associativeSet.add(evaluatorToken);

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"fatal error", e);
				System.exit(1);
			}
			// LOGGER.info("evaluatorToken = " + evaluatorToken + " arity = " + arity);
			arityMap.put(evaluatorToken, arity);
		}
	}

	public static Class getClassOf(String evaluatorToken) {
		return classMap.get(evaluatorToken);
	}

	public static int getArityOf(String evaluatorToken) {
		Integer i = arityMap.get(evaluatorToken);
		return i == null ? -1 : i;
	}

	public static boolean isSymmetric(String evaluatorToken) {
		return symmetricSet.contains(evaluatorToken);
	}

	public static boolean isAssociative(String evaluatorToken) {
		return associativeSet.contains(evaluatorToken);
	}

	// TODO this method should only be used during initialization (otherwise, too expensive)
	public int getArity() {
		String className = Toolkit.getRelativeClassNameOf(getClass());
		String evaluatorToken = className.substring(0, 1).toLowerCase() + className.substring(1, className.lastIndexOf("Evaluator"));
		return arityMap.get(evaluatorToken);
	}

	protected static int top = -1;

	public static int getTop() {
		return top;
	}

	public static void resetTop() {
		top = -1;
	}

	public static long getTopValue() {
		return stack[top];
	}

	protected static long[] stack = new long[100];

	public static void checkStackSize(int size) {
		if (stack.length < size)
			stack = new long[size];
	}

	public static void displayStack() {
		if(LOGGER.isLoggable(Level.INFO)) {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i <= top; i++)
				s.append(stack[i]).append(" ");
			LOGGER.info(new String(s));
		}
	}


	public abstract void evaluate();

	public String toString() {
		return Toolkit.getRelativeClassNameOf(this);
	}
}