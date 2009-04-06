package i_want_to_use_this_old_version_of_choco.real;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

import java.util.List;
import java.util.Set;

/**
 * An interface for real expressions.
 */
public interface RealExp extends RealInterval {
  /**
   * Computes the narrowest bounds with respect to sub terms.
   */
  public void tighten();

  /**
   * Projects computed bounds to the sub expressions.
   *
   * @throws ContradictionException
   */
  public void project() throws ContradictionException;

  /**
   * Computes recursively the sub expressions (avoids to tighten and project recursively).
   *
   * @return the flattened list of subexpressions
   */
  public List subExps(List l);

  /**
   * Collects recursively all the variable this expression depends on.
   *
   * @return the collected set
   */
  public Set collectVars(Set s);

  /**
   * Isolates sub terms depending or not on a variable x.
   *
   * @param var
   * @param wx
   * @param wox
   * @return TODO
   */
  public boolean isolate(RealVar var, List wx, List wox);

  String pretty();
}
