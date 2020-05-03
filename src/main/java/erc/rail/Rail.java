package erc.rail;

import erc._core.ERC_Logger;
import erc._mc.gui.GUIRail;
import mochisystems.bufferedrenderer.IBufferedRenderer;
import mochisystems.math.Math;
import mochisystems.math.Mat4;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

import static mochisystems.math.Math.AngleBetweenTwoVec;

public class Rail implements IRail{

	protected IRailController controller;

    public Rail() {
    }

	public Rail(IRailController controller)
	{
		this.controller = controller;
    }

    boolean isDirty = false;
    @Override
    public boolean IsDirty(){ return isDirty; }
    @Override
    public void ForceDirty(){ isDirty = true; }


    @Override
	public void SetController(IRailController controller)
	{
		this.controller = controller;
	}
	@Override
	public IRailController GetController(){ return this.controller; }

//	public int posX(){ return controller.x(); }
//	public int posY(){ return controller.y(); }
//	public int posZ(){ return controller.z(); }

	private IRail nextRail;
	@Override
	public IRail NextRail(){ return nextRail; }
	private IRail prevRail;
	@Override
	public IRail PrevRail(){ return prevRail; }

	@Nonnull
	private final RailPoint BasePoint = new RailPoint();
	@Override
	public RailPoint GetBasePoint(){ return BasePoint; }
	@Nonnull
	private final RailPoint NextPoint = new RailPoint();
	@Override
	public RailPoint GetNextPoint(){ return NextPoint; }

	protected double length;
	@Override
	public double Length(){return length;}

	private int pointNum = -1;
	public int GetPointNum(){return this.pointNum;}
	private double fixedParamTInPointIndex[];
	private Quaternion quaternionAtEachPoint[];
	@Override
	public double[] GetPointList() { return fixedParamTInPointIndex; }

	protected Vec3d gravity = new Vec3d(0, -1, 0);
	public Vec3d Gravity(){return gravity;}
	public void SetGravity(Vec3d g){gravity.CopyFrom(g);}

    final Vec3d vForAccel = new Vec3d();
    public double AccelAt(double pos)
	{
		return DirectionAt(vForAccel, pos).dot(gravity);
	}

	@Override
	public double RegisterAt(double pos, double speed)
	{
		return speed * 0.9985;
	}

	@Override
	public Vec3d DirectionAt(Vec3d out, double pos)
	{
		Math.SplineDirection(out, pos, BasePoint.Pos(), NextPoint.Pos(), BasePoint.Dir(), NextPoint.Dir());
		return out;
	}

	@Override
	public void SetBasePoint(Vec3d pos, Vec3d dir, Vec3d up, float power)
	{
		if(pos == null || dir == null || up == null) {
			ERC_Logger.warn("Rail::SetBasePoint : something vec3 is null");
		}
		BasePoint.Set(pos, dir, up, power);
		isDirty = true;
	}

	@Override
	public void SetBasePoint(RailPoint refPoint)
	{
		if(refPoint == null){
		    ERC_Logger.error("refPoint in SetBasePoint() is null");
		}
		boolean isNotEqual = !BasePoint.equals(refPoint);
        if(isNotEqual) this.BasePoint.CopyFrom(refPoint);
        isDirty |= isNotEqual;
	}

	@Override
	public void SetNextPoint(Vec3d pos, Vec3d dir, Vec3d up, float power)
	{
		if(pos == null || dir == null || up == null) {
			ERC_Logger.warn("Rail::SetNextPoint : something vec3 is null");
		}
		NextPoint.Set(pos, dir, up, power);
        isDirty = true;
	}

	@Override
	public void SetNextPoint(RailPoint refPoint)
	{
        if(refPoint == null){
            ERC_Logger.error("refPoint in SetNextPoint() is null");
        }
        boolean isNotEqual = !NextPoint.equals(refPoint);
        if(isNotEqual) this.NextPoint.CopyFrom(refPoint);
        isDirty |= isNotEqual;
	}

	public void SetNextRail(IRail nextRail)
	{
	    if(this.nextRail == nextRail) return;
			this.nextRail = nextRail;
		if(nextRail != null)
		{
            boolean ret = false;
            NextPoint.CopyFrom(nextRail.GetBasePoint());
			isDirty |= controller.RegisterNextRailPos(nextRail.GetController());
		}
        else
        {
			isDirty |= controller.RegisterNextRailPos(null);
        }
	}

