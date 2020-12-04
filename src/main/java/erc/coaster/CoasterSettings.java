package erc.coaster;

import mochisystems._mc._1_7_10._core.Logger;
import mochisystems.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

public class CoasterSettings{
    public String ModelID;
    public String ModelName;
    public String TextureName;
    public double Width;
    public double Height;
    public double Weight;
    public double ConnectLength;
    public SeatData[] Seats;
    public class SeatData{
        public float SeatSize;
        public Vec3d LocalPosition = new Vec3d();
        public Vec3d LocalRotationDegree = new Vec3d();
    }
    public IModelCustom Model;
    public ResourceLocation Texture;

    protected CoasterSettings(){}

    public static CoasterSettings Default()
    {
        CoasterSettings settings = new CoasterSettings();
        settings.Width = 1.2;
        settings.Height = 1.2;
        settings.Weight = 1000;
        settings.ConnectLength = 1.7;
        settings.setSeatNum(1);
        settings.Seats[0].SeatSize = 1;
        settings.Seats[0].LocalPosition = new Vec3d(0.9, 0, 0);
        settings.Seats[0].LocalRotationDegree = new Vec3d(0, 0, 0);
        settings.ModelID = "_Default";
        return settings;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append(Width); str.append(',');
        str.append(Height); str.append(',');
        str.append(Weight); str.append(',');
        str.append(ConnectLength); str.append(',');
        str.append(Seats.length); str.append(',');
        for (SeatData Seat : Seats) {
            str.append(Seat.SeatSize); str.append(',');
            str.append(Seat.LocalPosition.x); str.append(',');
            str.append(Seat.LocalPosition.y); str.append(',');
            str.append(Seat.LocalPosition.z); str.append(',');
            str.append(Seat.LocalRotationDegree.x); str.append(',');
            str.append(Seat.LocalRotationDegree.y); str.append(',');
            str.append(Seat.LocalRotationDegree.z);
        }
        return str.toString();
    }

    public void FromString(String str)
    {
        if("".equals(str)) return;
        String[] data = str.split(",");
        int i = 0;
        try{
            Width = Double.valueOf(data[i++]);
            Height = Double.valueOf(data[i++]);
            Weight = Double.valueOf(data[i++]);
            ConnectLength = Double.valueOf(data[i++]);
            setSeatNum(Integer.valueOf(data[i++]));
            for(int s = 0; s < Seats.length; ++s)
            {
                setSeatSize(s, Float.valueOf(data[i++]));
                setSeatOffset(s, Float.valueOf(data[i++]), Float.valueOf(data[i++]), Float.valueOf(data[i++]));
                setSeatRotation(s, Float.valueOf(data[i++]), Float.valueOf(data[i++]), Float.valueOf(data[i++]));
            }
        }catch (NumberFormatException e){
            Logger.warn("SeatData could not be restored from string.");
        }
    }

    public boolean setSeatNum(int num)
    {
        if(num < 1)throw new IllegalArgumentException("座席数が少なすぎます。1以上を指定してください。");
        this.Seats = new SeatData[num];
        for(int i = 0; i < num; ++i) Seats[i] = new SeatData();
        return true;
    }

    /*
	 * 指定の番号の座席の位置設定を行います。　index:座席番号[0,設定した数-1] CorePosX:横方向　y:高さ方向　z:進行方向　rotation:座席の回転量(degree)
	 */
    public boolean setSeatOffset(int index, double x, double y, double z)
    {
        //if(index < 0 || this.Seats.size() <= index)throw new IllegalArgumentException("座席が少なすぎます。");
        Seats[index].LocalPosition = new Vec3d(x, y, z);
        return true;
    }

    /*
     * 指定の番号の座席の回転量設定を行います。　index:座席番号[1-設定した数]　rotX:進行方向軸の回転量　rotY:垂直軸の回転量　rotZ:水平軸の回転量
     * 回転量の単位は弧度法（radian）です。
     * 回転の適用順はZ>Y>Xです。
     */
    public boolean setSeatRotation(int index, double rotX, double rotY, double rotZ)
    {
        //if(index < 0 || index >= this.Seats.size())return false;
        Seats[index].LocalRotationDegree = new Vec3d(0, 0, 0);
        return true;
    }

    /*
     * 指定の番号の座席の当たり判定設定を行います。　index:座席番号[1-設定した数]　size:座席当たり判定の立方体のサイズ
     * 座席に座るとき、連結コースターを接続するときにこの当たり判定が使われます。
     */
    public boolean setSeatSize(int index, float size)
    {
        //if(index < 0 || index >= this.Seats.size())return false;
        Seats[index].SeatSize = size;
        return true;
    }
}