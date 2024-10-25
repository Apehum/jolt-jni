package com.github.stephengold.joltjni;

abstract class IndexedShapeCollector extends JoltPhysicsObject {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a collector with no native object assigned.
     */
    IndexedShapeCollector() {
    }
    // *************************************************************************
    // new protected methods

    /**
     * Assign a native object, assuming there's none already assigned.
     *
     * @param collectorVa the virtual address of the native object to assign
     * (not zero)
     * @param owner true &rarr; make the JVM object the owner, false &rarr; it
     * isn't the owner
     */
    void setVirtualAddress(long collectorVa, boolean owner) {
        Runnable freeingAction = owner ? () -> free(collectorVa) : null;
        setVirtualAddress(collectorVa, freeingAction);
    }
    // *************************************************************************
    // native private methods

    native private static void free(long collectorVa);
}
