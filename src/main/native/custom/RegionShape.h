#pragma once

#include "Jolt/Physics/Collision/CollisionDispatch.h"
#include "Jolt/Physics/Collision/Shape/Shape.h"
#include "Jolt/Physics/Collision/Shape/SubShapeID.h"

JPH_NAMESPACE_BEGIN

struct IndexedShape : RefTarget<IndexedShape>
{
	/// Loads the shape from half extent
	JPH_INLINE void             LoadShapeFromHalfExtent(const Shape *inShape, const uint32 inShapeIndex, Vec3Arg halfExtent)
	{
		uint32 x = inShapeIndex & 0x1FF;
		uint32 y = (inShapeIndex >> 9) & 0x1FF;
		uint32 z = (inShapeIndex >> 18) & 0x1FF;

		// there is no UVec3 and I don't want to implement it,
		// so I just use UVec4
		Vec4 floats = UVec4(x, y, z, 0).ToFloat();
		Vec3 position_COM = Vec3(floats.GetX(), floats.GetY(), floats.GetZ());
		position_COM -= halfExtent;
		position_COM -= Vec3::sReplicate(0.5);

		mShape = inShape;
		mShapeIndex = inShapeIndex;;
		SetPositionCOM(position_COM);
	}

	/// Loads the shape
	JPH_INLINE void             LoadShape(const Shape *inShape, const uint32 inShapeIndex, Vec3Arg inPositionCOM)
	{
		mShape = inShape;
		mShapeIndex = inShapeIndex;
		SetPositionCOM(inPositionCOM);
	}

	/// Compress the center of mass position
	JPH_INLINE void				SetPositionCOM(Vec3Arg inPositionCOM)
	{
		inPositionCOM.StoreFloat3(&mPositionCOM);
	}

	/// Uncompress the center of mass position
	JPH_INLINE Vec3				GetPositionCOM() const {
		// return Vec3((mShapeIndex & 0x1FF) + 0.5f, ((mShapeIndex >> 9) & 0x1FF) + 0.5f, ((mShapeIndex >> 18) & 0x1FF) + 0.5f);
		return Vec3::sLoadFloat3Unsafe(mPositionCOM);
	}

	JPH_INLINE Quat				GetRotation() const {
		return Quat::sIdentity();
	}

	/// Get the local transform for this shape given the scale of the child shape
	/// The total transform of the child shape will be GetLocalTransformNoScale(inScale) * Mat44::sScaling(TransformScale(inScale))
	/// @param inScale The scale of the child shape (in local space of this shape)
	JPH_INLINE Mat44			GetLocalTransformNoScale(Vec3Arg inScale) const
	{
		return Mat44::sTranslation(inScale * GetPositionCOM());
	}

	RefConst<Shape>				mShape;
	uint32                      mShapeIndex;
	Float3						mPositionCOM;											///< Note: Position of center of mass of sub shape!
};

using IndexedShapes = Array<IndexedShape>;

class IndexedShapeCollector
{
public:
	/// Virtual destructor
	virtual						~IndexedShapeCollector() = default;

	virtual void				CollectAt(const AABox* inBox, IndexedShapes* inTo) = 0;

	virtual bool				CastRay(const RayCast& inRay, IndexedShape* ioShape) = 0;

	virtual void				CollectCastRay(const RayCast& inRay, IndexedShapes* ioShapes) = 0;

	virtual void				GetShapeAt(uint32 inShapeIndex, IndexedShape* ioShape) = 0;
};

/// Class that constructs a RegionShape.
class JPH_EXPORT RegionShapeSettings final : public ShapeSettings
{
public:
	JPH_DECLARE_SERIALIZABLE_VIRTUAL(JPH_EXPORT, RegionShapeSettings)

	/// Constructors
									RegionShapeSettings() = default;
									RegionShapeSettings(IndexedShapeCollector *inShapeCollector, Vec3Arg inHalfExtent) : mShapeCollector(inShapeCollector), mHalfExtent(inHalfExtent) {}