	public IRail GetNextRail()
	{
		return nextRail;
	}

	public void SetPrevRail(IRail prevRail)
	{
		if(this.prevRail == prevRail) return;
		this.prevRail = prevRail;
        if(prevRail != null)
		{
		    boolean ret = false;
			prevRail.SetNextRail(this);
			isDirty |= controller.RegisterPrevRailPos(prevRail.GetController());
		}
		else
        {
			isDirty |= controller.RegisterPrevRailPos(null);
        }
	}

	public static void Connect(IRail base, IRail next)
	{
		next.SetPrevRail(base);
		base.SetNextRail(next);
		next.ConstructCurve();
		base.ConstructCurve();
		base.SyncData();
		next.SyncData();
	}

	public IRail GetPrevRail()
	{
		return prevRail;
	}

	public void SetRailPower(float power)
	{
		isDirty |= BasePoint.SetPower(power);
	}

	public void RotateDirectionAsYaw(double angle)
	{
		BasePoint.RotateYaw(angle);
		isDirty = true;
	}

	public void RotateDirectionAsPitch(double angle)
	{
		BasePoint.RotatePitch(angle);
        isDirty = true;
    }

	public void RotateDirectionAsRoll(double angle)
	{
		BasePoint.RotateRoll(angle);
        isDirty = true;
    }

	public void AddPointNum(int add)
	{
        if(pointNum >= 100) return;
        if(pointNum <= 2) return;
        else SetPointNum(pointNum + add);
        isDirty = true;
    }

	public void SetPointNum(int num)
	{
	    if(pointNum == num) return;
		this.pointNum = num;
		fixedParamTInPointIndex = new double[pointNum];
        quaternionAtEachPoint = new Quaternion[pointNum];
        for(int i = 0; i < quaternionAtEachPoint.length; ++i) quaternionAtEachPoint[i] = new Quaternion();
	    isDirty = true;
	}

	public void AddPower(int idx)
	{
		float f=0;
		switch(idx)
		{
			case 0 : f = 1/2.0f; break;
			case 1 : f = 1/1.1f; break;
			case 2 : f = 1.1f; break;
			case 3 : f = 2.0f; break;
		}
		BasePoint.AddPower(f);
		isDirty = true;
	}

	public void ConstructCurve()
	{
	    if(!isDirty)
	        return;

		////pos
		Vec3d BasePos = new Vec3d();
		Vec3d NextPos = NextPoint.Pos().New().sub(BasePoint.Pos());

		////dir
		Vec3d BaseDir = BasePoint.Dir().New().mul(BasePoint.power);
		Vec3d NextDir = NextPoint.Dir().New().mul(NextPoint.power);

		length = 0;
		Vec3d center = new Vec3d();
		Vec3d prevCenter = center.New();
		double[] lengthInPointIndex = new double[pointNum];
		lengthInPointIndex[0] = 0;

		for(int i = 1; i<pointNum; ++i)
		{
			float f = (float)i/(float)(pointNum-1);

			////spline
			Math.Spline(center, f, BasePos, NextPos, BaseDir, NextDir);
			length += center.distanceTo(prevCenter);
			lengthInPointIndex[i] = length;
			prevCenter.CopyFrom(center);
		}

		calcFixedParamT(lengthInPointIndex); // 点の分布の偏りを補正
		MakeQuaternionForEachPoint();

        isDirty = false;
	}

	private void calcFixedParamT(double[] lengthInPointIndex)
	{
		double lengthPerPoint = length / (float)(pointNum-1);
		double tPerPoint = 1d / (pointNum-1);

		int I = 0;
		for(int i = 1; i < pointNum; ++i)
		{
			double fixedLengthInPoint = lengthPerPoint * i;
			boolean isExistFixedPointBetweenPoint =
					(lengthInPointIndex[I] <= fixedLengthInPoint)
					&& (fixedLengthInPoint < lengthInPointIndex[I+1]);
			if( isExistFixedPointBetweenPoint )
			{
				double t = (fixedLengthInPoint - lengthInPointIndex[I])
						/ (lengthInPointIndex[I+1] - lengthInPointIndex[I]);

				fixedParamTInPointIndex[i] = (I + t) * tPerPoint;
			}
			else
			{
				if(I < pointNum - 2)
				{
					++I;
					--i;
				}
				else
				{
					fixedParamTInPointIndex[i] = 1.0f;
				}
			}
		}
		fixedParamTInPointIndex[pointNum-1] = 1.0f;
	}

