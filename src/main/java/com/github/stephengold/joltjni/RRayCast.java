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

import com.github.stephengold.joltjni.readonly.RVec3Arg;
import com.github.stephengold.joltjni.readonly.Vec3Arg;

/**
 * A ray cast with an {@code RVec3} start location.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class RRayCast extends JoltPhysicsObject {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a ray cast with the specified endpoints.
     *
     * @param startLocation the desired start location (not null, unaffected)
     * @param offset the desired end offset from the start (not null,
     * unaffected)
     */
    public RRayCast(RVec3Arg startLocation, Vec3Arg offset) {
        double xx = startLocation.xx();
        double yy = startLocation.yy();
        double zz = startLocation.zz();
        float dx = offset.getX();
        float dy = offset.getY();
        float dz = offset.getZ();
        long raycastVa = createRRayCast(xx, yy, zz, dx, dy, dz);
        setVirtualAddress(raycastVa, () -> free(raycastVa));
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Copy the end offset. The ray cast is unaffected. (native attribute:
     * mDirection)
     *
     * @return a new offset vector (relative to the start location)
     */
    public Vec3 getDirection() {
        long raycastVa = va();
        float x = getDirectionX(raycastVa);
        float y = getDirectionY(raycastVa);
        float z = getDirectionZ(raycastVa);
        Vec3 result = new Vec3(x, y, z);

        return result;
    }

    /**
     * Copy the start location. The ray cast is unaffected. (native attribute:
     * mOrigin)
     *
     * @return a new location vector
     */
    public RVec3 getOrigin() {
        long raycastVa = va();
        double x = getOriginX(raycastVa);
        double y = getOriginY(raycastVa);
        double z = getOriginZ(raycastVa);
        RVec3 result = new RVec3(x, y, z);

        return result;
    }

    /**
     * Return a point on the ray with the specified fraction. The ray cast is
     * unaffected.
     *
     * @param fraction (0&rarr;start of ray, 1&rarr;end of ray)
     * @return a new location vector
     */
    public RVec3 getPointOnRay(float fraction) {
        long raycastVa = va();
        double xx = getPointOnRayX(raycastVa, fraction);
        double yy = getPointOnRayY(raycastVa, fraction);
        double zz = getPointOnRayZ(raycastVa, fraction);
        RVec3 result = new RVec3(xx, yy, zz);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static long createRRayCast(
            double xx, double yy, double zz, float dx, float dy, float dz);

    native private static void free(long raycastVa);

    native private static float getDirectionX(long raycastVa);

    native private static float getDirectionY(long raycastVa);

    native private static float getDirectionZ(long raycastVa);

    native private static double getOriginX(long raycastVa);

    native private static double getOriginY(long raycastVa);

    native private static double getOriginZ(long raycastVa);

    native private static double getPointOnRayX(long raycastVa, float fraction);

    native private static double getPointOnRayY(long raycastVa, float fraction);

    native private static double getPointOnRayZ(long raycastVa, float fraction);
}
