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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.BulkTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for IteratorIterable.
 */
class IteratorIterableTest extends BulkTest {

    private Iterator<Integer> createIterator() {
        final List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(0));
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        return list.iterator();
    }

    @Test
    @SuppressWarnings("unused")
    void testIterator() {
        final Iterator<Integer> iter = createIterator();
        final Iterable<Number> iterable = new IteratorIterable<>(iter);

        // first use
        verifyIteration(iterable);

        // second use
        for (final Number actual : iterable) {
            fail("should not be able to iterate twice");
        }
    }

    @Test
    void testMultipleUserIterator() {
        final Iterator<Integer> iter = createIterator();

        final Iterable<Number> iterable = new IteratorIterable<>(iter, true);

        // first use
        verifyIteration(iterable);

        // second use
        verifyIteration(iterable);
    }

    private void verifyIteration(final Iterable<Number> iterable) {
        int expected = 0;
        for (final Number actual : iterable) {
            assertEquals(expected, actual.intValue());
            ++expected;
        }
        assertTrue(expected > 0);
    }
}

