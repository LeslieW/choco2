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
package choco.cp.solver.constraints.global.geost.layers;


import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.geometricPrim.Point;
import choco.cp.solver.constraints.global.geost.geometricPrim.Region;
import choco.cp.solver.constraints.global.geost.internalConstraints.AvoidHoles;
import choco.cp.solver.constraints.global.geost.internalConstraints.Inbox;
import choco.cp.solver.constraints.global.geost.internalConstraints.InternalConstraint;
import choco.cp.solver.constraints.global.geost.internalConstraints.Outbox;
import choco.kernel.common.logging.ChocoLogging;

import java.util.Vector;
import java.util.logging.Logger;


/**
 * This is the intermediate layer class. It implements the functionality that permits access to infeasible sets of points according to  
 * some Internal Constraint ictr
 */
public class IntermediateLayer {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

	/**
	 * Creates an IntermediateLayer instance. Actually this class just provides functionality so
	 * we could have just made all the functions in it static but we prefer to do it this way for later changes if needed.
	 */
	public IntermediateLayer(){};


	/**
	 * @param ictr An internalConstraint object
	 * @param minLex Specifies if the point to return is the smallest or largest infeasible lexicographical point
	 * @param d Indicates which coordinate dimension we want to prune
	 * @param k The total number of dimensions (The dimension of the space we are working in)
	 * @param o The object in question
	 * @return A vector of 2 elements. The first is a Boolean object indicating the fact of whether a point was found and the second is a Point object
	 */
	public Vector LexInFeasible(InternalConstraint ictr, boolean minLex, int d, int k, Obj o)
	{
		Vector result = new Vector();
		switch (ictr.getIctrID())
		{
			case Constants.INBOX:
				result = LexInFeasibleForInbox((Inbox)ictr, minLex, d, k, o);
				break;
			case Constants.OUTBOX:
				result = LexInFeasibleForOutbox((Outbox)ictr, minLex, d, k, o);
				break;
			case Constants.AVOID_HOLES:
				result = LexInFeasibleForAvoidHoles((AvoidHoles)ictr, minLex, d, k, o);
				break;
			default: LOGGER.severe("A call to LexFeasible with incorrect ictrID parameter");
		}
		return result;
	}

	/**
	 * @param ictr An internalConstraint object
	 * @param min Specifies whether we want to prune the minimum or maximum value of coordinate at dimension d
	 * @param d Indicates which coordinate dimension we want to prune
	 * @param k The total number of dimensions (The dimension of the space we are working in)
	 * @param o The object in question
	 * @param c The point in question (so if c is feasible or not using object o)
	 * @return A vector of 2 elements. The first is a Boolean object indicating whether c is
	 * feasible or not and the second is a Region object indicating a forbidden region
	 * if the point is not feasible
	 */
	public Vector IsFeasible(InternalConstraint ictr, boolean min, int d, int k, Obj o, Point c)
	{
		Vector result = new Vector();
		switch (ictr.getIctrID())
		{
			case Constants.INBOX:
				result = IsFeasibleForInbox((Inbox)ictr, min, d, k, o, c);
				break;
			case Constants.OUTBOX:
				result = IsFeasibleForOutbox((Outbox)ictr, min, d, k, o, c);
				break;
			case Constants.AVOID_HOLES:
				result = IsFeasibleForAvoidHoles((AvoidHoles)ictr, min, d, k, o, c);
				break;
			default: LOGGER.severe("A call to IsFeasible with incorrect ictrID parameter");
		}
		return result;
	}

	/**
	 * @param ictr An internalConstraint object
	 * @param k The total number of dimensions (The dimension of the space we are working in)
	 * @param o The object in question
	 * @return An integer indicating the number of infeasible points for the origin of the object o under the assumption that ictr holds
	 */
	public int  CardInfeasible(InternalConstraint ictr, int k, Obj o)
	{
		int result = 0;
		switch (ictr.getIctrID())
		{
			case Constants.INBOX:
				result = CardInfeasibleForInbox((Inbox)ictr, k, o);
				break;
			case Constants.OUTBOX:
				result = CardInfeasibleForOutbox((Outbox)ictr, k, o);
				break;
			case Constants.AVOID_HOLES:
				result = CardInfeasibleForAvoidHoles((AvoidHoles)ictr, k, o);
				break;
			default: LOGGER.severe("A call to CardInfeasible with incorrect ictr parameter");
		}
		return result;
	}


