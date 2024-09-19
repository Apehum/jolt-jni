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

import com.github.stephengold.joltjni.enumerate.EShapeSubType;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Settings used to construct a {@code MeshShape}.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class MeshShapeSettings extends ShapeSettings {
    // *************************************************************************
    // constructors

    /**
     * Instantiate with the specified native object assigned but not owned.
     *
     * @param settingsVa the virtual address of the native object to assign (not
     * zero)
     */
    MeshShapeSettings(long settingsVa) {
        super(settingsVa);
        setSubType(EShapeSubType.Mesh);
    }

    /**
     * Instantiate settings for the specified list of triangles.
     *
     * @param triangleList the list of triangles (not null, unaffected)
     */
    public MeshShapeSettings(List<Triangle> triangleList) {
        this(triangleList, new PhysicsMaterialList());
    }

    /**
     * Instantiate settings for the specified parameters.
     *
     * @param triangleList the list of triangles (not null, unaffected)
     * @param materials the desired surface properties (not null, unaffected)
     */
    public MeshShapeSettings(
            List<Triangle> triangleList, PhysicsMaterialList materials) {
        int numTriangles = triangleList.size();
        int numVertices = 3 * numTriangles;
        int numFloats = 3 * numVertices;
        FloatBuffer buffer = Jolt.newDirectFloatBuffer(numFloats);
        for (int i = 0; i < numTriangles; ++i) {
            Triangle triangle = triangleList.get(i);
            triangle.putVertices(buffer);
        }
        long materialsVa = materials.va();
        long settingsVa = createSettingsFromTriangles(
                numTriangles, buffer, materialsVa);
        setVirtualAddress(settingsVa, null); // not owner due to ref counting
        setSubType(EShapeSubType.Mesh);
    }

    /**
     * Instantiate settings for the specified vertices and indices.
     *
     * @param vertices list of vertex locations (not null, unaffected)
     * @param indices list of triangles that use those vertices (not null)
     */
    public MeshShapeSettings(VertexList vertices, IndexedTriangleList indices) {
        int numVertices = vertices.size();
        FloatBuffer buffer = vertices.toDirectBuffer();
        long indicesVa = indices.va();
        long settingsVa
                = createMeshShapeSettings(numVertices, buffer, indicesVa);
        setVirtualAddress(settingsVa, null); // not owner due to ref counting
        setSubType(EShapeSubType.Mesh);
    }

    /**
     * Instantiate settings for the specified array of triangles.
     *
     * @param triangleArray the array of triangles (not null, unaffected)
     */
    public MeshShapeSettings(Triangle[] triangleArray) {
        this(triangleArray, new PhysicsMaterialList());
    }

    /**
     * Instantiate settings for the specified parameters.
     *
     * @param triangleArray the array of triangles (not null, unaffected)
     * @param materials the desired surface properties (not null, unaffected)
     */
    public MeshShapeSettings(
            Triangle[] triangleArray, PhysicsMaterialList materials) {
        int numTriangles = triangleArray.length;
        int numVertices = 3 * numTriangles;
        int numFloats = 3 * numVertices;
        FloatBuffer buffer = Jolt.newDirectFloatBuffer(numFloats);
        for (int i = 0; i < numTriangles; ++i) {
            Triangle triangle = triangleArray[i];
            triangle.putVertices(buffer);
        }
        long materialsVa = materials.va();
        long settingsVa = createSettingsFromTriangles(
                numTriangles, buffer, materialsVa);
        setVirtualAddress(settingsVa, null); // not owner due to ref counting
        setSubType(EShapeSubType.Mesh);
    }
    // *************************************************************************
    // new public methods

    /**
     * Count the triangles in the mesh. The settings are unaffected.
     *
     * @return the count (&ge;0)
     */
    public int countTriangles() {
        long settingsVa = va();
        int result = countTriangles(settingsVa);

        return result;
    }

    /**
     * Count the vertices in the mesh. The settings are unaffected.
     *
     * @return the count (&ge;0)
     */
    public int countTriangleVertices() {
        long settingsVa = va();
        int result = countTriangleVertices(settingsVa);

        return result;
    }

    /**
     * Return the cosine of the active-edge threshold angle. The settings are
     * unaffected. (native attribute: mActiveEdgeCosThresholdAngle)
     *
     * @return the cosine
     */
    public float getActiveEdgeCosThresholdAngle() {
        long settingsVa = va();
        float result = getActiveEdgeCosThresholdAngle(settingsVa);

        return result;
    }

    /**
     * Return the maximum number of triangles per leaf. The settings are
     * unaffected. (native attribute: mMaxTrianglesPerLeaf)
     *
     * @return the maximum number
     */
    public int getMaxTrianglesPerLeaf() {
        long settingsVa = va();
        int result = getMaxTrianglesPerLeaf(settingsVa);

        return result;
    }

    /**
     * Test whether each triangle will include user data. The settings are
     * unaffected. (native attribute: mPerTriangleUserData)
     *
     * @return true if per-triangle data is included, otherwise false
     */
    public boolean getPerTriangleUserData() {
        long settingsVa = va();
        boolean result = getPerTriangleUserData(settingsVa);

        return result;
    }

    /**
     * Alter the active-edge threshold angle. (native attribute:
     * mActiveEdgeCosThresholdAngle)
     *
     * @param cosine the cosine of the desired angle (default=0.996195)
     */
    public void setActiveEdgeCosThresholdAngle(float cosine) {
        long settingsVa = va();
        setActiveEdgeCosThresholdAngle(settingsVa, cosine);
    }

    /**
     * Alter the maximum number of triangles per leaf. (native attribute:
     * mMaxTrianglesPerLeaf)
     *
     * @param numTriangles the desired number (default=8)
     */
    public void setMaxTrianglesPerLeaf(int numTriangles) {
        long settingsVa = va();
        setMaxTrianglesPerLeaf(settingsVa, numTriangles);
    }

    /**
     * Alter whether each triangle will include user data. (native attribute:
     * mPerTriangleUserData)
     *
     * @param include true to include per-triangle data, false to omit it
     * (default=false)
     */
    public void setPerTriangleUserData(boolean include) {
        long settingsVa = va();
        setPerTriangleUserData(settingsVa, include);
    }
    // *************************************************************************
    // native private methods

    native private static long createMeshShapeSettings(
            int numVertices, FloatBuffer vertices, long indicesVa);

    native private static long createSettingsFromTriangles(
            int numTriangles, FloatBuffer buffer, long materialsVa);

    native private static int countTriangles(long settingsVa);

    native private static int countTriangleVertices(long settingsVa);

    native private static float getActiveEdgeCosThresholdAngle(long settingsVa);

    native private static int getMaxTrianglesPerLeaf(long settingsVa);

    native private static boolean getPerTriangleUserData(long settingsVa);

    native private static void setActiveEdgeCosThresholdAngle(
            long settingsVa, float cosine);

    native private static void setMaxTrianglesPerLeaf(
            long settingsVa, int numTriangles);

    native private static void setPerTriangleUserData(
            long settingsVa, boolean include);
}
