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
import choco.cp.solver.constraints.global.geost.externalConstraints.*;
import choco.cp.solver.constraints.global.geost.frames.Frame;
import choco.cp.solver.constraints.global.geost.frames.NonOverlappingFrame;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.geometricPrim.Region;
import choco.cp.solver.constraints.global.geost.internalConstraints.InternalConstraint;
import choco.cp.solver.constraints.global.geost.internalConstraints.Outbox;
import choco.kernel.model.variables.geost.ShiftedBox;

import java.util.Iterator;
import java.util.Vector;


/**
 * This is the external layer class. It implements the functionality that each external constraint should have. For every external constraint we 
 * should be able to create the corresponding FRAME and generate the corresponding internal constraints. 
 */
public class ExternalLayer {

	Constants cst;
	Setup stp;

	/**
	 * Creates an ExternalLayer instance for a specific constants class and a specific setup class
	 *
	 * @param c The constants class
	 * @param s The Setup class
	 */
	public ExternalLayer(Constants c, Setup s) {
		cst = c;
		stp = s;
	}

	/**
	 * @param ectr  An externalConstraint object
	 * @param oIDs  The list of object IDs
	 * @return The frame that correspond to the external constraint ectr.
	 */
	public Frame InitFrameExternalConstraint(ExternalConstraint ectr, int[] oIDs)
	{
		Frame result;
		switch (ectr.getEctrID()) {
		case Constants.COMPATIBLE:
			result = InitFrameExternalConstraintForCompatible((Compatible) ectr, oIDs);
			break;
		case Constants.INCLUDED:
			result = InitFrameExternalConstraintForIncluded((Included) ectr, oIDs);
			break;
		case Constants.NON_OVERLAPPING:
			result = InitFrameExternalConstraintForNonOverlapping((NonOverlapping) ectr, oIDs);
			break;
		case Constants.VISIBLE:
			result = InitFrameExternalConstraintForVisible((Visible) ectr, oIDs);
			break;
		default:
			System.err
					.println("A call to InitFrameExternalConstraint with incorrect ectr parameter");
			result = null;
		}
		return result;
	}

	/**
	 * @param ectr An externalConstraint object
	 * @param o    An object
	 * @return  A vector containing all the internal constraints that are applied to o caused by ectr
	 */
	public Vector<InternalConstraint> GenInternalCtrs(ExternalConstraint ectr, Obj o)
	{
		Vector<InternalConstraint> result;
		switch (ectr.getEctrID()) {
		case Constants.COMPATIBLE:
			result = GenInternalCtrsForCompatible((Compatible) ectr, o);
			break;
		case Constants.INCLUDED:
			result = GenInternalCtrsForIncluded((Included) ectr, o);
			break;
		case Constants.NON_OVERLAPPING:
			result = GenInternalCtrsForNonOverlapping((NonOverlapping) ectr, o);
			break;
		case Constants.VISIBLE:
			result = GenInternalCtrsForVisible((Visible) ectr, o);
			break;
		default:
			System.err.println("A call to InitFrameExternalConstraint with incorrect ectr parameter");
			result = null;
		}
		return result;
	}

	private Frame InitFrameExternalConstraintForCompatible(Compatible ectr, int[] oIDs)
	{
		// Should be changed for Compatible Frame
		NonOverlappingFrame f = new NonOverlappingFrame();

		return f;
	}

	private Frame InitFrameExternalConstraintForIncluded(Included ectr,	int[] oIDs)
	{
		// Should be changed for Included Frame
		NonOverlappingFrame f = new NonOverlappingFrame();

		return f;
	}





