package erc._mc._1_7_10.gui;

import erc._mc._1_7_10.tileentity.TileEntityRailModelConstructor;
import erc._mc._1_7_10.gui.container.ContainerRailModelConstructor;
import mochisystems._mc._1_7_10.gui.GUIBlockModelerBase;
import mochisystems._mc._1_7_10.message.MessageChangeLimitLine;
import mochisystems._mc._1_7_10.message.PacketHandler;
import mochisystems.math.Vec3d;
import mochisystems.util.gui.GuiButtonWrapper;
import mochisystems.util.gui.GuiGroupCanvas;
import mochisystems.util.gui.GuiUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

public class GUIRailModelConstructor extends GUIBlockModelerBase {

    public final int GUIAddLength = 0;
    public final int GUIAddWidth = 1;
    public final int GUIAddHeight = 2;
    public final int GUIConstruct = 11;
    public final int GUIDrawEntityFlag = 12;

    protected GuiGroupCanvas Canvas = new GuiGroupCanvas();
    private TileEntityRailModelConstructor tile;

    private GuiTextField textField;
//    protected ContainerFerrisConstructor container;

    private int blockposX;
    private int blockposY;
    private int blockposZ;

    public GUIRailModelConstructor(int x, int y, int z, InventoryPlayer playerInventory, TileEntityRailModelConstructor tile)
    {
        super(x, y, z, new ContainerRailModelConstructor(playerInventory, tile));
        this.tile = tile;
        this.tile.gui = this;
        blockposX = x;
        blockposY = y;
        blockposZ = z;
//        container = (ContainerFerrisConstructor) inventorySlots;
    }

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick)
    {
       dest.CopyFrom(Vec3d.Zero);
    }
 
	@Override
	public void initGui()
    {
        super.initGui();
        Canvas.Init();


        int textFieldWidth = 95;
        int textFieldHeight = 12;
        this.textField = new GuiTextField(this.fontRendererObj,
                (width-textFieldWidth)/2, 4,
                textFieldWidth, textFieldHeight);
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setMaxStringLength(40);
        this.textField.setText(tile.modelName);


//        GuiUtil.addButton6(Canvas, buttonList, 0, 42, 30, "Position", GUIAddLength);
//        GuiUtil.addButton6(Canvas, buttonList, 0, 42, 60, "Width", GUIAddWidth);
//        GuiUtil.addButton6(Canvas, buttonList, 0, 42, 90, "Height", GUIAddHeight);

        GuiUtil.addButton6(Canvas, -1,42, 30,
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, -50),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, -5),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, -1),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, +1),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, +5),
                () -> ChangeLimitLine(MessageChangeLimitLine.Length, +50)
        );
        GuiUtil.addButton6(Canvas, -1,42, 60,
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, -50),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, -5),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, -1),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, +1),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, +5),
                () -> ChangeLimitLine(MessageChangeLimitLine.Width, +50)
        );
        GuiUtil.addButton6(Canvas, -1,42, 90,
            () -> ChangeLimitLine(MessageChangeLimitLine.Height, -50),
            () -> ChangeLimitLine(MessageChangeLimitLine.Height, -5),
            () -> ChangeLimitLine(MessageChangeLimitLine.Height, -1),
            () -> ChangeLimitLine(MessageChangeLimitLine.Height, +1),
            () -> ChangeLimitLine(MessageChangeLimitLine.Height, +5),
            () -> ChangeLimitLine(MessageChangeLimitLine.Height, +50)
        );

//        GuiUtil.addButton1(Canvas, buttonList, 0, 60, 26, width - 70, height - 50, "Create!", GUIConstruct);
        Canvas.Register(-1,
                new GuiButtonWrapper(0,  width - 70, height - 50, 60, 26, "Create!",
                        tile::startConstructModel));

//        Canvas.addButton2(0, width-70, 14, "copy", GUIAddCopyNum);

//        Canvas.addCheckButton(0, width - 20, 42, tile.FlagDrawCore, "draw core", MessageFerrisMisc.GUIDrawCoreFlag);
//        GuiUtil.addCheckButton(Canvas, buttonList, 0, width - 20, 72, tile.FlagDrawEntity, "draw Mobs", GUIDrawEntityFlag);
        GuiUtil.addCheckButton(Canvas, fontRendererObj, 0, width - 20, 72,
                () -> tile.FlagDrawEntity, "draw Mobs",
                isOn -> SendMessageForIndex(GUIDrawEntityFlag, 0));
        //        Canvas.addCheckButton(0, width - 20, 102, tile.isCoreConnector,
