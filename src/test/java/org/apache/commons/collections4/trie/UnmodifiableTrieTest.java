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
package org.apache.commons.collections4.trie;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.map.AbstractSortedMapTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractSortedMapTest} for exercising the
 * {@link UnmodifiableTrie} implementation.
 *
 * @param <V> the value type.
 */
public class UnmodifiableTrieTest<V> extends AbstractSortedMapTest<String, V> {

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isPutChangeSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public Trie<String, V> makeFullMap() {
        final Trie<String, V> m = new PatriciaTrie<>();
        addSampleMappings(m);
        return UnmodifiableTrie.unmodifiableTrie(m);
    }

    @Override
    public Trie<String, V> makeObject() {
        return UnmodifiableTrie.unmodifiableTrie(new PatriciaTrie<>());
    }

    @Test
    void testDecorateFactory() {
        final Trie<String, V> trie = makeFullMap();
        assertSame(trie, UnmodifiableTrie.unmodifiableTrie(trie));

        assertThrows(NullPointerException.class, () -> UnmodifiableTrie.unmodifiableTrie(null));
    }

    @Test
    void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }

//    void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/UnmodifiableTrie.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/UnmodifiableTrie.fullCollection.version4.obj");
//    }

}
