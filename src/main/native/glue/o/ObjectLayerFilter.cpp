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

/*
 * Author: Stephen Gold
 */
#include "Jolt/Jolt.h"
#include "Jolt/Physics/Collision/ObjectLayer.h"
#include "auto/com_github_stephengold_joltjni_ObjectLayerFilter.h"
#include "glue/glue.h"

using namespace JPH;

/*
 * Class:     com_github_stephengold_joltjni_ObjectLayerFilter
 * Method:    createDefaultFilter
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_github_stephengold_joltjni_ObjectLayerFilter_createDefaultFilter
  (JNIEnv *, jclass) {
    ObjectLayerFilter * const pResult = new ObjectLayerFilter();
    TRACE_NEW("ObjectLayerFilter", pResult)
    return reinterpret_cast<jlong> (pResult);
}

/*
 * Class:     com_github_stephengold_joltjni_ObjectLayerFilter
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_ObjectLayerFilter_free
  (JNIEnv *, jclass, jlong filterVa) {
    ObjectLayerFilter * const pFilter
            = reinterpret_cast<ObjectLayerFilter *> (filterVa);
    TRACE_DELETE("ObjectLayerFilter", pFilter)
    delete pFilter;
}

/*
 * Class:     com_github_stephengold_joltjni_ObjectLayerFilter
 * Method:    shouldCollide
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_github_stephengold_joltjni_ObjectLayerFilter_shouldCollide
  (JNIEnv *, jclass, jlong filterVa, jint objLayerIndex) {
    const ObjectLayerFilter * const pFilter
            = reinterpret_cast<ObjectLayerFilter *> (filterVa);
    const bool result = pFilter->ShouldCollide(objLayerIndex);
    return result;
}