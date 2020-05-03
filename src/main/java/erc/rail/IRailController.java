package erc.rail;

import io.netty.buffer.ByteBuf;
import mochisystems.util.IModelController;

public interface IRailController extends IModelController{
    IRail GetRail(int x, int y, int z);
    int x();
    int y();
    int z();
    void NotifyChange();
    void FixConnection();
    void SyncData();
    void SyncMiscData();
    boolean RegisterPrevRailPos(IRailController prevController);
    boolean RegisterNextRailPos(IRailController nextController);
    void RegisterBufferForSync(ByteBuf buf);
    void ReadBufferForSync(ByteBuf buf);
}
