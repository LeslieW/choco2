package choco.kernel.memory;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;





public interface IStateCollection2<E> extends Collection<E>, IStateObject {

	/**
     * backtrackable  backtrackable addition of an element. 
     */
    boolean storedAdd(E element);

    /**
     * backtrackable removal of an element.  
     */
    boolean storedRemove(Object o);
    
    /**
     * static removal of an element.
     */
    boolean staticAdd(E element);

    /**
     * static addition of an element.
     */
    boolean staticRemove(Object o);
    
    /**
     * fragile addition of an element (not user method ITrailStorage only).
     */
    boolean fragileAdd(Object element);

    /**
     * fragile addition of an element (not user method ITrailStorage only).
     */
    boolean fragileRemove(Object o);
}

class CollectionWrapper<E> extends AbstractCollection<E> implements IStateCollection2<E> {

	protected final StoredCollection2Trail trail;
	
	protected final Collection<E> staticColl;
	
	protected final Collection<E> dynColl;
	
	public CollectionWrapper(StoredCollection2Trail trail,
			Collection<E> staticColl, Collection<E> dynColl) {
		super();
		this.trail = trail;
		this.staticColl = staticColl;
		this.dynColl = dynColl;
	}

	
	public final Collection<E> getSubCollection(boolean staticOrDynamic) {
		return Collections.unmodifiableCollection( staticOrDynamic ? staticColl : dynColl);
	}
	
	
	@Override
	public boolean storedAdd(E e) {
		if(dynColl.add(e)) {
			trail.notifyEvent(this, e, StoredCollection2Trail.ADD);
			return true;
		}else {return false;}
	}

	@Override
	public boolean storedRemove(Object o) {
		if(dynColl.remove(o)) {
			trail.notifyEvent(this,o, StoredCollection2Trail.REMOVE);
			return true;
		}else {return false;}
	}

	/**
	 * backtrackable add
	 */
	@Override
	public boolean add(E e) {
		return storedAdd(e);
	}

	@Override
	public void clear() {
		staticColl.clear();
		Iterator<E> iter = dynColl.iterator();
		while(iter.hasNext()) {
			trail.notifyEvent(this,iter.next(), StoredCollection2Trail.REMOVE);
			iter.remove();
		}
	}

	@Override
	public boolean contains(Object o) {
		return staticColl.contains(o) || dynColl.contains(o);
	}

	
	/**
	 * if it did not succeed removing from dynamic, remove from static
	 */
	@Override
	public boolean remove(Object o) {
		return storedRemove(o) || staticRemove(o);
	}

	@Override
	public int size() {
		return staticColl.size() + dynColl.size();
	}

	@Override
	public boolean staticAdd(E element) {
		return staticColl.add(element);
	}

	@Override
	public boolean staticRemove(Object o) {
		return staticColl.remove(o);
	}
	
	/**
	 * You should absolutly call hasNext() before next() with the iterator.
	 */
	@Override
	public Iterator<E> iterator() {
		return new CombinedIterator();
	}
	
	class CombinedIterator  implements Iterator<E> {

		private boolean first;
		
		private Iterator<E> inner;
		
		private CombinedIterator() {
			super();
			first=true;
			inner = staticColl.iterator();
		}

		@Override
		public boolean hasNext() {
			if(inner.hasNext()) {
				return true;
			}else if(first) {
				first=false;
				inner = dynColl.iterator();
				return inner.hasNext();
			}else {return false;}
		}

		@Override
		public E next() {
			return inner.next();
			
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("cant remove");
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean fragileAdd(Object element) {
		return dynColl.add((E) element);
	}


	@Override
	public boolean fragileRemove(Object element) {
		return dynColl.remove(element);
	}
	
	
	
}

