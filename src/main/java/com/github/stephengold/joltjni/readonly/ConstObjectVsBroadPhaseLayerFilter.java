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
package com.github.stephengold.joltjni.readonly;

/**
 * Read-only access to an {@code ObjectVsBroadPhaseLayerFilter}. (native type:
 * const ObjectVsBroadPhaseLayerFilter)
 *
 * @author Stephen Gold sgold@sonic.net
 */
public interface ConstObjectVsBroadPhaseLayerFilter
        extends ConstJoltPhysicsObject {
    // *************************************************************************
    // new methods exposed

    /**
     * Test whether the specified layers should collide. The filter is
     * unaffected.
     *
     * @param objLayer the index of an object layer (&ge;0, &lt;numObjectLayers)
     * @param bpLayer the index of a broad-phase layer (&ge;0, &lt;numBpLayers)
     * @return {@code true} if they should collide, otherwise {@code false}
     */
    boolean shouldCollide(int objLayer, int bpLayer);
}
