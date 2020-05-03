package erc.rail;

import erc.rail.Rail;
import io.netty.buffer.ByteBuf;

public class DetectorRail extends Rail{

    private boolean existCoaster = false;
    public boolean ExistCoaster()
    {
        return existCoaster;
    }

    @Override
    public void OnEnterCoaster()
    {
        existCoaster = true;
        controller.NotifyChange();
        controller.SyncMiscData();
    }

    @Override
    public void OnLeaveCoaster()
    {
        existCoaster = false;
        controller.NotifyChange();
        controller.SyncMiscData();
    }

    @Override
    public void OnDeleteCoaster()
    {
        OnLeaveCoaster();
    }

    public void WriteToBytes(ByteBuf buffer)
    {
        super.WriteToBytes(buffer);
        buffer.writeBoolean(existCoaster);
    }

    public void ReadFromBytes(ByteBuf buffer)
    {
        super.ReadFromBytes(buffer);
        existCoaster = buffer.readBoolean();
    }
}
