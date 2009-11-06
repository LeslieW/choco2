package choco.kernel.model.constraints.geost.externalConstraints;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 10 aožt 2009
 * Time: 09:40:35
 * To change this template use File | Settings | File Templates.
 */
public class NonOverlappingModel extends IExternalConstraint {

    public NonOverlappingModel(int ectrID, int[] dimensions, int[] objectIdentifiers)
	{
		super(ectrID, dimensions, objectIdentifiers);
	}
    
}
