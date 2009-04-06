package i_want_to_use_this_old_version_of_choco.palm.benders.search;

import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.benders.MasterSlavesRelation;

import java.util.ArrayList;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class SumRelation extends MasterSlavesRelation {


  public int computeBound(int mobj, int[] objs, int k) {
    int cbound = 0;
    int finIter = k == 0 ? objs.length : k;
    for (int i = 0; i < finIter; i++) {
      cbound += objs[i];
    }
    return cbound + mobj;
  }

  public ArrayList computeExpl(Explanation[] cuts) {
    ArrayList list = new ArrayList();
    Explanation exp = null;
    for (int i = 0; i < cuts.length; i++) {
      if (cuts[i] != null) {
        if (exp == null)
          exp = cuts[i];
        else {
          exp.merge(cuts[i]);
        }
      }

    }
    list.add(exp);
    return list;
  }


}
