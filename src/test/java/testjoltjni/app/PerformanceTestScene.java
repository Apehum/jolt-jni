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
package testjoltjni.app;

import com.github.stephengold.joltjni.PhysicsSystem;
import com.github.stephengold.joltjni.enumerate.EMotionQuality;

/**
 * Interface to a test scene to test performance.
 *
 * Derived from PerformanceTest/PerformanceTestScene.h by Jorrit Rouwe.
 *
 * @author sgold
 */
interface PerformanceTestScene {
	// Get name of test for debug purposes
 	String GetName();

	// Load assets for the scene
	boolean Load();

	// Start a new test by adding objects to inPhysicsSystem
	void StartTest(PhysicsSystem inPhysicsSystem, EMotionQuality inMotionQuality);

	// Stop a test and remove objects from inPhysicsSystem
	void StopTest(PhysicsSystem inPhysicsSystem);
};