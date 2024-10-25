package com.github.stephengold.joltjni;

public class RegionShapeSettings extends ShapeSettings {
    // *************************************************************************
    // constructors

    public RegionShapeSettings(CustomIndexedShapeCollector indexedShapeCollector, Vec3 halfExtents) {
        float hx = halfExtents.getX();
        float hy = halfExtents.getY();
        float hz = halfExtents.getZ();
        long settingsVa = createRegionShapeSettings(indexedShapeCollector.va(), hx, hy, hz);
        setVirtualAddress(settingsVa, null); // not owner due to ref counting
    }

    // *************************************************************************
    // native private methods

    native private long createRegionShapeSettings(long indexedShapeCollectorVa, float hX, float hY, float hZ);
}
