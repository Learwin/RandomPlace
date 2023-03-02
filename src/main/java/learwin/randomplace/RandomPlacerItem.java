package learwin.randomplace;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlacerItem extends Item {

    public RandomPlacerItem() {
        setCreativeTab(CreativeTabs.tabTools).setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(Tags.MODID + ":" + getUnlocalizedName());
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return false;
        Random random = new Random();
        InventoryPlayer inventory = player.inventory;
        List<ItemStack> currentBlocks = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            ItemStack selectedStack = player.inventory.getStackInSlot(i);
            if (selectedStack != null && selectedStack.getItem() instanceof ItemBlock)
                currentBlocks.add(selectedStack);
        }

        if (currentBlocks.size() <= 0)
            return false;

        ItemStack stackToPlace = currentBlocks.get(random.nextInt(currentBlocks.size()));
        int stackSize = stackToPlace.stackSize;

        int x1 = x;
        int y1 = y;
        int z1 = z;
        switch (side) {
            case 0:
                y1--;
                break;
            case 1:
                y1++;
                break;
            case 2:
                z1--;
                break;
            case 3:
                z1++;
                break;
            case 4:
                x1--;
                break;
            case 5:
                x1++;
                break;
            default:
        }
        ((ItemBlock) stackToPlace.getItem()).placeBlockAt(stackToPlace, player, world, x1, y1, z1, side, hitX, hitY, hitZ, stackToPlace.getItemDamage());
        if (!player.capabilities.isCreativeMode) {
            if (stackToPlace.stackSize - (stackSize - 1) >= 1)
                stackToPlace.stackSize = stackSize - 1;
            if (stackToPlace.stackSize - (stackSize - 1) <= 0)
                stackToPlace = null;
        }
        return true;
    }

}
