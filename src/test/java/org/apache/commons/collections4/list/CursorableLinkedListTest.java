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
package org.apache.commons.collections4.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class.
 */
public class CursorableLinkedListTest<E> extends AbstractLinkedListTest<E> {

    private CursorableLinkedList<E> list;

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public CursorableLinkedList<E> makeObject() {
        return new CursorableLinkedList<>();
    }

    @BeforeEach
    public void setUp() {
        list = new CursorableLinkedList<>();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAdd() {
        assertEquals("[]", list.toString());
        assertTrue(list.add((E) Integer.valueOf(1)));
        assertEquals("[1]", list.toString());
        assertTrue(list.add((E) Integer.valueOf(2)));
        assertEquals("[1, 2]", list.toString());
        assertTrue(list.add((E) Integer.valueOf(3)));
        assertEquals("[1, 2, 3]", list.toString());
        assertTrue(list.addFirst((E) Integer.valueOf(0)));
        assertEquals("[0, 1, 2, 3]", list.toString());
        assertTrue(list.addLast((E) Integer.valueOf(4)));
        assertEquals("[0, 1, 2, 3, 4]", list.toString());
        list.add(0, (E) Integer.valueOf(-2));
        assertEquals("[-2, 0, 1, 2, 3, 4]", list.toString());
        list.add(1, (E) Integer.valueOf(-1));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4]", list.toString());
        list.add(7, (E) Integer.valueOf(5));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4, 5]", list.toString());

        final List<E> list2 = new LinkedList<>();
        list2.add((E) "A");
        list2.add((E) "B");
        list2.add((E) "C");

