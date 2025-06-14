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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests the {@link Shape} class.
 */
class ShapeTest {

    /*
     * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
     *
     * n = 5
     *
     * p = 0.100375138 (1 in 10)
     *
     * m = 24 (3B)
     *
     * k = 3
     */

    private final Shape shape = Shape.fromKM(3, 24);

    /**
     * Tests that if the number of bits is less than 1 an exception is thrown
     */
    @Test
    void testBadNumberOfBits() {
        assertThrows(IllegalArgumentException.class, () -> Shape.fromKM(5, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNM(5, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(5, 0, 7));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromPMK(0.035, 0, 7));
    }

    /**
     * Tests that if the number of hash functions is less than 1 an exception is thrown.
     */
    @Test
    void testBadNumberOfHashFunctions() {
        assertThrows(IllegalArgumentException.class, () -> Shape.fromKM(0, 7));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(5, 26, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromPMK(0.35, 26, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNM(2, 1));
    }

    /**
     * Tests that if the number of items less than 1 an IllegalArgumentException is thrown.
     */
    @Test
    void testBadNumberOfItems() {
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNM(0, 24));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(0, 24, 5));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(0, 0.02));
    }

    /**
     * Tests that if the calculated probability is greater than or equal to 1 an IllegalArgumentException is thrown
     */
    @Test
    void testBadProbability() {
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(4000, 8, 1));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(10, 0.0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(10, 1.0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(10, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(10, Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(10, Double.NEGATIVE_INFINITY));
    }

    /**
     * Test equality of shape.
     */
    @ParameterizedTest
    @CsvSource({
        "3, 24",
        "1, 24",
        "1, 1",
        "13, 124",
        "13, 224",
    })
    void testEqualsAndHashCode(final int k, final int m) {
        final Shape shape1 = Shape.fromKM(k, m);
        assertEquals(shape1, shape1);
        assertEquals(Arrays.hashCode(new int[] {m, k}), shape1.hashCode(),
            "Doesn't match Arrays.hashCode(new int[] {m, k})");
        assertNotEquals(shape1, null);
        assertNotEquals(shape1, "text");
        assertNotEquals(shape1, Integer.valueOf(3));
        assertNotEquals(shape1, Shape.fromKM(k, m + 1));
        assertNotEquals(shape1, Shape.fromKM(k + 1, m));

        // Test this is reproducible
        final Shape shape2 = Shape.fromKM(k, m);
        assertEquals(shape1, shape2);
        assertEquals(shape1.hashCode(), shape2.hashCode());
    }

    /*
     * values from https://hur.st/bloomfilter/?n=5&p=.1&m=&k=
     *
     * n = 5
     *
     * p = 0.100375138 (1 in 10)
     *
     * m = 24 (3B)
     *
     * k = 3
     */

    @Test
    void testEstimateN() {
        for (int i = 0; i < 24; i++) {
            final double c = i;
            final double expected = -(24.0 / 3.0) * Math.log1p(-c / 24.0);
            assertEquals(expected, shape.estimateN(i), "Error on " + i);
        }

        assertEquals(Double.POSITIVE_INFINITY, shape.estimateN(24));

        assertEquals(Double.NaN, shape.estimateN(25));
    }

    /**
     * Tests that if the number of bits less than 1 an IllegalArgumentException is thrown.
     */
    @Test
    void testFromKM() {
        assertThrows(IllegalArgumentException.class, () -> Shape.fromKM(5, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromKM(0, 5));
    }

    /**
     * Tests that the number of items and number of bits is passed the other values are calculated correctly.
     */
    @Test
    void testFromNM() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24
         */
        final Shape shape = Shape.fromNM(5, 24);

        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(0.100375138, shape.getProbability(5), 0.000001);

        assertThrows(IllegalArgumentException.class, () -> Shape.fromNM(5, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNM(0, 5));
    }

    /**
     * Tests that when the number of items, number of bits and number of hash functions is passed the values are
     * calculated correctly.
     */
    @Test
    void testFromNMK() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&m=24&k=4
         */
        final Shape shape = Shape.fromNMK(5, 24, 4);

        assertEquals(24, shape.getNumberOfBits());
        assertEquals(4, shape.getNumberOfHashFunctions());
        assertEquals(0.102194782, shape.getProbability(5), 0.000001);

        assertThrows(IllegalArgumentException.class,
                () -> Shape.fromNMK(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(5, 5, 0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(5, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNMK(0, 5, 5));
    }

    /**
     * Tests the calculated values of calling the constructor with the probability, number of bits and number of hash
     * functions.
     */
    @Test
    void testFromNP() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=24&k=3
         */
        final double probability = 1.0 / 2000000;
        final Shape shape = Shape.fromNP(10, probability);

        assertEquals(302, shape.getNumberOfBits());
        assertEquals(21, shape.getNumberOfHashFunctions());

        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(Integer.MAX_VALUE, Math.nextDown(1.0)));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(0, probability));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(5, 0.0));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(Integer.MAX_VALUE, Math.nextUp(0.0)));
        // Test that if calculated number of bits is greater than Integer.MAX_VALUE an
        // IllegalArgumentException is thrown.
        assertThrows(IllegalArgumentException.class, () -> Shape.fromNP(Integer.MAX_VALUE, 0.1));
    }

    /**
     * Tests the calculated values of calling the constructor with the probability, number of bits and number of hash
     * functions.
     */
    @Test
    void testFromPMK() {
        /*
         * values from https://hur.st/bloomfilter/?n=5&p=.1&m=24&k=3
         */
        Shape shape = Shape.fromPMK(0.1, 24, 3);

        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(0.100375138, shape.getProbability(5), 0.000001);

        assertThrows(IllegalArgumentException.class,
                () -> Shape.fromPMK(Math.nextDown(1.0), Integer.MAX_VALUE, Integer.MAX_VALUE));
        shape = Shape.fromPMK(Math.nextUp(0.0), 5, 5);
        assertEquals(1.0, shape.getProbability(Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromPMK(Math.nextDown(1.0), 5, 5));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromPMK(0.0, 5, 5));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromPMK(0.5, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> Shape.fromPMK(0.5, 5, 0));
    }

    @Test
    void testGetProbability() {
        for (int i = 0; i <= 24; i++) {
            final double expected = Math.pow(-Math.expm1(-3.0 * i / 24), 3);
            assertEquals(expected, shape.getProbability(i), "error at " + i);
        }

        assertEquals(0.0, shape.getProbability(0), 0.0);

        assertThrows(IllegalArgumentException.class, () -> shape.getProbability(-1));
    }

    @Test
    void testIsSparse() {
        final int functions = 1; // Ignored
        for (int i = 1; i <= 3; i++) {
            final int bits = i * Long.SIZE;
            final Shape shape = Shape.fromKM(functions, bits);
            for (int n = 0; n <= bits; n++) {
                final int c = n;
                // is sparse when number of bits stored as integers is less than 2 times the
                // number of bitmaps
                assertEquals(n * Integer.SIZE <= Math.ceil((double) bits / Long.SIZE) * Long.SIZE,
                        shape.isSparse(n), () -> String.format("n=%d : bits=%d", c, bits));
            }
        }
    }

    /**
     * Tests that the probability is calculated correctly.
     */
    @Test
    void testProbability() {
        final Shape shape = Shape.fromNMK(5, 24, 3);
        assertEquals(24, shape.getNumberOfBits());
        assertEquals(3, shape.getNumberOfHashFunctions());
        assertEquals(0.100375138, shape.getProbability(5), 0.000001);
    }

    @Test
    void testToString() {
        assertEquals("Shape[k=3 m=5]", Shape.fromKM(3, 5).toString());
    }
}
