package parser.instances.xcsp;

import parser.instances.BasicSettings;

public class XcspSettings extends BasicSettings {
	
	private static final long serialVersionUID = -6142621495638857916L;
	
	//heuristics
	public static final int DOMOVERDEG = 0;
	public static final int DOMOVERWDEG = 1;
	public static final int IMPACT = 2;
	public static final int VERSATILE = 3;
	public static final int SIMPLE = 4;
	
	private int heuristic = DOMOVERDEG;
	
	
	/** perform singleton consistency step or not */
	public boolean singletonConsistency = false;

	/** external check of the solution */
	public boolean externalCheck = false;
	
	public XcspSettings() {
		super();
	}
	
	public final int getHeuristic() {
		return heuristic;
	}


	public final void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}



	public final boolean doSingletonConsistency() {
		return singletonConsistency;
	}

	public final void setDoSingletonConsistency(boolean singletonConsistency) {
		this.singletonConsistency = singletonConsistency;
	}


	public final boolean doExternalCheck() {
		return externalCheck;
	}


	//FIXME 
//	@Override
//	public void configure() {
//		super.configure();
//		heuristic = readInt(TOOLS_PREFIX+"xcsp.heuristic", heuristic);
//		ObjectFactory.algorithmAC = readInt(TOOLS_PREFIX+"xcsp.ac", ObjectFactory.algorithmAC);
//		singletonConsistency = readBoolean(TOOLS_PREFIX+"xcsp.singloton", singletonConsistency);
//		externalCheck = readBoolean(TOOLS_PREFIX+"xcsp.externalcheck", externalCheck);
//	}

	
}
