/*
 * DisposableIntIterator.java
 *
 * Created on 31 janvier 2007, 17:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package i_want_to_use_this_old_version_of_choco.util;

/**
 *
 * @author grochart
 */
public interface DisposableIntIterator extends IntIterator {
    
  /**
   * This method allows to declare that the iterator is not usefull anymoure. It 
   * can be reused by another object.
   */
  void dispose();
    
}