	private Frame InitFrameExternalConstraintForNonOverlapping(NonOverlapping ectr, int[] oIDs)
	{
		NonOverlappingFrame f = new NonOverlappingFrame();
		for (int i = 0; i < oIDs.length; i++)
		{
			Obj o = stp.getObject(oIDs[i]);
			int m = o.getShapeId().getDomainSize();

			Vector<Region> regions = new Vector<Region>();

			int [][] set = new int[m][];
			int ivalue = 0;
			for (int sid = o.getShapeId().getInf(); sid <= o.getShapeId().getSup(); sid = o.getShapeId().getNextDomainValue(sid))
			{
				int nbOfSbox = stp.getShape(sid).size();
				set[ivalue] = new int[nbOfSbox];
				for (int j = 0; j < nbOfSbox; j++)
				{
					set[ivalue][j] = j;
				}
				ivalue++;
			}


			int [] pointer = new int[m];
		    boolean print = true;
	        while(true) {
	        	Region r = new Region(cst.getDIM(), o.getObjectId());
	        	for (int j = 0; j < cst.getDIM(); j++)
	        	{
	        		int max = stp.getShape(o.getShapeId().getInf()).elementAt(set[0][pointer[0]]).getOffset(j);
					int min = stp.getShape(o.getShapeId().getInf()).elementAt(set[0][pointer[0]]).getOffset(j) + stp.getShape(o.getShapeId().getInf()).elementAt(set[0][pointer[0]]).getSize(j);
					int curDomVal = o.getShapeId().getNextDomainValue(o.getShapeId().getInf());
					for (int s = 1; s < m; s++)
					{
						max = Math.max(max, stp.getShape(curDomVal).elementAt(set[s][pointer[s]]).getOffset(j));
						min = Math.min(min, stp.getShape(curDomVal).elementAt(set[s][pointer[s]]).getOffset(j) + stp.getShape(curDomVal).elementAt(set[s][pointer[s]]).getSize(j));
						curDomVal = o.getShapeId().getNextDomainValue(curDomVal);
					}
					r.setMinimumBoundary(j, o.getCoord(j).getSup() + max + 1);
					r.setMaximumBoundary(j, o.getCoord(j).getInf() + min - 1);
				}
				regions.add(r);
				for(int j = m-1; j>=0 ;j--)
				{
					if(pointer[j] ==set[j].length-1)
					{
						if(j==0)
						{
							print = false;
						}
						pointer[j]=0;
						continue;
					}
					else
					{
						pointer[j]+=1;
						break;
					}
				}
				if(!print)
				{
					break;
		        }
			}
	        f.addForbidRegions(o.getObjectId(), regions);
	    }
		return f;
	}








	private Frame InitFrameExternalConstraintForVisible(Visible ectr, int[] oIDs)
	{
		// Should be changed for Visible Frame
		NonOverlappingFrame f = new NonOverlappingFrame();

		return f;
	}

	private Vector<InternalConstraint> GenInternalCtrsForCompatible(Compatible ectr, Obj o)
	{
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();

		return ictrs;
	}

	private Vector<InternalConstraint> GenInternalCtrsForIncluded(Included ectr, Obj o)
	{
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();

		return ictrs;
	}

	private Vector<InternalConstraint> GenInternalCtrsForNonOverlapping(NonOverlapping ectr, Obj o)
	{

		// Since non_overlapping constraint then we will generate outbox constraints
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();
		Vector<ShiftedBox> sb = stp.getShape(o.getShapeId().getInf());
		Iterator itr;
		itr = ectr.getFrame().getRelForbidRegions().keySet().iterator();
		while (itr.hasNext())
		{
			int i = ((Integer) itr.next()).intValue();
			if (!(o.getObjectId() == i))
			{
				for (int k = 0; k < sb.size(); k++) {
					// We will generate an outbox constraint corresponding to each relative forbidden region we already generated
					// for the shifted boxes of the shape corresponding to the Obj o

					// here we go into the relative forbidden regions
					loop: for (int l = 0; l < ectr.getFrame().getRelForbidRegions(i).size(); l++)
					{
						int[] t = new int[cst.getDIM()];
						int[] s = new int[cst.getDIM()];
						for (int j = 0; j < cst.getDIM(); j++)
						{
							int min = ectr.getFrame().getRelForbidRegions(i).elementAt(l).getMinimumBoundary(j)- sb.elementAt(k).getOffset(j)- sb.elementAt(k).getSize(j);
							int max = ectr.getFrame().getRelForbidRegions(i).elementAt(l).getMaximumBoundary(j)- sb.elementAt(k).getOffset(j);

							s[j] = max - min + 1; // length of the jth coordinate
							if (s[j] <= 0) // since the length is negative
								continue loop;
							t[j] = min; // It is the offset. lower left corner.
							if ((o.getCoord(j).getSup() < t[j])|| (o.getCoord(j).getInf() > t[j] + s[j]- 1))
								// this means the intersection of dom(o.x) and the region forbidden region associated with Outbox(t,s) is empty. In the other words all
								// the placement space is feasible for o.x according to the constraint Outbox(t,s)
								continue loop;
						}
						ictrs.add(new Outbox(t, s));
					}
				}
			}
		}
		return ictrs;
	}


	private Vector<InternalConstraint> GenInternalCtrsForVisible(Visible ectr, Obj o)
	{
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();

		return ictrs;
	}
}