	private Vector LexInFeasibleForInbox(Inbox ictr, boolean minLex, int d, int k, Obj o)
	{

		//RETURNS a vector of 2 elements. The first is a Boolean object and the second is a Point object
		Vector<Object> result = new Vector<Object>();

		int[] t = new int[ictr.getT().length];
		t = ictr.getT();
		int[] l = new int[ictr.getL().length];
		l = ictr.getL();
		boolean in = true;
		Point p = new Point(k);
		for(int j = 0; j < k; j++)
		{
			if (minLex)
				p.setCoord(j, o.getCoord(j).getInf());
			else
				p.setCoord(j, o.getCoord(j).getSup());

			if((p.getCoord(j) < t[j]) || (p.getCoord(j) > t[j] + l[j] - 1))
				in = false;
		}

		if (in)
		{
			for(int j = k-1; j >= 0; j--)
			{
				int jPrime = (j + d) % k;
				if (minLex)
				{
					if(t[jPrime] + l[jPrime] <= o.getCoord(jPrime).getSup())
					{
						p.setCoord(jPrime, t[jPrime] + l[jPrime]);
						result.clear();
						result.add(0,Boolean.valueOf(true));
						result.add(1, p);
						return result;
					}
					else
					{
						if(t[jPrime] - 1 >= o.getCoord(jPrime).getInf())
						{
							p.setCoord(jPrime, t[jPrime] - 1);
							result.clear();
							result.add(0,new Boolean(true));
							result.add(1, p);
							return result;
						}
					}
				}
			}
			result.clear();
			result.add(0, new Boolean(false));
			result.add(1, p);
			return result;
		}
		else
		{
			result.clear();
			result.add(0, new Boolean(true));
			result.add(1, p);
			return result;
		}
	}

	private Vector LexInFeasibleForOutbox(Outbox ictr, boolean minLex, int d, int k, Obj o)
	{
		//RETURNS a vector of 2 elements. The first is a Boolean object and the second is a Point object
		Vector<Object> result = new Vector<Object>();
		int[] t = new int[ictr.getT().length];
		t = ictr.getT();
		int[] l = new int[ictr.getL().length];
		l = ictr.getL();
		Point p = new Point(k);

		for (int j = 0; j < k; j++)
		{
			if((o.getCoord(j).getSup() < t[j]) || (o.getCoord(j).getInf() > t[j] + l[j] - 1))
			{
				result.clear();
				result.add(0, Boolean.valueOf(false));
				result.add(1, p);
				return result;
			}
			if (minLex)
				p.setCoord(j, Math.max(t[j], o.getCoord(j).getInf()));
			else
				p.setCoord(j, Math.max(t[j] + l[j] - 1, o.getCoord(j).getSup()));
		}
		result.clear();
		result.add(0, new Boolean(true));
		result.add(1, p);
		return result;
	}

	private Vector LexInFeasibleForAvoidHoles(AvoidHoles ictr, boolean minLex, int d, int k, Obj o)
	{
		//RETURNS a vector of 2 elements. The first is a Boolean object and the second is a Point object
		Vector result = new Vector();

		return result;
	}


