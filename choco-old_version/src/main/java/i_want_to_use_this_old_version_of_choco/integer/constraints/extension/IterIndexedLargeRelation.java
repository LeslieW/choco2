package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

/**
 * A large relation that provides the seekNextSupport function from
 * a given support of given pair var/val and indexes the tuples
 * by integers to store (eventually) the support as StoredInt
 */
public interface IterIndexedLargeRelation {

	/**
	 * seek from the next support available from the index of the
	 * old support and the pair variable/value given in argument
	 * @param oldIdxSupport
	 * @param var
	 * @param val
	 * @return
	 */
	public int seekNextTuple(int oldIdxSupport, int var, int val);


	/**
	 * return the tuple corresponding to the given index
	 *
	 * @param support
	 * @return
	 */
	public int[] getTuple(int support);


	/**
	 * returns the number of supports for the pair (var,val)
	 * @param var
	 * @param val
	 * @return
	 */
	public int getNbSupport(int var,int val);
}
