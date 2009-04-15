package choco.kernel.common.logging;



public enum Verbosity {

	OFF(Integer.MIN_VALUE), SILENT(0), VERBOSE(100), SOLUTION(200), SEARCH(300) , DEBUG(400), FINEST(Integer.MAX_VALUE);
	
	private final int levelValue;
	
	
	private Verbosity(int levelValue) {
		this.levelValue = levelValue;
	}


	public int intValue() {
		return levelValue;
	}
}
