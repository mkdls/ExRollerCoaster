package erc.asm;

import mochisystems._mc._1_7_10.easyasm.EasyAsm;
import mochisystems._mc._1_7_10.easyasm.adaptor.FieldAdapter_RemoveFinal;
import mochisystems._mc._1_7_10.easyasm.adaptor.Insert;
import mochisystems._mc._1_7_10.easyasm.distributor.ClassAdapter;
import mochisystems._mc._1_7_10.easyasm.distributor.FieldAdapter;
import mochisystems._mc._1_7_10.easyasm.distributor.MethodAdapter;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class AsmTransform extends EasyAsm implements Opcodes {

	@Override
	protected void MakeMap()
	{
        add(new ClassAdapter("net.minecraft.client.renderer.Tessellator")
//				.add(new FieldAdapter("field_78398_a", "instance", new FieldAdapter_Logger()))
			.add(new FieldAdapter("field_78398_a", "instance", new FieldAdapter_RemoveFinal()))
        );
		add(new ClassAdapter("erc.renderer.rail.BlockModelRailRenderer")
			.add(new MethodAdapter("SetOriginalTessallator", "SetOriginalTessallator", "(Lnet/minecraft/client/renderer/Tessellator;)V",
                new Insert.First(
                        (MethodVisitor mv) -> {
                            mv.visitVarInsn(Opcodes.ALOAD, 1);
                            mv.visitFieldInsn(PUTSTATIC, "net/minecraft/client/renderer/Tessellator", "instance", "Lnet/minecraft/client/renderer/Tessellator;");
                            //PUTSTATIC net/minecraft/client/renderer/Tessellator.instance : Lnet/minecraft/client/renderer/Tessellator;
                        }
                )
            ))
		);
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {AsmTransform.class.getName()};
	}
	@Override
	public String getModContainerClass() {
		return modContainer.class.getName();
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader)
	{
		classLoader.registerTransformer(AsmTransform.class.getName());
	}
}