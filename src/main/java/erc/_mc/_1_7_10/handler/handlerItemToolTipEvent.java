package erc._mc._1_7_10.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class handlerItemToolTipEvent {
	
	@SubscribeEvent
	public void onRenderItemText(ItemTooltipEvent event)
	{
			event.toolTip.add(event.itemStack.getItem().getUnlocalizedName());
	}
}
