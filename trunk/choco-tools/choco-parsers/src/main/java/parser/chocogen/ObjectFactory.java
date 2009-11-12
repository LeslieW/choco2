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
package parser.chocogen;


import choco.kernel.model.Model;
import parser.absconparseur.tools.InstanceParser;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 16 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public abstract class ObjectFactory {

	//ac
	public static final int AC32 = 32;
	public static final int AC2001 = 2001;
	public static final int AC2008 = 2008;
	
	/** algo d'ac : 2001 ou 32 ou 2008 */
	public static int algorithmAC = AC32;
	
	protected InstanceParser parser;

	protected Model m;

    protected ObjectFactory(Model m, InstanceParser parser) {
		this.parser = parser;
		this.m = m;
    }
}
