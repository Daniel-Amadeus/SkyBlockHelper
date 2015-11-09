package amaDeusCraft.dsh.proxy;

import org.lwjgl.input.Keyboard;

import amaDeusCraft.dsh.ModController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy{

	public static final KeyBinding KEYBINDING_AUTOMINE = new KeyBinding("Auto mine", Keyboard.KEY_M, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_HOME = new KeyBinding("/home", Keyboard.KEY_H, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_BAL = new KeyBinding("/bal", Keyboard.KEY_B, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_SHOP = new KeyBinding("/warp shop", Keyboard.KEY_V, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_NETHERWART_FARM = new KeyBinding("Netherwart Farming", Keyboard.KEY_N, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_TESTBUTTON = new KeyBinding("Test button", Keyboard.KEY_Y, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_KITS = new KeyBinding("/kit", Keyboard.KEY_K, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_PUMPKIN_CYCLE = new KeyBinding("farm cycle", Keyboard.KEY_P, "Deus' Skyblock Helper");

	public static final KeyBinding KEYBINDING_VISIT_GRIFF = new KeyBinding("/visit griff", Keyboard.KEY_UP, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_VISIT_NEKOEMMI = new KeyBinding("/visit NekoEmmi", Keyboard.KEY_DOWN, "Deus' Skyblock Helper");
	
	public static final KeyBinding KEYBINDING_JUMP = new KeyBinding("/jump", Keyboard.KEY_J, "Deus' Skyblock Helper");
	public static final KeyBinding KEYBINDING_CRAFT = new KeyBinding("/workbench", Keyboard.KEY_C, "Deus' Skyblock Helper");
	

	public static final KeyBinding KEYBINDING_LOG = new KeyBinding("toggle log", Keyboard.KEY_L, "Deus' Skyblock Helper");
	
	@Override
    public void init(FMLInitializationEvent e) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		ModController modController = new ModController(mc);
		FMLCommonHandler.instance().bus().register(modController);
		
		
//		LogScreen logScreen = new LogScreen(mc);
//		MinecraftForge.EVENT_BUS.register(logScreen);
//		
//		Bot bot = new Bot(logScreen);
//		FMLCommonHandler.instance().bus().register(bot);
//		
//		CountdownScreen countdownScreen = new CountdownScreen(mc);
//		MinecraftForge.EVENT_BUS.register(countdownScreen);
//
//		ChatHandler chatHandler = new ChatHandler();
//		MinecraftForge.EVENT_BUS.register(chatHandler);
//		FMLCommonHandler.instance().bus().register(chatHandler);
//		
//		BalanceScreen balanceScreen = new BalanceScreen(mc);
//		MinecraftForge.EVENT_BUS.register(balanceScreen);
//		
//		MyConnectionHandler connectionHandler = new MyConnectionHandler();
//		FMLCommonHandler.instance().bus().register(connectionHandler);
//		
//		AuctionScreen auctionScreen = new AuctionScreen(mc);
//		MinecraftForge.EVENT_BUS.register(auctionScreen);
		
		ClientRegistry.registerKeyBinding(KEYBINDING_AUTOMINE);
        ClientRegistry.registerKeyBinding(KEYBINDING_HOME);
        ClientRegistry.registerKeyBinding(KEYBINDING_BAL);
        ClientRegistry.registerKeyBinding(KEYBINDING_SHOP);
        ClientRegistry.registerKeyBinding(KEYBINDING_NETHERWART_FARM);
        ClientRegistry.registerKeyBinding(KEYBINDING_TESTBUTTON);
        ClientRegistry.registerKeyBinding(KEYBINDING_KITS);
        ClientRegistry.registerKeyBinding(KEYBINDING_PUMPKIN_CYCLE);
        ClientRegistry.registerKeyBinding(KEYBINDING_LOG);
        
    }
	
	@Override
	public void registerRenders(){
	}

}
