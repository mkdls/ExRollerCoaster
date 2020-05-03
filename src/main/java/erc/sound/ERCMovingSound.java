package erc.sound;

import erc._mc.entity.EntityCoasterSeat;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ERCMovingSound extends MovingSound {
	
	private final EntityPlayer player;
	private final EntityCoasterSeat seat;

	public ERCMovingSound(EntityPlayer player, EntityCoasterSeat seat)
	{
		super(new ResourceLocation("minecraft:minecart.base"));
	    this.player = player;
	    this.seat = seat;
	    this.field_147666_i = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.field_147665_h = 0;
	    this.field_147663_c = 0.2f;
	}

	public void update() {
		if ((!this.seat.isDead) && (this.player.isRiding()) && (this.player.ridingEntity == this.seat)) 
		{
			float f = seat.seat==null ? 0 : ((float) this.seat.seat.GetParent().getSpeed());
			if (Math.abs(f) >= 0.01D) 
			{
				this.volume = (MathHelper.clamp_float(Math.abs(f)*0.5F, 0.0F, 1.0F));
				
//				ERC_Logger.debugInfo(""+volume);
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else 
		{
			this.donePlaying = true; //or repeat?
		}
	}
}