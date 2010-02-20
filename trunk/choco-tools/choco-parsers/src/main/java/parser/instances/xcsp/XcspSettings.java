package parser.instances.xcsp;

import static choco.kernel.common.util.tools.PropertyUtils.TOOLS_PREFIX;
import static choco.kernel.common.util.tools.PropertyUtils.readBoolean;
import static choco.kernel.common.util.tools.PropertyUtils.readInteger;

import java.util.Properties;

import parser.chocogen.ObjectFactory;
import parser.instances.RestartSettings;

public class XcspSettings extends RestartSettings {
	
	
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

	
	public XcspSettings(XcspSettings set) {
		super(set);
		this.heuristic = set.heuristic;
		this.singletonConsistency = set.singletonConsistency;
		this.externalCheck = set.externalCheck;
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


	public final void setExternalCheck(boolean externalCheck) {
		this.externalCheck = externalCheck;
	}

	@Override
	public void configure(Properties properties) {
		super.configure(properties);
		heuristic = readInteger(properties, TOOLS_PREFIX+"xcsp.heuristic", heuristic);
		ObjectFactory.algorithmAC = readInteger(properties, TOOLS_PREFIX+"xcsp.ac", ObjectFactory.algorithmAC);
		singletonConsistency = readBoolean(properties, TOOLS_PREFIX+"xcsp.singloton", singletonConsistency);
		externalCheck = readBoolean(properties, TOOLS_PREFIX+"xcsp.externalcheck", externalCheck);
	}

	
}
