/**
 * 
 */
package choco.kernel.common.opres.graph;

import choco.kernel.common.IDotty;


/**
 * @author Arnaud Malapert</br> 
 * @since 9 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface INodeLabel extends IDotty {

	void updateInternalNode(IBinaryNode node);
	
	int getNbParameters();
	
	void setParameter(int idx, Object parameter);
	
	Object getParameter(int idx);
}