package i_want_to_use_this_old_version_of_choco.global.scheduling;

import java.util.Arrays;


public class DisjunctiveSettings {


	protected final static int NO_SINGLE_RULE=-1;

	public final static int NOT_FIRST=0;
	public final static int NOT_LAST=1;
	public final static int DP_EST=2;
	public final static int DP_LCT=3;
	public final static int EF_EST=4;
	public final static int EF_LCT=5;

	public final static int LAST_RULE=5;



	private final boolean[] settings;

	private int theRule=-1;


	public DisjunctiveSettings() {
		super();
		settings=new boolean[5];
		setDefault();
	}


	public void setDefault() {
		this.setOnlyEdgeFinding();
		setNotFirstNotLast();
		setDetectablePrecedence();
	}


	public void clear() {
		Arrays.fill(settings, false);
	}

	/**
	 * @return the edgeFinding
	 */
	public final boolean edgeFinding() {
		return settings[3];
	}

	/**
	 * @return the notFirstNotLast
	 */
	public final boolean notFirstNotLast() {
		return settings[1];
	}

	/**
	 * @return the overloadChecking
	 */
	public final boolean overloadChecking() {
		return settings[0];
	}

	/**
	 * @return the detectablePrecedence
	 */
	public final boolean detectablePrecedence() {
		return settings[2];
	}


	public boolean singleRule() {
		return settings[4];
	}

	public final boolean useDPorNFNL() {
		return notFirstNotLast() || detectablePrecedence();
	}

	public final boolean useDPorNFNLorOLC() {
		return useDPorNFNL() || overloadChecking();
	}


	public final void noEdgeFinding() {
		this.settings[3]= false;
	}

	public final void noNotFirstNotLast() {
		this.settings[1]= false;
	}

	public final void noOverloadChecking() {
		this.settings[0]= false;
	}


	public final void noDetectablePrecedence() {
		this.settings[2]= false;
	}

	public final void noSingleRule() {
		this.settings[4]= false;
		this.theRule=NO_SINGLE_RULE;
	}

	/**
	 * Edge Finding subsume Overload Checking
	 */
	public final void setEdgeFinding() {
		this.settings[3]= true;
		noOverloadChecking();
		noSingleRule();
	}

	public final void setNotFirstNotLast() {
		this.settings[1]= true;
		noSingleRule();
	}

	/**
	 * desactivate edge finding.
	 */
	public final void setOverloadChecking() {
		this.settings[0]= true;
		noEdgeFinding();
		noSingleRule();
	}


	public final void setDetectablePrecedence() {
		this.settings[2]= true;
		noSingleRule();
	}

	private final void onlyOneTrue(final int index) {
		Arrays.fill(settings, false);
		settings[index]=true;
	}

	public final void setSingleRule(final int rule) {
		theRule=rule;
		onlyOneTrue(4);
	}

	public final int getSingleRule() {
		return theRule;
	}



	public final void setOnlyOverloadChecking() {
		onlyOneTrue(0);
	}

	public final void setOnlyNotFirstNotLast() {
		onlyOneTrue(1);
	}

	public final void setOnlyDetectablePrecedence() {
		onlyOneTrue(2);
	}

	public final void setOnlyEdgeFinding() {
		onlyOneTrue(3);
	}

	/**
	 * Gets the activated rules.
	 *
	 */
	public 	void writeDescription(final StringBuilder buffer) {
		if(singleRule()) {buffer.append("SR-").append(this.theRule);}
		else {
			if(notFirstNotLast()) {buffer.append("NF/NL-");}
			if(detectablePrecedence()) {buffer.append("DP-");}
			if(edgeFinding()) {buffer.append("EF-");}
			if(overloadChecking()) {buffer.append("OC-");}
			if(buffer.length()>0) {buffer.deleteCharAt(buffer.length()-1);}
		}
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder buffer=new StringBuilder();
		writeDescription(buffer);
		return buffer.toString();
	}



}