        assertTrue(list.addAll(list2));
        assertEquals("[-2, -1, 0, 1, 2, 3, 4, 5, A, B, C]", list.toString());
        assertTrue(list.addAll(3, list2));
        assertEquals("[-2, -1, 0, A, B, C, 1, 2, 3, 4, 5, A, B, C]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testClear() {
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        list.add((E) "element");
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());

        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        list.add((E) "element1");
        list.add((E) "element2");
        assertEquals(2, list.size());
        assertFalse(list.isEmpty());

        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        for (int i = 0; i < 1000; i++) {
            list.add((E) Integer.valueOf(i));
        }
        assertEquals(1000, list.size());
        assertFalse(list.isEmpty());

        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testContains() {
        assertFalse(list.contains("A"));
        assertTrue(list.add((E) "A"));
        assertTrue(list.contains("A"));
        assertTrue(list.add((E) "B"));
        assertTrue(list.contains("A"));
        assertTrue(list.addFirst((E) "a"));
        assertTrue(list.contains("A"));
        assertTrue(list.remove("a"));
        assertTrue(list.contains("A"));
        assertTrue(list.remove("A"));
        assertFalse(list.contains("A"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testContainsAll() {
        assertTrue(list.containsAll(list));
        final java.util.List<E> list2 = new java.util.LinkedList<>();
        assertTrue(list.containsAll(list2));
        list2.add((E) "A");
        assertFalse(list.containsAll(list2));
        list.add((E) "B");
        list.add((E) "A");
        assertTrue(list.containsAll(list2));
        list2.add((E) "B");
        assertTrue(list.containsAll(list2));
        list2.add((E) "C");
        assertFalse(list.containsAll(list2));
        list.add((E) "C");
        assertTrue(list.containsAll(list2));
        list2.add((E) "C");
        assertTrue(list.containsAll(list2));
        assertTrue(list.containsAll(list));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorAdd() {
        final CursorableLinkedList.Cursor<E> it = list.cursor();
        it.add((E) "1");
        assertEquals("[1]", list.toString());
        it.add((E) "3");
        assertEquals("[1, 3]", list.toString());
        it.add((E) "5");
        assertEquals("[1, 3, 5]", list.toString());
        assertEquals("5", it.previous());
        it.add((E) "4");
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("4", it.previous());
        assertEquals("3", it.previous());
        it.add((E) "2");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        it.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorConcurrentModification() {
        // this test verifies that cursors remain valid when the list
        // is modified via other means.
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");
        list.add((E) "7");
        list.add((E) "9");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        final CursorableLinkedList.Cursor<E> c2 = list.cursor();
        final Iterator<E> li = list.iterator();

        // test cursors remain valid when list modified by std Iterator
        // test cursors skip elements removed via ListIterator
        assertEquals("1", li.next());
        assertEquals("2", li.next());
        li.remove();
        assertEquals("3", li.next());
        assertEquals("1", c1.next());
        assertEquals("3", c1.next());
        assertEquals("1", c2.next());

        // test cursor c1 can remove elements from previously modified list
        // test cursor c2 skips elements removed via different cursor
        c1.remove();
        assertEquals("5", c2.next());
        c2.add((E) "6");
        assertEquals("5", c1.next());
        assertEquals("6", c1.next());
        assertEquals("7", c1.next());

        // test cursors remain valid when list mod via CursorableLinkedList
        // test cursor remains valid when elements inserted into list before
        // the current position of the cursor.
        list.add(0, (E) "0");

        // test cursor remains valid when element inserted immediately after
        // current element of a cursor, and the element is seen on the
        // next call to the next method of that cursor.
        list.add(5, (E) "8");

        assertEquals("8", c1.next());
        assertEquals("9", c1.next());
        c1.add((E) "10");
        assertEquals("7", c2.next());
        assertEquals("8", c2.next());
        assertEquals("9", c2.next());
        assertEquals("10", c2.next());

        assertThrows(NoSuchElementException.class, () -> c2.next());

        assertThrows(ConcurrentModificationException.class, () -> li.next());

        c1.close(); // not necessary
        c2.close(); // not necessary
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorNavigation() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        final CursorableLinkedList.Cursor<E> it = list.cursor();
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("3", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("5", it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("5", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("4", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("3", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        it.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorNextIndexAddAfter() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        list.add(1, (E) "0");
        assertEquals(0, c1.nextIndex());
        assertEquals("1", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("0", c1.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorNextIndexAddBefore() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        assertEquals("1", c1.next());
        list.add(0, (E) "0");
        assertEquals(2, c1.nextIndex());
        assertEquals("2", c1.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorNextIndexAddNext() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        list.add(0, (E) "0");
        assertEquals(0, c1.nextIndex());
        assertEquals("0", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("1", c1.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorNextIndexFirst() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();

        assertEquals(0, c1.nextIndex());
        list.remove(0);
        assertEquals(0, c1.nextIndex());
        assertEquals("2", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("3", c1.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorNextIndexMid() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        final Iterator<E> li = list.iterator();

        // test cursors remain valid when list modified by std Iterator
        // test cursors skip elements removed via ListIterator
        assertEquals("1", li.next());
        assertEquals("2", li.next());
        li.remove();
        assertEquals(0, c1.nextIndex());
        assertEquals("1", c1.next());
        assertEquals(1, c1.nextIndex());
        assertEquals("3", c1.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorRemove() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> it = list.cursor();

        assertThrows(IllegalStateException.class, () -> it.remove());

        assertEquals("1", it.next());
        assertEquals("2", it.next());
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        it.remove();
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("3", it.next());
        assertEquals("3", it.previous());
        assertEquals("1", it.previous());
        it.remove();
        assertEquals("[3, 4, 5]", list.toString());
        assertFalse(it.hasPrevious());
        assertEquals("3", it.next());
        it.remove();
        assertEquals("[4, 5]", list.toString());
        assertThrows(IllegalStateException.class, it::remove);
        assertEquals("4", it.next());
        assertEquals("5", it.next());
        it.remove();
        assertEquals("[4]", list.toString());
        assertEquals("4", it.previous());
        it.remove();
        assertEquals("[]", list.toString());
        it.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCursorSet() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        final CursorableLinkedList.Cursor<E> it = list.cursor();
        assertEquals("1", it.next());
        it.set((E) "a");
        assertEquals("a", it.previous());
        it.set((E) "A");
        assertEquals("A", it.next());
        assertEquals("2", it.next());
        it.set((E) "B");
        assertEquals("3", it.next());
        assertEquals("4", it.next());
        it.set((E) "D");
        assertEquals("5", it.next());
        it.set((E) "E");
        assertEquals("[A, B, 3, D, E]", list.toString());
        it.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testEqualsAndHashCode() {
        assertEquals(list, list);
        assertEquals(list.hashCode(), list.hashCode());
        list.add((E) "A");
        assertEquals(list, list);
        assertEquals(list.hashCode(), list.hashCode());

        final CursorableLinkedList<E> list2 = new CursorableLinkedList<>();
        assertFalse(list.equals(list2));
        assertFalse(list2.equals(list));

        final java.util.List<E> list3 = new java.util.LinkedList<>();
        assertFalse(list.equals(list3));
        assertFalse(list3.equals(list));
        assertEquals(list2, list3);
        assertEquals(list3, list2);
        assertEquals(list2.hashCode(), list3.hashCode());

        list2.add((E) "A");
        assertEquals(list, list2);
        assertEquals(list2, list);
        assertFalse(list2.equals(list3));
        assertFalse(list3.equals(list2));

        list3.add((E) "A");
        assertEquals(list2, list3);
        assertEquals(list3, list2);
        assertEquals(list2.hashCode(), list3.hashCode());

        list.add((E) "B");
        assertEquals(list, list);
        assertFalse(list.equals(list2));
        assertFalse(list2.equals(list));
        assertFalse(list.equals(list3));
        assertFalse(list3.equals(list));

        list2.add((E) "B");
        list3.add((E) "B");
        assertEquals(list, list);
        assertEquals(list, list2);
        assertEquals(list2, list);
        assertEquals(list2, list3);
        assertEquals(list3, list2);
        assertEquals(list2.hashCode(), list3.hashCode());

        list.add((E) "C");
        list2.add((E) "C");
        list3.add((E) "C");
        assertEquals(list, list);
        assertEquals(list, list2);
        assertEquals(list2, list);
        assertEquals(list2, list3);
        assertEquals(list3, list2);
        assertEquals(list.hashCode(), list2.hashCode());
        assertEquals(list2.hashCode(), list3.hashCode());

        list.add((E) "D");
        list2.addFirst((E) "D");
        assertEquals(list, list);
        assertFalse(list.equals(list2));
        assertFalse(list2.equals(list));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGet() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0),
                "shouldn't get here");

        assertTrue(list.add((E) "A"));
        assertEquals("A", list.get(0));
        assertTrue(list.add((E) "B"));
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIndexOf() {
        assertEquals(-1, list.indexOf("A"));
        assertEquals(-1, list.lastIndexOf("A"));
        list.add((E) "A");
        assertEquals(0, list.indexOf("A"));
        assertEquals(0, list.lastIndexOf("A"));
        assertEquals(-1, list.indexOf("B"));
        assertEquals(-1, list.lastIndexOf("B"));
        list.add((E) "B");
        assertEquals(0, list.indexOf("A"));
        assertEquals(0, list.lastIndexOf("A"));
        assertEquals(1, list.indexOf("B"));
        assertEquals(1, list.lastIndexOf("B"));
        list.addFirst((E) "B");
        assertEquals(1, list.indexOf("A"));
        assertEquals(1, list.lastIndexOf("A"));
        assertEquals(0, list.indexOf("B"));
        assertEquals(2, list.lastIndexOf("B"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextAddIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());

        list.add(1, (E) "Z");

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals("A", c1.current.value);
        assertEquals("Z", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[Z, B, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextAddByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        c1.add((E) "Z");

        assertTrue(c1.nextIndexValid);
        assertEquals(3, c1.nextIndex);
        assertFalse(c1.currentRemovedByAnother);
        assertNull(c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, B, Z, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextAddIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        list.add(1, (E) "Z");

        assertFalse(c1.nextIndexValid);
        assertEquals("B", c1.current.value);
        assertEquals("C", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, Z, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextNextRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("C", c1.next());

        assertEquals("B", list.remove(1));

        assertFalse(c1.nextIndexValid);
        assertFalse(c1.currentRemovedByAnother);
        assertEquals("C", c1.current.value);
        assertEquals("D", c1.next.value);

        assertEquals("[A, C, D]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, D]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextPreviousAddByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        c1.add((E) "Z");

        assertTrue(c1.nextIndexValid);
        assertEquals(2, c1.nextIndex);
        assertNull(c1.current);
        assertEquals("B", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextPreviousAddIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        list.add(1, (E) "Z");

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals("B", c1.current.value);
        assertEquals("Z", c1.next.value);

        assertEquals("[A, Z, B, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, Z, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextPreviousRemoveByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        c1.remove();

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertFalse(c1.currentRemovedByAnother);
        assertNull(c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextPreviousRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        assertEquals("B", list.remove(1));

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertTrue(c1.currentRemovedByAnother);
        assertNull(c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextPreviousSetByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());
        assertEquals("B", c1.previous());

        c1.set((E) "Z");

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertEquals("Z", c1.current.value);
        assertEquals("Z", c1.next.value);

        assertEquals("[A, Z, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextRemoveByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        c1.remove();

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertFalse(c1.currentRemovedByAnother);
        assertNull(c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextRemoveByListSetByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        list.remove(1);

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertNull(c1.current);
        assertEquals("C", c1.next.value);
        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.set((E) "Z"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        assertEquals("B", list.remove(1));

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertTrue(c1.currentRemovedByAnother);
        assertNull(c1.current);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextNextSetByIterator() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());
        assertEquals("B", c1.next());

        c1.set((E) "Z");

        assertTrue(c1.nextIndexValid);
        assertEquals(2, c1.nextIndex);
        assertEquals("Z", c1.current.value);
        assertEquals("C", c1.next.value);

        assertEquals("[A, Z, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[A, C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInternalState_CursorNextRemoveIndex1ByList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");

        final CursorableLinkedList.Cursor<E> c1 = list.cursor();
        assertEquals("A", c1.next());

        assertEquals("B", list.remove(1));

        assertTrue(c1.nextIndexValid);
        assertEquals(1, c1.nextIndex);
        assertFalse(c1.currentRemovedByAnother);
        assertEquals("A", c1.current.value);
        assertEquals("C", c1.next.value);

        assertEquals("[A, C]", list.toString());
        c1.remove();  // works ok
        assertEquals("[C]", list.toString());

        assertThrows(IllegalStateException.class, () -> c1.remove());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIsEmpty() {
        assertTrue(list.isEmpty());
        list.add((E) "element");
        assertFalse(list.isEmpty());
        list.remove("element");
        assertTrue(list.isEmpty());
        list.add((E) "element");
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIterator() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        Iterator<E> it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertEquals("3", it.next());
        assertTrue(it.hasNext());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertEquals("5", it.next());
        assertFalse(it.hasNext());

        it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("1", it.next());
        it.remove();
        assertEquals("[2, 3, 4, 5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("2", it.next());
        it.remove();
        assertEquals("[3, 4, 5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("3", it.next());
        it.remove();
        assertEquals("[4, 5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("4", it.next());
        it.remove();
        assertEquals("[5]", list.toString());
        assertTrue(it.hasNext());
        assertEquals("5", it.next());
        it.remove();
        assertEquals("[]", list.toString());
        assertFalse(it.hasNext());
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testListIteratorAdd() {
        final ListIterator<E> it = list.listIterator();
        it.add((E) "1");
        assertEquals("[1]", list.toString());
        it.add((E) "3");
        assertEquals("[1, 3]", list.toString());
        it.add((E) "5");
        assertEquals("[1, 3, 5]", list.toString());
        assertEquals("5", it.previous());
        it.add((E) "4");
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("4", it.previous());
        assertEquals("3", it.previous());
        it.add((E) "2");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testListIteratorNavigation() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        final ListIterator<E> it = list.listIterator();
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
        assertEquals("1", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("2", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals("3", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(2, it.previousIndex());
        assertEquals(3, it.nextIndex());
        assertEquals("4", it.next());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3, it.previousIndex());
        assertEquals(4, it.nextIndex());
        assertEquals("5", it.next());
        assertFalse(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(4, it.previousIndex());
        assertEquals(5, it.nextIndex());
        assertEquals("5", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(3, it.previousIndex());
        assertEquals(4, it.nextIndex());
        assertEquals("4", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(2, it.previousIndex());
        assertEquals(3, it.nextIndex());
        assertEquals("3", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals("2", it.previous());
        assertTrue(it.hasNext());
        assertTrue(it.hasPrevious());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals("1", it.previous());
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testListIteratorRemove() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        final ListIterator<E> it = list.listIterator();
        assertThrows(IllegalStateException.class, it::remove);
        assertEquals("1", it.next());
        assertEquals("2", it.next());
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        it.remove();
        assertEquals("[1, 3, 4, 5]", list.toString());
        assertEquals("3", it.next());
        assertEquals("3", it.previous());
        assertEquals("1", it.previous());
        it.remove();
        assertEquals("[3, 4, 5]", list.toString());
        assertFalse(it.hasPrevious());
        assertEquals("3", it.next());
        it.remove();
        assertEquals("[4, 5]", list.toString());
        assertThrows(IllegalStateException.class, it::remove);
        assertEquals("4", it.next());
        assertEquals("5", it.next());
        it.remove();
        assertEquals("[4]", list.toString());
        assertEquals("4", it.previous());
        it.remove();
        assertEquals("[]", list.toString());
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    void testListIteratorSet() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        final ListIterator<E> it = list.listIterator();
        assertEquals("1", it.next());
        it.set((E) "a");
        assertEquals("a", it.previous());
        it.set((E) "A");
        assertEquals("A", it.next());
        assertEquals("2", it.next());
        it.set((E) "B");
        assertEquals("3", it.next());
        assertEquals("4", it.next());
        it.set((E) "D");
        assertEquals("5", it.next());
        it.set((E) "E");
        assertEquals("[A, B, 3, D, E]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testLongSerialization() throws Exception {
        // recursive serialization will cause a stack
        // overflow exception with long lists
        for (int i = 0; i < 10000; i++) {
            list.add((E) Integer.valueOf(i));
        }

        final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        final java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        final java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        final java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        final Object list2 = in.readObject();

        assertNotSame(list, list2);
        assertEquals(list2, list);
        assertEquals(list, list2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemove() {
        list.add((E) "1");
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        assertEquals("[1, 1, 2, 3, 4, 5, 2, 3, 4, 5]", list.toString());
        assertFalse(list.remove("6"));
        assertTrue(list.remove("5"));
        assertEquals("[1, 1, 2, 3, 4, 2, 3, 4, 5]", list.toString());
        assertTrue(list.remove("5"));
        assertEquals("[1, 1, 2, 3, 4, 2, 3, 4]", list.toString());
        assertFalse(list.remove("5"));
        assertTrue(list.remove("1"));
        assertEquals("[1, 2, 3, 4, 2, 3, 4]", list.toString());
        assertTrue(list.remove("1"));
        assertEquals("[2, 3, 4, 2, 3, 4]", list.toString());
        assertTrue(list.remove("2"));
        assertEquals("[3, 4, 2, 3, 4]", list.toString());
        assertTrue(list.remove("2"));
        assertEquals("[3, 4, 3, 4]", list.toString());
        assertTrue(list.remove("3"));
        assertEquals("[4, 3, 4]", list.toString());
        assertTrue(list.remove("3"));
        assertEquals("[4, 4]", list.toString());
        assertTrue(list.remove("4"));
        assertEquals("[4]", list.toString());
        assertTrue(list.remove("4"));
        assertEquals("[]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveAll() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        final HashSet<E> set = new HashSet<>();
        set.add((E) "A");
        set.add((E) "2");
        set.add((E) "C");
        set.add((E) "4");
        set.add((E) "D");

        assertTrue(list.removeAll(set));
        assertEquals("[1, 3, 5]", list.toString());
        assertFalse(list.removeAll(set));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveByIndex() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        assertEquals("1", list.remove(0));
        assertEquals("[2, 3, 4, 5]", list.toString());
        assertEquals("3", list.remove(1));
        assertEquals("[2, 4, 5]", list.toString());
        assertEquals("4", list.remove(1));
        assertEquals("[2, 5]", list.toString());
        assertEquals("5", list.remove(1));
        assertEquals("[2]", list.toString());
        assertEquals("2", list.remove(0));
        assertEquals("[]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRetainAll() {
        list.add((E) "1");
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "4");
        list.add((E) "5");
        list.add((E) "5");

        final HashSet<E> set = new HashSet<>();
        set.add((E) "A");
        set.add((E) "2");
        set.add((E) "C");
        set.add((E) "4");
        set.add((E) "D");

        assertTrue(list.retainAll(set));
        assertEquals("[2, 2, 4, 4]", list.toString());
        assertFalse(list.retainAll(set));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSerialization() throws Exception {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        final java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        final java.io.ByteArrayInputStream bufIn = new java.io.ByteArrayInputStream(buf.toByteArray());
        final java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufIn);
        final Object list2 = in.readObject();

        assertNotSame(list, list2);
        assertEquals(list2, list);
        assertEquals(list, list2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSerializationWithOpenCursor() throws Exception {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");
        final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        final java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(buf);
        out.writeObject(list);
        out.flush();
        out.close();

        final java.io.ByteArrayInputStream bufin = new java.io.ByteArrayInputStream(buf.toByteArray());
        final java.io.ObjectInputStream in = new java.io.ObjectInputStream(bufin);
        final Object list2 = in.readObject();

        assertNotSame(list, list2);
        assertEquals(list2, list);
        assertEquals(list, list2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSet() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");
        assertEquals("[1, 2, 3, 4, 5]", list.toString());
        list.set(0, (E) "A");
        assertEquals("[A, 2, 3, 4, 5]", list.toString());
        list.set(1, (E) "B");
        assertEquals("[A, B, 3, 4, 5]", list.toString());
        list.set(2, (E) "C");
        assertEquals("[A, B, C, 4, 5]", list.toString());
        list.set(3, (E) "D");
        assertEquals("[A, B, C, D, 5]", list.toString());
        list.set(4, (E) "E");
        assertEquals("[A, B, C, D, E]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubList() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        assertEquals("[A, B, C, D, E]", list.toString());
        assertEquals("[A, B, C, D, E]", list.subList(0, 5).toString());
        assertEquals("[B, C, D, E]", list.subList(1, 5).toString());
        assertEquals("[C, D, E]", list.subList(2, 5).toString());
        assertEquals("[D, E]", list.subList(3, 5).toString());
        assertEquals("[E]", list.subList(4, 5).toString());
        assertEquals("[]", list.subList(5, 5).toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubListAddBegin() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(0, 0);
        sublist.add((E) "a");
        assertEquals("[a, A, B, C, D, E]", list.toString());
        assertEquals("[a]", sublist.toString());
        sublist.add((E) "b");
        assertEquals("[a, b, A, B, C, D, E]", list.toString());
        assertEquals("[a, b]", sublist.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubListAddEnd() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(5, 5);
        sublist.add((E) "F");
        assertEquals("[A, B, C, D, E, F]", list.toString());
        assertEquals("[F]", sublist.toString());
        sublist.add((E) "G");
        assertEquals("[A, B, C, D, E, F, G]", list.toString());
        assertEquals("[F, G]", sublist.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubListAddMiddle() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(1, 3);
        sublist.add((E) "a");
        assertEquals("[A, B, C, a, D, E]", list.toString());
        assertEquals("[B, C, a]", sublist.toString());
        sublist.add((E) "b");
        assertEquals("[A, B, C, a, b, D, E]", list.toString());
        assertEquals("[B, C, a, b]", sublist.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubListRemove() {
        list.add((E) "A");
        list.add((E) "B");
        list.add((E) "C");
        list.add((E) "D");
        list.add((E) "E");

        final List<E> sublist = list.subList(1, 4);
        assertEquals("[B, C, D]", sublist.toString());
        assertEquals("[A, B, C, D, E]", list.toString());
        sublist.remove("C");
        assertEquals("[B, D]", sublist.toString());
        assertEquals("[A, B, D, E]", list.toString());
        sublist.remove(1);
        assertEquals("[B]", sublist.toString());
        assertEquals("[A, B, E]", list.toString());
        sublist.clear();
        assertEquals("[]", sublist.toString());
        assertEquals("[A, E]", list.toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testToArray() {
        list.add((E) "1");
        list.add((E) "2");
        list.add((E) "3");
        list.add((E) "4");
        list.add((E) "5");

        final Object[] elts = list.toArray();
        assertEquals("1", elts[0]);
        assertEquals("2", elts[1]);
        assertEquals("3", elts[2]);
        assertEquals("4", elts[3]);
        assertEquals("5", elts[4]);
        assertEquals(5, elts.length);

        final String[] elts2 = list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        assertEquals("1", elts2[0]);
        assertEquals("2", elts2[1]);
        assertEquals("3", elts2[2]);
        assertEquals("4", elts2[3]);
        assertEquals("5", elts2[4]);
        assertEquals(5, elts2.length);

        final String[] elts3 = new String[5];
        assertSame(elts3, list.toArray(elts3));
        assertEquals("1", elts3[0]);
        assertEquals("2", elts3[1]);
        assertEquals("3", elts3[2]);
        assertEquals("4", elts3[3]);
        assertEquals("5", elts3[4]);
        assertEquals(5, elts3.length);

        final String[] elts4 = new String[3];
        final String[] elts4b = list.toArray(elts4);
        assertNotSame(elts4, elts4b);
        assertEquals("1", elts4b[0]);
        assertEquals("2", elts4b[1]);
        assertEquals("3", elts4b[2]);
        assertEquals("4", elts4b[3]);
        assertEquals("5", elts4b[4]);
        assertEquals(5, elts4b.length);
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CursorableLinkedList.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) getCollection(), "src/test/resources/data/test/CursorableLinkedList.fullCollection.version4.obj");
//    }

}
