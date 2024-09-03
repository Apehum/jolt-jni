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
 * A {@code DebugRenderer} that records events for future playback.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class DebugRendererRecorder extends DebugRenderer {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a recorder that uses the specified stream for output.
     *
     * @param stream the output stream to use (not null)
     */
    public DebugRendererRecorder(StreamOut stream) {
        long streamVa = stream.va();
        long recorderVa = createDebugRendererRecorder(streamVa);
        setVirtualAddress(recorderVa, true);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Signify the end of a frame.
     */
    public void endFrame() {
        long recorderVa = va();
        endFrame(recorderVa);
    }
    // *************************************************************************
    // native private methods

    native private static long createDebugRendererRecorder(long streamVa);

    native private static void endFrame(long recorderVa);
}