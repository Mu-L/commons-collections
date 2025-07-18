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

class BitMapExtractorFromLayeredBloomFilterTest extends AbstractBitMapExtractorTest {

    protected Shape shape = Shape.fromKM(17, 72);

    @Override
    protected BitMapExtractor createEmptyExtractor() {
        return LayeredBloomFilterTest.fixed(shape, 10);
    }

    @Override
    protected BitMapExtractor createExtractor() {
        final Hasher hasher = new IncrementingHasher(0, 1);
        final BloomFilter bf = LayeredBloomFilterTest.fixed(shape, 10);
        bf.merge(hasher);
        return bf;
    }
}
