#pragma once

#include <Jolt/Physics/Collision/Shape/Shape.h>
#include <Jolt/Physics/Collision/RayCast.h>
#include <Jolt/Physics/Collision/CastResult.h>
#include <Jolt/Geometry/RayAABox.h>

JPH_NAMESPACE_BEGIN

struct RegionShape::CollideCompoundVsShapeVisitor
{
	JPH_INLINE			CollideCompoundVsShapeVisitor(const RegionShape* inShape1, const Shape* inShape2, Vec3Arg inScale1, Vec3Arg inScale2, Mat44Arg inCenterOfMassTransform1, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, const CollideShapeSettings& inCollideShapeSettings, CollideShapeCollector& ioCollector, const ShapeFilter& inShapeFilter) :
		mCollideShapeSettings(inCollideShapeSettings),
		mCollector(ioCollector),
		mShape2(inShape2),
		mScale1(inScale1),
		mScale2(inScale2),
		mTransform1(inCenterOfMassTransform1),
		mTransform2(inCenterOfMassTransform2),
		mSubShapeIDCreator1(inSubShapeIDCreator1),
		mSubShapeIDCreator2(inSubShapeIDCreator2),
		mSubShapeBits(inShape1->GetSubShapeIDBits()),
		mShapeFilter(inShapeFilter)
	{
		mBoundsOf1 = inShape1->GetLocalBounds();

		// Get transform from shape 2 to shape 1
		Mat44 transform2_to_1 = inCenterOfMassTransform1.InversedRotationTranslation() * inCenterOfMassTransform2;

		// Convert bounding box of 2 into space of 1
		mBoundsOf2InSpaceOf1 = inShape2->GetLocalBounds().Scaled(inScale2).Transformed(transform2_to_1);
	}

	/// Returns true when collision detection should abort because it's not possible to find a better hit
	JPH_INLINE bool		ShouldAbort() const
	{
		return mCollector.ShouldEarlyOut();
	}

	JPH_INLINE bool TestBounds()
	{
		return mBoundsOf2InSpaceOf1.Overlaps(mBoundsOf1);
	}

	JPH_INLINE AABox GetTestWorldSpaceBounds() {
		return mBoundsOf2InSpaceOf1;
	}

	/// Test the shape against a single subshape
	JPH_INLINE void		VisitShape(const IndexedShape& inSubShape, uint32 inSubShapeIndex)
	{
		// Get world transform of 1
		Mat44 transform1 = mTransform1 * inSubShape.GetLocalTransformNoScale(mScale1);

		// Create ID for sub shape
		SubShapeIDCreator shape1_sub_shape_id = mSubShapeIDCreator1.PushID(inSubShapeIndex, mSubShapeBits);

		CollisionDispatch::sCollideShapeVsShape(inSubShape.mShape, mShape2, mScale1, mScale2, transform1, mTransform2, shape1_sub_shape_id, mSubShapeIDCreator2, mCollideShapeSettings, mCollector, mShapeFilter);
	}

	const CollideShapeSettings&		mCollideShapeSettings;
	CollideShapeCollector&			mCollector;
	const Shape*                    mShape2;
	Vec3							mScale1;
	Vec3							mScale2;
	Mat44							mTransform1;
	Mat44							mTransform2;
	AABox							mBoundsOf1;
	AABox							mBoundsOf2InSpaceOf1;
	SubShapeIDCreator				mSubShapeIDCreator1;
	SubShapeIDCreator				mSubShapeIDCreator2;
	uint							mSubShapeBits;
	const ShapeFilter&				mShapeFilter;
};

