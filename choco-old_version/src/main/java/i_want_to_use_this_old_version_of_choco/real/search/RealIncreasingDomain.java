package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 2 nov. 2004
 */
public class RealIncreasingDomain implements ValIterator {

  public boolean hasNextVal(Var x, int i) {
    return i < 2;
  }

  public int getFirstVal(Var x) {
    return 1;
  }

  public int getNextVal(Var x, int i) {
    return 2;
  }
}
