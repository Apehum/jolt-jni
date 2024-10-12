package com.github.stephengold.joltjni;

public final class RegionShape extends Shape {

    // *************************************************************************
    // constructors

    /**
     * Instantiate a shape with the specified native object assigned but not
     * owned.
     *
     * @param shapeVa the virtual address of the native object to assign (not
     *                zero)
     */
    RegionShape(long shapeVa) {
        super(shapeVa);
    }

    public static void sRegister() {
        _sRegister();
    }

    // *************************************************************************
    // native private methods

    static native private void _sRegister();
}
