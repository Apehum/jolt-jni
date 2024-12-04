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

import com.github.stephengold.joltjni.readonly.ConstJoltPhysicsObject;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.template.Ref;
import com.github.stephengold.joltjni.template.RefTarget;

/**
 * A shape with index used by {@code RegionShape}.
 * Index encodes position in the region:
 * {@code (subShapeIndex & 0x1F << 27) | (z << 18) | (y << 9) or x}
 *
 * @author Apehum apehum@proton.me
 */
public final class IndexedShape extends JoltPhysicsObject
        implements ConstJoltPhysicsObject, RefTarget {
    // *************************************************************************
    // constructors

    IndexedShape(long indexedShapeVa) {
        setVirtualAddress(indexedShapeVa);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Sets the shape.
     *
     * @param shape the shape to set
     * @param shapeIndex the shape index to set
     * @param positionCom the local position of the shape inside RegionShape
     */
    public void loadShape(ConstShape shape, int shapeIndex, Vec3 positionCom) {
        loadShape(va(), shape.targetVa(), shapeIndex,
                positionCom.getX(), positionCom.getY(), positionCom.getZ());
    }

    /**
     * Sets the shape.
     *
     * @param shape the shape to set
     * @param shapeIndex the shape index to set
     * @param positionCom the local position of the shape inside RegionShape
     */
    public void loadShape(ShapeRefC shape, int shapeIndex, Vec3 positionCom) {
        loadShape(va(), shape.va(), shapeIndex,
                positionCom.getX(), positionCom.getY(), positionCom.getZ());
    }

    /**
     * Count the active references to the native {@code IndexedShape}.
     * The indexed shape is unaffected.
     *
     * @return the count (&ge;0)
     */
    @Override
    public int getRefCount() {
        long indexedShapeVa = va();
        int result = getRefCount(indexedShapeVa);

        return result;
    }

    /**
     * Mark the native {@code IndexedShape} as embedded.
     */
    @Override
    public void setEmbedded() {
        long indexedShapeVa = va();
        setEmbedded(indexedShapeVa);
    }

    /**
     * Create a counted reference to the native {@code IndexedShape}.
     *
     * @return a new JVM object with a new native object assigned
     */
    @Override
    public Ref toRef() {
        long indexedShapeVa = va();
        long refVa = toRef(indexedShapeVa);
        IndexedShapeRef result = new IndexedShapeRef(refVa, true);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static void loadShape(
            long indexedShapeVa, long shapeVa, int shapeIndex,
            float pX, float pY, float pZ);

    native private static int getRefCount(long collectorVa);

    native private static void setEmbedded(long collectorVa);

    native private static long toRef(long collectorVa);
}
