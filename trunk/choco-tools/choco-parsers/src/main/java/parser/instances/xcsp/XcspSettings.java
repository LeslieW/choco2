package parser.instances.xcsp;

import parser.instances.BasicSettings;

public class XcspSettings extends BasicSettings {
	
	private static final long serialVersionUID = -6142621495638857916L;

    public enum Heuristic {
        DOMOVERDEG, DOMOVERWDEG, IMPACT, VERSATILE, SIMPLE
    }

    /**
	 * <br/><b>Goal</b>: Set the heuristic for CSP competition
	 * <br/><b>Type</b>: Heuristic
	 * <br/><b>Default value</b>: DOMOVERDEG
	 */
	@Default(value = "DOMOVERDEG")
	public static final String HEURISTIC = "xcsp.heuristic";
	
	/**
	 * <br/><b>Goal</b>: perform singleton consistency step or not
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String SINGLETON_CONSISTENCY= "xcsp.singletonConsistency";

    /**
	 * <br/><b>Goal</b>: external check of the solution
	 * <br/><b>Type</b>: boolean
	 * <br/><b>Default value</b>: false
	 */
	@Default(value = VALUE_FALSE)
	public static final String EXTERNAL_CHECK= "xcsp.externalCheck";

	public XcspSettings() {
		super();
	}
    
    public static Heuristic match(int heuristic){
        return Heuristic.values()[heuristic];
    }
	
}
