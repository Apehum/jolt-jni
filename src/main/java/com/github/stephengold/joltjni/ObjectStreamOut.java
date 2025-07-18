/*
Copyright (c) 2024-2025 Stephen Gold

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

import com.github.stephengold.joltjni.enumerate.EStreamType;
import com.github.stephengold.joltjni.readonly.ConstBodyCreationSettings;
import com.github.stephengold.joltjni.readonly.ConstConstraintSettings;
import com.github.stephengold.joltjni.readonly.ConstGroupFilter;
import com.github.stephengold.joltjni.readonly.ConstPhysicsMaterial;
import com.github.stephengold.joltjni.readonly.ConstSoftBodyCreationSettings;
import com.github.stephengold.joltjni.readonly.ConstSoftBodySharedSettings;
import com.github.stephengold.joltjni.readonly.ConstVehicleControllerSettings;
import com.github.stephengold.joltjni.std.StringStream;

/**
 * Utility class for writing uncooked Jolt-Physics objects to streams. Data
 * serialized this way is more like to be compatible with future versions of the
 * library than the {@code saveBinaryState()}/{@code restoreBinaryState()}
 * methods.
 *
 * @author Stephen Gold sgold@sonic.net
 * @see com.github.stephengold.joltjni.ObjectStreamIn
 */
final public class ObjectStreamOut {
    // *************************************************************************
    // constructors

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private ObjectStreamOut() {
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Write the specified body-creation settings to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstBodyCreationSettings settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result = sWriteBcs(streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified constraint settings to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstConstraintSettings settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result
                = sWriteConstraintSettings(streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified group filter to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param filter the filter to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstGroupFilter filter) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long filterVa = filter.targetVa();
        boolean result = sWriteGroupFilter(streamVa, ordinal, filterVa);

        return result;
    }

    /**
     * Write the specified material to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param material the material to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstPhysicsMaterial material) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long materialVa = material.targetVa();
        boolean result = sWritePhysicsMaterial(streamVa, ordinal, materialVa);

        return result;
    }

    /**
     * Write the specified soft-body creation settings to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstSoftBodyCreationSettings settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result = sWriteSbcs(streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified soft-body shared settings to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstSoftBodySharedSettings settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result = sWriteSbss(streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified controller settings to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, ConstVehicleControllerSettings settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result = sWriteVehicleControllerSettings(
                streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified scene to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param scene the scene to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, PhysicsScene scene) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long sceneVa = scene.va();
        boolean result = sWritePhysicsScene(streamVa, ordinal, sceneVa);

        return result;
    }

    /**
     * Write the specified ragdoll settings to the specified stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, RagdollSettings settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.va();
        boolean result = sWriteRagdollSettings(streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified tracked-vehicle wheel settings to the specified
     * stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, WheelSettingsTv settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result = sWriteWheelSettingsTv(streamVa, ordinal, settingsVa);

        return result;
    }

    /**
     * Write the specified wheeled-vehicle wheel settings to the specified
     * stream.
     *
     * @param stream the stream to write to (not null)
     * @param streamType the type of stream (not null)
     * @param settings the settings to write (not null, unaffected)
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean sWriteObject(StringStream stream,
            EStreamType streamType, WheelSettingsWv settings) {
        long streamVa = stream.va();
        int ordinal = streamType.ordinal();
        long settingsVa = settings.targetVa();
        boolean result = sWriteWheelSettingsWv(streamVa, ordinal, settingsVa);

        return result;
    }
    // *************************************************************************
    // native private methods

    native private static boolean sWriteBcs(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteConstraintSettings(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteGroupFilter(
            long streamVa, int ordinal, long filterVa);

    native private static boolean sWritePhysicsMaterial(
            long streamVa, int ordinal, long materialVa);

    native private static boolean sWritePhysicsScene(
            long streamVa, int ordinal, long sceneVa);

    native private static boolean sWriteRagdollSettings(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteSbcs(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteSbss(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteVehicleControllerSettings(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteWheelSettingsTv(
            long streamVa, int ordinal, long settingsVa);

    native private static boolean sWriteWheelSettingsWv(
            long streamVa, int ordinal, long settingsVa);
}
