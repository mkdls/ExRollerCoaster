package erc.rail;

import erc._mc.gui.GUIRail;
import mochisystems.math.Mat4;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import io.netty.buffer.ByteBuf;

public interface IRail{

	void SetController(IRailController controller);
	IRailController GetController();

	IRail NextRail();
	IRail PrevRail();

	RailPoint GetBasePoint();
	RailPoint GetNextPoint();

//	int posX();
//	int posY();
//	int posZ();

	double Length();
	boolean IsDirty();
	void ForceDirty();
	int GetPointNum();
	void SetPointNum(int num);
	void AddPointNum(int add);
	double AccelAt(double pos);
	double RegisterAt(double pos, double speed);
	Vec3d DirectionAt(Vec3d out, double pos);
	void SetBasePoint(Vec3d pos, Vec3d dir, Vec3d up, float power);
	void SetBasePoint(RailPoint refPoint);
	void SetNextPoint(Vec3d pos, Vec3d dir, Vec3d up, float power);
	void SetNextPoint(RailPoint refPoint);

	void SetNextRail(IRail nextRail);
	IRail GetNextRail();
	void SetPrevRail(IRail prevRail);
	IRail GetPrevRail();
	void SetRailPower(float power);
	void RotateDirectionAsYaw(double angle);
	void RotateDirectionAsPitch(double angle);
	void RotateDirectionAsRoll(double angle);
	void AddPower(int idx);
	void ConstructCurve();
	void Break();
	void CalcAttitudeAt(double t, Mat4 outAttitudeMatrix, Vec3d pos);
	void CalcAttitudeAt(double t, Quaternion outAttitude, Vec3d pos);
	void Smoothing();
	double FixParameter(double t);

	void SetSpecialData(int i);
	void SpecialGUIInit(GUIRail gui);

	void SyncData();

	// event
	void OnEnterCoaster();
	void OnLeaveCoaster();
	void OnDeleteCoaster();

	// MessageHandler
	void WriteToBytes(ByteBuf buffer);
	void ReadFromBytes(ByteBuf buffer);

	double[] GetPointList();
}
