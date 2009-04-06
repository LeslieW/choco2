package i_want_to_use_this_old_version_of_choco.reified.gacreified;

import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 8 avr. 2008
 * Time: 14:06:49
 * To change this template use File | Settings | File Templates.
 */
public class OrLeaves extends Leaves {


	public OrLeaves(Propagator p) {
		super(p);
	}

	public OrLeaves(Propagator p1, Propagator p2) {
		super(p1, p2);
	}

	public OrLeaves(Propagator[] ps) {
		super(ps);
	}

	public boolean checkTuple(int[] tuple) {
		for (int i = 0; i < leaves.length; i++) {
			if (((IntConstraint) leaves[i]).isSatisfied(getTupleForLeave(i, tuple)))
				return true;
		}
		return false;
	}

	public String pretty() {
		String s = "(";
		for (int i = 0; i < leaves.length - 1; i++) {
			s += leaves[i].pretty() + " OR ";
		}
		s += leaves[leaves.length - 1].pretty() + ")";
		return s;
	}

	public Predicat getOpposite() {
		Propagator[] negpreds = new Propagator[leaves.length];
		for (int i = 0; i < negpreds.length; i++) {
			negpreds[i] = (Propagator) pb.not(leaves[i]);
		}
		return new AndLeaves(negpreds);
	}
}
