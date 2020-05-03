package erc.rail;

import erc.rail.Rail;
import io.netty.buffer.ByteBuf;

public class ConstVelocityRail extends Rail{

    private double velocitySetting = 0.1;
    public double GetVelocity(){ return velocitySetting; }
    public void setAccel(double velocity){ this.velocitySetting = velocity; }

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
        velocitySetting = 0.1;
        if(!isActive) return speed * 0.85;
        double speedDiff = speed - velocitySetting;
        return speedDiff * 0.55 + velocitySetting;
    }

    @Override
    public void WriteToBytes(ByteBuf buf)
    {
        super.WriteToBytes(buf);
        buf.writeBoolean(this.isActive);
        buf.writeDouble(velocitySetting);
    }

    @Override
    public void ReadFromBytes(ByteBuf buf)
    {
        super.ReadFromBytes(buf);
        isActive = buf.readBoolean();
        velocitySetting = buf.readDouble();
    }
}