struct RegionShape::CollideShapeVsCompoundVisitor
{
	JPH_INLINE			CollideShapeVsCompoundVisitor(const Shape* inShape1, const RegionShape* inShape2, Vec3Arg inScale1, Vec3Arg inScale2, Mat44Arg inCenterOfMassTransform1, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, const CollideShapeSettings& inCollideShapeSettings, CollideShapeCollector& ioCollector, const ShapeFilter& inShapeFilter) :
		mCollideShapeSettings(inCollideShapeSettings),
		mCollector(ioCollector),
		mShape1(inShape1),
		mScale1(inScale1),
		mScale2(inScale2),
		mTransform1(inCenterOfMassTransform1),
		mTransform2(inCenterOfMassTransform2),
		mSubShapeIDCreator1(inSubShapeIDCreator1),
		mSubShapeIDCreator2(inSubShapeIDCreator2),
		mSubShapeBits(inShape2->GetSubShapeIDBits()),
		mShapeFilter(inShapeFilter)
	{
		mBoundsOf2 = inShape2->GetLocalBounds();

		// Get transform from shape 1 to shape 2
		Mat44 transform1_to_2 = inCenterOfMassTransform2.InversedRotationTranslation() * inCenterOfMassTransform1;

		// Convert bounding box of 1 into space of 2
		mBoundsOf1InSpaceOf2 = inShape1->GetLocalBounds().Scaled(inScale1).Transformed(transform1_to_2);
		mBoundsOf1InSpaceOf2.ExpandBy(Vec3::sReplicate(inCollideShapeSettings.mMaxSeparationDistance));
	}

	/// Returns true when collision detection should abort because it's not possible to find a better hit
	JPH_INLINE bool		ShouldAbort() const
	{
		return mCollector.ShouldEarlyOut();
	}

	JPH_INLINE bool TestBounds()
	{
		return mBoundsOf2.Overlaps(mBoundsOf1InSpaceOf2);
	}

	JPH_INLINE AABox GetTestWorldSpaceBounds() {
		return mBoundsOf1InSpaceOf2;

	}

	/// Test the shape against a single subshape
	JPH_INLINE void		VisitShape(const IndexedShape& inSubShape, uint32 inSubShapeIndex)
	{
		// Create ID for sub shape
		SubShapeIDCreator shape2_sub_shape_id = mSubShapeIDCreator2.PushID(inSubShapeIndex, mSubShapeBits);

		// Get world transform of 2
		Mat44 transform2 = mTransform2 * inSubShape.GetLocalTransformNoScale(mScale2);

		CollisionDispatch::sCollideShapeVsShape(mShape1, inSubShape.mShape, mScale1, mScale2, mTransform1, transform2, mSubShapeIDCreator1, shape2_sub_shape_id, mCollideShapeSettings, mCollector, mShapeFilter);
	}

	const CollideShapeSettings& mCollideShapeSettings;
	CollideShapeCollector& mCollector;
	const Shape* mShape1;
	Vec3							mScale1;
	Vec3							mScale2;
	Mat44							mTransform1;
	Mat44							mTransform2;
	AABox							mBoundsOf1InSpaceOf2;
	AABox							mBoundsOf2;
	SubShapeIDCreator				mSubShapeIDCreator1;
	SubShapeIDCreator				mSubShapeIDCreator2;
	uint							mSubShapeBits;
	const ShapeFilter& mShapeFilter;
};

struct RegionShape::CastShapeVisitor
{
	JPH_INLINE			CastShapeVisitor(const ShapeCast& inShapeCast, const ShapeCastSettings& inShapeCastSettings, const RegionShape* inShape, Vec3Arg inScale, const ShapeFilter& inShapeFilter, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, CastShapeCollector& ioCollector) :
		mBoxCenter(inShapeCast.mShapeWorldBounds.GetCenter()),
		mBoxExtent(inShapeCast.mShapeWorldBounds.GetExtent()),
		mScale(inScale),
		mShapeCast(inShapeCast),
		mShapeCastSettings(inShapeCastSettings),
		mShapeFilter(inShapeFilter),
		mCollector(ioCollector),
		mCenterOfMassTransform2(inCenterOfMassTransform2),
		mSubShapeIDCreator1(inSubShapeIDCreator1),
		mSubShapeIDCreator2(inSubShapeIDCreator2),
		mSubShapeBits(inShape->GetSubShapeIDBits()),
		mWorldBoundsOfShape(inShape->GetWorldSpaceBounds(inCenterOfMassTransform2, inScale)) // todo: this is not correct I think
	{
		// Determine ray properties of cast
		mInvDirection.Set(inShapeCast.mDirection);
	}

	/// Returns true when collision detection should abort because it's not possible to find a better hit
	JPH_INLINE bool		ShouldAbort() const
	{
		return mCollector.ShouldEarlyOut();
	}