	IndexedShapeCollector*		GetShapeCollector() const { return mShapeCollector; }

	Vec3                            GetHalfExtent() const { return mHalfExtent; }

	// See: ShapeSettings
	virtual ShapeResult				Create() const override;

private:
	IndexedShapeCollector*     mShapeCollector;
	Vec3                       mHalfExtent;
};

class JPH_EXPORT RegionShape : public Shape
{
public:
	JPH_OVERRIDE_NEW_DELETE

	/// Constructor
									RegionShape() : Shape(EShapeType::User1, EShapeSubType::User1) { }
									RegionShape(const RegionShapeSettings& inSettings, ShapeResult& outResult);

	// See Shape::CastRay
	bool							CastRay(const RayCast& inRay, const SubShapeIDCreator& inSubShapeIDCreator, RayCastResult& ioHit) const override;
	void							CastRay(const RayCast& inRay, const RayCastSettings& inRayCastSettings, const SubShapeIDCreator& inSubShapeIDCreator, CastRayCollector& ioCollector, const ShapeFilter& inShapeFilter = { }) const override;

	// See: Shape::CollidePoint
	void							CollidePoint(Vec3Arg inPoint, const SubShapeIDCreator& inSubShapeIDCreator, CollidePointCollector& ioCollector, const ShapeFilter& inShapeFilter = { }) const override;

	// See: Shape::CollideSoftBodyVertices
	void                            CollideSoftBodyVertices(Mat44Arg inCenterOfMassTransform, Vec3Arg inScale, const CollideSoftBodyVertexIterator &inVertices, uint inNumVertices, int inCollidingShapeIndex) const override;
	// void							CollideSoftBodyVertices(Mat44Arg inCenterOfMassTransform, Vec3Arg inScale, SoftBodyVertex *ioVertices, uint inNumVertices, float inDeltaTime, Vec3Arg inDisplacementDueToGravity, int inCollidingShapeIndex) const override;

	// See Shape::GetTrianglesStart
	void							GetTrianglesStart(GetTrianglesContext &ioContext, const AABox &inBox, Vec3Arg inPositionCOM, QuatArg inRotation, Vec3Arg inScale) const override { JPH_ASSERT(false, "Cannot call on non-leaf shapes, use CollectTransformedShapes to collect the leaves first!"); }

	// See Shape::GetTrianglesNext
	int								GetTrianglesNext(GetTrianglesContext &ioContext, int inMaxTrianglesRequested, Float3 *outTriangleVertices, const PhysicsMaterial **outMaterials = nullptr) const override { JPH_ASSERT(false, "Cannot call on non-leaf shapes, use CollectTransformedShapes to collect the leaves first!"); return 0; }

	// See Shape::GetLocalBounds
	AABox							GetLocalBounds() const override { return AABox(-mHalfExtent, mHalfExtent); }

	// See Shape::GetInnerRadius
	virtual float					GetInnerRadius() const override { return 0.5; } // todo: can't get child, because they are "unknown"

	// See Shape::GetMassProperties
	virtual MassProperties			GetMassProperties() const override { return MassProperties(); }; // todo: can't get child, because they are "unknown"

	// See Shape::GetMaterial
	virtual const PhysicsMaterial*  GetMaterial(const SubShapeID& inSubShapeID) const override;

	// See Shape::GetSurfaceNormal
	virtual Vec3					GetSurfaceNormal(const SubShapeID& inSubShapeID, Vec3Arg inLocalSurfacePosition) const override;

	// See Shape::GetSubmergedVolume
	virtual void					GetSubmergedVolume(Mat44Arg inCenterOfMassTransform, Vec3Arg inScale, const Plane& inSurface, float& outTotalVolume, float& outSubmergedVolume, Vec3& outCenterOfBuoyancy JPH_IF_DEBUG_RENDERER(, RVec3Arg inBaseOffset)) const override;

	// See Shape::GetVolume
	virtual float					GetVolume() const override;

