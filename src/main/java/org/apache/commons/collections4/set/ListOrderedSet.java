/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.list.UnmodifiableList;

/**
 * Decorates another {@code Set} to ensure that the order of addition is
 * retained and used by the iterator.
 * <p>
 * If an object is added to the set for a second time, it will remain in the
 * original position in the iteration. The order can be observed from the set
 * via the iterator or toArray methods.
 * </p>
 * <p>
 * The ListOrderedSet also has various useful direct methods. These include many
 * from {@code List}, such as {@code get(int)},
 * {@code remove(int)} and {@code indexOf(int)}. An unmodifiable
 * {@code List} view of the set can be obtained via {@code asList()}.
 * </p>
 * <p>
 * This class cannot implement the {@code List} interface directly as
 * various interface methods (notably equals/hashCode) are incompatible with a
 * set.
 * </p>
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * </p>
 *
 * @param <E> the type of the elements in this set
 * @since 3.0
 */
public class ListOrderedSet<E>
    extends AbstractSerializableSetDecorator<E> {

    /**
     * Internal iterator handle remove.
     */
    static class OrderedSetIterator<E>
        extends AbstractIteratorDecorator<E>
        implements OrderedIterator<E> {

        /** Object we iterate on */
        private final Collection<E> set;

        /** Last object retrieved */
        private E last;

        private OrderedSetIterator(final ListIterator<E> iterator, final Collection<E> set) {
            super(iterator);
            this.set = set;
        }

        @Override
        public boolean hasPrevious() {
            return ((ListIterator<E>) getIterator()).hasPrevious();
        }

        @Override
        public E next() {
            last = getIterator().next();
            return last;
        }

        @Override
        public E previous() {
            last = ((ListIterator<E>) getIterator()).previous();
            return last;
        }

        @Override
        public void remove() {
            set.remove(last);
            getIterator().remove();
            last = null;
        }
    }

    /** Serialization version */
    private static final long serialVersionUID = -228664372470420141L;

    /**
     * Factory method to create an ordered set using the supplied list to retain order.
     * <p>
     * A {@code HashSet} is used for the set behavior.
     * </p>
     * <p>
     * NOTE: If the list contains duplicates, the duplicates are removed,
     * altering the specified list.
     * </p>
     *
     * @param <E> the element type
     * @param list the list to decorate, must not be null
     * @return a new ordered set
     * @throws NullPointerException if list is null
     * @since 4.0
     */
    public static <E> ListOrderedSet<E> listOrderedSet(final List<E> list) {
        Objects.requireNonNull(list, "list");
        CollectionUtils.filter(list, UniquePredicate.uniquePredicate());
        final Set<E> set = new HashSet<>(list);

        return new ListOrderedSet<>(set, list);
    }

    /**
     * Factory method to create an ordered set.
     * <p>
     * An {@code ArrayList} is used to retain order.
     * </p>
     *
     * @param <E> the element type
     * @param set the set to decorate, must not be null
     * @return a new ordered set
     * @throws NullPointerException if set is null
     * @since 4.0
     */
    public static <E> ListOrderedSet<E> listOrderedSet(final Set<E> set) {
        return new ListOrderedSet<>(set);
    }

    /**
     * Factory method to create an ordered set specifying the list and set to use.
     * <p>
     * The list and set must both be empty.
     * </p>
     *
     * @param <E> the element type
     * @param set the set to decorate, must be empty and not null
     * @param list the list to decorate, must be empty and not null
     * @return a new ordered set
     * @throws NullPointerException if set or list is null
     * @throws IllegalArgumentException if either the set or list is not empty
     * @since 4.0
     */
    public static <E> ListOrderedSet<E> listOrderedSet(final Set<E> set, final List<E> list) {
        Objects.requireNonNull(set, "set");
        Objects.requireNonNull(list, "list");
        if (!set.isEmpty() || !list.isEmpty()) {
            throw new IllegalArgumentException("Set and List must be empty");
        }
        return new ListOrderedSet<>(set, list);
    }

    /** Internal list to hold the sequence of objects */
    private final List<E> setOrder;

    /**
     * Constructs a new empty {@code ListOrderedSet} using a
     * {@code HashSet} and an {@code ArrayList} internally.
     *
     * @since 3.1
     */
    public ListOrderedSet() {
        super(new HashSet<>());
        setOrder = new ArrayList<>();
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param set the set to decorate, must not be null
     * @throws NullPointerException if set is null
     */
    protected ListOrderedSet(final Set<E> set) {
        super(set);
        setOrder = new ArrayList<>(set);
    }

    /**
     * Constructor that wraps (not copies) the Set and specifies the list to
     * use.
     * <p>
     * The set and list must both be correctly initialized to the same elements.
     * </p>
     *
     * @param set the set to decorate, must not be null
     * @param list the list to decorate, must not be null
     * @throws NullPointerException if set or list is null
     */
    protected ListOrderedSet(final Set<E> set, final List<E> list) {
        super(set);
        setOrder = Objects.requireNonNull(list, "list");
    }

    @Override
    public boolean add(final E object) {
        if (decorated().add(object)) {
            setOrder.add(object);
            return true;
        }
        return false;
    }

    /**
     * Inserts the specified element at the specified position if it is not yet
     * contained in this ordered set (optional operation). Shifts the element
     * currently at this position and any subsequent elements to the right.
     *
     * @param index the index at which the element is to be inserted
     * @param object the element to be inserted
     * @see List#add(int, Object)
     */
    public void add(final int index, final E object) {
        if (!contains(object)) {
            decorated().add(object);
            setOrder.add(index, object);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        boolean result = false;
        for (final E e : coll) {
            result |= add(e);
        }
        return result;
    }

    /**
     * Inserts all elements in the specified collection not yet contained in the
     * ordered set at the specified position (optional operation). Shifts the
     * element currently at the position and all subsequent elements to the
     * right.
     *
     * @param index the position to insert the elements
     * @param coll the collection containing the elements to be inserted
     * @return {@code true} if this ordered set changed as a result of the call
     * @see List#addAll(int, Collection)
     */
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        boolean changed = false;
        // collect all elements to be added for performance reasons
        final List<E> toAdd = new ArrayList<>();
        for (final E e : coll) {
            if (contains(e)) {
                continue;
            }
            decorated().add(e);
            toAdd.add(e);
            changed = true;
        }

        if (changed) {
            setOrder.addAll(index, toAdd);
        }

        return changed;
    }

    /**
     * Gets an unmodifiable view of the order of the Set.
     *
     * @return an unmodifiable list view
     */
    public List<E> asList() {
        return UnmodifiableList.unmodifiableList(setOrder);
    }

    @Override
    public void clear() {
        decorated().clear();
        setOrder.clear();
    }

    /**
     * Gets the element at the specified position in this ordered set.
     *
     * @param index the position of the element in the ordered {@link Set}.
     * @return the element at position {@code index}
     * @see List#get(int)
     */
    public E get(final int index) {
        return setOrder.get(index);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * ordered set.
     *
     * @param object the element to search for
     * @return the index of the first occurrence of the object, or {@code -1} if
     *         this ordered set does not contain this object
     * @see List#indexOf(Object)
     */
    public int indexOf(final Object object) {
        return setOrder.indexOf(object);
    }

    @Override
    public OrderedIterator<E> iterator() {
        return new OrderedSetIterator<>(setOrder.listIterator(), decorated());
    }

    /**
     * Removes the element at the specified position from the ordered set.
     * Shifts any subsequent elements to the left.
     *
     * @param index the index of the element to be removed
     * @return the element that has been remove from the ordered set
     * @see List#remove(int)
     */
    public E remove(final int index) {
        final E obj = setOrder.remove(index);
        remove(obj);
        return obj;
    }

    @Override
    public boolean remove(final Object object) {
        final boolean result = decorated().remove(object);
        if (result) {
            setOrder.remove(object);
        }
        return result;
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean result = false;
        for (final Object name : coll) {
            result |= remove(name);
        }
        return result;
    }

    /**
     * @since 4.4
     */
    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        if (Objects.isNull(filter)) {
            return false;
        }
        final boolean result = decorated().removeIf(filter);
        if (result) {
            setOrder.removeIf(filter);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation iterates over the elements of this set, checking
     * each element in turn to see if it's contained in {@code coll}.
     * If it's not contained, it's removed from this set. As a consequence,
     * it is advised to use a collection type for {@code coll} that provides
     * a fast (for example O(1)) implementation of {@link Collection#contains(Object)}.
     * </p>
     */
    @Override
    public boolean retainAll(final Collection<?> coll) {
        final boolean result = decorated().retainAll(coll);
        if (!result) {
            return false;
        }
        if (decorated().isEmpty()) {
            setOrder.clear();
        } else {
            setOrder.removeIf(e -> !decorated().contains(e));
        }
        return result;
    }

    @Override
    public Object[] toArray() {
        return setOrder.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return setOrder.toArray(a);
    }

    /**
     * Uses the underlying List's toString so that order is achieved. This means
     * that the decorated Set's toString is not used, so any custom toStrings
     * will be ignored.
     *
     * @return a string representation of the ordered set
     */
    // Fortunately List.toString and Set.toString look the same
    @Override
    public String toString() {
        return setOrder.toString();
    }

}
