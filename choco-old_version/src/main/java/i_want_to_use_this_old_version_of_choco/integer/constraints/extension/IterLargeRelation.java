package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 30 janv. 2007
 * Time: 11:45:44
 * To change this template use File | Settings | File Templates.
 */
public interface IterLargeRelation extends LargeRelation {

  public int[] seekAllowedSupport(int[] tuple, int indexVar, int value);

}
