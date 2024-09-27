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
#include "Jolt/Physics/Constraints/DistanceConstraint.h"
#include "auto/com_github_stephengold_joltjni_DistanceConstraint.h"

using namespace JPH;

/*
 * Class:     com_github_stephengold_joltjni_DistanceConstraint
 * Method:    getLimitsSpringSettings
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_github_stephengold_joltjni_DistanceConstraint_getLimitsSpringSettings
  (JNIEnv *, jclass, jlong constraintVa) {
    DistanceConstraint * const pConstraint
            = reinterpret_cast<DistanceConstraint *> (constraintVa);
    SpringSettings * const pResult = &pConstraint->GetLimitsSpringSettings();
    return reinterpret_cast<jlong> (pResult);
}

/*
 * Class:     com_github_stephengold_joltjni_DistanceConstraint
 * Method:    getMaxDistance
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_com_github_stephengold_joltjni_DistanceConstraint_getMaxDistance
  (JNIEnv *, jclass, jlong constraintVa) {
    const DistanceConstraint * const pConstraint
            = reinterpret_cast<DistanceConstraint *> (constraintVa);
    const float result = pConstraint->GetMaxDistance();
    return result;
}

/*
 * Class:     com_github_stephengold_joltjni_DistanceConstraint
 * Method:    getMinDistance
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_com_github_stephengold_joltjni_DistanceConstraint_getMinDistance
  (JNIEnv *, jclass, jlong constraintVa) {
    const DistanceConstraint * const pConstraint
            = reinterpret_cast<DistanceConstraint *> (constraintVa);
    const float result = pConstraint->GetMinDistance();
    return result;
}

/*
 * Class:     com_github_stephengold_joltjni_DistanceConstraint
 * Method:    setDistance
 * Signature: (JFF)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_DistanceConstraint_setDistance
  (JNIEnv *, jclass, jlong constraintVa, jfloat min, jfloat max) {
    DistanceConstraint * const pConstraint
            = reinterpret_cast<DistanceConstraint *> (constraintVa);
    pConstraint->SetDistance(min, max);
}