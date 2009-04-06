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
import choco.cp.solver.constraints.global.geost.Setup;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.geometricPrim.Point;
import choco.cp.solver.constraints.global.geost.geometricPrim.Region;
import choco.cp.solver.constraints.global.geost.internalConstraints.InternalConstraint;
import choco.kernel.solver.ContradictionException;

import java.util.Vector;


/**
 * This is the Geometric kernel class. It implements the functionality of the sweep point algorithm.  
 */
public class GeometricKernel {

	Constants cst;
	Setup stp;
	ExternalLayer externalLayer;
	IntermediateLayer intermediateLayer;

	/**
	 * Creates an ExternalLayer instance for a specific Constants class, a specific Setup class, a specific ExternalLayer class and a specific
	 * IntermediateLayer class.
	 *
	 * @param c  The constants class
	 * @param s  The Setup class
	 * @param extrL
	 * @param intermL
	 */
	public GeometricKernel(Constants c, Setup s, ExternalLayer extrL, IntermediateLayer intermL)
	{
		cst = c;
		stp = s;
		externalLayer = extrL;
		intermediateLayer = intermL;
	}

	/**
	 * It gets the forbidden region. Basically this answers the following question: Is point c infeasible according to any active internal
	 * constraint? if yes, it also specifies the forbidden region.
	 *
	 * @param d   Indicates which coordinate dimension we want to prune
	 * @param k   The total number of dimensions (The dimension of the space we  are working in)
	 * @param o   The object in question
	 * @param c   The current point in question (basically he sweep point)
	 * @param ACTRS  A vector of all active internal constraints
	 * @param increase  A boolean specifying if we are pruning the min (true) or the max (false)
	 * @return A vector of 2 elements. The first is a Boolean object indicating  the fact of whether there is a forbidden region or not and the
	 *         second is a Region object indicating the forbidden region if it exists.
	 */
	public Vector GetFR(int d, int k, Obj o, Point c, Vector<InternalConstraint> ACTRS, boolean increase)
	{
		// return a vector of 2 elements: the first is a Boolean object the second is a Region object
		Vector<Object> result = new Vector<Object>();
		Vector v = new Vector();
		if (increase)
		{
			for (int i = 0; i < ACTRS.size(); i++)
			{
				v = intermediateLayer.IsFeasible(ACTRS.elementAt(i), true, d, k, o, c);
				if (((Boolean) v.elementAt(0)).booleanValue() == false)
				{
					result.clear();
					result.add(0, Boolean.valueOf(true));
					result.add(1, v.elementAt(1));
					return result;
				}
			}
			result.clear();
			result.add(0, new Boolean(false));
			result.add(1, new Region(cst.getDIM(), -1));
			return result;
		} else
		{
			for (int i = 0; i < ACTRS.size(); i++)
			{
				v = intermediateLayer.IsFeasible(ACTRS.elementAt(i), false, d, k, o, c);
				if (((Boolean) v.elementAt(0)).booleanValue() == false)
				{
					result.clear();
					result.add(0, new Boolean(true));
					result.add(1, v.elementAt(1));
					return result;
				}
			}
			result.clear();
			result.add(0, new Boolean(false));
			result.add(1, new Region(cst.getDIM(), -1));
			return result;
		}
	}

	/**
	 * This is the main filtering algorithm associated with the Geost_Constraint.
	 *
	 * @param k  The total number of dimensions (The dimension of the space we are working in)
	 * @param oIDs  The list of object IDs
	 * @param ectrs The list of external constraints
	 * @return It return false if we couldn't prune anything, this means that we sweeped the whole space and couldn't find a placement. This cause
	 *         a failure of the Geost_Constraint. Otherwise it returns true.
	 */


