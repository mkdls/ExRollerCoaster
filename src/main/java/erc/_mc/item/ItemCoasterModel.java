package erc._mc.item;

import erc.model.CoasterBlockModel;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.IModelController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCoasterModel extends Item implements IItemBlockModelHolder {


    @Override
    public IModel GetBlockModel(IModelController controller) {
        return new CoasterBlockModel(controller);
    }

    @Override
    public void OnSetInventory(IModel part, int slotidx, ItemStack itemStack, EntityPlayer player) { }
}