//                StatCollector.translateToLocal("gui.constructor.switch.coreconnector"),
//                MessageFerrisMisc.GUIToggleCoreIsConnector);
//        Canvas.addButton1(0, 40, 16, width - 42, 132, tile.copyMode != 0 ? "Clone" : "Add", GUICopyModeChange);
    }

    public void SendMessageForIndex(int flag, int index)
    {
//        MessageFerrisMisc packet = new MessageFerrisMisc(blockposX, blockposY, blockposZ, flag, index, 0);
//        MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }

    @Override
	public void onGuiClosed()
    {
    	tile.modelName = textField.getText();
		super.onGuiClosed();
	}
    
    public String getName()
    {
    	return textField.getText();
    }
    

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);

        this.fontRendererObj.drawString("Name:", width/2-76, 4, 0xB0B0B0);
        
//        for(GUIName g :  GUINameMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.x,g.y,0x404040);
//        }

        drawString(this.fontRendererObj, String.format("Position  %d",tile.getLimitFrameLength()), 2, 20, 0xffffff);
        drawString(this.fontRendererObj, String.format("Width   %d",tile.getLimitFrameWidth()), 2, 50, 0xffffff);
        drawString(this.fontRendererObj, String.format("Height  %d",tile.getLimitFrameHeight()), 2, 80, 0xffffff);

        drawString(this.fontRendererObj, "RotateCopy", width-76, 3, 0xffffff);
        drawString(this.fontRendererObj, "Is Draw Core", width-76, 33, 0xffffff);
        drawString(this.fontRendererObj, "Copy Entity", width-76, 63, 0xffffff);
        drawString(this.fontRendererObj, "Make Connector", width-76, 93, 0xffffff);
        drawString(this.fontRendererObj, "Copy Mode", width-76, 123, 0xffffff);
//        drawRightedString(this.fontRendererObj, Integer.toString(tile.copyNum), width-2, 15, 0xffffff);
    }
 
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ)
    {
        Canvas.DrawContents(mouseX, mouseZ);
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

	@Override
	protected void actionPerformed(GuiButton button)
	{
//        int data = (button.id - Canvas.GetBaseIdFromButtonId(button.id));
//        int flag = Canvas.GetFlagFromButtonId(button.id);

//        switch(flag) {
//            case GUIConstruct:
//                tile.startConstructModel();
//                return;
//            case GUIAddLength:
//            case GUIAddHeight:
//            case GUIAddWidth:
//                int add = 0;
//                int type = 0;
//                switch(flag) {
//                    case GUIAddLength: type = MessageChangeLimitLine.Length; break;
//                    case GUIAddHeight: type = MessageChangeLimitLine.Height; break;
//                    case GUIAddWidth: type = MessageChangeLimitLine.Width; break;
//                }
//                switch (data) {
//                    case 0: add = -50; break;
//                    case 1: add = -5; break;
//                    case 2: add = -1; break;
//                    case 3: add = 1; break;
//                    case 4: add = 5; break;
//                    case 5: add = 50; break;
//                }
//                ChangeLimitLine(type, add);
//                return;
//            case GUIAddCopyNum:
//                switch (data) {
//                    case 0: data = -1; break;
//                    case 1: data = 1; break;
//                }
//                break;
//            case GUIDrawCoreFlag:
//                GuiToggleButton t = ((GuiToggleButton) button);
//                t.Toggle();
//                tile.FlagDrawCore = t.GetToggle();
//                break;
//            case GUIToggleCoreIsConnector:
//                t = ((GuiToggleButton) button);
//                t.Toggle();
//                tile.isCoreConnector = t.GetToggle();
//                break;
//            case GUIDrawEntityFlag:
//                GuiToggleButton t = ((GuiToggleButton) button);
//                t.Toggle();
//                tile.FlagDrawEntity = t.GetToggle();
//                break;
//            case GUICopyModeChange:
//                ((GuiButtonExt)button).displayString = tile.copyMode == 0 ? "Clone" : "Add";
//                break;
//        }

//		MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ,
//				flag , data, textField.getText().getBytes(Charsets.UTF_8));
//	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
	}

	private void ChangeLimitLine(int dirType, int add)
    {
        MessageChangeLimitLine m = new MessageChangeLimitLine(blockposX, blockposY, blockposZ, dirType, add);
        PacketHandler.INSTANCE.sendToServer(m);
    }
	
    ////////////////////////////text field//////////////////////////////////
	/**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        if (this.textField.textboxKeyTyped(p_73869_1_, p_73869_2_))
        {
//        	ERC_Logger.info("name:"+textField.getText());
            this.writeName();
        }
        else
        {
            super.keyTyped(p_73869_1_, p_73869_2_);
        }
    }
    
    private void writeName()
    {
        String s = this.textField.getText();
        Slot slot = this.inventorySlots.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
        {
            s = "";
        }

        tile.modelName = s;
//       ((ERC_ContainerFerris)this.inventorySlots).updateItemName(s);
//        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", s.getBytes(Charsets.UTF_8)));
    }
    
    /**
     * Called when the mouse is clicked.
     */
    public void mouseClicked(int x, int y, int buttonId)
    {
        super.mouseClicked(x, y, buttonId);
//        this.textField.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        Canvas.MouseClicked(x, y, buttonId);
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.textField.drawTextBox();
    }
}
