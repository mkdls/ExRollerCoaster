package erc.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;
import erc._core.ERC_Core;
import erc._core.ERC_Logger;
import erc.coaster.CoasterSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelPackLoader {

	private static String defaultCoasterID;
	
	public class CoasterPackData{
		public String IconName;
		public CoasterSettings MainSetting;
		public CoasterSettings ConnectSetting;
	}

	public static class RailPack{
		public IModelCustom[] RailModels;
		public ResourceLocation[] RailTexs;
		public String IconStr;
	}

	private static Map<String, CoasterPackData> CoasterModelDataMap = new HashMap<>();
	private static Map<String, RailPack> RailPackMap = new HashMap<>();
	
	public static void Load() {
		try {
			loadDefaultCoasterModelPack();
			loadExternalCoasterModelPack();
//			loadRailModelPack();
		}
		catch (Exception e) {
			ERC_Logger.error("モデルのロードに失敗しました。");
			ERC_Logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static CoasterSettings GetHeadCoasterSettings(String id) {
		if(!CoasterModelDataMap.containsKey(id)) id = defaultCoasterID;
		return CoasterModelDataMap.get(id).MainSetting;
	}

	public static CoasterSettings GetConnectCoasterSettings(String id) {
		if(!CoasterModelDataMap.containsKey(id)) id = defaultCoasterID;
		return CoasterModelDataMap.get(id).ConnectSetting;
	}

	public static String GetIconName(String id) {
		return CoasterModelDataMap.get(id).IconName;
	}



//	public static ERC_ModelAddedRail getRailModel(int Index, int flag)
//	{
////		if(Index < 0 || Index >= RailPackMap.size())Index = 0;
////		if(flag < 0 || flag >= 6)flag = 0;
////		return RailPackMap.get(Index).getModelSet(flag);
//		return null;
//	}

	public static String[] getRailIconStrings()
	{
		String[] ret = new String[RailPackMap.size()+1];
		ret[0] = "erc:railicondef";
		int idx = 1;
//		for(RailPack rp : RailPackMap)
//		{
//			ret[idx++] = rp.IconStr;
//		}
		return ret;
	}

	public static Collection<CoasterPackData> getCoasterModelCollection()
	{
		return CoasterModelDataMap.values();
	}

	public static int getRailModelCount()
	{
		return RailPackMap.size();
	}




    private static void loadDefaultCoasterModelPack()
	{
		CoasterPackData def = loadSettingsFromJsonForDef(ERC_Core.MODID+":models/coaster.json");
		loadSettingsFromJsonForDef(ERC_Core.MODID+":models/double.json");
		loadSettingsFromJsonForDef(ERC_Core.MODID+":models/inverted.json");
		defaultCoasterID = def.MainSetting.ModelID;
	}

	private static void loadExternalCoasterModelPack()
	{
        try {
            for (Path path : EnumerateExternalModelFolder()) {
                if (Files.isDirectory(path)) {
                    loadModelFromFolder(path);
                } else if (path.toAbsolutePath().toString().contains(".zip")) {
                    //loadModelFromZip(path);
                }
            }
        }
        catch(IOException e){
            ERC_Logger.warn("カスタムモデル読み込み中にエラーが発生しました。");
        }
	}

	private static DirectoryStream<Path> EnumerateExternalModelFolder() throws IOException
	{
		Path modelFolderPath = Paths.get(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/ERCModels");
        if (!Files.exists(modelFolderPath)) {
            Files.createDirectory(modelFolderPath);
        }
		return Files.newDirectoryStream(modelFolderPath);
	}

	private static void loadModelFromFolder(Path folderPath) throws IOException
	{
		String folderName = folderPath.toString();
		Path settingFile = null;
		try {
			for (Path filePath : Files.newDirectoryStream(folderPath)) {
				String fileName = filePath.toString().toLowerCase();
				if (fileName.contains(".json")) settingFile = filePath;
			}
		}
		catch (IOException e){
			return;
		}

		if (settingFile == null) {
			throw new IOException(folderPath.getFileName().toString() + "モデルのフォルダにsetting.jsonがありません");
		}
		CoasterPackData packData = loadSettingsFromJson(settingFile);

		if (packData.MainSetting.ModelName == null) {
			throw new IOException(folderPath.getFileName().toString() + "モデルのフォルダにmain.objがありません");
		}
		if (packData.ConnectSetting.ModelName == null) {
			packData.ConnectSetting.ModelName = packData.MainSetting.ModelName;
		}
		if (packData.IconName == null) {
			throw new IOException(folderPath.getFileName().toString() + "モデルのフォルダにアイコンファイルが見つかりません");
		}
		if (packData.MainSetting.TextureName == null) {
			throw new IOException(folderPath.getFileName().toString() + "モデルのフォルダにメインのテクスチャがありません");
		}
		if (packData.ConnectSetting.TextureName == null) {
			packData.ConnectSetting.TextureName = packData.MainSetting.TextureName;
		}

        packData.IconName = "minecraft:"+ packData.IconName;
        packData.MainSetting.Model = loadModel("minecraft:models/"+packData.MainSetting.ModelName);
		packData.ConnectSetting.Model = loadModel("minecraft:models/"+packData.ConnectSetting.ModelName);
		packData.MainSetting.Texture = new ResourceLocation("minecraft:models/"+packData.MainSetting.TextureName);
		packData.ConnectSetting.Texture = new ResourceLocation("minecraft:models/"+packData.ConnectSetting.TextureName);

		AddCoasterModelData(packData);
	}

	private static CoasterPackData loadSettingsFromJsonForDef(String jsonName)
	{
		// load file
        StringBuilder builder = new StringBuilder();
        try {
            ResourceLocation setting = new ResourceLocation(jsonName);
            InputStream stream = ERC_Core.GetInModPackageFileStream(setting);
            InputStreamReader reader = new InputStreamReader(stream);
            char[] buffer = new char[512];
            int read;
			while (0 <= (read = reader.read(buffer))) {
				builder.append(buffer, 0, read);
			}
		}
		catch (IOException e){
			ERC_Logger.error("setting.jsonがModPackage内にありませんでした。");
			return null;
		}

		// to json
		Gson gson = new Gson();
		CoasterPackData data = gson.fromJson(builder.toString(), CoasterPackData.class);
		if(data.ConnectSetting == null) data.ConnectSetting = data.MainSetting;
        data.IconName = ERC_Core.MODID + ":" + data.IconName;
        data.MainSetting.Model = loadModel(ERC_Core.MODID+":models/"+data.MainSetting.ModelName);
		data.ConnectSetting.Model = loadModel(ERC_Core.MODID+":models/"+data.ConnectSetting.ModelName);
		data.MainSetting.Texture = new ResourceLocation(ERC_Core.MODID+":models/"+data.MainSetting.TextureName);
		data.ConnectSetting.Texture = new ResourceLocation(ERC_Core.MODID+":models/"+data.ConnectSetting.TextureName);

		AddCoasterModelData(data);
		return data;
	}

	private static CoasterPackData loadSettingsFromJson(Path path) throws IOException
    {
        // load file
        StringBuilder builder = new StringBuilder();
        for(String str : Files.readAllLines(path)){
            builder.append(str);
        }

        // to json
        Gson gson = new Gson();
        return gson.fromJson(builder.toString(), CoasterPackData.class);
    }

	private static IModelCustom loadModel(String fileNameWithDomain)
	{
		return AdvancedModelLoader.loadModel(new ResourceLocation(fileNameWithDomain));
	}

	private static void AddCoasterModelData(CoasterPackData data)
	{
		data.MainSetting.ModelID = ERC_Core.MODID+"."+data.MainSetting.ModelID;
		if(data.MainSetting != data.ConnectSetting)
		    data.ConnectSetting.ModelID = ERC_Core.MODID+"."+data.ConnectSetting.ModelID;
		CoasterModelDataMap.put(data.MainSetting.ModelID, data);
	}
}
