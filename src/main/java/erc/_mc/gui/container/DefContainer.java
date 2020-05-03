package erc._mc.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class DefContainer extends Container {

    int xCoord, yCoord, zCoord;
    TileEntity tile;
    
    public DefContainer(int x, int y, int z, TileEntity tile) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.tile = tile;
    }
 
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        //�����A�u���b�N�Ƃ̈ʒu�֌W��GUI���䂵�����Ȃ�A��������g��
//        return player.getDistanceSq(this.CorePosX + 0.5D, this.CorePosY + 0.5D, this.CorePosZ + 0.5D) <= 64D;
        return true;
    }
}
