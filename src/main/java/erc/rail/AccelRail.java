package erc.rail;

import erc.rail.Rail;
import io.netty.buffer.ByteBuf;


public class AccelRail extends Rail {

    private double accel = 5.0;
    public double getAccel(){ return accel; }
    public void setAccel(double accel){ this.accel = accel; }

    private boolean isActive = false;
    public boolean IsActive()
    {
        return isActive;
    }
    public void CycleActivation()
    {
        isActive = !isActive;
    }

    @Override
    public double RegisterAt(double pos, double speed)
    {
        return isActive ? speed : speed * 0.8;
    }

    @Override
    public double AccelAt(double pos)
    {
        if(isActive)
        {
            return super.AccelAt(pos) + accel;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void WriteToBytes(ByteBuf buf)
    {
        super.WriteToBytes(buf);
        buf.writeBoolean(this.isActive);
        buf.writeDouble(accel);
    }

    @Override
    public void ReadFromBytes(ByteBuf buf)
    {
        super.ReadFromBytes(buf);
        isActive = buf.readBoolean();
        accel = buf.readDouble();
    }
}
