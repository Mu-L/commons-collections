/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;

/**
 * Implements {@link BidiMap} with two {@link LinkedHashMap} instances.
 * <p>
 * Two {@link LinkedHashMap} instances are used in this class.
 * This provides fast lookups at the expense of storing two sets of map entries and two linked lists.
 * </p>
 *
 * @param <K> the type of the keys in the map
 * @param <V> the type of the values in the map
 * @since 4.0
 */
public class DualLinkedHashBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements Serializable {

    /** Ensure serialization compatibility */
    private static final long serialVersionUID = 721969328361810L;

    /**
     * Creates an empty {@code HashBidiMap}.
     */
    public DualLinkedHashBidiMap() {
        super(new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    /**
     * Constructs a {@code LinkedHashBidiMap} and copies the mappings from
     * specified {@link Map}.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public DualLinkedHashBidiMap(final Map<? extends K, ? extends V> map) {
        super(new LinkedHashMap<>(), new LinkedHashMap<>());
        putAll(map);
    }

    /**
     * Constructs a {@code LinkedHashBidiMap} that decorates the specified maps.
     *
     * @param normalMap      the normal direction map
     * @param reverseMap     the reverse direction map
     * @param inverseBidiMap the inverse BidiMap
     */
    protected DualLinkedHashBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap,
                                    final BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }

    /**
     * Creates a new instance of this object.
     *
     * @param normalMap      the normal direction map
     * @param reverseMap     the reverse direction map
     * @param inverseBidiMap the inverse BidiMap
     * @return new bidi map
     */
    @Override
    protected BidiMap<V, K> createBidiMap(final Map<V, K> normalMap, final Map<K, V> reverseMap,
            final BidiMap<K, V> inverseBidiMap) {
        return new DualLinkedHashBidiMap<>(normalMap, reverseMap, inverseBidiMap);
    }

    /**
     * Deserializes an instance from an ObjectInputStream.
     *
     * @param in The source ObjectInputStream.
     * @throws IOException            Any of the usual Input/Output related exceptions.
     * @throws ClassNotFoundException A class of a serialized object cannot be found.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        normalMap = new LinkedHashMap<>();
        reverseMap = new LinkedHashMap<>();
        @SuppressWarnings("unchecked") // will fail at runtime if stream is incorrect
        final Map<K, V> map = (Map<K, V>) in.readObject();
        putAll(map);
    }

    /**
     * Serializes this object to an ObjectOutputStream.
     *
     * @param out the target ObjectOutputStream.
     * @throws IOException thrown when an I/O errors occur writing to the target stream.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(normalMap);
    }
}