	public boolean FilterCtrs(int k, int[] oIDs, Vector<ExternalConstraint> ectrs) throws ContradictionException
	{

		boolean nonFix = true;
		while (nonFix) {
			nonFix = false;
			for (int i = 0; i < ectrs.size(); i++)
			{
				ectrs.elementAt(i).setFrame(externalLayer.InitFrameExternalConstraint(ectrs.elementAt(i), oIDs));
			}
			for (int i = 0; i < oIDs.length; i++)
			{
				Obj o = stp.getObject(oIDs[i]);
				int domainsSize = o.calculateDomainSize();
				if (!FilterObjWP(k, oIDs[i]))
					return false;
				else
				{
					// need to check if Object attributes has been pruned
					if (domainsSize != o.calculateDomainSize())
					{
						//update the relative forbidden regions attached to object o
						for (int j = 0; j < o.getRelatedExternalConstraints().size(); j++)
						{
							o.getRelatedExternalConstraints().elementAt(j).getFrame().getRelForbidRegions().remove(o.getObjectId());
							int[] oIDi = { oIDs[i] };
							o.getRelatedExternalConstraints().elementAt(j).getFrame().getRelForbidRegions().put(o.getObjectId(),externalLayer.InitFrameExternalConstraint(
															o.getRelatedExternalConstraints().elementAt(j),oIDi).getRelForbidRegions(oIDs[i]));
						}
						//has to saturate once again
						nonFix = true;
					}
				}
			}
		}
		return true;
	}



	/**
	 * Filters all the k coordinates and the shape of a given object o according to all external geometrical constraints where o occurs.
	 *
	 * @param k  The total number of dimensions (The dimension of the space we are working in)
	 * @param oid The object id
	 * @return It return false if we couldn't prune anything, this means that we  sweeped the whole space and couldn't find a placement. This cause
	 *         a failure of the Geost_Constraint. Otherwise it returns true.
	 */

	//WP in "FilterObjWP" means With Polymorphic. In fact there also FilterObj (see bellow) for filtering the coordinate of an object with fixed shape.
	public boolean FilterObjWP(int k, int oid) throws ContradictionException
	// In the technical report  we pass the Frame also however there is no need here since
	// the Frame is part of the external constraint
	{
		Obj o = stp.getObject(oid);
		if (o.getShapeId().isInstantiated())
		{
			return FilterObj(k, oid);
		}
		else
		{
			int[] minG = new int[k];
			int[] maxG = new int[k];
			for (int d = 0; d < k; d++)
			{
				// initialize generalization
				minG[d] = o.getCoord(d).getSup() + 1;
				maxG[d] = o.getCoord(d).getInf() - 1;
			}

			//
			for (int sid = o.getShapeId().getInf(); sid <= o.getShapeId().getSup(); sid = o.getShapeId().getNextDomainValue(sid))
			{

				int[] max = new int[k];
				int[] min = new int[k];
				boolean b = false;

	            // We call FilterObj with the fixed shape sid. To avoid the creation of another object we use worldPush and worldPop. Actually, by doing so, the object o
				//is modified between worldPush() and worldPop() (where we collect the information we interested to : b, max, min) and restored into its state after worldPop
				o.getCoord(0).getSolver().worldPushDuringPropagation();
			    o.getShapeId().instantiate(sid, -1);


				b = FilterObj(k, oid);

				if(b)
				{
					for (int d = 0; d < k; d++)
					{
						max[d] = o.getCoord(d).getSup();
						min[d] = o.getCoord(d).getInf();
					}
				}
				o.getCoord(0).getSolver().worldPopDuringPropagation();



				if (!b)
					o.getShapeId().removeVal(sid, -1);
				else
				{
					for (int d = 0; d < k; d++)
					{
						minG[d] = Math.min(min[d], minG[d]);
						maxG[d] = Math.max(max[d], maxG[d]);
					}
				}
			}
			for (int d = 0; d < k; d++)
			{
				o.getCoord(d).updateInf(minG[d], -1);
				o.getCoord(d).updateSup(maxG[d], -1);
			}
			return true;
		}
	}






	/**
	 * Filters all the k coordinates of a given object o with fixed shape according to all external geometrical constraints where o occurs.
	 *
	 * @param k  The total number of dimensions (The dimension of the space we  are working in)
	 * @param oid  The object id
	 * @return It return false if we couldn't prune anything, this means that we  sweeped the whole space and couldn't find a placement. This cause
	 *         a failure of the Geost_Constraint. Otherwise it returns true.
	 */
	public boolean FilterObj(int k, int oid) throws ContradictionException
	// In the technical report we pass the Frame also however there is no need here since the Frame is part of the external constraint
	{
		Obj o = stp.getObject(oid);
		o.getRelatedInternalConstraints().clear();
//		for (int d = 0; d < k; d++)
//		{
//			// Add Possible outbox constraints corresponding to holes of o.coords[d]
//		}

		for (int i = 0; i < o.getRelatedExternalConstraints().size(); i++)
		{
			Vector<InternalConstraint> v = externalLayer.GenInternalCtrs(o.getRelatedExternalConstraints().elementAt(i), o);
			for (int j = 0; j < v.size(); j++)
			{
				o.addRelatedInternalConstraint(v.elementAt(j));
			}
		}
		for (int d = 0; d < k; d++)
		{
		   //if((!o.getCoord(d).isInstantiated())||(d==k-1)  )
			if(o.getRelatedInternalConstraints().size()>0)
			{
			   if ((!PruneMin(o, d, k, o.getRelatedInternalConstraints()))|| (!PruneMax(o, d, k, o.getRelatedInternalConstraints())))
				   return false;
			}
		}

		return true;
	}




