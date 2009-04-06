// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.mem;

/**
 * An interface for classes implementing trails of modifications to objects.
 * <p/>
 * Toutes les classes doivent implementer les fonctions de l'interface pour
 * permettre a l'environnement de deleguer la gestion des mondes pour chaque type
 * de donnee.
 */
public interface ITrailStorage {

  /**
   * Moving up to the next world.
   * <p/>
   * Cette methode doit garder l'etat de la variable avant la modification
   * de sorte a la remettre en etat le cas echeant.
   */

  public void worldPush();


  /**
   * Moving down to the previous world.
   * <p/>
   * Cette methode reattribute a la variable ou l'element d'un tableau sa valeur
   * precedente.
   */

  public void worldPop();


  /**
   * Comitting the current world: merging it with the previous one.
   * <p/>
   * Not used yet.
   */

  public void worldCommit();


  /**
   * Retrieving the size of the trail (number of saved past values).
   */

  public int getSize();

  /**
   * increase the capacity of the environment to a given number of worlds
   *
   * @param newWorldCapacity
   */
  public void resizeWorldCapacity(int newWorldCapacity);


}