	private void MakeQuaternionForEachPoint()
    {
        for(int i = 0; i < pointNum; ++i)
        {
            double t = i / (float)(pointNum);//fixedParamTInPointIndex[i];
            Vec3d BasePos = BasePoint.Pos();
            Vec3d NextPos = NextPoint.Pos();
            Vec3d dir = new Vec3d();
            Vec3d up = new Vec3d();
            Vec3d BaseDir = BasePoint.Dir().New().mul(BasePoint.power);
            Vec3d NextDirPower = NextPoint.Dir().New().mul(NextPoint.power);
            Math.SplineDirection(dir, t, BasePos, NextPos, BaseDir, NextDirPower);
            dir.normalize();
            Math.Slerp(up, t, BasePoint.Up(), NextPoint.Up());
            up.normalize();

            Math.MakeQuaternionFromDirUp(quaternionAtEachPoint[i], dir, up);
        }
    }

	// 媒介変数を補正
	@Override
	public double FixParameter(double t)
	{
		int indexT = (int) java.lang.Math.floor(t * (pointNum - 1));
		double fixedT = 0;
		fixedT = (pointNum - 1 <= indexT) ?
				fixedParamTInPointIndex[pointNum-1] :
				Math.Lerp(t*(pointNum-1)-indexT, fixedParamTInPointIndex[indexT], fixedParamTInPointIndex[indexT+1]);
		return fixedT;
	}

	// 媒介変数から座標と回転量を計算
	@Override
	public void CalcAttitudeAt(double t, Mat4 outAttitudeMatrix, Vec3d pos)
	{
		if(t < 0)
		{
			ERC_Logger.warn("Rail::CalcAttitudeAt : paramT is smaller than 0");
			t = 0;
		}

		////pos
		Vec3d BasePos = BasePoint.Pos();
		Vec3d NextPos = NextPoint.Pos();

		////dir
		Vec3d BaseDir = BasePoint.Dir().New().mul(BasePoint.power);
		Vec3d NextDirPower = NextPoint.Dir().New().mul(NextPoint.power);

//		int indexT = (int) java.lang.Math.floor(t * (pointNum - 1));
//		double fixedT = 0;
//		fixedT = (pointNum - 1 <= indexT) ?
//					fixedParamTInPointIndex[pointNum-1] :
//					Math.Lerp(t*(pointNum-1)-indexT, fixedParamTInPointIndex[indexT], fixedParamTInPointIndex[indexT+1]);
		t = FixParameter(t);

		////spline
		// pos
		Math.Spline(pos, t, BasePos, NextPos, BaseDir, NextDirPower);

		// dir
		Math.SplineDirection(outAttitudeMatrix.Dir(), t, BasePos, NextPos, BaseDir, NextDirPower);
		outAttitudeMatrix.Dir().normalize();

		// up
		Math.Slerp(outAttitudeMatrix.Up(), t, BasePoint.Up(), NextPoint.Up());
		outAttitudeMatrix.Up().normalize();

		// calc side vec from dir CorePosX up
		outAttitudeMatrix.Side().CopyFrom(outAttitudeMatrix.Up()).cross(outAttitudeMatrix.Dir()).normalize();
	}

    public void CalcAttitudeAt(double t, Quaternion outAttitude, Vec3d pos)
    {
		int indexT = (int) java.lang.Math.floor(t * (pointNum - 1));
		if (pointNum - 1 <= indexT) {
			outAttitude.CopyFrom(quaternionAtEachPoint[pointNum-1]);
		}
		else if(indexT <= 0){
			outAttitude.CopyFrom(quaternionAtEachPoint[0
					]);
		}
		else {
			Quaternion.Lerp(outAttitude, quaternionAtEachPoint[indexT], quaternionAtEachPoint[indexT+1], t*(pointNum-1)-indexT);
		}

		t = FixParameter(t);
		////pos
		Vec3d BasePos = BasePoint.Pos();
		Vec3d NextPos = NextPoint.Pos();
		////dir
		Vec3d BaseDir = BasePoint.Dir().New().mul(BasePoint.power);
		Vec3d NextDirPower = NextPoint.Dir().New().mul(NextPoint.power);
		Math.Spline(pos, t, BasePos, NextPos, BaseDir, NextDirPower);
    }

