package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class AssignSetVar extends AbstractSetBranching {

  SetVarSelector varselector;
  SetValSelector valselector;
  String[] LOG_DECISION_MSG = new String[]{"contains ", "contains not "};

  public AssignSetVar(SetVarSelector varselect, SetValSelector valselect) {
    varselector = varselect;
    valselector = valselect;
  }

  public Object selectBranchingObject() throws ContradictionException {
    Object x = varselector.selectSetVar();
    if (x == null) return null;
    return new Object[]{x, valselector.getBestVal((SetVar) x)};
  }

  public int getFirstBranch(Object x) {
    return 1;
  }

  public String getDecisionLogMsg(int i) {
    if (i == 1) return LOG_DECISION_MSG[0];
    else if (i == 2) return LOG_DECISION_MSG[1];
    else return "";
  }

}
