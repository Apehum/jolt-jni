package com.github.stephengold.joltjni;

import com.github.stephengold.joltjni.template.Array;

public class IndexedShapes extends Array<IndexedShape> {
    // *************************************************************************
    // constructors

    IndexedShapes(long vectorVa) {
        setVirtualAddress(vectorVa, null);
    }

    // *************************************************************************
    // Array<Body> methods

    @Override
    public int capacity() {
        long vectorVa = va();
        int result = capacity(vectorVa);

        return result;
    }

    @Override
    public void erase(int startIndex, int stopIndex) {
        long vectorVa = va();
        erase(vectorVa, startIndex, stopIndex);
    }

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

    @Override
    public void resize(int numBodies) {
        long vectorVa = va();
        resize(vectorVa, numBodies);
    }

    @Override
    public void set(int elementIndex, IndexedShape body) {
        long vectorVa = va();
        long indexedShapeVa = body.va();
        setShape(vectorVa, elementIndex, indexedShapeVa);
    }

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

    native private static void setShape(long vectorVa, int elementIndex, long bodyVa);

    native private static int size(long vectorVa);
}
