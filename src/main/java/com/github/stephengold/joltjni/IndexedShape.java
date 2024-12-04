package com.github.stephengold.joltjni;

import com.github.stephengold.joltjni.readonly.ConstJoltPhysicsObject;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.template.Ref;
import com.github.stephengold.joltjni.template.RefTarget;

public final class IndexedShape extends JoltPhysicsObject
        implements ConstJoltPhysicsObject, RefTarget {
    // *************************************************************************
    // constructors

    IndexedShape(long indexedShapeVa) {
        setVirtualAddress(indexedShapeVa);
    }
    // *************************************************************************
    // new methods exposed

    public void loadShape(ConstShape shape, int shapeIndex, Vec3 positionCom) {
        loadShape(va(), shape.targetVa(), shapeIndex, positionCom.getX(), positionCom.getY(), positionCom.getZ());
    }

    public void loadShape(ShapeRefC shape, int shapeIndex, Vec3 positionCom) {
        loadShape(va(), shape.va(), shapeIndex, positionCom.getX(), positionCom.getY(), positionCom.getZ());
    }

    @Override
    public int getRefCount() {
        long indexedShapeVa = va();
        int result = getRefCount(indexedShapeVa);

        return result;
    }

    @Override
    public void setEmbedded() {
        long indexedShapeVa = va();
        setEmbedded(indexedShapeVa);
    }

    @Override
    public Ref toRef() {
        long indexedShapeVa = va();
        long refVa = toRef(indexedShapeVa);
        IndexedShapeRef result = new IndexedShapeRef(refVa, true);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static void loadShape(long indexedShapeVa, long shapeVa, int shapeIndex,
                                         float pX, float pY, float pZ);

    native private static int getRefCount(long collectorVa);

    native private static void setEmbedded(long collectorVa);

    native private static long toRef(long collectorVa);
}
