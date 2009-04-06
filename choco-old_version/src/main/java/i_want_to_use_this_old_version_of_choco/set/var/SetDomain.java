package i_want_to_use_this_old_version_of_choco.set.var;

import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 15:11:54
 * To change this template use File | Settings | File Templates.
 */
public interface SetDomain {

  public BitSetEnumeratedDomain getKernelDomain();

  public BitSetEnumeratedDomain getEnveloppeDomain();

  public IntIterator getKernelIterator();

  public IntIterator getEnveloppeIterator();

  public IntIterator getOpenDomainIterator();
}
