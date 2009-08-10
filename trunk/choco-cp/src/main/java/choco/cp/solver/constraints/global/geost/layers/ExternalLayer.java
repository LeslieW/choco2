package choco.cp.solver.constraints.global.geost.layers;


import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.constraints.global.geost.Setup;
import choco.cp.solver.constraints.global.geost.externalConstraints.*;
import choco.cp.solver.constraints.global.geost.frames.DistLinearFrame;
import choco.cp.solver.constraints.global.geost.frames.ForbiddenRegionFrame;
import choco.cp.solver.constraints.global.geost.frames.Frame;
import choco.cp.solver.constraints.global.geost.frames.NonOverlappingFrame;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.cp.solver.constraints.global.geost.geometricPrim.Region;
import choco.cp.solver.constraints.global.geost.internalConstraints.*;
import choco.kernel.common.util.objects.Pair;
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
        case Constants.DIST_LEQ:
            result = InitFrameExternalConstraintForDistLeq((DistLeq) ectr, oIDs);
            break;
        case Constants.DIST_GEQ:
            result = InitFrameExternalConstraintForDistGeq((DistGeq) ectr, oIDs);
            break;
        case Constants.DIST_LINEAR:
            result = InitFrameExternalConstraintForDistLinear((DistLinear) ectr, oIDs);
            break;
        case Constants.NON_OVERLAPPING_CIRCLE:
            result = InitFrameExternalConstraintForNonOverlappingCircle((NonOverlappingCircle) ectr, oIDs);
            break;

		default:
			System.err.println("A call to InitFrameExternalConstraint with incorrect ectr parameter");
            System.exit(-1);
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
        case Constants.DIST_LEQ:
            result = GenInternalCtrsForDistLeq((DistLeq) ectr, o);
            break;
        case Constants.DIST_GEQ:
            result = GenInternalCtrsForDistGeq((DistGeq) ectr, o);
            break;
        case Constants.DIST_LINEAR:
            result = GenInternalCtrsForDistLinear((DistLinear) ectr, o);
            break;
		default:
			System.err.println("A call to GenInternalCstrs with incorrect ectr parameter");
            System.exit(-1);
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



   	private Frame InitFrameExternalConstraintForDistLeq(DistLeq ectr, int[] oIDs)
	{
        /*No ploymorphism for now*/
        int s1=stp.getObject(ectr.o1).getShapeId().getVal();
        int s2=stp.getObject(ectr.o2).getShapeId().getVal();
		ForbiddenRegionFrame f = new ForbiddenRegionFrame(ectr.q,ectr.D,s1,s2,ectr.o1, ectr.o2);
        for (int i = 0; i < oIDs.length; i++)
        {
            Obj o = stp.getObject(oIDs[i]);
            Vector<Region> regions = new Vector<Region>();
            f.addForbidRegions(o.getObjectId(), regions);            
        }

		return f;
	}

    private Frame InitFrameExternalConstraintForDistGeq(DistGeq ectr, int[] oIDs)
 {
     /*No ploymorphism for now*/
     int s1=stp.getObject(ectr.o1).getShapeId().getVal();
     int s2=stp.getObject(ectr.o2).getShapeId().getVal();
     ForbiddenRegionFrame f = new ForbiddenRegionFrame(ectr.q,ectr.D,s1,s2,ectr.o1, ectr.o2);
//     for (int i = 0; i < oIDs.length; i++)
//     {
//         Obj o = stp.getObject(oIDs[i]);
//         Vector<Region> regions = new Vector<Region>();
//         f.addForbidRegions(o.getObjectId(), regions);
//     }

     return f;
 }

    private Frame InitFrameExternalConstraintForDistLinear(DistLinear ectr, int[] oIDs)
 {
     /*No ploymorphism for now*/
     DistLinearFrame f = new DistLinearFrame(ectr.a,ectr.o1,ectr.b);
     for (int i = 0; i < oIDs.length; i++)
     {
         Obj o = stp.getObject(oIDs[i]);
         Vector<Region> regions = new Vector<Region>();
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

    private Frame InitFrameExternalConstraintForNonOverlappingCircle(NonOverlappingCircle ectr, int[] oIDs)
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

    public Pair<Outbox,Boolean> mergeAdjacent(Outbox new_ob, Outbox last_ob) {
        //true if merging has occured

        //Check if the last outbox is adjacent on a single dimension with the last outbox

        int dim = new_ob.adjacent(last_ob);
        if ((dim!=-1) && (!new_ob.sameSize(last_ob, dim))) dim=-1;        
        if (dim!=-1) new_ob.merge(last_ob, dim); //merge the two objects
//        if (dim!=-1) System.out.println("after merge:"+new_ob);

        return new Pair(new_ob, new Boolean(dim!=-1));

    }



    private Vector<InternalConstraint> GenInternalCtrsForNonOverlapping(NonOverlapping ectr, Obj o)
	{

		// Since non_overlapping constraint then we will generate outbox constraints
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();
		Vector<ShiftedBox> sb = stp.getShape(o.getShapeId().getInf());
		Iterator itr;
		itr = ectr.getFrame().getRelForbidRegions().keySet().iterator();
        boolean printit=false;
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

                            if (printit) System.out.println(o.getObjectId()+" "+j+" "+o);
                            int supDom = o.getCoord(j).getSup();// + sb.elementAt(k).getOffset(j) + sb.elementAt(k).getSize(j);
                            int infDom = o.getCoord(j).getInf();// + sb.elementAt(k).getOffset(j) ;
                            int maxObj = o.getCoord(j).getSup() + sb.elementAt(k).getOffset(j) + sb.elementAt(k).getSize(j) -1;
                            if (maxObj > o.getCoord(j).getSup()) maxObj = o.getCoord(j).getSup();
                            int minObj = o.getCoord(j).getInf() + sb.elementAt(k).getOffset(j);
                            if (minObj < o.getCoord(j).getInf()) minObj=o.getCoord(j).getInf();

                            if(printit) System.out.println("box: "+t[j]+" "+s[j]);
                            if(printit) System.out.println("dom: "+minObj+" "+maxObj);
                            

							if ((supDom < t[j])|| (infDom > t[j] + s[j])) {
								// this means the intersection of dom(o.x) and the region forbidden region associated with Outbox(t,s) is empty. In the other words all
								// the placement space is feasible for o.x according to the constraint Outbox(t,s)
							    if(printit) System.out.println("skip");
									continue loop;
                            }
                            if ((maxObj < t[j])|| (minObj > t[j] + s[j])) {
                                // this means the intersection of dom(o.x) and the region forbidden region associated with Outbox(t,s) is empty. In the other words all
                                // the placement space is feasible for o.x according to the constraint Outbox(t,s)
                                if(printit) System.out.println("skip2");
                                    continue loop;
                            }

                           //clipping
                            if (stp.opt.clipping) {
                            //   t[j] = Math.max(minObj, t[j]);
                           //     s[j] = Math.min(maxObj, t[j] + s[j]) - t[j]  ;
                            }

                            if(printit) System.out.println("result box: "+t[j]+" "+s[j]);


						}

                        Outbox new_ob = new Outbox(t,s);



                        Pair<Outbox,Boolean> result;
                        if (ictrs.size()!=0) {
                            Outbox last_ob=(Outbox) ictrs.elementAt(ictrs.size()-1);
                            result = mergeAdjacent(new_ob,last_ob);

                            new_ob=result.fst;

                           if (result.snd) ictrs.removeElementAt(ictrs.size()-1);

                        }

                        ictrs.add(new_ob);
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

    private Vector<InternalConstraint> GenInternalCtrsForDistGeq(DistGeq ectr, Obj o)
    {
        Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();
        ForbiddenRegionFrame f=((ForbiddenRegionFrame) ectr.getFrame());
        DistGeqIC ic = new DistGeqIC(stp,f.q,f.D,f.s1,f.s2,f.o1,f.o2,ectr.getDistanceVar());
        ictrs.add(ic);
        return ictrs;
    }

    private Vector<InternalConstraint> GenInternalCtrsForDistLeq(DistLeq ectr, Obj o)
	{
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();
        ForbiddenRegionFrame f=((ForbiddenRegionFrame) ectr.getFrame());
        DistLeqIC ic = new DistLeqIC(stp,f.q,f.D,f.s1,f.s2,f.o1,f.o2,ectr.getDistanceVar());
        ictrs.add(ic);
		return ictrs;
	}

    private Vector<InternalConstraint> GenInternalCtrsForDistLinear(DistLinear ectr, Obj o)
	{
		Vector<InternalConstraint> ictrs = new Vector<InternalConstraint>();
        DistLinearFrame f=((DistLinearFrame) ectr.getFrame());
        DistLinearIC ic = new DistLinearIC(stp,f.a,f.o1,f.b);
        ictrs.add(ic);
		return ictrs;
	}

}