	// See Shape::GetStats
	virtual Stats					GetStats() const override { return Stats(0, 0); } // todo: can't get child, because they are "unknown"

#ifdef JPH_DEBUG_RENDERER
	// See Shape::Draw
	virtual void					Draw(DebugRenderer* inRenderer, RMat44Arg inCenterOfMassTransform, Vec3Arg inScale, ColorArg inColor, bool inUseMaterialColors, bool inDrawWireframe) const override {};
#endif // JPH_DEBUG_RENDERER

	// See Shape::GetSubShapeIDBitsRecursive
	virtual uint					GetSubShapeIDBitsRecursive() const override { return GetSubShapeIDBits(); }; // todo: can't get child, because they are "unknown"

	/// Convert SubShapeID to sub shape index
	/// @param inSubShapeID Sub shape id that indicates the leaf shape relative to this shape
	/// @param outRemainder This is the sub shape ID for the sub shape of the compound after popping off the index
	/// @return The index of the sub shape of this compound
	inline uint32					GetSubShapeIndexFromID(SubShapeID inSubShapeID, SubShapeID& outRemainder) const
	{
		uint32 idx = inSubShapeID.PopID(GetSubShapeIDBits(), outRemainder);
		return idx;
	}

	/// @brief Convert a sub shape index to a sub shape ID
	/// @param inIdx Index of the sub shape of this compound
	/// @param inParentSubShapeID Parent SubShapeID (describing the path to the compound shape)
	/// @return A sub shape ID creator that contains the full path to the sub shape with index inIdx
	inline SubShapeIDCreator		GetSubShapeIDFromIndex(int inIdx, const SubShapeIDCreator& inParentSubShapeID) const
	{
		return inParentSubShapeID.PushID(inIdx, GetSubShapeIDBits());
	}

	template <class Visitor>
	JPH_INLINE void					WalkSubShapes(Visitor& ioVisitor) const;

	// Register shape functions with the registry
	static void						sRegister();

	// Helper functions called by CollisionDispatch
	static void						sCollideCompoundVsShape(const Shape* inShape1, const Shape* inShape2, Vec3Arg inScale1, Vec3Arg inScale2, Mat44Arg inCenterOfMassTransform1, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, const CollideShapeSettings& inCollideShapeSettings, CollideShapeCollector& ioCollector, const ShapeFilter& inShapeFilter);
	static void						sCollideShapeVsCompound(const Shape* inShape1, const Shape* inShape2, Vec3Arg inScale1, Vec3Arg inScale2, Mat44Arg inCenterOfMassTransform1, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, const CollideShapeSettings& inCollideShapeSettings, CollideShapeCollector& ioCollector, const ShapeFilter& inShapeFilter);
	static void						sCastCompoundVsShape(const ShapeCast& inShapeCast, const ShapeCastSettings& inShapeCastSettings, const Shape* inShape, Vec3Arg inScale, const ShapeFilter& inShapeFilter, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, CastShapeCollector& ioCollector);
	static void						sCastShapeVsCompound(const ShapeCast& inShapeCast, const ShapeCastSettings& inShapeCastSettings, const Shape* inShape, Vec3Arg inScale, const ShapeFilter& inShapeFilter, Mat44Arg inCenterOfMassTransform2, const SubShapeIDCreator& inSubShapeIDCreator1, const SubShapeIDCreator& inSubShapeIDCreator2, CastShapeCollector& ioCollector);

protected:
	// Visitors for collision detection
	struct CastRayVisitor;
	struct CastRayVisitorCollector;
	struct CollidePointVisitor;
	struct CollideCompoundVsShapeVisitor;
	struct CollideShapeVsCompoundVisitor;
	struct CastShapeVisitor;

	/// Determine amount of bits needed to encode sub shape id
	inline uint						GetSubShapeIDBits() const
	{
		return 32; // todo: region/chunk size?
	}

private:
	IndexedShapeCollector*          mShapeCollector;
	Vec3                            mHalfExtent;
};

JPH_NAMESPACE_END
