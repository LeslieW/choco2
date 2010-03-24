package choco.kernel.common.util.tools;

import java.io.Serializable;
import java.util.*;

/**
 * At the contrary of collections in the class {@link Collections}, these collections do not throw any access exception
 * @author Arnaud Malapert</br> 
 * @since 10 févr. 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public final class CollectionUtils {

	
    private CollectionUtils() {
		super();
    }
    
    /**
     * The empty set (immutable).  This set is serializable.
     *
     * @see #emptySet()
     */
    public static final Set EMPTY_SET = new EmptySet();

    /**
     * Returns the empty set (immutable).  This set is serializable.
     * Unlike the like-named field, this method is parameterized.
     *
     * <p>This example illustrates the type-safe way to obtain an empty set:
     * <pre>
     *     Set&lt;String&gt; s = Collections.emptySet();
     * </pre>
     * Implementation note:  Implementations of this method need not
     * create a separate <tt>Set</tt> object for each call.   Using this
     * method is likely to have comparable cost to using the like-named
     * field.  (Unlike this method, the field does not provide type safety.)
     *
     * @see #EMPTY_SET
     * @since 1.5
     */
    public static final <T> Set<T> emptySet() {
	return (Set<T>) EMPTY_SET;
    }

    /**
     * @serial include
     */
    private static class EmptySet extends AbstractSet<Object> implements Serializable {
	// use serialVersionUID from JDK 1.2.2 for interoperability
	private static final long serialVersionUID = 1582296315990362920L;

        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                public boolean hasNext() {
                    return false;
                }
                public Object next() {
                    throw new NoSuchElementException();
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public int size() {return 0;}

        public boolean contains(Object obj) {return false;}
        
        

        @Override
		public boolean add(Object e) {
        	return false;
		}

		@Override
		public boolean remove(Object o) {
			return false;
		}

		// Preserves singleton property
        private Object readResolve() {
            return EMPTY_SET;
        }
    }


	/**
     * The empty list (immutable but does not throw any exception).  This list is serializable.
     *
     * @see #emptyList()
     */
    public static final List EMPTY_LIST = new EmptyList();

    /**
     * Returns the empty list (immutable).  This list is serializable.
     *
     * <p>This example illustrates the type-safe way to obtain an empty list:
     * <pre>
     *     List&lt;String&gt; s = Collections.emptyList();
     * </pre>
     * Implementation note:  Implementations of this method need not
     * create a separate <tt>List</tt> object for each call.   Using this
     * method is likely to have comparable cost to using the like-named
     * field.  (Unlike this method, the field does not provide type safety.)
     *
     * @see #EMPTY_LIST
     * @since 1.5
     */
    public static <T> List<T> emptyList() {
	return (List<T>) EMPTY_LIST;
    }

    /**
     * @serial include
     */
    private static class EmptyList
	extends AbstractList<Object>
	implements RandomAccess, Serializable {
	// use serialVersionUID from JDK 1.2.2 for interoperability
	private static final long serialVersionUID = 8842843931221139166L;

        public int size() {return 0;}

        public boolean contains(Object obj) {return false;}

        public Object get(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
        
               

        @Override
		public void add(int index, Object element) {
		}

		@Override
		public Object remove(int index) {
			return null;
		}

		@Override
		public Object set(int index, Object element) {
			return null;
		}

		// Preserves singleton property
        private Object readResolve() {
            return EMPTY_LIST;
        }
    }

}
