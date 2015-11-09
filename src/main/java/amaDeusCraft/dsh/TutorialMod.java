package amaDeusCraft.dsh;

import amaDeusCraft.dsh.helper.Reference;
import amaDeusCraft.dsh.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class TutorialMod {
	
	@Mod.Instance(Reference.MOD_ID)
	public static TutorialMod instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROX_CLASS, serverSide = Reference.SERVER_PROX_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
	}
	

	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		proxy.registerRenders();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		//MinecraftForge.EVENT_BUS.register(new TestBar(Minecraft.getMinecraft()));
	}
	
}
