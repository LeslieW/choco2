package parser.instances.checker;

public interface IOptimChecker extends IStatusChecker {
	
	boolean checkLowerBound(boolean doMaximize, int lbVal);

	boolean checkUpperBound(boolean doMaximize, int ubVal);

	boolean checkOptimum(int optVal);
	
	boolean checkLowerBound(boolean doMaximize, Number lbVal);

	boolean checkUpperBound(boolean doMaximize, Number ubVal);

	boolean checkOptimum(Number optVal);
	
}
