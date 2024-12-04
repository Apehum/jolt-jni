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

/**
 * Settings used to construct a {@code RegionShape}.
 *
 * @author Apehum apehum@proton.me
 */
public class RegionShapeSettings extends ShapeSettings {
    // *************************************************************************
    // constructors

    /**
     * Instantiate settings with the specified space collector and half-extents.
     *
     * @param shapeCollector the shape collector used to collect shapes
     *                              for collisions
     * @param halfExtents the half-extents sizes of the region
     */
    public RegionShapeSettings(
            CustomIndexedShapeCollector shapeCollector,
            Vec3 halfExtents) {
        float hx = halfExtents.getX();
        float hy = halfExtents.getY();
        float hz = halfExtents.getZ();
        long settingsVa = createRegionShapeSettings(
                shapeCollector.va(), hx, hy, hz);
        setVirtualAddress(settingsVa, null); // not owner due to ref counting
    }

    // *************************************************************************
    // native private methods

    native private long createRegionShapeSettings(long shapeCollectorVa,
                                                  float hX, float hY, float hZ);
}