    @Override
	public void Smoothing()
	{
		boolean isConnectedFromPrevAndNext = (nextRail != null) && (prevRail != null);
		if(!isConnectedFromPrevAndNext) return;
		// PrevからNextへのベクトルの半分を自身のDirBaseへ
		Vec3d nextDir = nextRail.GetBasePoint().Dir();
		Vec3d prevDir = prevRail.GetBasePoint().Dir();
		BasePoint.Dir().CopyFrom(nextDir).sub(prevDir).mul(0.5);

		// 自身のUpは前後のDirを自身のDirに回したときのUpの球面線形補完
		Vec3d prevUp = prevRail.GetBasePoint().Up();
		Vec3d prevRotAxis = prevDir.New().cross(BasePoint.Dir());
		double prevAngle = AngleBetweenTwoVec(prevDir, BasePoint.Dir());
		prevUp.Rotate(prevRotAxis, prevAngle);

		Vec3d nextUp = nextRail.GetBasePoint().Up();
		Vec3d nextRotAxis = BasePoint.Dir().New().cross(nextDir);
		double nextAngle = AngleBetweenTwoVec(nextDir, BasePoint.Dir());
		nextUp.Rotate(nextRotAxis, nextAngle);

		Math.Slerp(BasePoint.Up(), 0.5, prevUp, nextUp);

		// 調整おしまい　設定を適用する
		ConstructCurve();
		prevRail.SetNextPoint(this.BasePoint);
		prevRail.ConstructCurve();
//		BasePoint.Power = (float) prevDir.subtract(nextDir).lengthVector()/2;
//		BasePoint.Power = ERC_MathHelper.CalcSmoothRailPower(BasePoint.vecDir, NextPoint.vecDir, BasePoint.vecPos, NextPoint.vecPos);

//		prevtl.SetNextRailPosition(vecBase, dirBase, vecUp, Power);
	}

	@Override
    public void WriteToBytes(ByteBuf buf)
    {
        GetBasePoint().WriteToBytes(buf);
        GetNextPoint().WriteToBytes(buf);
        buf.writeInt(GetPointNum());
        if(prevRail != null) {
			buf.writeInt(prevRail.GetController().x());
			buf.writeInt(prevRail.GetController().y());
			buf.writeInt(prevRail.GetController().z());
		}
		else{
			buf.writeInt(-1);
			buf.writeInt(-1);
			buf.writeInt(-1);
		}
		if(nextRail != null) {
			buf.writeInt(nextRail.GetController().x());
			buf.writeInt(nextRail.GetController().y());
			buf.writeInt(nextRail.GetController().z());
		}
		else{
			buf.writeInt(-1);
			buf.writeInt(-1);
			buf.writeInt(-1);
		}
    }

    @Override
    public void ReadFromBytes(ByteBuf buf)
    {
        GetBasePoint().ReadFromBytes(buf);
        GetNextPoint().ReadFromBytes(buf);
        int point = buf.readInt();
        if(point != GetPointNum()){
            SetPointNum(point);
        }
        int px = buf.readInt();
        int py = buf.readInt();
        int pz = buf.readInt();
        SetPrevRail(controller.GetRail(px, py, pz));
        int nx = buf.readInt();
        int ny = buf.readInt();
        int nz = buf.readInt();
        SetNextRail(controller.GetRail(nx, ny, nz));
    }

	public void Break()
	{
		if(nextRail != null) nextRail.SetPrevRail(null);
		if(prevRail != null) prevRail.SetNextRail(null);
		nextRail = null;
		prevRail = null;
	}

	@Override
    public void SetSpecialData(int i){
    }

    @Override
	public void SpecialGUIInit(GUIRail gui)
	{

	}


	public void OnEnterCoaster(){}

	public void OnLeaveCoaster(){}

	public void OnDeleteCoaster(){}

	public void SyncData(){
	    controller.SyncData();
    }

}
