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
package org.apache.commons.collections4.bag;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractCollectionTest} for exercising the
 * {@link UnmodifiableBag} implementation.
 */
public class UnmodifiableBagTest<E> extends AbstractBagTest<E> {

    @Override
    public Bag<E> getCollection() {
        return super.getCollection();
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    @Override
    protected int getIterationBehaviour() {
        return UNORDERED;
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public Bag<E> makeFullCollection() {
        final Bag<E> bag = new HashBag<>();
        bag.addAll(Arrays.asList(getFullElements()));
        return UnmodifiableBag.unmodifiableBag(bag);
    }

    @Override
    public Bag<E> makeObject() {
        return UnmodifiableBag.unmodifiableBag(new HashBag<>());
    }

    @Test
    void testDecorateFactory() {
        final Bag<E> queue = makeFullCollection();
        assertSame(queue, UnmodifiableBag.unmodifiableBag(queue));

        assertThrows(NullPointerException.class, () -> UnmodifiableBag.unmodifiableBag(null));
    }

    @Test
    void testUnmodifiable() {
        assertTrue(makeObject() instanceof Unmodifiable);
        assertTrue(makeFullCollection() instanceof Unmodifiable);
    }

//    void testCreate() throws Exception {
//        Bag<E> bag = makeObject();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/UnmodifiableBag.emptyCollection.version4.obj");
//        bag = makeFullCollection();
//        writeExternalFormToDisk((java.io.Serializable) bag, "src/test/resources/data/test/UnmodifiableBag.fullCollection.version4.obj");
//    }

}
