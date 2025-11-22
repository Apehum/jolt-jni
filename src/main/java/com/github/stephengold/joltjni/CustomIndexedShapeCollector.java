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
 * A customizable {@code IndexedShapeCollector}.
 *
 * @author Apehum apehum@proton.me
 */
abstract public class CustomIndexedShapeCollector
        extends IndexedShapeCollector {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a customizable collector.
     */
    public CustomIndexedShapeCollector() {
        long collectorVa = createCustomCollector();
        setVirtualAddress(collectorVa, true);
    }
    // *************************************************************************
    // new methods exposed

    private void collectAt(long boxVa, long shapesVa) {
        collectAt(new AaBox(boxVa, false), new IndexedShapes(shapesVa));
    }

    private boolean castRay(long rayVa, long shapeVa) {
        return castRay(new RayCast(rayVa), new IndexedShape(shapeVa));
    }

    private void collectCastRay(long rayVa, long shapesVa) {
        collectCastRay(new RayCast(rayVa), new IndexedShapes(shapesVa));
    }

    private void getShapeAt(int shapeIndex, long shapeVa) {
        getShapeAt(shapeIndex, new IndexedShape(shapeVa));
    }

    abstract public void collectAt(AaBox box, IndexedShapes shapes);

    abstract public boolean castRay(RayCast ray, IndexedShape shape);

    abstract public void collectCastRay(RayCast ray, IndexedShapes shapes);

    abstract public void getShapeAt(int shapeIndex, IndexedShape shape);
    // *************************************************************************
    // native private methods

    native private long createCustomCollector();

}
