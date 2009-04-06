package i_want_to_use_this_old_version_of_choco.set;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.set.var.SetVarEventListener;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 09:52:09
 * To change this template use File | Settings | File Templates.
 */
public interface SetConstraint extends Constraint, Propagator, SetVarEventListener {

  public SetVar getSetVar(int i);

  public void awakeOnkerAdditions(int varIdx, IntIterator deltaDomain) throws ContradictionException;

  public void awakeOnEnvRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException;

}
