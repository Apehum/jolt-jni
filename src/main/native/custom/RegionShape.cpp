#include <Jolt/Jolt.h>

#include <Jolt/Geometry/OrientedBox.h>
#include <Jolt/Physics/Collision/CollisionDispatch.h>
#include <Jolt/Physics/Collision/Shape/SubShapeID.h>

#include "RegionShape.h"
#include "RegionShapeVisitors.h"

#include "Jolt/Physics/Collision/PhysicsMaterial.h"
#include "Jolt/Physics/Collision/CollideSoftBodyVertexIterator.h"

JPH_NAMESPACE_BEGIN
	JPH_IMPLEMENT_SERIALIZABLE_VIRTUAL(RegionShapeSettings) {}

ShapeSettings::ShapeResult RegionShapeSettings::Create() const
{
	// Build a mutable compound shape
	if (mCachedResult.IsEmpty())
		Ref<Shape> shape = new RegionShape(*this, mCachedResult);

	return mCachedResult;
}

RegionShape::RegionShape(const RegionShapeSettings& inSettings, ShapeResult& outResult) :
	Shape(EShapeType::User1, EShapeSubType::User1, inSettings, outResult)
{
	mShapeCollector = inSettings.GetShapeCollector();
	mHalfExtent = inSettings.GetHalfExtent();
	outResult.Set(this);
}

const PhysicsMaterial* RegionShape::GetMaterial(const SubShapeID& inSubShapeID) const
{
	// Decode sub shape index
	SubShapeID remainder;
	uint32 index = GetSubShapeIndexFromID(inSubShapeID, remainder);

	IndexedShape shape;
	mShapeCollector->GetShapeAt(index, &shape);

	if (shape.mShape == nullptr) {
		return PhysicsMaterial::sDefault;
	}

	// Pass call on
	return shape.mShape->GetMaterial(remainder);
}

Vec3 RegionShape::GetSurfaceNormal(const SubShapeID& inSubShapeID, Vec3Arg inLocalSurfacePosition) const
{
	// Decode sub shape index
	SubShapeID remainder;

	uint32 index = GetSubShapeIndexFromID(inSubShapeID, remainder);

	// Transform surface position to local space and pass call on
	IndexedShape shape;
	mShapeCollector->GetShapeAt(index, &shape);

	Mat44 transform = Mat44::sInverseRotationTranslation(Quat::sIdentity(), shape.GetPositionCOM());
	Vec3 normal = shape.mShape->GetSurfaceNormal(remainder, transform * inLocalSurfacePosition);

	// Transform normal to this shape's space
	return transform.Multiply3x3Transposed(normal);
}

void RegionShape::GetSubmergedVolume(Mat44Arg inCenterOfMassTransform, Vec3Arg inScale, const Plane& inSurface, float& outTotalVolume, float& outSubmergedVolume, Vec3& outCenterOfBuoyancy JPH_IF_DEBUG_RENDERER(, RVec3Arg inBaseOffset)) const
{
	// todo: can't get child, because they are "unknown"
}

float RegionShape::GetVolume() const
{
	// todo: can't get child, because they are "unknown"
	return 0.0f;
}

template <class Visitor>
JPH_INLINE void RegionShape::WalkSubShapes(Visitor& ioVisitor) const
{
	if (!ioVisitor.TestBounds()) return;

	AABox collect_at = ioVisitor.GetTestWorldSpaceBounds();

	IndexedShapes shapes;
	mShapeCollector->CollectAt(&collect_at, &shapes);
	uint shapes_size = shapes.size();

	for (uint index = 0; index < shapes_size; index++)
	{
		const IndexedShape &sub_shape = shapes[index];
		ioVisitor.VisitShape(sub_shape, sub_shape.mShapeIndex);

		if (ioVisitor.ShouldAbort())
			break;
	}
}

