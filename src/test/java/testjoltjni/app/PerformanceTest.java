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
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.*;
import java.io.*;
import java.util.*;
import testjoltjni.TestUtils;

public class PerformanceTest {
    private static DebugRendererRecorder renderer;
    final private static String endl = System.lineSeparator();

// Time step for physics
final static float cDeltaTime = 1.0f / 60.0f;

    private static void Trace(String format, Object... args) {
        System.out.printf(format, args);
        System.out.println();
        System.out.flush();
    }

// Program entry point
public static void main(String[] argv) throws IOException
{
	TestUtils.loadNativeLibrary();
	// Install callbacks
	Jolt.installDefaultTraceCallback();

	// Register allocation hook
	Jolt.registerDefaultAllocator();

	// Parse command line parameters
	int specified_quality = -1;
	int specified_threads = -1;
	int max_iterations = 500;
	boolean disable_sleep = false;
	boolean enable_profiler = false;
	boolean enable_debug_renderer = false;
	boolean enable_per_frame_recording = false;
	boolean record_state = false;
	boolean validate_state = false;
	PerformanceTestScene scene = null;
	String validate_hash = null;
	int repeat = 1;
	for (int argidx = 1; argidx < argv.length; ++argidx)
	{
		String arg = argv[argidx];

		if (arg.startsWith("-s="))
		{
			// Parse scene
			if (arg.substring(3).equals("ConvexVsMesh"))
				scene = new ConvexVsMeshScene();
			else if (arg.substring(3).equals("Pyramid"))
				scene = new PyramidScene();
			else
			{
				Trace("Invalid scene");
				System.exit(1);
			}
		}
		else if (arg.startsWith("-i="))
		{
			// Parse max iterations
			max_iterations = Integer.parseInt(arg.substring(3));
		}
		else if (arg.startsWith("-q="))
		{
			// Parse quality
			if (arg.substring(3).equals("Discrete"))
				specified_quality = 0;
			else if (arg.substring(3).equals("LinearCast"))
				specified_quality = 1;
			else
			{
				Trace("Invalid quality");
				System.exit(1);
			}
		}
		else if (arg.startsWith("-t=max"))
		{
			// Default to number of threads on the system
			specified_threads = TestUtils.numThreads();
		}
		else if (arg.startsWith("-t="))
		{
			// Parse threads
			specified_threads = Integer.parseInt(arg.substring(3));
		}
		else if (arg.equals("-no_sleep"))
		{
			disable_sleep = true;
		}
		else if (arg.equals("-p"))
		{
			enable_profiler = true;
		}
		else if (arg.equals("-r"))
		{
			enable_debug_renderer = true;
		}
		else if (arg.equals("-f"))
		{
			enable_per_frame_recording = true;
		}
		else if (arg.equals("-rs"))
		{
			record_state = true;
		}
		else if (arg.equals("-vs"))
		{
			validate_state = true;
		}
		else if (arg.startsWith("-validate_hash="))
		{
			validate_hash = arg.substring(15);
		}
		else if (arg.startsWith("-repeat="))
		{
			// Parse repeat count
			repeat = Integer.parseInt(arg.substring(8));
		}
		else if (arg.equals("-h"))
		{
			// Print usage
			Trace("Usage:\n"
				 + "-s=<scene>: Select scene (ConvexVsMesh, Pyramid)\n"
				 + "-i=<num physics steps>: Number of physics steps to simulate (default 500)\n"
				 + "-q=<quality>: Test only with specified quality (Discrete, LinearCast)\n"
				 + "-t=<num threads>: Test only with N threads (default is to iterate over 1 .. num hardware threads)\n"
				 + "-t=max: Test with the number of threads available on the system\n"
				 + "-p: Write out profiles\n"
				 + "-r: Record debug renderer output for JoltViewer\n"
				 + "-f: Record per frame timings\n"
				 + "-no_sleep: Disable sleeping\n"
				 + "-rs: Record state\n"
				 + "-vs: Validate state\n"
				 + "-validate_hash=<hash>: Validate hash (return 0 if successful, 1 if failed)\n"
				 + "-repeat=<num>: Repeat all tests <num> times");
			System.exit(0);
		}
	}

	// Create a factory
	Jolt.newFactory();

	// Register all Jolt physics types
	Jolt.registerTypes();

	// Create temp allocator
	TempAllocatorImpl temp_allocator = new TempAllocatorImpl(32 * 1024 * 1024);

	// Load the scene
	if (scene == null || !scene.Load())
		System.exit(1);

	// Show used instruction sets
	Trace(Jolt.getConfigurationString());

	// Output scene we're running
	Trace("Running scene: %s", scene.GetName());

	// Create mapping table from object layer to broadphase layer
	MapObj2Bp broad_phase_layer_interface = new MapObj2Bp(Layers.OBJ_NUM_LAYERS, Layers.BP_NUM_LAYERS)
	.add(Layers.OBJ_NON_MOVING, Layers.BP_NON_MOVING)
	.add(Layers.OBJ_MOVING, Layers.BP_MOVING);

	// Create class that filters object vs broadphase layers
	ObjVsBpFilter object_vs_broadphase_layer_filter = new ObjVsBpFilter(Layers.OBJ_NUM_LAYERS, Layers.BP_NUM_LAYERS)
	.disablePair(Layers.OBJ_NON_MOVING, Layers.BP_NON_MOVING);

	// Create class that filters object vs object layers
	ObjVsObjFilter object_vs_object_layer_filter = new ObjVsObjFilter(Layers.OBJ_NUM_LAYERS)
	.disablePair(Layers.OBJ_NON_MOVING, Layers.OBJ_NON_MOVING);

	// Start profiling this program
	Jolt.profileStart("Main");

	// Trace header
	Trace("Motion Quality, Thread Count, Steps / Second, Hash");

	// Repeat test
	for (int r = 0; r < repeat; ++r)
	{
		// Iterate motion qualities
		for (int mq = 0; mq < 2; ++mq)
		{
			// Skip quality if another was specified
			if (specified_quality != -1 && mq != (int)specified_quality)
				continue;

			// Determine motion quality
			EMotionQuality motion_quality = mq == 0? EMotionQuality.Discrete : EMotionQuality.LinearCast;
			String motion_quality_str = mq == 0? "Discrete" : "LinearCast";

			// Determine which thread counts to test
			List<Integer> thread_permutations = new ArrayList<>();
			if (specified_threads > 0)
				thread_permutations.add(specified_threads - 1);
			else
				for (int num_threads = 0; num_threads <= TestUtils.numThreads(); ++num_threads)
					thread_permutations.add(num_threads);

			// Test thread permutations
			for (int num_threads : thread_permutations)
			{
				// Create job system with desired number of threads
				JobSystemThreadPool job_system = new JobSystemThreadPool(Jolt.cMaxPhysicsJobs, Jolt.cMaxPhysicsBarriers, num_threads);

				// Create physics system
				PhysicsSystem physics_system = new PhysicsSystem();
				physics_system.init(10240, 0, 65536, 20480, broad_phase_layer_interface, object_vs_broadphase_layer_filter, object_vs_object_layer_filter);

				// Start test scene
				scene.StartTest(physics_system, motion_quality);

				// Disable sleeping if requested
				if (disable_sleep)
				{
					final BodyLockInterface bli = physics_system.getBodyLockInterfaceNoLock();
					BodyIdVector body_ids = new BodyIdVector();
					physics_system.getBodies(body_ids);
					for (int i = 0; i < body_ids.size(); ++i)
					{
						BodyId id = body_ids.get(i);
						BodyLockWrite lock = new BodyLockWrite(bli, id);
						if (lock.succeeded())
						{
							Body body = lock.getBody();
							if (!body.isStatic())
								body.setAllowSleeping(false);
						}
					}
				}

				// Optimize the broadphase to prevent an expensive first frame
				physics_system.optimizeBroadPhase();

				// A tag used to identify the test
				String tag = motion_quality_str.toLowerCase() + "_th" + (num_threads + 1);

				// Open renderer output
				if (enable_debug_renderer) {
					String buildType = Jolt.buildType();
					assert buildType.equals("Debug") : buildType;
					String fileName = "performance_test_" + tag + ".jor";
                                	int mode = StreamOutWrapper.out() | StreamOutWrapper.binary() | StreamOutWrapper.trunc();
					StreamOutWrapper renderer_stream = new StreamOutWrapper(fileName, mode);
					renderer = new DebugRendererRecorder(renderer_stream);
				}

				// Open per frame timing output
				Writer per_frame_file = null;
				if (enable_per_frame_recording)
				{
					per_frame_file = new BufferedWriter(new FileWriter(("per_frame_" + tag + ".csv"), false));
					per_frame_file.append("Frame, Time (ms)" + endl);
				}

				DataOutputStream record_state_file = null;
				DataInputStream validate_state_file = null;
				if (record_state)
					record_state_file = new DataOutputStream(new FileOutputStream("state_" + motion_quality_str.toLowerCase() + ".bin", false));
				else if (validate_state)
					validate_state_file = new DataInputStream(new FileInputStream("state_" + motion_quality_str.toLowerCase() + ".bin"));

				long total_duration = 0;

				// Step the world for a fixed amount of iterations
				for (int iterations = 0; iterations < max_iterations; ++iterations)
				{
					Jolt.profileNextFrame();
					Jolt.detLog("Iteration: " + iterations);

					// Start measuring
					long clock_start = System.nanoTime();

					// Do a physics step
					physics_system.update(cDeltaTime, 1, temp_allocator, job_system);

					// Stop measuring
					long clock_end = System.nanoTime();
					long duration = clock_end - clock_start;
					total_duration += duration;

					if (enable_debug_renderer)
					{
						// Draw the state of the world
						DrawSettings settings = new DrawSettings();
						physics_system.drawBodies(settings, renderer);

						// Mark end of frame
						renderer.endFrame();
					}

					// Record time taken this iteration
					if (enable_per_frame_recording)
						per_frame_file.append(iterations + ", " + (1.0e-6 * duration) + endl);

					// Dump profile information every 100 iterations
					if (enable_profiler && iterations % 100 == 0)
					{
						Jolt.profileDump(tag + "_it" + (iterations));
					}

					if (record_state)
					{
						// Record state
						StateRecorderImpl recorder = new StateRecorderImpl();
						physics_system.saveState(recorder);

						// Write to file
						byte[] data = recorder.getData();
						int size = data.length;
						record_state_file.writeInt(size);
						record_state_file.write(data);
					}
					else if (validate_state)
					{
						// Read state
						int size = validate_state_file.readInt();
						byte[] data = new byte[size];
                                                validate_state_file.readFully(data);

						// Copy to validator
						StateRecorderImpl validator = new StateRecorderImpl();
						validator.writeBytes(data, size);

						// Validate state
						validator.setValidating(true);
						physics_system.restoreState(validator);
					}

					final BodyLockInterface bli = physics_system.getBodyLockInterfaceNoLock();
					BodyIdVector body_ids = new BodyIdVector();
					physics_system.getBodies(body_ids);
					for (int i = 0; i < body_ids.size(); ++i)
					{
						BodyId id = body_ids.get(i);
						BodyLockRead lock = new BodyLockRead(bli, id);
						final Body body = lock.getBody();
						if (!body.isStatic())
							Jolt.detLog(id + ": p: " + body.getPosition() + " r: " + body.getRotation() + " v: " + body.getLinearVelocity() + " w: " + body.getAngularVelocity());
					}
				}

				// Calculate hash of all positions and rotations of the bodies
				long hash = Jolt.hashBytes(0L, 0); // Ensure we start with the proper seed
				BodyInterface bi = physics_system.getBodyInterfaceNoLock();
				BodyIdVector body_ids = new BodyIdVector();
				physics_system.getBodies(body_ids);
				for (int i = 0; i < body_ids.size(); ++i)
				{
					BodyId id = body_ids.get(i);
					RVec3 pos = bi.getPosition(id);
					hash = Jolt.hashBytes(pos, hash);
					Quat rot = bi.getRotation(id);
					hash = Jolt.hashBytes(rot, hash);
				}

				// Convert hash to string
				String hash_str = String.format("0x%x", hash);

				// Stop test scene
				scene.StopTest(physics_system);

				// Trace stat line
				Trace("%s, %d, %f, %s", motion_quality_str, num_threads + 1, ((double)max_iterations) / (1.0e-9 * total_duration), hash_str);

				// Check hash code
				if (validate_hash != null && !hash_str.equals(validate_hash))
				{
					Trace("Fail hash validation. Was: %s, expected: %s", hash_str, validate_hash);
					System.exit(1);
				}
			}
		}
	}

	NarrowPhaseStat.sReportStats();

	// Unregisters all types with the factory and cleans up the default material
	Jolt.unregisterTypes();

	// Destroy the factory
	Jolt.destroyFactory();

	// End profiling this program
	Jolt.profileEnd();
}
}