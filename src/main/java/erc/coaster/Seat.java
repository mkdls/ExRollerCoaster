package erc.coaster;


import mochisystems._core.Logger;
import mochisystems.math.Mat4;

public class Seat{

    public interface ISeatController
    {
        void SetSeat(Seat seat);
        void Destroy();
    }

    private Coaster parentCoaster;
//    public final Mat4 AttitudeMatrix = new Mat4();
    private ISeatController controller;
    public CoasterSettings.SeatData setting;

    public Seat(Coaster coaster)
    {
        this.parentCoaster = coaster;
    }

    public void SetController(ISeatController controller)
    {
        this.controller = controller;
        controller.SetSeat(this);
    }

    public Coaster GetParent()
    {
        return parentCoaster;
    }

    void SetSetting(CoasterSettings.SeatData setting)
    {
        this.setting = setting;
    }

    void Destroy()
    {
        if(controller != null) controller.Destroy();
    }

    public void Update(long tick)
    {
        move();
    }

    private void move()
    {
//        AttitudeMatrix.Pos().CopyFrom(parentCoaster.AttitudeMatrix.Pos());
    }
}
