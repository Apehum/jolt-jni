#include "Jolt/Jolt.h"
#include "auto/com_github_stephengold_joltjni_IndexedShape.h"
#include "auto/com_github_stephengold_joltjni_IndexedShapes.h"
#include "auto/com_github_stephengold_joltjni_IndexedShapeRef.h"
#include "auto/com_github_stephengold_joltjni_IndexedShapeCollector.h"
#include "auto/com_github_stephengold_joltjni_RegionShape.h"
#include "auto/com_github_stephengold_joltjni_RegionShapeSettings.h"
#include "auto/com_github_stephengold_joltjni_CustomIndexedShapeCollector.h"
#include "auto/com_github_stephengold_joltjni_IndexedShapeCollector.h"
#include "glue/glue.h"
#include "custom/RegionShape.h"

using namespace JPH;

IMPLEMENT_REF(IndexedShape,
  Java_com_github_stephengold_joltjni_IndexedShapeRef_copy,
  Java_com_github_stephengold_joltjni_IndexedShapeRef_createEmpty,
  Java_com_github_stephengold_joltjni_IndexedShapeRef_free,
  Java_com_github_stephengold_joltjni_IndexedShapeRef_getPtr)

/*
 * Class:     com_github_stephengold_joltjni_IndexedShape
 * Method:    loadShape
 * Signature: (JJIFFF)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_IndexedShape_loadShape
  (JNIEnv *, jclass, jlong indexedShapeVa, jlong shapeVa, jint shapeIndex,
  jfloat pX, jfloat pY, jfloat pZ) {
    IndexedShape * const pIndexedShape = reinterpret_cast<IndexedShape *> (indexedShapeVa);
    const Shape * const pShape = reinterpret_cast<Shape *> (shapeVa);
    Vec3 position = Vec3(pX, pY, pZ);
    pIndexedShape->LoadShape(pShape, shapeIndex, position);
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapes
 * Method:    capacity
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_stephengold_joltjni_IndexedShapes_capacity
  (JNIEnv *, jclass, jlong vectorVa) {
    const IndexedShapes * const pVector
            = reinterpret_cast<IndexedShapes *> (vectorVa);
    const IndexedShapes::size_type result = pVector->capacity();
    return result;
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapes
 * Method:    getShape
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_com_github_stephengold_joltjni_IndexedShapes_getShape
  (JNIEnv *, jclass, jlong vectorVa, jint elementIndex) {
    IndexedShapes * const pVector = reinterpret_cast<IndexedShapes *> (vectorVa);
    IndexedShape& result = pVector->at(elementIndex);
    return reinterpret_cast<jlong> (&result);
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapes
 * Method:    resize
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_IndexedShapes_resize
  (JNIEnv *, jclass, jlong vectorVa, jint numElements) {
    IndexedShapes * const pVector = reinterpret_cast<IndexedShapes *> (vectorVa);
    pVector->resize(numElements);
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapes
 * Method:    erase
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_IndexedShapes_erase
  (JNIEnv *, jclass, jlong vectorVa, jint startIndex, jint stopIndex) {
    IndexedShapes * const pVector = reinterpret_cast<IndexedShapes *> (vectorVa);
    IndexedShapes::iterator origin = pVector->begin();
    pVector->erase(origin + startIndex, origin + stopIndex);
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapes
 * Method:    setShape
 * Signature: (JIJ)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_IndexedShapes_setShape
  (JNIEnv *, jclass, jlong vectorVa, jint elementIndex, jlong shapeVa) {
    IndexedShapes * const pVector = reinterpret_cast<IndexedShapes *> (vectorVa);
    IndexedShape * const pIndexedShape = reinterpret_cast<IndexedShape *> (shapeVa);
    pVector->at(elementIndex) = *pIndexedShape;
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapes
 * Method:    size
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_github_stephengold_joltjni_IndexedShapes_size
  (JNIEnv *, jclass, jlong vectorVa) {
    const IndexedShapes * const pVector
            = reinterpret_cast<IndexedShapes *> (vectorVa);
    const IndexedShapes::size_type result = pVector->size();
    return result;
}

/*
 * Class:     com_github_stephengold_joltjni_IndexedShapeCollector
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_IndexedShapeCollector_free
  (JNIEnv *, jclass, jlong collectorVa) {
    IndexedShapeCollector * const pCollector
            = reinterpret_cast<IndexedShapeCollector *> (collectorVa);
    TRACE_DELETE("CollideShapeCollector", pCollector)
    delete pCollector;
}

class CustomIndexedShapeCollector : IndexedShapeCollector {
    JavaVM *mpVM;
    jmethodID mCollectAtMethodId;
    jmethodID mCastRayMethodId;
    jmethodID mGetShapeAtMethodId;
    jobject mJavaObject;

public:
    CustomIndexedShapeCollector(JNIEnv *pEnv, jobject javaObject) {
        pEnv->GetJavaVM(&mpVM);

        mJavaObject = pEnv->NewGlobalRef(javaObject);
        JPH_ASSERT(!pEnv->ExceptionCheck());

        const jclass clss = pEnv->FindClass(
                "com/github/stephengold/joltjni/CustomIndexedShapeCollector");
        JPH_ASSERT(!pEnv->ExceptionCheck());

        mCollectAtMethodId = pEnv->GetMethodID(clss, "collectAt", "(JJ)V");
        mCastRayMethodId = pEnv->GetMethodID(clss, "castRay", "(JJ)Z");
        mGetShapeAtMethodId = pEnv->GetMethodID(clss, "getShapeAt", "(IJ)V");
        JPH_ASSERT(!pEnv->ExceptionCheck());
    }

    void CollectAt(const AABox* inBox, IndexedShapes* inTo) {
        JNIEnv *pAttachEnv;
        jint retCode = mpVM->AttachCurrentThread((void **)&pAttachEnv, NULL);
        JPH_ASSERT(retCode == JNI_OK);

        const jlong boxVa = reinterpret_cast<jlong> (inBox);
        const jlong shapeVa = reinterpret_cast<jlong> (inTo);
        pAttachEnv->CallVoidMethod(mJavaObject, mCollectAtMethodId, boxVa, shapeVa);
        JPH_ASSERT(!pAttachEnv->ExceptionCheck());
        mpVM->DetachCurrentThread();
    }

    bool CastRay(const RayCast& inRay, IndexedShape* ioShape) {
        JNIEnv *pAttachEnv;
        jint retCode = mpVM->AttachCurrentThread((void **)&pAttachEnv, NULL);
        JPH_ASSERT(retCode == JNI_OK);

        const jlong rayVa = reinterpret_cast<jlong> (&inRay);
        const jlong shapeVa = reinterpret_cast<jlong> (ioShape);
        bool result = pAttachEnv->CallBooleanMethod(mJavaObject, mCastRayMethodId, rayVa, shapeVa);
        JPH_ASSERT(!pAttachEnv->ExceptionCheck());
        mpVM->DetachCurrentThread();
        return result;
    }

    void GetShapeAt(uint32 inShapeIndex, IndexedShape* ioShape) {
        JNIEnv *pAttachEnv;
        jint retCode = mpVM->AttachCurrentThread((void **)&pAttachEnv, NULL);
        JPH_ASSERT(retCode == JNI_OK);

        const jlong shapeVa = reinterpret_cast<jlong> (ioShape);
        pAttachEnv->CallVoidMethod(mJavaObject, mGetShapeAtMethodId, inShapeIndex, shapeVa);
        JPH_ASSERT(!pAttachEnv->ExceptionCheck());
        mpVM->DetachCurrentThread();
    }

    ~CustomIndexedShapeCollector() {
        JNIEnv *pAttachEnv;
        jint retCode = mpVM->AttachCurrentThread((void **)&pAttachEnv, NULL);
        JPH_ASSERT(retCode == JNI_OK);

        pAttachEnv->DeleteGlobalRef(mJavaObject);
        JPH_ASSERT(!pAttachEnv->ExceptionCheck());
        mpVM->DetachCurrentThread();
    }
};

/*
 * Class:     com_github_stephengold_joltjni_CustomIndexedShapeCollector
 * Method:    createCustomCollector
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_github_stephengold_joltjni_CustomIndexedShapeCollector_createCustomCollector
  (JNIEnv *pEnv, jobject javaObject) {
    CustomIndexedShapeCollector * const pResult
            = new CustomIndexedShapeCollector(pEnv, javaObject);
    TRACE_NEW("CustomIndexedShapeCollector", pResult)
    return reinterpret_cast<jlong> (pResult);
}

/*
 * Class:     com_github_stephengold_joltjni_RegionShapeSettings
 * Method:    createRegionShapeSettings
 * Signature: (JFFF)J
 */
JNIEXPORT jlong JNICALL Java_com_github_stephengold_joltjni_RegionShapeSettings_createRegionShapeSettings
  (JNIEnv *, jobject, jlong indexedShapeCollectorVa, jfloat hx, jfloat hy, jfloat hz) {
    IndexedShapeCollector * const pIndexedShapeCollector
            = reinterpret_cast<IndexedShapeCollector *> (indexedShapeCollectorVa);
    const Vec3 halfExtents(hx, hy, hz);

    RegionShapeSettings * const pResult
            = new RegionShapeSettings(pIndexedShapeCollector, halfExtents);
    TRACE_NEW("RegionShapeSettings", pResult)
    return reinterpret_cast<jlong> (pResult);
}

/*
 * Class:     com_github_stephengold_joltjni_RegionShape
 * Method:    _sRegister
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_github_stephengold_joltjni_RegionShape__1sRegister
  (JNIEnv *, jclass) {
    RegionShape::sRegister();
}