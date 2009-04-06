package i_want_to_use_this_old_version_of_choco.real;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

/**
 * An interface for real intervals.
 */
public interface RealInterval {
  /**
   * @return the lower bound.
   */
  public double getInf();

  /**
   * @return the upper bound.
   */
  public double getSup();

  /**
   * Modifies the bounds for intersecting with the specified interval.
   *
   * @param interval
   * @throws ContradictionException
   */
  public void intersect(RealInterval interval) throws ContradictionException;

  /**
   * Modifies the bounds for intersecting with the specified interval.
   *
   * @param interval
   * @param index    of the constraint responsible to this withdraw
   * @throws ContradictionException
   */
  public void intersect(RealInterval interval, int index) throws ContradictionException;

  Object pretty();
}
