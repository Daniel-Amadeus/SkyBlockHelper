package amaDeusCraft.dsh;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import amaDeusCraft.dsh.ai.PlayerPathNavigateGround;
import amaDeusCraft.dsh.gui.LogScreen;
import amaDeusCraft.dsh.helper.InteractionHelper;
import amaDeusCraft.dsh.ai.PlayerMoveHelper;
import amaDeusCraft.dsh.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Bot {

	private Minecraft mc;
	private boolean isMining = false;
	private PlayerMoveHelper pmh;
	private PlayerPathNavigateGround png;
	private boolean craftPicks = false;
	private long lastUpdateTimeCrafting = 0;

	private int craftTime = 0;

	private LinkedList<int[]> invClicks = new LinkedList<int[]>();

	private InteractionHelper iah;
	private CobbleFarmer cobbleFarmer;
	private NetherwartFarm netherwartFarm;

	private LogScreen logScreen;

	//TODO Move/splitt into different classes
	//TODO use item move queue

	public Bot(LogScreen logScreen) {
		this.logScreen = logScreen;
		mc = FMLClientHandler.instance().getClient();
		iah = new InteractionHelper(mc);
		cobbleFarmer = new CobbleFarmer(mc, iah, logScreen);
		netherwartFarm = new NetherwartFarm(mc, iah, logScreen);
	}
	
	public void autoStart(){
		netherwartFarm.start();
	}
	
	public void stop(){
		netherwartFarm.stop();
		cobbleFarmer.stop();
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent tick) {
		if (mc == null) {
			mc = FMLClientHandler.instance().getClient();
		}
		if (mc != null) {
			if (ClientProxy.KEYBINDING_LOG.isPressed()) {
				if (logScreen != null) {
					logScreen.show = !logScreen.show;
				}
			}
			if (mc.inGameHasFocus) {
				handleCommands();
				netherwartFarm.handleNetherwartFarm();
				handleAutoMine();
			}
			iah.update();
			netherwartFarm.update();
			testFunc();
			craftPicks();
			cobbleFarmer.toggleFarmCycle();
			cobbleFarmer.update();

			invClickTick();

			if (png != null)
				png.onUpdateNavigation();

			if (pmh != null)
				pmh.onUpdateMoveHelper();
		}
	}

	private void invClickTick() {
		/*
		 * Handles slot click. Args : slotId, clickedButton, mode (0 = basic
		 * click, 1 = shift click, 2 = Hotbar, 3 = pickBlock, 4 = Drop, 5 = ?, 6
		 * = Double click), player
		 */
		if (craftTime > 10) {
			craftTime = 0;
			try {
				int[] clickPos = invClicks.remove();
				mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, clickPos[0], clickPos[1], clickPos[2], mc.thePlayer);
			} catch (NoSuchElementException e) {

			}
		} else {
			craftTime++;
		}
	}

	private void craftPicks() {
		/*
		 * Handles slot click. Args : slotId, clickedButton, mode (0 = basic
		 * click, 1 = shift click, 2 = Hotbar, 3 = pickBlock, 4 = Drop, 5 = ?, 6
		 * = Double click), player
		 */
		if (craftPicks) {
			// mc.thePlayer.sendChatMessage(Long.toString(lastActionTime));
			if (lastUpdateTimeCrafting == 0) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
			} else if (lastUpdateTimeCrafting == 50) {

				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

				ItemStack kindOfLog = new ItemStack(Blocks.log, 1, 2); // birch
				new ItemStack(Blocks.planks, 1, 2);

				//mc.thePlayer.sendChatMessage(itemInInv(kindOfLog) + " | " + itemInInv(new ItemStack(Blocks.cobblestone)));

				if (iah.itemInInv(kindOfLog) >= 1 && iah.itemInInv(new ItemStack(Blocks.cobblestone)) >= 24) {

					//putBlockFromInvAt(kindOfLog, 1);
					invClicks.add(new int[] { 11, 0, 0 });
					invClicks.add(new int[] { 1, 1, 0 });
					invClicks.add(new int[] { 11, 0, 0 });

					//take planks
					invClicks.add(new int[] { 0, 0, 0 });

					//place planks
					invClicks.add(new int[] { 1, 0, 0 });
					invClicks.add(new int[] { 1, 1, 0 });
					invClicks.add(new int[] { 4, 0, 0 });

					//take sticks
					invClicks.add(new int[] { 0, 0, 0 });
					invClicks.add(new int[] { 0, 0, 0 });

					//place sticks
					invClicks.add(new int[] { 5, 0, 0 });
					invClicks.add(new int[] { 5, 1, 0 });
					invClicks.add(new int[] { 8, 0, 0 });

					//take cobble
					invClicks.add(new int[] { 10, 0, 0 });

					//place cobble
					invClicks.add(new int[] { 1, 1, 0 });
					invClicks.add(new int[] { 1, 1, 0 });
					invClicks.add(new int[] { 1, 1, 0 });
					invClicks.add(new int[] { 1, 1, 0 });

					invClicks.add(new int[] { 2, 1, 0 });
					invClicks.add(new int[] { 2, 1, 0 });
					invClicks.add(new int[] { 2, 1, 0 });
					invClicks.add(new int[] { 2, 1, 0 });

					invClicks.add(new int[] { 3, 1, 0 });
					invClicks.add(new int[] { 3, 1, 0 });
					invClicks.add(new int[] { 3, 1, 0 });
					invClicks.add(new int[] { 3, 1, 0 });

					invClicks.add(new int[] { 10, 0, 0 });

					//take picks
					invClicks.add(new int[] { 0, 0, 1 });

				}

				//mc.displayGuiScreen(null);
			} else if (lastUpdateTimeCrafting > 70) {
				System.out.println("lastUpdateTimeCrafting == 70");
				if (invClicks.isEmpty()) {
					mc.displayGuiScreen(null);

					craftPicks = false;
				}
			}
			lastUpdateTimeCrafting++;
		}
	}

	private void testFunc() {
		if (ClientProxy.KEYBINDING_TESTBUTTON.isPressed()) {
			//craftPicks = true;
			//lastUpdateTimeCrafting = 0;
			//mc.thePlayer.openGui(TutorialMod.instance, 20, mc.theWorld, 0, 0, 0);
			//testBar.showLog("testFunc: " + (System.currentTimeMillis() & 0xffff));
			pmh = new PlayerMoveHelper(mc);
			png = new PlayerPathNavigateGround(mc.thePlayer, mc.theWorld, pmh);
			png.tryMoveToXYZ(new BlockPos(-2092, 195, 2192));
			
		}
	}

	private void handleCommands() {
		handleCommand(ClientProxy.KEYBINDING_HOME, "/home");
		handleCommand(ClientProxy.KEYBINDING_BAL, "/bal");
		handleCommand(ClientProxy.KEYBINDING_SHOP, "/warp shop");
		handleCommand(ClientProxy.KEYBINDING_JUMP, "/jump");
		handleCommand(ClientProxy.KEYBINDING_CRAFT, "/workbench");
		
		handleCommand(ClientProxy.KEYBINDING_VISIT_GRIFF, "/visit griffdawg15");
		handleCommand(ClientProxy.KEYBINDING_VISIT_NEKOEMMI, "/visit NekoEmmi");
		
		handleCommand(ClientProxy.KEYBINDING_KITS, new String[] { "/kit bones", "/kit donor25" });
	}

	private void handleCommand(KeyBinding keyBinding, String command) {
		handleCommand(keyBinding, new String[] { command });
	}

	private void handleCommand(KeyBinding keyBinding, String[] command) {
		if (keyBinding.isPressed()) {
			for (int i = 0; i < command.length; i++) {
				mc.thePlayer.sendChatMessage(command[i]);
			}
		}
	}

	private void handleAutoMine() {
		if (ClientProxy.KEYBINDING_AUTOMINE.isPressed()) {
			if (isMining) {
				iah.attack(false);
			} else {
				iah.attack(true);
			}
		}
	}

}
