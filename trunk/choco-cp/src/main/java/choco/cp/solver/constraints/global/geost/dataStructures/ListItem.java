package choco.cp.solver.constraints.global.geost.dataStructures;

final class ListItem
{
	Object obj;
	ListItem previous, next;

	public ListItem(Object obj)
	{
		this(null, obj, null);
	}

	public ListItem(ListItem previous, Object obj, ListItem next)
	{
		this.previous = previous;
		this.obj = obj;
		this.next = next;
	}
}