	private Vector IsFeasibleForInbox(Inbox ictr, boolean min, int d, int k, Obj o, Point c)
	{
		//RETURNS a vector of 2 elements. The first is a Boolean object and the second is a Region object
		Vector<Object> result = new Vector<Object>();
		int[] t = new int[ictr.getT().length];
		t = ictr.getT();
		int[] l = new int[ictr.getL().length];
		l = ictr.getL();

		boolean before = false;
		boolean after = false;
		boolean feasible = false;
		Region f = new Region(k,o.getObjectId());

		for(int j = 0; j < k; j++)
		{
			int jPrime = (j + d) % k;
			if(min)
			{
				f.setMinimumBoundary(jPrime, c.getCoord(jPrime));
				if((c.getCoord(jPrime) < t[jPrime]) && (!before))
				{
					f.setMaximumBoundary(jPrime, t[jPrime] -1);
					before = true;
				}
				else
				{
					f.setMaximumBoundary(jPrime, o.getCoord(jPrime).getSup());;
					if(c.getCoord(jPrime) > t[jPrime] + l[jPrime] - 1)
						after = true;
				}
			}
			else
			{
				f.setMaximumBoundary(jPrime, c.getCoord(jPrime));
				if(c.getCoord(jPrime) < t[jPrime])
					before = true;
			}
		}
		feasible = !(before || after);
		result.clear();
		result.add(0, Boolean.valueOf(feasible));
		result.add(1, f);
		return result;
	}

	private Vector IsFeasibleForOutbox(Outbox ictr, boolean min, int d, int k, Obj o, Point c)
	{
		//RETURNS a vector of 2 elements. The first is a Boolean object and the second is a Region object
		Vector<Object> result = new Vector<Object>();
		int[] t = new int[ictr.getT().length];
		t = ictr.getT();
		int[] l = new int[ictr.getL().length];
		l = ictr.getL();
		Region f = new Region(k, o.getObjectId());

		for(int j = 0; j < k; j++)
		{
			if((c.getCoord(j) < t[j]) || (c.getCoord(j) > t[j] + l[j] - 1))
			{
				result.clear();
				result.add(0, Boolean.valueOf(true));
				result.add(1, f);
				return result;
			}

			if (min)
			{
				f.setMinimumBoundary(j, c.getCoord(j));
				f.setMaximumBoundary(j, Math.min(o.getCoord(j).getSup(), t[j] + l[j] - 1));
			}
			else
			{
				f.setMaximumBoundary(j, c.getCoord(j));
				f.setMinimumBoundary(j, Math.max(o.getCoord(j).getInf(), t[j]));
			}
		}
		result.clear();
		result.add(0, new Boolean(false));
		result.add(1, f);
		return result;
	}


	private Vector IsFeasibleForAvoidHoles(AvoidHoles ictr, boolean min, int d, int k, Obj o, Point c)
	{
		//RETURNS a vector of 2 elements. The first is a Boolean object and the second is a Region object
		Vector<Object> result = new Vector<Object>();

		return result;
	}



	private int CardInfeasibleForInbox(Inbox ictr, int k, Obj o)
	{
		//RETURNS an interger indicating the number of infeasible points for the origin of the object o
		int n = 1;
		int[] t = new int[ictr.getT().length];
		t = ictr.getT();
		int[] l = new int[ictr.getL().length];
		l = ictr.getL();

		for(int j = 0; j < k; j++)
			n = n * (o.getCoord(j).getSup() - o.getCoord(j).getInf() + 1);

		int m = 1;
		for(int j = 0; j < k; j++)
			m = m * Math.max(0, ((Math.min(o.getCoord(j).getSup(), t[j] + l[j] - 1) - Math.max(o.getCoord(j).getInf(), t[j])) + 1));

		return n - m;
	}

	private int CardInfeasibleForOutbox(Outbox ictr, int k, Obj o)
	{
		//RETURNS an interger indicating the number of infeasible points for the origin of the object o
		int n = 1;
		int[] t = new int[ictr.getT().length];
		t = ictr.getT();
		int[] l = new int[ictr.getL().length];
		l = ictr.getL();

		for(int j = 0; j < k; j++)
			n = n * ((Math.min(o.getCoord(j).getSup(), t[j] + l[j] -1) - Math.max(o.getCoord(j).getInf(), t[j])) + 1);

		return n;
	}

	private int CardInfeasibleForAvoidHoles(AvoidHoles ictr, int k, Obj o)
	{
		//RETURNS an interger indicating the number of infeasible points for the origin of the object o
		int result = 0;

		return result;
	}

}