package amaDeusCraft.dsh;

import amaDeusCraft.dsh.gui.AuctionScreen;
import amaDeusCraft.dsh.gui.BalanceScreen;
import amaDeusCraft.dsh.gui.CountdownScreen;
import amaDeusCraft.dsh.gui.LogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ModController {

	private Minecraft mc;
	private boolean isConnected = false;

	private LogScreen logScreen;

	private Bot bot;

	private CountdownScreen countdownScreen;
	private ChatHandler chatHandler;
	private BalanceScreen balanceScreen;
	private AuctionScreen auctionScreen;

	private long lastConnectingTryTime = System.currentTimeMillis();
	private long connectionStartTime = System.currentTimeMillis();

	private boolean isAutoStarted = false;

	public ModController(Minecraft mc) {
		this.mc = mc;

		logScreen = new LogScreen(mc);
		MinecraftForge.EVENT_BUS.register(logScreen);

		bot = new Bot(logScreen);
		FMLCommonHandler.instance().bus().register(bot);

		countdownScreen = new CountdownScreen(mc);
		MinecraftForge.EVENT_BUS.register(countdownScreen);

		chatHandler = new ChatHandler();
		MinecraftForge.EVENT_BUS.register(chatHandler);
		FMLCommonHandler.instance().bus().register(chatHandler);

		balanceScreen = new BalanceScreen(mc);
		MinecraftForge.EVENT_BUS.register(balanceScreen);

		auctionScreen = new AuctionScreen(mc);
		MinecraftForge.EVENT_BUS.register(auctionScreen);
	}

	private void start() {

	}
	
	private void autoStart(){
		mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
		bot.autoStart();
		isAutoStarted = true;
	}

	private void stop() {
		bot.stop();
		isAutoStarted = false;
	}

	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
		System.out.println("\n\n[=====onClientConnectedToServer=====]\n\n");
		isConnected = true;
		start();
		//		mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
		//		KeyBinding.setKeyBindState(mc.gameSettings.keyBindInventory.getKeyCode(), true);
		connectionStartTime = System.currentTimeMillis();
	}

	@SubscribeEvent
	public void onClientDisconnectionFromServer(ClientDisconnectionFromServerEvent event) {
		System.out.println("\n\n[=====onClientDisconnectionFromServer=====]\n\n");
		isConnected = false;
		lastConnectingTryTime = System.currentTimeMillis();
		stop();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent tick) {
		//System.out.println("tick");
		if (!isConnected) {
			long diff = (System.currentTimeMillis() - lastConnectingTryTime) / 1000;
			if (diff % 10 == 0)
				System.out.println((60 - diff) + "secs till reconnect...");
			if (diff >= 60) {
				System.out.println("\nstart trying to connect to skyblock.org...");
				FMLClientHandler.instance().connectToServerAtStartup("skyblock.org", 25565);
				lastConnectingTryTime = System.currentTimeMillis();
				System.out.println("\nend trying to connect to skyblock.org...");
			}
		} else {
			if (!isAutoStarted) {
				long diff = (System.currentTimeMillis() - connectionStartTime) / 1000;
				if (diff > 1/**/) {
					autoStart();
				}
			}
		}
	}
}
