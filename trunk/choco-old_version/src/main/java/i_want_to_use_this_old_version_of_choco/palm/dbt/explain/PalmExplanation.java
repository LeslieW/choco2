//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.explain;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain.IBoundExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.explain.IRemovalExplanation;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;
import i_want_to_use_this_old_version_of_choco.palm.real.explain.RealBoundExplanation;

import java.util.BitSet;

/**
 * PalmExplanation interface.
 */

public interface PalmExplanation extends Explanation {


  /**
   * Makes an IncInfExplanation from the current explain by adding dependencies.
   *
   * @param inf The previous value of the bound.
   * @param var The involved variable.
   */

  public IBoundExplanation makeIncInfExplanation(int inf, PalmIntVar var);


  /**
   * Makes a DecSupExplanation from the current explain by adding dependencies.
   *
   * @param sup The previous value of the bound.
   * @param var The involved variable.
   */

  public IBoundExplanation makeDecSupExplanation(int sup, PalmIntVar var);


  /**
   * Makes a RemovalExplanation from the current explain by adding dependencies.
   *
   * @param value The removed value of the domain.
   * @param var   The involved variable.
   */

  public IRemovalExplanation makeRemovalExplanation(int value, PalmIntVar var);


  /**
   * Makes an IncInfExplanation from the current explain by adding dependencies.
   *
   * @param inf The previous value of the bound.
   * @param var The involved variable.
   */

  public RealBoundExplanation makeIncInfExplanation(double inf, PalmRealVar var);


  /**
   * Makes a DecSupExplanation from the current explain by adding dependencies.
   *
   * @param sup The previous value of the bound.
   * @param var The involved variable.
   */

  public RealBoundExplanation makeDecSupExplanation(double sup, PalmRealVar var);


  /**
   * Posts a restoration prop.
   *
   * @param constraint
   */

  public void postUndoRemoval(Constraint constraint);


  /**
   * Copies the explain set and returns the new bitset.
   *
   * @return The explain as a BitSet.
   */

  public BitSet getBitSet();


  /**
   * Checks if the explain is valid, that is wether all the constraint are active.
   */

  public boolean isValid();


  /**
   * Checks if the explain is valid, that is wether all the constraint are active. Moreover,
   * it checks that all constraints were poseted before the specified date.
   */

  public boolean isValid(int date);

}
