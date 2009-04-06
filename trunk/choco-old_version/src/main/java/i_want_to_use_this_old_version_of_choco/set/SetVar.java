package i_want_to_use_this_old_version_of_choco.set;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.set.var.SetDomain;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 6 juin 2004
 * Time: 09:51:52
 * To change this template use File | Settings | File Templates.
 */
public interface SetVar extends Var {

	/**
	 * @return the IntDomainVar representing the cardinality
	 * of this set
	 */
	public IntDomainVar getCard();

	/**
	 * <b>Public user API:</b>
	 * setting a value to the kernel of a set variable
	 *
	 * @param x the value that is set to the variable
	 */

	public void setValIn(int x) throws ContradictionException;


	/**
	 * <b>Public user API:</b>
	 * removing a value from the Enveloppe of a set variable.
	 *
	 * @param x the removed value
	 */

	public void setValOut(int x) throws ContradictionException;


	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> returns the object responsible for storing the enumeration of values in the domain
	 */

	public SetDomain getDomain();

	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> testing whether a value is in the kernel domain
	 *
	 * @param x the tested value
	 */
	public boolean isInDomainKernel(int x);

	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> testing whether a value is in the enveloppe domain.
	 *
	 * @param x the tested value
	 */

	public boolean isInDomainEnveloppe(int x);


	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> testing whether two variables have intersecting domains.
	 *
	 * @param x the other variable
	 */

	public boolean canBeEqualTo(SetVar x);


	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> retrieves the number of values in the kernel domain.
	 */

	public int getKernelDomainSize();

	/**
	 * <b>Public user API:</b>
	 * <i>Domains :</i> retrieves the number of values in the enveloppe domain.
	 */

	public int getEnveloppeDomainSize();


	/**
	 * Returns the lower bound of the enveloppe variable domain.
	 * (i.e the smallest value contained in the enveloppe)
	 *
	 * @return the enveloppe domain lower bound
	 */

	public int getEnveloppeInf();

	public int getEnveloppeSup();

	public int getKernelInf();

	public int getKernelSup();


	/**
	 * Returns the value of the variable if instantiated.
	 *
	 * @return the value of the variable
	 */

	public int[] getValue();

	/**
	 * set the value of the variable to the set val.
	 *
	 * @param val the value to be set
	 */
	public void setVal(int[] val) throws ContradictionException;

	/**
	 * <i>Propagation events</i> updating the kernel of a variable
	 * (i.e adding a value)
	 *
	 * @param x   a value of the enveloppe domain to be added to the kernel
	 * @param idx the index of the constraint that generated the var
	 * @return a boolean indicating whether this method call added new information or not
	 */

	public boolean addToKernel(int x, int idx) throws ContradictionException;


	/**
	 * <i>Propagation events</i> updating the enveloppe of a variable
	 * (i.e removing a value)
	 *
	 * @param x   a value of the enveloppe domain to be removed
	 * @param idx the index of the constraint that generated the var
	 * @return a boolean indicating whether this method call added new information or not
	 */

	public boolean remFromEnveloppe(int x, int idx) throws ContradictionException;

	/**
	 * <i>Propagation events</i> instantiated a set var to a specific set of values
	 *
	 * @param x   a set of values describing the final instantiated kernel
	 * @param idx the index of the constraint that generated the var
	 * @return a boolean indicating whether this method call added new information or not
	 */
	public boolean instantiate(int[] x, int idx) throws ContradictionException;

}
