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

package choco.kernel.memory.trailing;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;

/**
 * An interface for classes implementing trails of modifications to objects.
 * <p/>
 * Toutes les classes doivent implementer les fonctions de l'interface pour
 * permettre a l'environnement de deleguer la gestion des mondes pour chaque type
 * de donnee.
 */
public interface ITrailStorage {
	
	/**
	 * Reference to an object for logging trace statements related memory & backtrack (using the java.util.logging package)
	 */
	final static Logger LOGGER = ChocoLogging.getEngineLogger();

	/**
	 * Moving up to the next world.
	 * <p/>
	 * Cette methode doit garder l'etat de la variable avant la modification
	 * de sorte a la remettre en etat le cas echeant.
	 */

	public void worldPush();


	/**
	 * Moving down to the previous world.
	 * <p/>
	 * Cette methode reattribute a la variable ou l'element d'un tableau sa valeur
	 * precedente.
	 */

	public void worldPop();


	/**
	 * Comitting the current world: merging it with the previous one.
	 * <p/>
	 * Not used yet.
	 */

	public void worldCommit();


	/**
	 * Retrieving the size of the trail (number of saved past values).
	 */

	public int getSize();

	/**
	 * increase the capacity of the environment to a given number of worlds
	 *
	 * @param newWorldCapacity
	 */
	public void resizeWorldCapacity(int newWorldCapacity);


}