	JPH_INLINE bool TestBounds()
	{
		// todo: this is probably incorrect
		return RayAABox(mBoxCenter, mInvDirection, mWorldBoundsOfShape.mMin, mWorldBoundsOfShape.mMax);
	}

	JPH_INLINE AABox GetTestWorldSpaceBounds() {
		return mWorldBoundsOfShape;
	}

	/// Test the shape against a single subshape
	JPH_INLINE void		VisitShape(const IndexedShape& inSubShape, uint32 inSubShapeIndex)
	{
		// Create ID for sub shape
		SubShapeIDCreator shape2_sub_shape_id = mSubShapeIDCreator2.PushID(inSubShapeIndex, mSubShapeBits);

		// Calculate the local transform for this sub shape
		Mat44 local_transform = Mat44::sTranslation(mScale * inSubShape.GetPositionCOM());

		// Transform the center of mass of 2
		Mat44 center_of_mass_transform2 = mCenterOfMassTransform2 * local_transform;

		// Transform the shape cast
		ShapeCast shape_cast = mShapeCast.PostTransformed(local_transform.InversedRotationTranslation());

		CollisionDispatch::sCastShapeVsShapeLocalSpace(shape_cast, mShapeCastSettings, inSubShape.mShape, mScale, mShapeFilter, center_of_mass_transform2, mSubShapeIDCreator1, shape2_sub_shape_id, mCollector);
	}

	AABox                       mWorldBoundsOfShape;
	RayInvDirection				mInvDirection;
	Vec3						mBoxCenter;
	Vec3						mBoxExtent;
	Vec3						mScale;
	const ShapeCast&			mShapeCast;
	const ShapeCastSettings&	mShapeCastSettings;
	const ShapeFilter&			mShapeFilter;
	CastShapeCollector&			mCollector;
	Mat44						mCenterOfMassTransform2;
	SubShapeIDCreator			mSubShapeIDCreator1;
	SubShapeIDCreator			mSubShapeIDCreator2;
	uint						mSubShapeBits;
};

struct RegionShape::CastRayVisitor
{
	JPH_INLINE 			CastRayVisitor(const RayCast& inRay, const RegionShape* inShape, const SubShapeIDCreator& inSubShapeIDCreator, RayCastResult& ioHit) :
		mRay(inRay),
		mHit(ioHit),
		mSubShapeIDCreator(inSubShapeIDCreator),
		mSubShapeBits(inShape->GetSubShapeIDBits()),
		mShapeBounds(inShape->GetLocalBounds())
	{
		// Determine ray properties of cast
		mInvDirection.Set(inRay.mDirection);
	}

	/// Returns true when collision detection should abort because it's not possible to find a better hit
	JPH_INLINE bool		ShouldAbort() const
	{
		return mHit.mFraction <= 0.0f;
	}

	JPH_INLINE bool		TestBounds() const
	{
		// todo: this is probably incorrect
		return RayAABox(mRay.mOrigin, mInvDirection, mShapeBounds.mMax, mShapeBounds.mMax);
	}

	JPH_INLINE AABox GetTestWorldSpaceBounds() {
		return mShapeBounds; // todo: this is not world bounds
	}

	/// Test the ray against a single subshape
	JPH_INLINE void		VisitShape(const IndexedShape& inSubShape, uint32 inSubShapeIndex)
	{
		// Create ID for sub shape
		SubShapeIDCreator shape2_sub_shape_id = mSubShapeIDCreator.PushID(inSubShapeIndex, mSubShapeBits);

		// Transform the ray
		Mat44 transform = Mat44::sInverseRotationTranslation(inSubShape.GetRotation(), inSubShape.GetPositionCOM());
		RayCast ray = mRay.Transformed(transform);
		if (inSubShape.mShape->CastRay(ray, shape2_sub_shape_id, mHit))
			mReturnValue = true;
	}

	AABox               mShapeBounds;
	RayInvDirection		mInvDirection;
	const RayCast&		mRay;
	RayCastResult&		mHit;
	SubShapeIDCreator	mSubShapeIDCreator;
	uint				mSubShapeBits;
	bool				mReturnValue = false;
};

