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
package org.apache.commons.collections4.comparators;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Test the NullComparator.
 */
public abstract class AbstractNullComparatorTest extends AbstractComparatorTest<Integer> {

    /**
     *  Test the NullComparator with nulls high, using comparable comparator
     **/
    public static class TestNullComparator1 extends AbstractNullComparatorTest {

        @Override
        public String getCanonicalComparatorName(final Object object) {
            return super.getCanonicalComparatorName(object) + "1";
        }

        @Override
        public List<Integer> getComparableObjectsOrdered() {
            final List<Integer> list = new LinkedList<>();
            list.add(Integer.valueOf(1));
            list.add(Integer.valueOf(2));
            list.add(Integer.valueOf(3));
            list.add(Integer.valueOf(4));
            list.add(Integer.valueOf(5));
            list.add(null);
            return list;
        }

        @Override
        public String getCompatibilityVersion() {
            return "4";
        }

        @Override
        public Comparator<Integer> makeObject() {
            return new NullComparator<>();
        }

//        void testCreate() throws Exception {
//            writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/NullComparator.version4.obj1");
//        }

    }

    /**
     *  Test the NullComparator with nulls low using the comparable comparator
     **/
    public static class TestNullComparator2 extends AbstractNullComparatorTest {

        @Override
        public String getCanonicalComparatorName(final Object object) {
            return super.getCanonicalComparatorName(object) + "2";
        }

        @Override
        public List<Integer> getComparableObjectsOrdered() {
            final List<Integer> list = new LinkedList<>();
            list.add(null);
            list.add(Integer.valueOf(1));
            list.add(Integer.valueOf(2));
            list.add(Integer.valueOf(3));
            list.add(Integer.valueOf(4));
            list.add(Integer.valueOf(5));
            return list;
        }

        @Override
        public String getCompatibilityVersion() {
            return "4";
        }

        @Override
        public Comparator<Integer> makeObject() {
            return new NullComparator<>(false);
        }

//        void testCreate() throws Exception {
//            writeExternalFormToDisk((java.io.Serializable) makeObject(), "src/test/resources/data/test/NullComparator.version4.obj2");
//        }

    }

}
