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
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.collections4.trie.UnmodifiableTrie;
import org.junit.jupiter.api.Test;

/**
 * Tests for TrieUtils factory methods.
 */
class TrieUtilsTest {

    @Test
    void testUnmodifiableTrie() {
        final Trie<String, Object> trie = TrieUtils.unmodifiableTrie(new PatriciaTrie<>());
        assertInstanceOf(UnmodifiableTrie.class, trie, "Returned object should be an UnmodifiableTrie.");

        assertThrows(NullPointerException.class, () -> TrieUtils.unmodifiableTrie(null));

        assertSame(trie, TrieUtils.unmodifiableTrie(trie), "UnmodifiableTrie shall not be decorated");
    }

}