struct RegionShape::CollidePointVisitor
{
	JPH_INLINE			CollidePointVisitor(Vec3Arg inPoint, const RegionShape* inShape, const SubShapeIDCreator& inSubShapeIDCreator, CollidePointCollector& ioCollector, const ShapeFilter& inShapeFilter) :
		mPoint(inPoint),
		mSubShapeIDCreator(inSubShapeIDCreator),
		mCollector(ioCollector),
		mSubShapeBits(inShape->GetSubShapeIDBits()),
		mShapeFilter(inShapeFilter),
		mShapeBounds(inShape->GetLocalBounds())
	{
	}

	/// Returns true when collision detection should abort because it's not possible to find a better hit
	JPH_INLINE bool		ShouldAbort() const
	{
		return mCollector.ShouldEarlyOut();
	}

	JPH_INLINE bool		TestBounds() const
	{
		return mShapeBounds.Contains(mPoint);
	}

	JPH_INLINE AABox GetTestWorldSpaceBounds() {
		return mShapeBounds; // todo: this is not world bounds
	}

	/// Test the point against a single subshape
	JPH_INLINE void		VisitShape(const IndexedShape& inSubShape, uint32 inSubShapeIndex)
	{
		// Create ID for sub shape
		SubShapeIDCreator shape2_sub_shape_id = mSubShapeIDCreator.PushID(inSubShapeIndex, mSubShapeBits);

		// Transform the point
		Mat44 transform = Mat44::sInverseRotationTranslation(inSubShape.GetRotation(), inSubShape.GetPositionCOM());
		inSubShape.mShape->CollidePoint(transform * mPoint, shape2_sub_shape_id, mCollector, mShapeFilter);
	}

	AABox						mShapeBounds;
	Vec3						mPoint;
	SubShapeIDCreator			mSubShapeIDCreator;
	CollidePointCollector&		mCollector;
	uint						mSubShapeBits;
	const ShapeFilter&			mShapeFilter;
};

struct RegionShape::CastRayVisitorCollector
{
	JPH_INLINE 			CastRayVisitorCollector(const RayCast &inRay, const RayCastSettings &inRayCastSettings, const RegionShape *inShape, const SubShapeIDCreator &inSubShapeIDCreator, CastRayCollector &ioCollector, const ShapeFilter &inShapeFilter) :
		mRay(inRay),
		mCollector(ioCollector),
		mSubShapeIDCreator(inSubShapeIDCreator),
		mSubShapeBits(inShape->GetSubShapeIDBits()),
		mRayCastSettings(inRayCastSettings),
		mShapeFilter(inShapeFilter),
		mShapeBounds(inShape->GetLocalBounds())
	{
		// Determine ray properties of cast
		mInvDirection.Set(inRay.mDirection);
	}

	/// Returns true when collision detection should abort because it's not possible to find a better hit
	JPH_INLINE bool		ShouldAbort() const
	{
		return mCollector.ShouldEarlyOut();
	}

	JPH_INLINE bool		TestBounds() const
	{
		return RayAABox(mRay.mOrigin, mInvDirection, mShapeBounds.mMax, mShapeBounds.mMax);
	}

	JPH_INLINE AABox GetTestWorldSpaceBounds() {
		return mShapeBounds; // todo: this is not world bounds
	}

	/// Test the ray against a single subshape
	JPH_INLINE void		VisitShape(const IndexedShape &inSubShape, uint32 inSubShapeIndex)
	{
		// Create ID for sub shape
		SubShapeIDCreator shape2_sub_shape_id = mSubShapeIDCreator.PushID(inSubShapeIndex, mSubShapeBits);

		// Transform the ray
		Mat44 transform = Mat44::sInverseRotationTranslation(inSubShape.GetRotation(), inSubShape.GetPositionCOM());
		RayCast ray = mRay.Transformed(transform);
		inSubShape.mShape->CastRay(ray, mRayCastSettings, shape2_sub_shape_id, mCollector, mShapeFilter);
	}

	AABox				mShapeBounds;
	RayInvDirection		mInvDirection;
	const RayCast &		mRay;
	CastRayCollector &	mCollector;
	SubShapeIDCreator	mSubShapeIDCreator;
	uint				mSubShapeBits;
	RayCastSettings		mRayCastSettings;
	const ShapeFilter &	mShapeFilter;
};

JPH_NAMESPACE_END