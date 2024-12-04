/*
Copyright (c) 2024 Stephen Gold

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.github.stephengold.joltjni;

import com.github.stephengold.joltjni.template.Array;

/**
 * An array of {@code IndexedShape}.
 *
 * @author Apehum apehum@proton.me
 */
public class IndexedShapes extends Array<IndexedShape> {
    // *************************************************************************
    // constructors

    IndexedShapes(long vectorVa) {
        setVirtualAddress(vectorVa, null);
    }

    // *************************************************************************
    // Array<Body> methods

    /**
     * Count how many indexed shapes the currently allocated storage can hold.
     * The vector is unaffected.
     *
     * @return the number of bodies (&ge;size)
     */
    @Override
    public int capacity() {
        long vectorVa = va();
        int result = capacity(vectorVa);

        return result;
    }

    /**
     * Remove all indexed shapes in the specified range of indices.
     *
     * @param startIndex the index of the first element to remove (&ge;0)
     * @param stopIndex one plus the index of the last element to remove (&ge;0)
     */
    @Override
    public void erase(int startIndex, int stopIndex) {
        long vectorVa = va();
        erase(vectorVa, startIndex, stopIndex);
    }

    /**
     * Access the indexed shape at the specified index.
     *
     * @param elementIndex the index from which to get the shape (&ge;0)
     * @return a new JVM object with the pre-existing native object assigned, or
     * else {@code null}
     */
    @Override
    public IndexedShape get(int elementIndex) {
        long vectorVa = va();
        long indexedShapeVa = getShape(vectorVa, elementIndex);
        IndexedShape result;
        if (indexedShapeVa == 0L) {
            result = null;
        } else {
            result = new IndexedShape(indexedShapeVa);
        }

        return result;
    }

    /**
     * Expand or truncate the vector.
     *
     * @param numBodies the desired size (number of shapes, &ge;0)
     */
    @Override
    public void resize(int numBodies) {
        long vectorVa = va();
        resize(vectorVa, numBodies);
    }

    /**
     * Put the specified indexed shape at the specified index.
     *
     * @param elementIndex the index at which to put the shape (&ge;0)
     * @param shape the shape to put (not null)
     */
    @Override
    public void set(int elementIndex, IndexedShape shape) {
        long vectorVa = va();
        long indexedShapeVa = shape.va();
        setShape(vectorVa, elementIndex, indexedShapeVa);
    }

    /**
     * Count how many indexed shapes are in the vector.
     * The vector is unaffected.
     *
     * @return the number of shapes (&ge;0, &le;capacity)
     */
    @Override
    public int size() {
        long vectorVa = va();
        int result = size(vectorVa);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static int capacity(long vectorVa);

    native private static long getShape(long vectorVa, int elementIndex);

    native private static void resize(long vectorVa, int numBodies);

    native private static void erase(
            long vectorVa, int startIndex, int stopIndex);

    native private static void setShape(
            long vectorVa, int elementIndex, long bodyVa);

    native private static int size(long vectorVa);
}
