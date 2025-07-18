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
package org.apache.commons.collections4.bidimap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.iterators.AbstractMapIteratorTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link OrderedBidiMap}.
 */
public abstract class AbstractOrderedBidiMapTest<K, V> extends AbstractBidiMapTest<K, V> {

    public class BidiOrderedMapIteratorTest extends AbstractMapIteratorTest<K, V> {

        @Override
        public V[] addSetValues() {
            return getNewSampleValues();
        }

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return AbstractOrderedBidiMapTest.this.confirmed;
        }

        @Override
        public Map<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractOrderedBidiMapTest.this.map;
        }

        @Override
        public MapIterator<K, V> makeEmptyIterator() {
            resetEmpty();
            return AbstractOrderedBidiMapTest.this.getMap().mapIterator();
        }

        @Override
        public MapIterator<K, V> makeObject() {
            resetFull();
            return AbstractOrderedBidiMapTest.this.getMap().mapIterator();
        }

        @Override
        public boolean supportsRemove() {
            return isRemoveSupported();
        }

        @Override
        public boolean supportsSetValue() {
            return isSetValueSupported();
        }

        @Override
        public void verify() {
            super.verify();
            AbstractOrderedBidiMapTest.this.verify();
        }

    }

    public BulkTest bulkTestOrderedMapIterator() {
        return new BidiOrderedMapIteratorTest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedBidiMap<K, V> getMap() {
        return (OrderedBidiMap<K, V>) super.getMap();
    }

    @Test
    void testFirstKey() {
        resetEmpty();
        OrderedBidiMap<K, V> bidi = getMap();

        final OrderedBidiMap<K, V> finalBidi = bidi;
        assertThrows(NoSuchElementException.class, () -> finalBidi.firstKey());

        resetFull();
        bidi = getMap();
        final K confirmedFirst = confirmed.keySet().iterator().next();
        assertEquals(confirmedFirst, bidi.firstKey());
    }

    @Test
    void testLastKey() {
        resetEmpty();
        OrderedBidiMap<K, V> bidi = getMap();

        final OrderedBidiMap<K, V> finalBidi = bidi;
        assertThrows(NoSuchElementException.class, () -> finalBidi.lastKey());

        resetFull();
        bidi = getMap();
        K confirmedLast = null;
        for (final Iterator<K> it = confirmed.keySet().iterator(); it.hasNext();) {
            confirmedLast = it.next();
        }
        assertEquals(confirmedLast, bidi.lastKey());
    }

    @Test
    void testNextKey() {
        resetEmpty();
        OrderedBidiMap<K, V> bidi = (OrderedBidiMap<K, V>) map;
        assertNull(bidi.nextKey(getOtherKeys()[0]));
        if (!isAllowNullKey()) {
            try {
                assertNull(bidi.nextKey(null)); // this is allowed too
            } catch (final NullPointerException ignore) {
                // ignore
            }
        } else {
            assertNull(bidi.nextKey(null));
        }

        resetFull();
        bidi = (OrderedBidiMap<K, V>) map;
        final Iterator<K> it = confirmed.keySet().iterator();
        K confirmedLast = it.next();
        while (it.hasNext()) {
            final K confirmedObject = it.next();
            assertEquals(confirmedObject, bidi.nextKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertNull(bidi.nextKey(confirmedLast));

        if (!isAllowNullKey()) {
            final OrderedBidiMap<K, V> finalBidi = bidi;
            assertThrows(NullPointerException.class, () -> finalBidi.nextKey(null));

        } else {
            assertNull(bidi.nextKey(null));
        }
    }

    @Test
    void testPreviousKey() {
        resetEmpty();
        OrderedBidiMap<K, V> bidi = getMap();
        assertNull(bidi.previousKey(getOtherKeys()[0]));
        if (!isAllowNullKey()) {
            try {
                assertNull(bidi.previousKey(null)); // this is allowed too
            } catch (final NullPointerException ignore) {
                // ignore
            }
        } else {
            assertNull(bidi.previousKey(null));
        }

        resetFull();
        bidi = getMap();
        final List<K> list = new ArrayList<>(confirmed.keySet());
        Collections.reverse(list);
        final Iterator<K> it = list.iterator();
        K confirmedLast = it.next();
        while (it.hasNext()) {
            final K confirmedObject = it.next();
            assertEquals(confirmedObject, bidi.previousKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertNull(bidi.previousKey(confirmedLast));

        if (!isAllowNullKey()) {
            final OrderedBidiMap<K, V> finalBidi = bidi;
            assertThrows(NullPointerException.class, () -> finalBidi.previousKey(null));

        } else {
            assertNull(bidi.previousKey(null));
        }
    }

}
