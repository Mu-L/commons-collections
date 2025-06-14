/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the PeekingIterator.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class PeekingIteratorTest<E> extends AbstractIteratorTest<E> {

    private final String[] testArray = { "a", "b", "c" };

    private List<E> testList;

    @Override
    public Iterator<E> makeEmptyIterator() {
        return PeekingIterator.peekingIterator(Collections.<E>emptyList().iterator());
    }

    @Override
    public PeekingIterator<E> makeObject() {
        return PeekingIterator.peekingIterator(testList.iterator());
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    protected void setUp() throws Exception {
        testList = new ArrayList<>(Arrays.asList((E[]) testArray));
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    @Test
    void testEmpty() {
        final Iterator<E> it = makeEmptyIterator();
        assertFalse(it.hasNext());
    }

    @Test
    void testIllegalRemove() {
        final PeekingIterator<E> it = makeObject();
        it.next();
        it.remove(); // supported

        assertTrue(it.hasNext());
        assertEquals("b", it.peek());

        assertThrows(IllegalStateException.class, () -> it.remove());
    }

    @Test
    void testIteratorExhausted() {
        final PeekingIterator<E> it = makeObject();
        it.next();
        it.next();
        it.next();
        assertFalse(it.hasNext());
        assertNull(it.peek());

        assertThrows(NoSuchElementException.class, () -> it.element());
    }

    @Test
    void testMultiplePeek() {
        final PeekingIterator<E> it = makeObject();
        assertEquals("a", it.peek());
        assertEquals("a", it.peek());
        assertEquals("a", it.next());
        assertTrue(it.hasNext());
        assertEquals("b", it.peek());
        assertEquals("b", it.peek());
        assertEquals("b", it.next());
        assertTrue(it.hasNext());
        assertEquals("c", it.peek());
        assertEquals("c", it.peek());
        assertEquals("c", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSinglePeek() {
        final PeekingIterator<E> it = makeObject();
        assertEquals("a", it.peek());
        assertEquals("a", it.element());
        validate(it, (E[]) testArray);
    }

    private void validate(final Iterator<E> iter, final E... items) {
        for (final E x : items) {
            assertTrue(iter.hasNext());
            assertEquals(x, iter.next());
        }
        assertFalse(iter.hasNext());
    }

}