bool RegionShape::CastRay(const RayCast& inRay, const SubShapeIDCreator& inSubShapeIDCreator, RayCastResult& ioHit) const
{
	IndexedShape shape;
	if (!mShapeCollector->CastRay(inRay, &shape))
		return false;

	// Create ID for sub shape
	SubShapeIDCreator shape2_sub_shape_id = inSubShapeIDCreator.PushID(shape.mShapeIndex, GetSubShapeIDBits());

	// Transform the ray
	Mat44 transform = Mat44::sInverseRotationTranslation(shape.GetRotation(), shape.GetPositionCOM());
	RayCast ray = inRay.Transformed(transform);
	if (shape.mShape->CastRay(ray, shape2_sub_shape_id, ioHit))
		return true;

	// CastRayVisitor visitor(inRay, this, inSubShapeIDCreator, ioHit);
	// WalkSubShapes(visitor);
	// return visitor.mReturnValue;
	return false;
}

void RegionShape::CastRay(const RayCast &inRay, const RayCastSettings &inRayCastSettings, const SubShapeIDCreator &inSubShapeIDCreator, CastRayCollector &ioCollector, const ShapeFilter &inShapeFilter) const {
    IndexedShapes shapes;
    mShapeCollector->CollectCastRay(inRay, &shapes);
    uint shapes_size = shapes.size();

    for (uint index = 0; index < shapes_size; index++)
    {
        const IndexedShape &sub_shape = shapes[index];
        SubShapeIDCreator shape2_sub_shape_id = inSubShapeIDCreator.PushID(sub_shape.mShapeIndex, GetSubShapeIDBits());

        Mat44 transform = Mat44::sInverseRotationTranslation(sub_shape.GetRotation(), sub_shape.GetPositionCOM());
        RayCast ray = inRay.Transformed(transform);
        sub_shape.mShape->CastRay(ray, inRayCastSettings, shape2_sub_shape_id, ioCollector, inShapeFilter);
    }
}


void RegionShape::CollidePoint(Vec3Arg inPoint, const SubShapeIDCreator &inSubShapeIDCreator, CollidePointCollector &ioCollector, const ShapeFilter &inShapeFilter) const {
	CollidePointVisitor visitor(inPoint, this, inSubShapeIDCreator, ioCollector, inShapeFilter);
	WalkSubShapes(visitor);
}

void RegionShape::CollideSoftBodyVertices(Mat44Arg inCenterOfMassTransform, Vec3Arg inScale, const CollideSoftBodyVertexIterator &inVertices, uint inNumVertices, int inCollidingShapeIndex) const {
    if (inNumVertices == 0) {
        return;
    }

	// todo: move this to collector
	Vec3 min_position = Vec3::sReplicate(FLT_MAX), max_position = Vec3::sReplicate(-FLT_MAX);

    for (CollideSoftBodyVertexIterator v = inVertices, sbv_end = inVertices + inNumVertices; v != sbv_end; ++v) {
		min_position = Vec3::sMin(min_position, v.GetPosition());
		max_position = Vec3::sMax(max_position, v.GetPosition());
	}

	Mat44 inverse_transform = inCenterOfMassTransform.InversedRotationTranslation();
	AABox collect_at = AABox(min_position, max_position).Scaled(inScale).Transformed(inverse_transform);

	IndexedShapes shapes;
	mShapeCollector->CollectAt(&collect_at, &shapes);
	uint shapes_size = shapes.size();

	for (uint index = 0; index < shapes_size; index++)
	{
		const IndexedShape &sub_shape = shapes[index];
		sub_shape.mShape->CollideSoftBodyVertices(inCenterOfMassTransform * Mat44::sRotationTranslation(sub_shape.GetRotation(), sub_shape.GetPositionCOM()), inScale, inVertices, inNumVertices, inCollidingShapeIndex);
	}
}

void RegionShape::sCollideCompoundVsShape(const Shape* inShape1, const Shape* inShape2, Vec3Arg inScale1, Vec3Arg inScale2, Mat44Arg inCenterOfMassTransform1, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, const CollideShapeSettings& inCollideShapeSettings, CollideShapeCollector& ioCollector, const ShapeFilter& inShapeFilter)
{
	JPH_ASSERT(inShape1->GetSubType() == EShapeSubType::User1);
	const RegionShape* shape1 = static_cast<const RegionShape*>(inShape1);

	CollideCompoundVsShapeVisitor visitor(shape1, inShape2, inScale1, inScale2, inCenterOfMassTransform1, inCenterOfMassTransform2, inSubShapeIDCreator1, inSubShapeIDCreator2, inCollideShapeSettings, ioCollector, inShapeFilter);
	shape1->WalkSubShapes(visitor);
}

