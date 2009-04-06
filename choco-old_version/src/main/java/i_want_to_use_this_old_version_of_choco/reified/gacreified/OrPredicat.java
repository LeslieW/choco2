package i_want_to_use_this_old_version_of_choco.reified.gacreified;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 8 avr. 2008
 * Time: 14:06:24
 * To change this template use File | Settings | File Templates.
 */
public class OrPredicat extends InternalNode {

	public OrPredicat(Predicat[] subt) {
		super(subt);
	}

	/**
	 * Check if the tuple is valid among one of the branches of the
	 * tree representing the predicat
	 * @param tuple
	 * @return
	 */
	public boolean checkTuple(int[] tuple) {
		for (int i = 0; i < subtrees.length; i++) {
			if (subtrees[i].checkTuple(tuple)) return true;
		}
		return false;
	}

	public String pretty() {
		String s = "(";
		for (int i = 0; i < subtrees.length - 1; i++) {
			s += subtrees[i].pretty() + " OR ";
		}
		s += subtrees[subtrees.length - 1].pretty() + ")";
		return s;
	}

	public Predicat getOpposite() {
		Predicat[] negpreds = new Predicat[subtrees.length];
		for (int i = 0; i < negpreds.length; i++) {
			negpreds[i] = subtrees[i].getOpposite();
		}
		return new AndPredicat(negpreds);
	}
}
