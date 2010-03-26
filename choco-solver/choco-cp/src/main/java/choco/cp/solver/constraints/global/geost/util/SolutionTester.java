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
package choco.cp.solver.constraints.global.geost.util;


import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.Setup;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.model.variables.geost.ShiftedBox;

import java.util.Iterator;
import java.util.List;

/**
 * After solving and finding a solution, this class provides a function that tests whether a given solution is a valid solution. 
 * It is only for the non_overlapping constraint. 
 */
public final class SolutionTester {
	Setup stp;
	Constants cst;
	public SolutionTester(Setup s, Constants c)
	{
		this.stp = s;
		this.cst = c; 
	}
	
	public boolean testSolution()
	{
		
		Iterator objItr1;
		Iterator objItr2;
		
		objItr1 = stp.getObjectKeySet().iterator();
		while(objItr1.hasNext())
		{
			int oid1 = (Integer) objItr1.next();
			Obj o1 = stp.getObject(oid1);
			objItr2 = stp.getObjectKeySet().iterator();
			while(objItr2.hasNext())
			{
				int oid2 = (Integer) objItr2.next();
				Obj o2 = stp.getObject(oid2);
				if(oid1 != oid2)
				{
					//check for intersection: Two objects do not intersect if there exist at least on dim where they do not intersect
					List<ShiftedBox> sb1 = stp.getShape(o1.getShapeId().getInf());
					List<ShiftedBox> sb2 = stp.getShape(o2.getShapeId().getInf());
					
					for(int i = 0; i < sb1.size(); i++)
					{
						for(int j = 0; j < sb2.size(); j++)
						{
							boolean intersect = true;
							for(int k = 0; k < cst.getDIM(); k++)
							{
								if(!(
										((sb2.get(j).getOffset(k) + o2.getCoord(k).getInf() >= sb1.get(i).getOffset(k) + o1.getCoord(k).getInf()) &&
										(sb2.get(j).getOffset(k) + o2.getCoord(k).getInf() < sb1.get(i).getOffset(k) + o1.getCoord(k).getInf()  + sb1.get(i).getSize(k)))
										||
										((sb2.get(j).getOffset(k) + o2.getCoord(k).getInf() + sb2.get(j).getSize(k) > sb1.get(i).getOffset(k) + o1.getCoord(k).getInf()) && 
										(sb2.get(j).getOffset(k) + o2.getCoord(k).getInf() + sb2.get(j).getSize(k) <= sb1.get(i).getOffset(k) + o1.getCoord(k).getInf() + sb1.get(i).getSize(k)))
									)
								   )
								{
									intersect = false;
									break;
								}
							}
							if(intersect)
								return false;
						}
					}
				}
			}
		}
		return true;
	}

}
