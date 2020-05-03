package erc.rail;

import erc._mc.gui.GUIRail;
import mochisystems.math.Mat4;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import io.netty.buffer.ByteBuf;

public class BranchRail implements IRail {

    private Rail[] rails = { new Rail(), new Rail() };
    private int selectedIndex = 0;
    private IRailController controller;

    public void RotateSelected()
    {
        selectedIndex = (selectedIndex + 1)%rails.length;
    }

    public void changeBranchNum(int num)
    {
        rails = new Rail[num];
        selectedIndex = 0;
    }

    @Override
    public boolean IsDirty()
    {
        boolean ret = false;
        for(Rail rail : rails) ret |= rail.isDirty;
        return ret;
    }
    @Override
    public void ForceDirty()
    {
        rails[selectedIndex].ForceDirty();
    }

    public int posX(){ return controller.x(); }
    public int posY(){ return controller.y(); }
    public int posZ(){ return controller.z(); }

    @Override
    public void SetController(IRailController controller) {
        this.controller = controller;
    }
    @Override
    public IRailController GetController(){ return this.controller; }

    @Override
    public IRail NextRail() {
        return rails[selectedIndex].NextRail();
    }

    @Override
    public IRail PrevRail() {
        return rails[selectedIndex].PrevRail();
    }

    @Override
    public RailPoint GetBasePoint() {
        return rails[selectedIndex].GetBasePoint();
    }

    @Override
    public RailPoint GetNextPoint() {
        return rails[selectedIndex].GetNextPoint();
    }

    @Override
    public double Length() {
        return rails[selectedIndex].Length();
    }

    @Override
    public int GetPointNum() {
        return rails[selectedIndex].GetPointNum();
    }

    @Override
    public void AddPointNum(int add) {
        rails[selectedIndex].AddPower(add);
    }

    @Override
    public double[] GetPointList() { return rails[selectedIndex].GetPointList(); }

    @Override
    public double AccelAt(double pos) {
        return rails[selectedIndex].AccelAt(pos);
    }

    @Override
    public double RegisterAt(double pos, double speed) {
        return rails[selectedIndex].RegisterAt(pos, speed);
    }

    @Override
    public Vec3d DirectionAt(Vec3d out, double pos) {
        return rails[selectedIndex].DirectionAt(out, pos);
    }

    @Override
    public void SetBasePoint(Vec3d pos, Vec3d dir, Vec3d up, float power) {
        for(Rail rail : rails) rail.SetBasePoint(pos, dir, up, power);
    }

    @Override
    public void SetBasePoint(RailPoint refPoint) {
        for(Rail rail : rails) rail.SetBasePoint(refPoint);
    }

    @Override
    public void SetNextPoint(Vec3d pos, Vec3d dir, Vec3d up, float power) {
        rails[selectedIndex].SetNextPoint(pos, dir, up, power);
    }

    @Override
    public void SetNextPoint(RailPoint refPoint) {
        rails[selectedIndex].SetNextPoint(refPoint);
    }

    @Override
    public void SetNextRail(IRail nextRail) {
        rails[selectedIndex].SetNextRail(nextRail);
    }

    @Override
    public IRail GetNextRail() {
        return null;
    }

    @Override
    public void SetPrevRail(IRail prevRail) {
        for(Rail rail : rails) rail.SetPrevRail(prevRail);
    }

    @Override
    public IRail GetPrevRail() {
        return rails[0].GetPrevRail();
    }

    @Override
    public void SetRailPower(float power) {
        for(Rail rail : rails) rail.SetRailPower(power);
    }

    @Override
    public void RotateDirectionAsYaw(double angle) {
        for(Rail rail : rails)rail.RotateDirectionAsYaw(angle);
    }

    @Override
    public void RotateDirectionAsPitch(double angle) {
        for(Rail rail : rails) rail.RotateDirectionAsPitch(angle);
    }

    @Override
    public void RotateDirectionAsRoll(double angle) {
        for(Rail rail : rails)rail.RotateDirectionAsRoll(angle);
    }

    @Override
    public void SetPointNum(int num) {
        rails[selectedIndex].SetPointNum(num);
    }

    @Override
    public void AddPower(int idx) {
        for(Rail rail : rails)rail.AddPower(idx);
    }

    @Override
    public void ConstructCurve() {
        for(Rail rail : rails)rail.ConstructCurve();
    }

    @Override
    public double FixParameter(double t){ return rails[0].FixParameter(t);}

    @Override
    public void Break() {
        for(Rail rail : rails)rail.Break();
    }

    @Override
    public void WriteToBytes(ByteBuf buf) {
        buf.writeInt(selectedIndex);
        buf.writeInt(rails.length);
        for(Rail rail : rails)rail.WriteToBytes(buf);
    }

    @Override
    public void ReadFromBytes(ByteBuf buf) {
        selectedIndex = buf.readInt();
        for(Rail rail : rails)rail.ReadFromBytes(buf);
    }

    @Override
    public void OnEnterCoaster() {}

    @Override
    public void OnLeaveCoaster() {}

    @Override
    public void OnDeleteCoaster() {}

    @Override
    public void CalcAttitudeAt(double t, Mat4 outAttitudeMatrix, Vec3d pos) {

    }

    @Override
    public void CalcAttitudeAt(double t, Quaternion outAttitude, Vec3d pos){

    }

    @Override
    public void Smoothing() {
        rails[selectedIndex].Smoothing();
    }

    @Override
    public void SetSpecialData(int i) {

    }

    @Override
    public void SpecialGUIInit(GUIRail gui) {

    }

    @Override
    public void SyncData()
    {
        controller.SyncData();
    }

}
