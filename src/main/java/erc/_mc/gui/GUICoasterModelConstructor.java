package erc._mc.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._mc.gui.container.ContainerCoasterModelConstructor;
import erc._mc.network.ERC_PacketHandler;
import erc._mc.tileentity.TileEntityCoasterModelConstructor;
import io.netty.buffer.ByteBuf;
import mochisystems._mc.gui.GUIBlockModelerBase;
import mochisystems.blockcopier.message.MessageChangeLimitLine;
import mochisystems.blockcopier.message.PacketHandler;
import mochisystems.math.Vec3d;
import mochisystems.util.gui.GuiButtonWrapper;
import mochisystems.util.gui.GuiFormatedTextField;
import mochisystems.util.gui.GuiGroupCanvas;
import mochisystems.util.gui.GuiUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class GUICoasterModelConstructor extends GUIBlockModelerBase {

    public final int GUIAddLength = 0;
    public final int GUIAddWidth = 1;
    public final int GUIAddHeight = 2;
    public final int GUIConstruct = 11;
    public final int GUIDrawEntityFlag = 12;

    static final int GuiModelScale = 100;
    static final int GuiCoreOffsetX = 101;
    static final int GuiCoreOffsetY = 102;
    static final int GuiCoreOffsetZ = 103;
    static final int GuiCoreRotateX = 104;
    static final int GuiCoreRotateY = 105;
    static final int GuiCoreRotateZ = 106;

    protected GuiGroupCanvas Canvas = new GuiGroupCanvas();
    private TileEntityCoasterModelConstructor tile;

    private GuiTextField textField;
//    protected ContainerFerrisConstructor container;

    private int blockposX;
    private int blockposY;
    private int blockposZ;

    public GUICoasterModelConstructor(int x, int y, int z, InventoryPlayer playerInventory, TileEntityCoasterModelConstructor tile) {
        super(x, y, z, new ContainerCoasterModelConstructor(playerInventory, tile));
        this.tile = tile;
        this.tile.gui = this;
        blockposX = x;
        blockposY = y;
        blockposZ = z;
//        container = (ContainerFerrisConstructor) inventorySlots;
    }

    protected void UpdateCameraPosFromCore(Vec3d dest, float tick) {
        dest.CopyFrom(Vec3d.Zero);
    }

    @Override
    public void initGui() {
        super.initGui();
        Canvas.Init();


        int textFieldWidth = 95;
        int textFieldHeight = 12;
        this.textField = new GuiTextField(this.fontRendererObj,
                (width - textFieldWidth) / 2, 4,
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
        //modelScale
        Canvas.Register(0,
                new GuiFormatedTextField(fontRendererObj, 40, 150, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.1f", tile.modelScale),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiModelScale, Float.parseFloat(t))));

        //model offset
        Canvas.Register(-1,
                new GuiFormatedTextField(fontRendererObj, 60, 134, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.2f", tile.modelOffset.x),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiCoreOffsetX, Float.parseFloat(t))));
        Canvas.Register(-1,
                new GuiFormatedTextField(fontRendererObj, 60, 154, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.2f", tile.modelOffset.y),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiCoreOffsetY, Float.parseFloat(t))));
        Canvas.Register(-1,
                new GuiFormatedTextField(fontRendererObj, 60, 174, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.2f", tile.modelOffset.z),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiCoreOffsetZ, Float.parseFloat(t))));

        //rotate
        Canvas.Register(-1,
                new GuiFormatedTextField(fontRendererObj, 160, 134, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.2f", tile.modelRotate.x),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiCoreRotateX, Float.parseFloat(t))));
        Canvas.Register(-1,
                new GuiFormatedTextField(fontRendererObj, 160, 154, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.2f", tile.modelRotate.y),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiCoreRotateY, Float.parseFloat(t))));
        Canvas.Register(-1,
                new GuiFormatedTextField(fontRendererObj, 160, 174, 30, 11, 0xffffff, 12,
                        () -> String.format("%4.2f", tile.modelRotate.z),
                        s -> s.matches(GuiFormatedTextField.regexNumber),
                        t -> SendMessageToSetParam(GuiCoreRotateZ, Float.parseFloat(t))));


//        Canvas.addButton2(0, width-70, 14, "copy", GUIAddCopyNum);

//        Canvas.addCheckButton(0, width - 20, 42, tile.FlagDrawCore, "draw core", MessageFerrisMisc.GUIDrawCoreFlag);
//        GuiUtil.addCheckButton(Canvas, buttonList, 0, width - 20, 72, tile.FlagDrawEntity, "draw Mobs", GUIDrawEntityFlag);
        GuiUtil.addCheckButton(Canvas, fontRendererObj, 0, width - 20, 72, tile.FlagDrawEntity, "draw Mobs",
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
    public void onGuiClosed() {
        tile.modelName = textField.getText();
        super.onGuiClosed();
    }

    public String getName() {
        return textField.getText();
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);

        this.fontRendererObj.drawString("Name:", width / 2 - 76, 4, 0xB0B0B0);

//        for(GUIName g :  GUINameMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.x,g.y,0x404040);
//        }

        drawString(this.fontRendererObj, String.format("Position  %d", tile.getLimitFrameLength()), 2, 20, 0xffffff);
        drawString(this.fontRendererObj, String.format("Width   %d", tile.getLimitFrameWidth()), 2, 50, 0xffffff);
        drawString(this.fontRendererObj, String.format("Height  %d", tile.getLimitFrameHeight()), 2, 80, 0xffffff);

        drawString(this.fontRendererObj, "RotateCopy", width - 76, 3, 0xffffff);
        drawString(this.fontRendererObj, "Is Draw Core", width - 76, 33, 0xffffff);
        drawString(this.fontRendererObj, "Copy Entity", width - 76, 63, 0xffffff);
        drawString(this.fontRendererObj, "Make Connector", width - 76, 93, 0xffffff);
        drawString(this.fontRendererObj, "Copy Mode", width - 76, 123, 0xffffff);
//        drawRightedString(this.fontRendererObj, Integer.toString(tile.copyNum), width-2, 15, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        Canvas.DrawContents(mouseX, mouseY);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
//        int data = (button.id - Canvas.GetBaseIdFromButtonId(button.id));
//        int flag = Canvas.GetFlagFromButtonId(button.id);

//        switch (flag) {
//            case GUIConstruct:
//                tile.startConstructModel();
//                return;
//            case GUIAddLength:
//            case GUIAddHeight:
//            case GUIAddWidth:
//                int add = 0;
//                int type = 0;
//                switch (flag) {
//                    case GUIAddLength:
//                        type = MessageChangeLimitLine.Length;
//                        break;
//                    case GUIAddHeight:
//                        type = MessageChangeLimitLine.Height;
//                        break;
//                    case GUIAddWidth:
//                        type = MessageChangeLimitLine.Width;
//                        break;
//                }
//                switch (data) {
//                    case 0:
//                        add = -50;
//                        break;
//                    case 1:
//                        add = -5;
//                        break;
//                    case 2:
//                        add = -1;
//                        break;
//                    case 3:
//                        add = 1;
//                        break;
//                    case 4:
//                        add = 5;
//                        break;
//                    case 5:
//                        add = 50;
//                        break;
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

    private void ChangeLimitLine(int dirType, int add) {
        MessageChangeLimitLine m = new MessageChangeLimitLine(blockposX, blockposY, blockposZ, dirType, add);
        PacketHandler.INSTANCE.sendToServer(m);
    }

    ////////////////////////////text field//////////////////////////////////

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char c, int keyCode) {
        if (this.textField.textboxKeyTyped(c, keyCode)) {
            this.writeName();
        }
        if (! Canvas.KeyTyped(c, keyCode))
        {
            super.keyTyped(c, keyCode);
        }
    }

    private void writeName() {
        String s = this.textField.getText();
        Slot slot = this.inventorySlots.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName())) {
            s = "";
        }

        tile.modelName = s;
//       ((ERC_ContainerFerris)this.inventorySlots).updateItemName(s);
//        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", s.getBytes(Charsets.UTF_8)));
    }

    /**
     * Called when the mouse is clicked.
     */
    public void mouseClicked(int x, int y, int buttonId) {
        super.mouseClicked(x, y, buttonId);
//        this.textField.mouseClicked(x, y, buttonId);
        Canvas.MouseClicked(x, y, buttonId);

    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.textField.drawTextBox();
    }

    public void SendMessageToSetParam(int flag, float value) {
        Message packet = new Message(blockposX, blockposY, blockposZ, flag, value);
        ERC_PacketHandler.INSTANCE.sendToServer(packet);
    }

    public static class Message implements IMessage, IMessageHandler<Message, IMessage> {

        public int x, y, z;
        public int flag;
        public float value;

        public Message() {}

        public Message(int x, int y, int z, int flag, float value) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.flag = flag;
            this.value = value;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.x = buf.readInt();
            this.y = buf.readInt();
            this.z = buf.readInt();
            this.flag = buf.readInt();
            this.value = buf.readFloat();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.x);
            buf.writeInt(this.y);
            buf.writeInt(this.z);
            buf.writeInt(this.flag);
            buf.writeFloat(this.value);
        }

        @Override
        public IMessage onMessage(Message message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
            if ((tile instanceof TileEntityCoasterModelConstructor)) {
                TileEntityCoasterModelConstructor TILE = (TileEntityCoasterModelConstructor) tile;
                switch (message.flag) {
                    case GuiModelScale: TILE.modelScale = message.value; break;
                    case GuiCoreOffsetX: TILE.modelOffset.x = message.value; break;
                    case GuiCoreOffsetY: TILE.modelOffset.y = message.value; break;
                    case GuiCoreOffsetZ: TILE.modelOffset.z = message.value; break;
                    case GuiCoreRotateX: TILE.modelRotate.x = message.value; break;
                    case GuiCoreRotateY: TILE.modelRotate.y = message.value; break;
                    case GuiCoreRotateZ: TILE.modelRotate.z = message.value; break;
                }
            }
            player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
            return null;
        }
    }

}