package choco.kernel.memory;

import choco.kernel.common.util.ModifiableInteger;
import choco.kernel.memory.trailing.trail.ITrailStorage;

import java.util.*;
import java.util.Map.Entry;


class TrailEventSource {

	private final IStateCollection2<?> collection;

	private final Object element;



	public TrailEventSource(IStateCollection2<?> collection, Object element) {
		super();
		this.collection = collection;
		this.element = element;
	}

	public final IStateCollection2<?> getCollection() {
		return collection;
	}

	public final Object getElement() {
		return element;
	}

}


class TrailEvent {

	private final IStateCollection2<?> collection;

	private final Map<Object,ModifiableInteger> collEvents;


	public TrailEvent(IStateCollection2<?> collection, Map<Object,ModifiableInteger> collEvents) {
		super();
		this.collection = collection;
		this.collEvents = collEvents;

	}


	public final IStateCollection2<?> getCollection() {
		return collection;
	}


	public final Map<Object, ModifiableInteger> getCollectionEvents() {
		return collEvents;
	}

}




class WorldStamp {

	protected final int worldIndex;

	protected final int size;

	public WorldStamp(int worldIndex, int size) {
		super();
		this.worldIndex = worldIndex;
		this.size = size;
	}

	public final int getWorldIndex() {
		return worldIndex;
	}

	public final int getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "("+getWorldIndex()+", "+getSize()+")";
	}

	

}

public class StoredCollection2Trail implements ITrailStorage {


	public final static int ADD = 1;

	public final static int REMOVE = -1;

	protected final IEnvironment env;

	protected final Stack<TrailEvent> eventStack;

	protected final Stack<WorldStamp> worldStampStack;

	protected Map<IStateCollection2<?>, Map<Object,ModifiableInteger>> pending;

	public StoredCollection2Trail(IEnvironment env, int maxUpdate, int maxWorld) {
		super();
		this.env = env;
		eventStack = new Stack<TrailEvent>();
		eventStack.ensureCapacity(maxUpdate);
		worldStampStack = new Stack<WorldStamp>();
		worldStampStack.ensureCapacity(maxWorld);
		worldStampStack.push(new WorldStamp(-1,0));
		pending = new HashMap<IStateCollection2<?>, Map<Object, ModifiableInteger>>();
	}

	public <E> IStateCollection2<E> makeStoredCollection() {
		return new CollectionWrapper<E>(this, new HashSet<E>(), new LinkedList<E>());
	}

	protected void popCollEvents(IStateCollection2<?> coll, Map<Object,ModifiableInteger> collEvents) {
		for (Entry<Object, ModifiableInteger> collEvent : collEvents.entrySet()) {
			final int val = collEvent.getValue().getValue();
			if( val > 0 ) {
				//backtrack on additions
				for (int i = 0; i < val; i++) {
					coll.fragileRemove(collEvent.getKey());
				}
			}else if(val < 0) {
				//backtrack on removals
				for (int i = 0; i < -val; i++) {
					coll.fragileAdd(collEvent.getKey());
				}
			}
		}
	}
	
	protected void popTrailEvents(TrailEvent event) {
		popCollEvents(event.getCollection(), event.getCollectionEvents());
	}

	protected void notifyEvent(IStateCollection2<?> collection, Object element, int value) {
		if(value != 0) {
			Map<Object, ModifiableInteger> cevts = pending.get(collection);
			if(cevts == null) {
				//Add a new listener for the given collection
				cevts = new HashMap<Object, ModifiableInteger>();
				cevts.put(element, new ModifiableInteger(value));
				pending.put(collection, cevts);
			}else {
				//check existing events
				ModifiableInteger evt = cevts.get(element);
				if(evt==null) {cevts.put(element, new ModifiableInteger(value));}
				else {
					evt.add(value);
					if(evt.getValue() == 0) {
						cevts.remove(element);
					}
				}
			}
		}
	}

	@Override
	public int getSize() {
		return eventStack.size();
	}

	@Override
	public void resizeWorldCapacity(int newWorldCapacity) {
		this.worldStampStack.ensureCapacity(newWorldCapacity);
	}

	@Override
	public void worldCommit() {
		throw new UnsupportedOperationException("not yet implemented");
		//TODO merge worldStamp ? commit => push ?
	}

	@Override
	public void worldPop() {
		//reverse pending events
		if( ! pending.isEmpty()) {
			for (Entry<IStateCollection2<?>, Map<Object,ModifiableInteger>> entry: pending.entrySet()) {
				popCollEvents(entry.getKey(), entry.getValue());
			}
			pending.clear();
		}
		//searching new stack size
		WorldStamp last = null;
		while(worldStampStack.peek().getWorldIndex()> this.env.getWorldIndex()-1) {
			last = worldStampStack.pop();
		}
		//update stack and stored objects
		if(last != null) {
			while(eventStack.size()>last.getSize()) {
				popTrailEvents(eventStack.pop());
			}
		}
	}

	@Override
	public void worldPush() {
		if(! pending.isEmpty()) {
			//insert world stamp
			worldStampStack.add(new WorldStamp(this.env.getWorldIndex(), eventStack.size()));
			//no event with value set to 0
			for (Entry<IStateCollection2<?>, Map<Object,ModifiableInteger>> entry: pending.entrySet()) {
				if( ! entry.getValue().isEmpty()) {
					eventStack.add(new TrailEvent(entry.getKey(),entry.getValue()));
				}
			}
			pending.clear();
			
		}

	}

}