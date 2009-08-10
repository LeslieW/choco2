package choco.cp.solver.constraints.global.geost.dataStructures;

final class ListIterator
{
	LinkedList owner;
	ListItem pos;

	ListIterator(LinkedList owner, ListItem pos){
		this.owner = owner;
		this.pos = pos;
	}

	/*
	 * check whether object owns the iterator
	 */
	public boolean belongsTo(Object owner)
	{
		return this.owner == owner;
	}

	/*
	 * move to head position
	 */
	public void head()
	{
		pos = owner.head;
	}

	/*
	 * move to next position
	 */
	public void next()
	{
		pos = pos.next;
	}

	/*
	 * move to previous position
	 */
	public void previous()
	{
		pos = pos.previous;
	}
}