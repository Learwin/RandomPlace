package learwin.randomplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class RandomPlacerItem extends Item {

    private boolean useWholeInventory = false;
    private static final String NBT_USEWHOLEINV_TAG = "useWholeInventory";

    public RandomPlacerItem() {
        setCreativeTab(CreativeTabs.tabTools).setMaxStackSize(1);
        useWholeInventory = false;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(Tags.MODID + ":" + getUnlocalizedName());
    }

    @Override
    public boolean shouldRotateAroundWhenRendering() {
        return true;
    }

    @Override
    public boolean hasEffect(ItemStack p_77636_1_) {
        return useWholeInventory;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote && player.isSneaking()) {
            NBTTagCompound nbt = itemStackIn.getTagCompound();
            useWholeInventory = !useWholeInventory;
            if (nbt == null) {
                nbt = new NBTTagCompound();
                itemStackIn.setTagCompound(nbt);
            }
            nbt.setBoolean(NBT_USEWHOLEINV_TAG, useWholeInventory);
        }
        return itemStackIn;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {

        if (world.isRemote) return false;
        Random random = new Random();
        List<ItemStack> currentBlocks = new ArrayList<>();

        int useSlots = 9;
        if (useWholeInventory) useSlots = 36;
        for (int i = 0; i < useSlots; i++) {
            ItemStack selectedStack = player.inventory.getStackInSlot(i);
            if (selectedStack != null && selectedStack.getItem() instanceof ItemBlock) currentBlocks.add(selectedStack);
        }

        if (currentBlocks.size() <= 0) return false;

        ItemStack stackToPlace = currentBlocks.get(random.nextInt(currentBlocks.size()));

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

        if (world.blockExists(x1, y1, z1)) {
            final Block block = world.getBlock(x1, y1, z1);
            if (!block.isAir(world, x1, y1, z1) && !block.isReplaceable(world, x1, y1, z1)) return false;
        }

        Block block = ((ItemBlock) stackToPlace.getItem()).field_150939_a;
        ((ItemBlock) stackToPlace.getItem()).placeBlockAt(
                stackToPlace,
                player,
                world,
                x1,
                y1,
                z1,
                side,
                hitX,
                hitY,
                hitZ,
                block.onBlockPlaced(world, x1, y1, z1, side, hitX, hitY, hitZ, stackToPlace.getItemDamage()));
        if (!player.capabilities.isCreativeMode) {
            consumeInventoryItem(stackToPlace.getItem(), stackToPlace.getItemDamage(), player.inventory.mainInventory);
            player.inventory.markDirty();
            player.inventoryContainer.detectAndSendChanges();
        }

        world.playSoundEffect(x1, y1, z1, block.stepSound.getBreakSound(), 1f, block.stepSound.getPitch());

        return true;
    }

    private int getSlotOfItem(Item itemIn, int metaData, ItemStack[] playerInv) {
        for (int i = 0; i < playerInv.length; ++i) {
            if (playerInv[i] != null && playerInv[i].getItem() == itemIn && playerInv[i].getItemDamage() == metaData)
                return i;
        }
        return -1;
    }

    public boolean consumeInventoryItem(Item itemIn, int metaData, ItemStack[] playerInv) {
        int i = getSlotOfItem(itemIn, metaData, playerInv);
        if (i < 0) return false;
        else {
            if (--playerInv[i].stackSize <= 0) {
                playerInv[i] = null;
            }
            return true;
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stackIn) {
        NBTTagCompound nbt = stackIn.getTagCompound();
        String info = "";
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stackIn.setTagCompound(nbt);
        }
        nbt.setBoolean(NBT_USEWHOLEINV_TAG, useWholeInventory);
        info = StatCollector.translateToLocal("info.placer." + useWholeInventory);

        return super.getItemStackDisplayName(stackIn) + " " + info;
    }
}
