/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package net.sf.xmlunit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A couple of (functional) sequence processing constructs.
 */
public final class Linqy {
    /**
     * Turns the iterable into a list.
     */
    public static <E> List<E> asList(Iterable<E> i) {
        ArrayList<E> a = new ArrayList<E>();
        for (E e : i) {
            a.add(e);
        }
        return a;
    }

    /**
     * Create a new iterable by applying a mapper function to each
     * element of a given sequence.
     */
    public static <F, T> Iterable<T> map(final Iterable<F> from,
                                         final Mapper<? super F, T> mapper) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new MappingIterator<F, T>(from.iterator(), mapper);
            }
        };
    }

    /**
     * A function mapping from one type to another.
     */
    public interface Mapper<F, T> {
        T map(F from);
    }

    private static class MappingIterator<F, T> implements Iterator<T> {
        private final Iterator<F> i;
        private final Mapper<? super F, T> mapper;
        private MappingIterator(Iterator<F> i, Mapper<? super F, T> mapper) {
            this.i = i;
            this.mapper = mapper;
        }
        public void remove() {
            i.remove();
        }
        public T next() {
            return mapper.map(i.next());
        }
        public boolean hasNext() {
            return i.hasNext();
        }
    }
}
