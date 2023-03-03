package learwin.randomplace;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.7.10]")
public class RandomPlace {

    public static Item randomPlacer;

    @Mod.EventHandler

    public void preInit(FMLPreInitializationEvent event) {
        randomPlacer = new RandomPlacerItem().setUnlocalizedName("randomPlacer");
        GameRegistry.registerItem(randomPlacer, Tags.MODNAME + "randomPlacer");

        GameRegistry.addRecipe(
                new ItemStack(randomPlacer),
                new Object[] { "  S", "ISI", "II ", 'S', Items.stick, 'I', Items.iron_ingot });
    }
}
