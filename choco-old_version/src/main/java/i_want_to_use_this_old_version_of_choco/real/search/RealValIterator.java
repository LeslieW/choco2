package i_want_to_use_this_old_version_of_choco.real.search;

import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 2 nov. 2004
 */
public interface RealValIterator extends ValIterator {
  /**
   * testing whether more branches can be considered after branch i, on the alternative associated to variable x
   *
   * @param x the variable under scrutiny
   * @param i the index of the last branch explored
   * @return true if more branches can be expanded after branch i
   */
  public boolean hasNextVal(RealVar x, int i);

  /**
   * Accessing the index of the first branch for variable x
   *
   * @param x the variable under scrutiny
   * @return the index of the first branch (such as the first value to be assigned to the variable)
   */
  public int getFirstVal(RealVar x);

  /**
   * generates the index of the next branch after branch i, on the alternative associated to variable x
   *
   * @param x the variable under scrutiny
   * @param i the index of the last branch explored
   * @return the index of the next branch to be expanded after branch i
   */
  public int getNextVal(RealVar x, int i);
}
