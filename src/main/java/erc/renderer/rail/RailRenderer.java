package erc.renderer.rail;


import erc.rail.IRail;
import erc.rail.RailPoint;
import mochisystems.math.Math;
import mochisystems.math.Vec3d;
import mochisystems.bufferedrenderer.IBufferedRenderer;

public abstract class RailRenderer extends IBufferedRenderer implements IRailRenderer{

    protected IRail rail;
    public void SetRail(IRail rail){
        this.rail = rail;
    }
    public IRail GetRail(){
        return rail;
    }

    public RailRenderer(IRail rail)
    {
        SetRail(rail);
    }

    ////pos
    private Vec3d Base;
    private Vec3d Next;
    ////dir
    private Vec3d BaseDir;
    private Vec3d NextDir;
    ////pair of rail Vertex
    private Vec3d baseUp;
    private Vec3d nextUp;

    @Override
    public void RenderRail()
    {
        super.Render();
    }

    @Override
    protected void Draw()
    {
        UpdateRailData();
        DrawRailModel();
    }

    final void UpdateRailData()
    {
        RailPoint BasePoint = rail.GetBasePoint();
        RailPoint NextPoint = rail.GetNextPoint();

        ////pos
        Base = new Vec3d();
        Next = NextPoint.Pos().New().sub(BasePoint.Pos());

        ////dir
        BaseDir = BasePoint.Dir().New().mul(BasePoint.Power());
        NextDir = NextPoint.Dir().New().mul(NextPoint.Power());

        ////pair of rail Vertex
        baseUp = BasePoint.Up();
        nextUp = NextPoint.Up();
    }

    protected abstract void DrawRailModel();

    private Vec3d pos = new Vec3d();
    private Vec3d dir = new Vec3d();
    private Vec3d up = new Vec3d();
    private Vec3d cross = new Vec3d();
    protected double t = 0;

    Vec3d renderPos = new Vec3d();
    Vec3d renderNormal = new Vec3d();
    @Override
    protected void RegisterVertex(Vec3d pos, Vec3d normal, float u, float v)
    {
//        double t = pos.z / 1;//rail.Position(); TODO ｔの補正が必要　pointとpointの間にある点のtを求める
        TransformVertex(t, pos, normal);
        super.RegisterVertex(renderPos, renderNormal, u, v);
    }

    final void TransformVertex(double point, Vec3d vPos, Vec3d normal)
    {
//        vPos.x -= 1f;
//        vPos.y += 1f;
//        renderPos.CopyFrom(vPos);
//        renderNormal.CopyFrom(normal);
//        renderPos.z += 0.5;
//        if(true)return;

        pos.SetFrom(0, 0, 0);
        dir.SetFrom(0, 0, 0);
        up.SetFrom(0, 0, 0);
        Math.Spline(pos, point, Base, Next, BaseDir, NextDir);
        Math.SplineDirection(dir, point, Base, Next, BaseDir, NextDir);
        Math.Slerp(up, point, baseUp, nextUp);
        up.normalize();
        cross.CopyFrom(up).cross(dir);
        cross.normalize();

        double px = vPos.x;
        double py = vPos.y;
        double pz = vPos.z;
        double nx = normal.x;
        double ny = normal.y;
        double nz = normal.z;
        // RenderVertexPos = pos + cross * v.CorePosX + up * v.y; // + (v.z * length - pos.z) * v.z;
        renderPos.CopyFrom(pos).add(cross.New().mul(px)).add(up.New().mul(py)); // add(dir.mul(pz * n - pos.z));
        renderNormal.SetFrom(0, 0, 0).add(cross.mul(nx)).add(up.mul(ny)).add(dir.mul(nz));
    }

}
