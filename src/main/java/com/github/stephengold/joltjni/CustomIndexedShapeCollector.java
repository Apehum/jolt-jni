package com.github.stephengold.joltjni;

abstract public class CustomIndexedShapeCollector extends IndexedShapeCollector {
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

    public abstract void collectAt(AaBox box, IndexedShapes shapes);

    public abstract boolean castRay(RayCast ray, IndexedShape shape);

    public abstract void collectCastRay(RayCast ray, IndexedShapes shapes);

    public abstract void getShapeAt(int shapeIndex, IndexedShape shape);
    // *************************************************************************
    // native private methods

    native private long createCustomCollector();

}