	/**
	 * Adjusts the lower bound of the d^th coordinate of the origin of the  object o according to the set of internal constraints associated with  object o.
	 *
	 * @param o  The object.
	 * @param d  The dimension we want to prune.
	 * @param k  The total number of dimensions (The dimension of the space we  are working in)
	 * @param ictrs The internal constraints associated with o.
	 * @return It return false if we couldn't prune the min of Object o, this means that we sweeped the whole space and couldn't prune the min of o.
	 */
	public boolean PruneMin(Obj o, int d, int k, Vector<InternalConstraint> ictrs) throws ContradictionException
	{
		boolean b = true;
		Point c = new Point(k);
		Point n = new Point(k);

		Vector<InternalConstraint> ACTRS = new Vector<InternalConstraint>();
		ACTRS = ictrs;

		for (int i = 0; i < o.getCoordinates().length; i++)
		{
			c.setCoord(i, o.getCoord(i).getInf()); // Initial position of point
			n.setCoord(i, o.getCoord(i).getSup() + 1); // Upper limits + 1 in the different dimensions
		}

		Vector forbidRegion = GetFR(d, k, o, c, ACTRS, true);
		boolean infeasible = ((Boolean) forbidRegion.elementAt(0)).booleanValue();
		Region f = (Region) forbidRegion.elementAt(1);

		while (b && infeasible)
		{
			for (int i = 0; i < k; i++)
			{
				// update n according to f
				n.setCoord(i, Math.min(n.getCoord(i), f.getMaximumBoundary(i) + 1));
			}
			Vector adjUp = AdjustUp(c, n, o, d, k); // update the position of c to check
			c = (Point) adjUp.elementAt(0);
			n = (Point) adjUp.elementAt(1);
			b = ((Boolean) adjUp.elementAt(2)).booleanValue();

			forbidRegion = GetFR(d, k, o, c, ACTRS, true);
			infeasible = ((Boolean) forbidRegion.elementAt(0)).booleanValue();
			f = (Region) forbidRegion.elementAt(1);
		}
		if (b)
		{
			o.getCoord(d).updateInf(c.getCoord(d), -1);
			cst.nbOfUpdates++;

		}
		return b;
	}

	/**
	 * Moves up to the next feasible point, this function is used by the PruneMin function.
	 */


	public Vector AdjustUp(Point c, Point n, Obj o, int d, int k)
	{
		Vector<Object> result = new Vector<Object>();
		int jPrime = 0;
		int j = k - 1;
		while (j >= 0)
		{
			jPrime = (j + d) % k;
			c.setCoord(jPrime, n.getCoord(jPrime));
			n.setCoord(jPrime, o.getCoord(jPrime).getSup() + 1);
			if (c.getCoord(jPrime) <= o.getCoord(jPrime).getSup())
			{
				result.clear();
				result.add(0, c);
				result.add(1, n);
				result.add(2, Boolean.valueOf(true));
				return result;
			} else
				c.setCoord(jPrime, o.getCoord(jPrime).getInf());
			j--;
		}
		result.clear();
		result.add(0, c);
		result.add(1, n);
		result.add(2, new Boolean(false));
		return result;
	}

