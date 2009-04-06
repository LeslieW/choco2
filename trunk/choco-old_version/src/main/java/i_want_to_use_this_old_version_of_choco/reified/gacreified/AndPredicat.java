package i_want_to_use_this_old_version_of_choco.reified.gacreified;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 8 avr. 2008
 * Time: 14:07:06
 * To change this template use File | Settings | File Templates.
 */
public class AndPredicat extends InternalNode {

	public AndPredicat(Predicat[] subt) {
		super(subt);
	}

	/**
	 * Check if the tuple is valid for both branches of the tree
	 * representing the predicat
	 * @param tuple
	 * @return
	 */
	public boolean checkTuple(int[] tuple) {
		for (int i = 0; i < subtrees.length; i++) {
			if (!subtrees[i].checkTuple(tuple))
				return false;
		}
		return true;
	}

	public String pretty() {
		String s = "(";
		for (int i = 0; i < subtrees.length - 1; i++) {
			s += subtrees[i].pretty() + " AND ";
		}
		s += subtrees[subtrees.length - 1].pretty() + ")";
		return s;
	}

	public Predicat getOpposite() {
		Predicat[] negpreds = new Predicat[subtrees.length];
		for (int i = 0; i < negpreds.length; i++) {
			negpreds[i] = subtrees[i].getOpposite();
		}
		return new OrPredicat(negpreds);
	}
}
