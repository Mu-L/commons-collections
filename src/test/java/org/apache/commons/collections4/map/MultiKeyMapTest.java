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
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class MultiKeyMapTest<K, V> extends AbstractIterableMapTest<MultiKey<? extends K>, V> {

    static final Integer I1 = Integer.valueOf(1);
    static final Integer I2 = Integer.valueOf(2);
    static final Integer I3 = Integer.valueOf(3);
    static final Integer I4 = Integer.valueOf(4);
    static final Integer I5 = Integer.valueOf(5);
    static final Integer I6 = Integer.valueOf(6);
    static final Integer I7 = Integer.valueOf(7);
    static final Integer I8 = Integer.valueOf(8);

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiKeyMap<K, V> getMap() {
        return (MultiKeyMap<K, V>) super.getMap();
    }

    @SuppressWarnings("unchecked")
    private MultiKey<K>[] getMultiKeyKeys() {
        return new MultiKey[] {
            new MultiKey<>(I1, I2),
            new MultiKey<>(I2, I3),
            new MultiKey<>(I3, I4),
            new MultiKey<>(I1, I1, I2),
            new MultiKey<>(I2, I3, I4),
            new MultiKey<>(I3, I7, I6),
            new MultiKey<>(I1, I1, I2, I3),
            new MultiKey<>(I2, I4, I5, I6),
            new MultiKey<>(I3, I6, I7, I8),
            new MultiKey<>(I1, I1, I2, I3, I4),
            new MultiKey<>(I2, I3, I4, I5, I6),
            new MultiKey<>(I3, I5, I6, I7, I8),
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public V[] getNewSampleValues() {
        return (V[]) new Object[] {
            "1a", "1b", "1c",
            "2d", "2e", "2f",
            "3g", "3h", "3i",
            "4j", "4k", "4l",
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultiKey<K>[] getOtherKeys() {
        return new MultiKey[] {
            new MultiKey<>(I1, I7),
            new MultiKey<>(I1, I8),
            new MultiKey<>(I2, I4),
            new MultiKey<>(I2, I5),
        };
    }

    @Override
    public MultiKey<K>[] getSampleKeys() {
        return getMultiKeyKeys();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        return (V[]) new Object[] {
            "2A", "2B", "2C",
            "3D", "3E", "3F",
            "4G", "4H", "4I",
            "5J", "5K", "5L",
        };
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public MultiKeyMap<K, V> makeObject() {
        return new MultiKeyMap<>();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testClone() {
        final MultiKeyMap<K, V> map = new MultiKeyMap<>();
        map.put(new MultiKey<>((K) I1, (K) I2), (V) "1-2");
        final Map<MultiKey<? extends K>, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get(new MultiKey<>((K) I1, (K) I2)), cloned.get(new MultiKey<>((K) I1, (K) I2)));
    }

    /**
     * Tests COMPRESS-872
     * <p>
     * Claim:
     * </p>
     * <ol>
     * <li>Create a MultiKeyMap, with key(s) of a type (class/record) which has some fields.
     * <li>Use multiKeyMap.put(T... keys, V value), to create an entry in the Map, to map the keys to a value
     * <li>Use multiKeyMap.get(T... keys), to verify that the mapping exists and returns the expected value.
     * <li>Modify/alter any of the objects used as a key. It is enough to change the value of any member field of any of the objects.
     * <li>Use multiKeyMap.get(T... keys) again, however, now there is no mapping for these keys!
     * <li>Use multiKeyMap.get(T... keys) with the new modified/altered objects, and it will return the expected value
     * </ol>
     */
    @Test
    void testCompress872() {
        final AtomicReference<String> k1 = new AtomicReference<>("K1v1");
        final AtomicReference<String> k2 = new AtomicReference<>("K2v1");
        final MultiKeyMap<AtomicReference<String>, String> map = (MultiKeyMap<AtomicReference<String>, String>) makeObject();
        assertNull(map.put(k1, k2, "V"));
        assertEquals("V", map.get(k1, k2));
        k1.set("K1v2");
        assertEquals("V", map.get(k1, k2));
        assertEquals("V", map.get(k1, k2));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testLRUMultiKeyMap() {
        final MultiKeyMap<K, V> map = MultiKeyMap.multiKeyMap(new LRUMap<>(2));
        map.put((K) I1, (K) I2, (V) "1-2");
        map.put((K) I1, (K) I3, (V) "1-1");
        assertEquals(2, map.size());
        map.put((K) I1, (K) I4, (V) "1-4");
        assertEquals(2, map.size());
        assertTrue(map.containsKey(I1, I3));
        assertTrue(map.containsKey(I1, I4));
        assertFalse(map.containsKey(I1, I2));

        final MultiKeyMap<K, V> cloned = map.clone();
        assertEquals(2, map.size());
        assertTrue(cloned.containsKey(I1, I3));
        assertTrue(cloned.containsKey(I1, I4));
        assertFalse(cloned.containsKey(I1, I2));
        cloned.put((K) I1, (K) I5, (V) "1-5");
        assertEquals(2, cloned.size());
        assertTrue(cloned.containsKey(I1, I4));
        assertTrue(cloned.containsKey(I1, I5));
    }

    @Test
    void testMultiKeyContainsKey() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        final MultiKey<K>[] keys = getMultiKeyKeys();

        for (final MultiKey<K> key : keys) {
            switch (key.size()) {
            case 2:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertFalse(multimap.containsKey(null, key.getKey(1)));
                assertFalse(multimap.containsKey(key.getKey(0), null));
                assertFalse(multimap.containsKey(null, null));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), null));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), null, null));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), null, null, null));
                break;
            case 3:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertFalse(multimap.containsKey(null, key.getKey(1), key.getKey(2)));
                assertFalse(multimap.containsKey(key.getKey(0), null, key.getKey(2)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), null));
                assertFalse(multimap.containsKey(null, null, null));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null, null));
                break;
            case 4:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertFalse(multimap.containsKey(null, key.getKey(1), key.getKey(2), key.getKey(3)));
                assertFalse(multimap.containsKey(key.getKey(0), null, key.getKey(2), key.getKey(3)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), null, key.getKey(3)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertFalse(multimap.containsKey(null, null, null, null));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                break;
            case 5:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertFalse(multimap.containsKey(null, key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertFalse(multimap.containsKey(key.getKey(0), null, key.getKey(2), key.getKey(3), key.getKey(4)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), null, key.getKey(3), key.getKey(4)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), null, key.getKey(4)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                assertFalse(multimap.containsKey(null, null, null, null, null));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    @Test
    void testMultiKeyGet() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        final MultiKey<K>[] keys = getMultiKeyKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            final MultiKey<K> key = keys[i];
            final V value = values[i];

            switch (key.size()) {
            case 2:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1)));
                assertNull(multimap.get(null, key.getKey(1)));
                assertNull(multimap.get(key.getKey(0), null));
                assertNull(multimap.get(null, null));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), null));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), null, null));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), null, null, null));
                break;
            case 3:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertNull(multimap.get(null, key.getKey(1), key.getKey(2)));
                assertNull(multimap.get(key.getKey(0), null, key.getKey(2)));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), null));
                assertNull(multimap.get(null, null, null));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null, null));
                break;
            case 4:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertNull(multimap.get(null, key.getKey(1), key.getKey(2), key.getKey(3)));
                assertNull(multimap.get(key.getKey(0), null, key.getKey(2), key.getKey(3)));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), null, key.getKey(3)));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertNull(multimap.get(null, null, null, null));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                break;
            case 5:
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertNull(multimap.get(null, key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertNull(multimap.get(key.getKey(0), null, key.getKey(2), key.getKey(3), key.getKey(4)));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), null, key.getKey(3), key.getKey(4)));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), null, key.getKey(4)));
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                assertNull(multimap.get(null, null, null, null, null));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    @Test
    void testMultiKeyPut() {
        final MultiKey<K>[] keys = getMultiKeyKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            final MultiKeyMap<K, V> multimap = new MultiKeyMap<>();

            final MultiKey<K> key = keys[i];
            final V value = values[i];

            switch (key.size()) {
            case 2:
                assertNull(multimap.put(key.getKey(0), key.getKey(1), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertTrue(multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), null));
                assertEquals(1, multimap.size());
                assertNull(multimap.get(key.getKey(0), key.getKey(1)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1)));
                break;
            case 3:
                assertNull(multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertTrue(multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1), key.getKey(2))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), null));
                assertEquals(1, multimap.size());
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                break;
            case 4:
                assertNull(multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertTrue(multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), null));
                assertEquals(1, multimap.size());
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                break;
            case 5:
                assertNull(multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4), value));
                assertEquals(1, multimap.size());
                assertEquals(value, multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertTrue(multimap.containsKey(new MultiKey<>(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4))));
                assertEquals(value, multimap.put(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4), null));
                assertEquals(1, multimap.size());
                assertNull(multimap.get(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    @Test
    void testMultiKeyPutWithNullKey() {
        final MultiKeyMap<String, String> map = new MultiKeyMap<>();
        map.put("a", null, "value1");
        map.put("b", null, "value2");
        map.put("c", null, "value3");
        map.put("a", "z",  "value4");
        map.put("a", null, "value5");
        map.put(null, "a", "value6");
        map.put(null, null, "value7");

        assertEquals(6, map.size());
        assertEquals("value5", map.get("a", null));
        assertEquals("value4", map.get("a", "z"));
        assertEquals("value6", map.get(null, "a"));
    }

    @Test
    void testMultiKeyRemove() {
        final MultiKey<K>[] keys = getMultiKeyKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            resetFull();
            final MultiKeyMap<K, V> multimap = getMap();
            final int size = multimap.size();

            final MultiKey<K> key = keys[i];
            final V value = values[i];

            switch (key.size()) {
            case 2:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1)));
                assertEquals(size - 1, multimap.size());
                assertNull(multimap.removeMultiKey(key.getKey(0), key.getKey(1)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1)));
                break;
            case 3:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertEquals(size - 1, multimap.size());
                assertNull(multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2)));
                break;
            case 4:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertEquals(size - 1, multimap.size());
                assertNull(multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3)));
                break;
            case 5:
                assertTrue(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(value, multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertEquals(size - 1, multimap.size());
                assertNull(multimap.removeMultiKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                assertFalse(multimap.containsKey(key.getKey(0), key.getKey(1), key.getKey(2), key.getKey(3), key.getKey(4)));
                break;
            default:
                fail("Invalid key size");
            }
        }
    }

    @Test
    void testMultiKeyRemoveAll1() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I1);
        assertEquals(8, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertFalse(I1.equals(key.getKey(0)));
        }
    }

    @Test
    void testMultiKeyRemoveAll2() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I2, I3);
        assertEquals(9, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertFalse(I2.equals(key.getKey(0)) && I3.equals(key.getKey(1)));
        }
    }

    @Test
    void testMultiKeyRemoveAll3() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I1, I1, I2);
        assertEquals(9, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertFalse(I1.equals(key.getKey(0)) && I1.equals(key.getKey(1)) && I2.equals(key.getKey(2)));
        }
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/MultiKeyMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/MultiKeyMap.fullCollection.version4.obj");
//    }

    @Test
    void testMultiKeyRemoveAll4() {
        resetFull();
        final MultiKeyMap<K, V> multimap = getMap();
        assertEquals(12, multimap.size());

        multimap.removeAll(I1, I1, I2, I3);
        assertEquals(10, multimap.size());
        for (final MapIterator<MultiKey<? extends K>, V> it = multimap.mapIterator(); it.hasNext();) {
            final MultiKey<? extends K> key = it.next();
            assertFalse(I1.equals(key.getKey(0)) && I1.equals(key.getKey(1)) && I2.equals(key.getKey(2)) && key.size() >= 4 && I3.equals(key.getKey(3)));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNullHandling() {
        resetFull();
        assertNull(map.get(null));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertNull(map.remove(null));
        assertFalse(map.entrySet().contains(null));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));

        assertThrows(NullPointerException.class, () -> map.put(null, null));

        assertNull(map.put(new MultiKey<>(null, null), null));

        assertThrows(NullPointerException.class, () -> map.put(null, (V) new Object()));
    }

}