	/**
	 * Adjusts the upper bound of the d^th coordinate of the origin of the  object o according to the set of internal constraints associated with object o.
	 *
	 * @param o  The object.
	 * @param d  The dimension we want to prune.
	 * @param k  The total number of dimensions (The dimension of the space we  are working in)
	 * @param ictrs  The internal constraints associated with o.
	 * @return It return false if we couldn't prune the max of Object o, this means that we sweeped the whole space and couldn't prune the max  of o.
	 */
	public boolean PruneMax(Obj o, int d, int k, Vector<InternalConstraint> ictrs) throws ContradictionException
	{
		boolean b = true;
		Point c = new Point(k);
		Point n = new Point(k);

		Vector<InternalConstraint> ACTRS = new Vector<InternalConstraint>();
	    ACTRS = ictrs;


		for (int i = 0; i < o.getCoordinates().length; i++)
		{
			c.setCoord(i, o.getCoord(i).getSup()); // Initial position of point
			n.setCoord(i, o.getCoord(i).getInf() - 1); // Lower limits - 1 in the different dimensions
		}


		Vector forbidRegion = GetFR(d, k, o, c, ACTRS, false);
		boolean infeasible = ((Boolean) forbidRegion.elementAt(0)).booleanValue();
		Region f = (Region) forbidRegion.elementAt(1);
		while (b && infeasible)
		{
			for (int i = 0; i < k; i++)
			{
				// update n according to f
				n.setCoord(i, Math.max(n.getCoord(i), f.getMinimumBoundary(i) - 1));
			}
			Vector adjDown = AdjustDown(c, n, o, d, k);// update the position of c to check
			c = (Point) adjDown.elementAt(0);
			n = (Point) adjDown.elementAt(1);
			b = ((Boolean) adjDown.elementAt(2)).booleanValue();

			forbidRegion = GetFR(d, k, o, c, ACTRS, false);
			infeasible = ((Boolean) forbidRegion.elementAt(0)).booleanValue();
			f = (Region) forbidRegion.elementAt(1);
		}

		if (b) {
			o.getCoord(d).updateSup(c.getCoord(d), -1);
			cst.nbOfUpdates++;
		}

		return b;
	}

	/**
	 * Moves down to the next feasible point, this function is used by the PruneMax function.
	 */
	public Vector AdjustDown(Point c, Point n, Obj o, int d, int k)
	{
		Vector<Object> result = new Vector<Object>();
		int jPrime = 0;
		int j = k - 1;
		while (j >= 0)
		{
			jPrime = (j + d) % k;
			c.setCoord(jPrime, n.getCoord(jPrime));
			n.setCoord(jPrime, o.getCoord(jPrime).getInf() - 1);
			if (c.getCoord(jPrime) >= o.getCoord(jPrime).getInf())
			{
				result.clear();
				result.add(0, c);
				result.add(1, n);
				result.add(2, Boolean.valueOf(true));
				return result;
			} else
				c.setCoord(jPrime, o.getCoord(jPrime).getSup());
			j--;
		}
		result.clear();
		result.add(0, c);
		result.add(1, n);
		result.add(2, new Boolean(false));
		return result;
	}

	/**
	 * Tries to fix all the objects within one single propagation.
	 *
	 * @param k  The total number of dimensions (The dimension of the space we are working in)
	 * @param oIDs  The list of object IDs
	 * @param ectrs The list of external constraints
	 * @param ctrlVs The list of controlling vectors
	 * @return It return true if we can fix all the objects. Otherwise it returns false.
	 */
	public boolean FixAllObjs(int k, int[] oIDs, Vector<ExternalConstraint> ectrs, Vector<int[]> ctrlVs) throws ContradictionException
	{
		for (int i = 0; i < ectrs.size(); i++)
		{
			ectrs.elementAt(i).setFrame(externalLayer.InitFrameExternalConstraint(ectrs.elementAt(i), oIDs));

		}
		int nbOfCtrlV = ctrlVs.size();
		for (int i = 0; i < oIDs.length; i++)
		{
			Obj o = stp.getObject(oIDs[i]);
			int m = i % nbOfCtrlV;
			if (!FixObj(k, oIDs[i], ctrlVs.elementAt(m)))
				return false;
			else
				for (int j = 0; j < o.getRelatedExternalConstraints().size(); j++)
				{
					int[] oIDi = {oIDs[i]};
					o.getRelatedExternalConstraints().elementAt(j).getFrame().getRelForbidRegions().remove(o.getObjectId());
					o.getRelatedExternalConstraints().elementAt(j).getFrame().getRelForbidRegions().put(o.getObjectId(), externalLayer.InitFrameExternalConstraint(o
									.getRelatedExternalConstraints().elementAt(j), oIDi).getRelForbidRegions(oIDs[i]));
				}
		}
		return true;
	}

	/**
	 * Tries to fix the shape and the k coordinates of a given object o according to all external geometrical constraints where o occurs and according the
	 * corresponding controlling vector v.
	 *
	 * @param k  The total number of dimensions (The dimension of the space we are working in)
	 * @param oid The object id
	 * @param ctrlV The control vector
	 * @return It return true if we can fin a feasible point for the object o. Otherwise it returns false.
	 */

