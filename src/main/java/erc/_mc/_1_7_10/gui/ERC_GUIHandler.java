
package erc._mc._1_7_10.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import erc._mc._1_7_10._core.ERC_Core;
import erc._mc._1_7_10.gui.container.ContainerCoasterModelConstructor;
import erc._mc._1_7_10.tileentity.TileEntityCoasterModelConstructor;
import erc._mc._1_7_10.tileentity.TileEntityRailModelConstructor;
import erc._mc._1_7_10.gui.container.ContainerRailModelConstructor;
import erc._mc._1_7_10.gui.container.DefContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class ERC_GUIHandler implements IGuiHandler {
		
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case ERC_Core.GUIID_RailBase :
                return new DefContainer(x, y, z, null);
            case ERC_Core.GUIID_RailModelConstructor :
                return new ContainerRailModelConstructor(player.inventory, (TileEntityRailModelConstructor) world.getTileEntity(x, y, z));
            case ERC_Core.GUIID_CoasterModelConstructor :
                return new ContainerCoasterModelConstructor(player.inventory, (TileEntityCoasterModelConstructor) world.getTileEntity(x, y, z));

        }
        return null;
    }
    
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case ERC_Core.GUIID_RailBase :
                return new GUIRail(x, y, z);
            case ERC_Core.GUIID_RailModelConstructor :
                return new GUIRailModelConstructor(x, y, z, player.inventory, (TileEntityRailModelConstructor) world.getTileEntity(x, y, z));
            case ERC_Core.GUIID_CoasterModelConstructor :
                return new GUICoasterModelConstructor(x, y, z, player.inventory, (TileEntityCoasterModelConstructor) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
