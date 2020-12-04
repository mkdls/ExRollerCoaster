package erc._mc._1_7_10.handler;

import erc._mc._1_7_10.item.ItemCoaster;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import erc._mc._1_7_10._core.ERC_Reflection;
import erc._mc._1_7_10.item.ERC_ItemSwitchingRailModel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;

public class ERC_InputEventHandler {
	
	Minecraft mc;
	
	int wheelsum;
	
	public ERC_InputEventHandler(Minecraft minecraft)
	{
	    mc = minecraft;
	    wheelsum = 0;
	}
	
	@SubscribeEvent
	public void interceptMouseInput(MouseEvent event)
	{
	    if(Keyboard.isKeyDown(Keyboard.KEY_LMENU))
	    {
	    	wheelsum = event.dwheel;
	    	ERC_Reflection.setMouseDHweel(0);
	    
		    if(wheelsum != 0)
		    {
				if(mc.thePlayer == null)return;
				ItemStack heldItemstack = mc.thePlayer.getHeldItem();
				if(heldItemstack == null)return;
				Item heldItem = heldItemstack.getItem();
				if(heldItem == null)return;
				if(heldItem instanceof ItemCoaster)
				{
					((ItemCoaster)heldItem).ScrollMouseHweel(event.dwheel);
				}
				else if(heldItem instanceof ERC_ItemSwitchingRailModel)
				{
					((ERC_ItemSwitchingRailModel)heldItem).ScrollMouseHweel(event.dwheel);
				}
		    }
	    }
	}
}
