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

class CellExtractorFromLayeredBloomFilterTest extends AbstractCellExtractorTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected CellExtractor createEmptyExtractor() {
        return CellExtractor.from(LayeredBloomFilterTest.fixed(shape, 10));
    }

    @Override
    protected CellExtractor createExtractor() {
        final Hasher hasher = new IncrementingHasher(3, 2);
        final BloomFilter bf = LayeredBloomFilterTest.fixed(shape, 10);
        bf.merge(hasher);
        return CellExtractor.from(bf);
    }

    @Override
    protected int[] getExpectedIndices() {
        return new int[] {3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35};
    }

    @Override
    protected int[] getExpectedValues() {
        return new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    }
}
