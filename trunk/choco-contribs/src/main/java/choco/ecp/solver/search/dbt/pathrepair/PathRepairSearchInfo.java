package choco.ecp.solver.search.dbt.pathrepair;


import java.util.Comparator;

import choco.kernel.solver.constraints.AbstractSConstraint;


/**
 * Created by IntelliJ IDEA.
 * <p/>
 * User: Administrateur
 * <p/>
 * Date: 15 janv. 2004
 * <p/>
 * Time: 14:59:44
 * <p/>
 * To change this template use Options | File Templates.
 */

//public class PathRepairSearchInfo implements SearchInfo {
//
//
//  /*
//
//  *  The weigth of a decision constraint appearing in the conflicts in memory
//
//  *  It corresponds to its frequency among the all conflict set weighted by the size of each conflict
//
//  */
//
//  private float weigthInfo = 0;
//
//  private Comparator comparatorInfo;
//
//
//  public PathRepairSearchInfo() {
//
//    comparatorInfo = new PathRepairComparator();
//
//  }
//
//
//  public float getWeigth() {
//
//    return weigthInfo;
//
//  }
//
//
//  public Comparator getComparator() {
//
//    return comparatorInfo;
//
//  }
//
//
//  public void add(float val) {
//    this.weigthInfo += val;
//  }
//
//  public void set(float val) {
//    this.weigthInfo = val;
//  }
//
//  private class PathRepairComparator implements Comparator {
//
//
//    public int compare(Object o1, Object o2) {
//
//      PalmConstraintPlugin plug1 = (PalmConstraintPlugin) ((AbstractSConstraint) o1).getPlugIn();
//
//      PalmConstraintPlugin plug2 = (PalmConstraintPlugin) ((AbstractSConstraint) o2).getPlugIn();
//
//      if (((PathRepairSearchInfo) plug1.getSearchInfo()).getWeigth() > ((PathRepairSearchInfo) plug2.getSearchInfo()).getWeigth())
//
//        return -1;
//
//      else if (((PathRepairSearchInfo) plug1.getSearchInfo()).getWeigth() == ((PathRepairSearchInfo) plug2.getSearchInfo()).getWeigth()) {
//
//        if (plug1.getTimeStamp() > plug2.getTimeStamp())
//
//          return -1;
//
//        else if (plug1.getTimeStamp() < plug2.getTimeStamp())
//
//          return 1;
//
//        else
//
//          return 0;
//
//      } else
//
//        return 1;
//
//    }
//
//  }
//
//
//}