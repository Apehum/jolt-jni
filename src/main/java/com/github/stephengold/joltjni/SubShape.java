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

import com.github.stephengold.joltjni.readonly.ConstShape;

/**
 * An element of a compound shape. (native type:
 * {@code CompoundShape::SubShape})
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class SubShape extends JoltPhysicsObject {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a subshape with the specified native object assigned but not
     * owned.
     *
     * @param subshapeVa the virtual address of the native object to assign (not
     * zero)
     */
    SubShape(long subshapeVa) {
        setVirtualAddress(subshapeVa, null);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Get the shape.
     *
     * @return a reference to the shape
     */
    public ConstShape getShape() {
        long subShapeVa = va();
        long shapeSettingsVa = getShape(subShapeVa);
        ConstShape result = Shape.newShape(shapeSettingsVa);

        return result;
    }

    /**
     * Copy the center of mass position. The subshape is unaffected.
     *
     * @return a new position
     */
    public Vec3 getPositionCOM() {
        long subShapeVa = va();
        float x = getPositionX(subShapeVa);
        float y = getPositionY(subShapeVa);
        float z = getPositionZ(subShapeVa);
        Vec3 result = new Vec3(x, y, z);

        return result;
    }

    /**
     * Copy the rotation. The subshape is unaffected.
     *
     * @return a new quaternion
     */
    public Quat getRotation() {
        long subShapeVa = va();
        float w = getRotationW(subShapeVa);
        float x = getRotationX(subShapeVa);
        float y = getRotationY(subShapeVa);
        float z = getRotationZ(subShapeVa);
        Quat result = new Quat(x, y, z, w);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static long getShape(long subShapeVa);

    native private static float getPositionX(long subShapeVa);

    native private static float getPositionY(long subShapeVa);

    native private static float getPositionZ(long subShapeVa);

    native private static float getRotationW(long subShapeVa);

    native private static float getRotationX(long subShapeVa);

    native private static float getRotationY(long subShapeVa);

    native private static float getRotationZ(long subShapeVa);
}
