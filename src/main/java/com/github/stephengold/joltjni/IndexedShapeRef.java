package com.github.stephengold.joltjni;

import com.github.stephengold.joltjni.template.Ref;

public class IndexedShapeRef extends Ref {
    // *************************************************************************
    // constructors

    IndexedShapeRef(long refVa, boolean owner) {
        Runnable freeingAction = owner ? () -> free(refVa) : null;
        setVirtualAddress(refVa, freeingAction);
    }
    // *************************************************************************
    // Ref methods

    @Override
    public IndexedShape getPtr() {
        long refVa = va();
        long indexedShapeVa = getPtr(refVa);
        IndexedShape result = new IndexedShape(indexedShapeVa);

        return result;
    }

    @Override
    public IndexedShapeRef toRef() {
        long refVa = va();
        long copyVa = copy(refVa);
        IndexedShapeRef result = new IndexedShapeRef(copyVa, true);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static long copy(long refVa);

    native private static void free(long refVa);

    native private static long getPtr(long refVa);
}
