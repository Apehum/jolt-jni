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
 * An implementation of {@code ObjectLayerPairFilter} that can be configured at
 * runtime.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class ObjVsObjFilter extends ObjectLayerPairFilter {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a filter with all interactions enabled.
     *
     * @param numObjectLayers the number of object layers
     */
    public ObjVsObjFilter(int numObjectLayers) {
        long filterVa = createObjVsObjFilter(numObjectLayers);
        setVirtualAddress(filterVa, () -> free(filterVa));
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Disable interactions between the specified layers.
     *
     * @param layer1 the index of the first object layer (&lt; numObjectLayers)
     * @param layer2 the index of the 2nd object layer (&lt; numObjectLayers)
     * @return the modified filter (for chaining)
     */
    public ObjVsObjFilter disablePair(int layer1, int layer2) {
        disablePair(va(), layer1, layer2);
        return this;
    }
    // *************************************************************************
    // native private methods

    native private static long createObjVsObjFilter(int numObjectLayers);

    native private static void disablePair(
            long filterVa, int layer1, int layer2);

    native private static void free(long filterVa);
}
