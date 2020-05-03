package erc.rail;

import mochisystems.math.Vec3d;
import io.netty.buffer.ByteBuf;

public class NonGravityRail extends Rail{

    public NonGravityRail()
    {
        super();
        gravity = new Vec3d(0, 0, 0);
    }

    public void WriteToBytes(ByteBuf buffer)
    {
        super.WriteToBytes(buffer);
        buffer.writeDouble(gravity.x);
        buffer.writeDouble(gravity.y);
        buffer.writeDouble(gravity.z);
    }

    public void ReadFromBytes(ByteBuf buffer)
    {
        super.ReadFromBytes(buffer);
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        gravity = new Vec3d(x, y, z);
    }

}
