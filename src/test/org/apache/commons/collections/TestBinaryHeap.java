/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.collection.AbstractTestCollection;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

/**
 * Tests the BinaryHeap.
 * 
 * @version $Revision: 1.15 $ $Date: 2004/01/14 21:34:30 $
 * 
 * @author Michael A. Smith
 */
public class TestBinaryHeap extends AbstractTestCollection {

    public static Test suite() {
        return new TestSuite(TestBinaryHeap.class);
    }

    public TestBinaryHeap(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------  
    public void verify() {
        super.verify();
        BinaryHeap heap = (BinaryHeap) collection;

        Comparator c = heap.m_comparator;
        if (c == null)
            c = ComparatorUtils.naturalComparator();
        if (!heap.m_isMinHeap)
            c = ComparatorUtils.reversedComparator(c);

        Object[] tree = heap.m_elements;
        for (int i = 1; i <= heap.m_size; i++) {
            Object parent = tree[i];
            if (i * 2 <= heap.m_size) {
                assertTrue("Parent is less than or equal to its left child", c.compare(parent, tree[i * 2]) <= 0);
            }
            if (i * 2 + 1 < heap.m_size) {
                assertTrue("Parent is less than or equal to its right child", c.compare(parent, tree[i * 2 + 1]) <= 0);
            }
        }
    }

    //-----------------------------------------------------------------------  
    /**
     * Overridden because UnboundedFifoBuffer isn't fail fast.
     * @return false
     */
    public boolean isFailFastSupported() {
        return false;
    }

    //-----------------------------------------------------------------------  
    public Collection makeConfirmedCollection() {
        return new ArrayList();
    }

    public Collection makeConfirmedFullCollection() {
        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    /**
     * Return a new, empty {@link Object} to used for testing.
     */
    public Collection makeCollection() {
        return new BinaryHeap();
    }

    //-----------------------------------------------------------------------  
    public Object[] getFullElements() {
        return getFullNonNullStringElements();
    }

    public Object[] getOtherElements() {
        return getOtherNonNullStringElements();
    }

    //-----------------------------------------------------------------------  
    public void testBasicOps() {
        BinaryHeap heap = new BinaryHeap();

        assertTrue("heap should be empty after create", heap.isEmpty());

        try {
            heap.peek();
            fail("NoSuchElementException should be thrown if peek is called before any elements are inserted");
        } catch (NoSuchElementException e) {
            // expected
        }

        try {
            heap.pop();
            fail("NoSuchElementException should be thrown if pop is called before any elements are inserted");
        } catch (NoSuchElementException e) {
            // expected
        }

        heap.insert("a");
        heap.insert("c");
        heap.insert("e");
        heap.insert("b");
        heap.insert("d");
        heap.insert("n");
        heap.insert("m");
        heap.insert("l");
        heap.insert("k");
        heap.insert("j");
        heap.insert("i");
        heap.insert("h");
        heap.insert("g");
        heap.insert("f");

        assertTrue("heap should not be empty after inserts", !heap.isEmpty());

        for (int i = 0; i < 14; i++) {
            assertEquals(
                "peek using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('a' + i)),
                heap.peek());

            assertEquals(
                "pop using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('a' + i)),
                heap.pop());

            if (i + 1 < 14) {
                assertTrue("heap should not be empty before all elements are popped", !heap.isEmpty());
            } else {
                assertTrue("heap should be empty after all elements are popped", heap.isEmpty());
            }
        }

        try {
            heap.peek();
            fail("NoSuchElementException should be thrown if peek is called after all elements are popped");
        } catch (NoSuchElementException e) {
            // expected
        }

        try {
            heap.pop();
            fail("NoSuchElementException should be thrown if pop is called after all elements are popped");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testBasicComparatorOps() {
        BinaryHeap heap = new BinaryHeap(new ReverseComparator(new ComparableComparator()));

        assertTrue("heap should be empty after create", heap.isEmpty());

        try {
            heap.peek();
            fail("NoSuchElementException should be thrown if peek is called before any elements are inserted");
        } catch (NoSuchElementException e) {
            // expected
        }

        try {
            heap.pop();
            fail("NoSuchElementException should be thrown if pop is called before any elements are inserted");
        } catch (NoSuchElementException e) {
            // expected
        }

        heap.insert("a");
        heap.insert("c");
        heap.insert("e");
        heap.insert("b");
        heap.insert("d");
        heap.insert("n");
        heap.insert("m");
        heap.insert("l");
        heap.insert("k");
        heap.insert("j");
        heap.insert("i");
        heap.insert("h");
        heap.insert("g");
        heap.insert("f");

        assertTrue("heap should not be empty after inserts", !heap.isEmpty());

        for (int i = 0; i < 14; i++) {

            // note: since we're using a comparator that reverses items, the
            // "minimum" item is "n", and the "maximum" item is "a".

            assertEquals(
                "peek using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('n' - i)),
                heap.peek());

            assertEquals(
                "pop using default constructor should return minimum value in the binary heap",
                String.valueOf((char) ('n' - i)),
                heap.pop());

            if (i + 1 < 14) {
                assertTrue("heap should not be empty before all elements are popped", !heap.isEmpty());
            } else {
                assertTrue("heap should be empty after all elements are popped", heap.isEmpty());
            }
        }

        try {
            heap.peek();
            fail("NoSuchElementException should be thrown if peek is called after all elements are popped");
        } catch (NoSuchElementException e) {
            // expected
        }

        try {
            heap.pop();
            fail("NoSuchElementException should be thrown if pop is called after all elements are popped");
        } catch (NoSuchElementException e) {
            // expected
        }
    }
    
    /**
     * Illustrates bad internal heap state reported in Bugzilla PR #235818. 
     */  
    public void testAddRemove() {
        resetEmpty();
        BinaryHeap heap = (BinaryHeap) collection;
        heap.add(new Integer(0));
        heap.add(new Integer(2));
        heap.add(new Integer(4));
        heap.add(new Integer(3));
        heap.add(new Integer(8));
        heap.add(new Integer(10));
        heap.add(new Integer(12));
        heap.add(new Integer(3));
        confirmed.addAll(heap);
        // System.out.println(heap);
        Object obj = new Integer(10);
        heap.remove(obj);
        confirmed.remove(obj);
        // System.out.println(heap);
        verify();
    }
    
    /**
     * Generate heaps staring with Integers from 0 - heapSize - 1.
     * Then perform random add / remove operations, checking
     * heap order after modifications. Alternates minHeaps, maxHeaps.
     *
     * Based on code provided by Steve Phelps in PR #25818
     *
     */
    public void testRandom() {
        int iterations = 500;
        int heapSize = 100;
        int operations = 20;
        Random randGenerator = new Random();
        BinaryHeap h = null;
        for(int i=0; i < iterations; i++) {
            if (i < iterations / 2) {          
                h = new BinaryHeap(true);
            } else {
                h = new BinaryHeap(false);
            }
            for(int r = 0; r < heapSize; r++) {
                h.add( new Integer( randGenerator.nextInt(heapSize)) );
            }
            for( int r = 0; r < operations; r++ ) {
                h.remove(new Integer(r));
                h.add(new Integer(randGenerator.nextInt(heapSize)));
            }
            checkOrder(h);
        }
    }
     
    /**
     * Pops all elements from the heap and verifies that the elements come off
     * in the correct order.  NOTE: this method empties the heap.
     */
    protected void checkOrder(BinaryHeap h) {
        Integer lastNum = null;
        Integer num = null;
        boolean fail = false;
        while (!h.isEmpty()) {
            num = (Integer) h.pop();
            if (h.m_isMinHeap) {
                assertTrue(lastNum == null || num.intValue() >= lastNum.intValue());
            } else { // max heap
                assertTrue(lastNum == null || num.intValue() <= lastNum.intValue());
            }
            lastNum = num;
            num = null;
        }
    }
    
    /**
     * Returns a string showing the contents of the heap formatted as a tree.
     * Makes no attempt at padding levels or handling wrapping. 
     */
    protected String showTree(BinaryHeap h) {
        int count = 1;
        StringBuffer buffer = new StringBuffer();
        for (int offset = 1; count < h.size() + 1; offset *= 2) {
            for (int i = offset; i < offset * 2; i++) {
                if (i < h.m_elements.length && h.m_elements[i] != null) 
                    buffer.append(h.m_elements[i] + " ");
                count++;
            }
            buffer.append('\n');
        }
        return buffer.toString();
    }

}