	public boolean FixObj(int k, int oid, int[] ctrlV)
			throws ContradictionException
	// In the technical report we pass the Frame also however there is no need here since the Frame is part of the external constraint
	{
		Obj o = stp.getObject(oid);
		if (ctrlV[0] < 0)
			o.getShapeId().instantiate(o.getShapeId().getInf(), -1);
		else
			o.getShapeId().instantiate(o.getShapeId().getSup(), -1);

		o.getRelatedInternalConstraints().clear();
//		for (int d = 0; d < k; d++) {
//			// Add Possible outbox constraints corresponding to holes of o.coords[d]
//		}
		for (int i = 0; i < o.getRelatedExternalConstraints().size(); i++)
		{
			Vector<InternalConstraint> v = externalLayer.GenInternalCtrs(o.getRelatedExternalConstraints().elementAt(i), o);
			for (int j = 0; j < v.size(); j++)
			{
				o.addRelatedInternalConstraint(v.elementAt(j));
			}
		}
		return PruneFix(o, k, ctrlV, o.getRelatedInternalConstraints());
	}

	/**
	 * Fix completely all the coordinates of the origin of the object o according to the set of internal constraints associated with object o.
	 *
	 * @param o  The object.
	 * @param k  The total number of dimensions (The dimension of the space we are working in)
	 * @param ictrs  The internal constraints associated with o.
	 * @param ctrlV  The control vector
	 * @return It return false if we couldn't fix the coordinates of Object o  according to the order specified by the controlling vector ctrlV.
	 */

	public boolean PruneFix(Obj o, int k, int[] ctrlV, Vector<InternalConstraint> ictrs) throws ContradictionException
	{
		Point c = new Point(k);
		Point n = new Point(k);
		int dPrime = 0;
		for (int d = k - 1; d > -1; d--)
		{
			dPrime = Math.abs(ctrlV[d+1]) - 2;
			if (ctrlV[d+1] < 0)
			{
				c.setCoord(dPrime, o.getCoord(dPrime).getInf());
				n.setCoord(dPrime, o.getCoord(dPrime).getSup() + 1);
			} else
			{
				c.setCoord(dPrime, o.getCoord(dPrime).getSup());
				n.setCoord(dPrime, o.getCoord(dPrime).getInf() - 1);
			}
		}
		Vector forbidRegion = GetFR(Math.abs(ctrlV[1]) -2, k, o, c, ictrs,false);
		boolean infeasible = ((Boolean) forbidRegion.elementAt(0)).booleanValue();
		Region f = (Region) forbidRegion.elementAt(1);
		while (infeasible)
		{
			for (int d = k - 1; d > -1; d--)
			{
				dPrime = Math.abs(ctrlV[d+1]) - 2;
				if (ctrlV[d+1] < 0)
				{
					n.setCoord(dPrime, Math.min(n.getCoord(dPrime), f.getMaximumBoundary(dPrime) + 1));
				}
				else
				{
					n.setCoord(dPrime, Math.max(n.getCoord(dPrime), f.getMinimumBoundary(dPrime) - 1));
				}
			}
			nextcand: {
				for (int d = k - 1; d > -1; d--)
				{
					dPrime = Math.abs(ctrlV[d + 1]) - 2;
					c.setCoord(dPrime, n.getCoord(dPrime));
					if (ctrlV[d + 1] < 0)
					{
						n.setCoord(dPrime, o.getCoord(dPrime).getSup() + 1);
						if (c.getCoord(dPrime) < n.getCoord(dPrime))
							break nextcand;
						else
							c.setCoord(dPrime, o.getCoord(dPrime).getInf());
					} else
					{
						n.setCoord(dPrime, o.getCoord(dPrime).getInf() - 1);
						if (c.getCoord(dPrime) > n.getCoord(dPrime))
							break nextcand;
						else
							c.setCoord(dPrime, o.getCoord(dPrime).getSup());
					}
				}
				return false;
			}
			forbidRegion = GetFR(Math.abs(ctrlV[1]) - 2, k, o, c, ictrs, true);
			infeasible = ((Boolean) forbidRegion.elementAt(0)).booleanValue();
			f = (Region) forbidRegion.elementAt(1);
		}
		for (int d = 0; d < k; d++)
		{
			o.getCoord(d).instantiate(c.getCoord(d), -1);
		}
		return true;
	}
}