void RegionShape::sCollideShapeVsCompound(const Shape* inShape1, const Shape* inShape2, Vec3Arg inScale1, Vec3Arg inScale2, Mat44Arg inCenterOfMassTransform1, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, const CollideShapeSettings& inCollideShapeSettings, CollideShapeCollector& ioCollector, const ShapeFilter& inShapeFilter)
{
	JPH_ASSERT(inShape2->GetSubType() == EShapeSubType::User1);
	const RegionShape* shape2 = static_cast<const RegionShape*>(inShape2);

	CollideShapeVsCompoundVisitor visitor(inShape1, shape2, inScale1, inScale2, inCenterOfMassTransform1, inCenterOfMassTransform2, inSubShapeIDCreator1, inSubShapeIDCreator2, inCollideShapeSettings, ioCollector, inShapeFilter);
	shape2->WalkSubShapes(visitor);
}

void RegionShape::sCastShapeVsCompound(const ShapeCast& inShapeCast, const ShapeCastSettings& inShapeCastSettings, const Shape* inShape, Vec3Arg inScale, const ShapeFilter& inShapeFilter, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, CastShapeCollector& ioCollector)
{
	JPH_ASSERT(inShape->GetSubType() == EShapeSubType::User1);
	const RegionShape* shape = static_cast<const RegionShape*>(inShape);

	CastShapeVisitor visitor(inShapeCast, inShapeCastSettings, shape, inScale, inShapeFilter, inCenterOfMassTransform2, inSubShapeIDCreator1, inSubShapeIDCreator2, ioCollector);
	shape->WalkSubShapes(visitor);
}

void RegionShape::sCastCompoundVsShape(const ShapeCast& inShapeCast, const ShapeCastSettings& inShapeCastSettings, const Shape* inShape, Vec3Arg inScale, const ShapeFilter& inShapeFilter, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, CastShapeCollector& ioCollector)
{
	// Fetch compound shape from cast shape
	JPH_ASSERT(inShapeCast.mShape->GetType() == EShapeType::User1);
	const RegionShape* compound = static_cast<const RegionShape*>(inShapeCast.mShape);

	// Collect shapes
	IndexedShapes shapes;
	compound->mShapeCollector->CollectAt(&inShapeCast.mShapeWorldBounds, &shapes);

	// Number of sub shapes
	int n = (int)shapes.size();

	// Determine amount of bits for sub shape
	uint sub_shape_bits = compound->GetSubShapeIDBits();

	// Recurse to sub shapes
	for (int i = 0; i < n; ++i)
	{
		const IndexedShape& shape = shapes[i];

		// Create ID for sub shape
		SubShapeIDCreator shape1_sub_shape_id = inSubShapeIDCreator1.PushID(i, sub_shape_bits);

		// Transform the shape cast and update the shape
		Mat44 transform = inShapeCast.mCenterOfMassStart * shape.GetLocalTransformNoScale(inShapeCast.mScale);
		ShapeCast shape_cast(shape.mShape, inShapeCast.mScale, transform, inShapeCast.mDirection);

		CollisionDispatch::sCastShapeVsShapeLocalSpace(shape_cast, inShapeCastSettings, inShape, inScale, inShapeFilter, inCenterOfMassTransform2, shape1_sub_shape_id, inSubShapeIDCreator2, ioCollector);

		if (ioCollector.ShouldEarlyOut())
			break;
	}
}

void RegionShape::sRegister() {
	ShapeFunctions &f = ShapeFunctions::sGet(EShapeSubType::User1);
	f.mConstruct = []() -> Shape * { return new RegionShape; };
	f.mColor = Color::sDarkRed;

	for (const EShapeSubType s : sAllSubShapeTypes)
	{
		CollisionDispatch::sRegisterCastShape(EShapeSubType::User1, s, sCastCompoundVsShape);
		CollisionDispatch::sRegisterCastShape(s, EShapeSubType::User1, sCastShapeVsCompound);

		CollisionDispatch::sRegisterCollideShape(EShapeSubType::User1, s, sCollideCompoundVsShape);
		CollisionDispatch::sRegisterCollideShape(s, EShapeSubType::User1, sCollideShapeVsCompound);
	}
}

JPH_NAMESPACE_END
