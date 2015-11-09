package amaDeusCraft.dsh;

import amaDeusCraft.dsh.ai.PlayerMoveHelper;
import amaDeusCraft.dsh.ai.PlayerPathNavigateGround;
import amaDeusCraft.dsh.gui.LogScreen;
import amaDeusCraft.dsh.helper.InteractionHelper;
import amaDeusCraft.dsh.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class CobbleFarmer {

	private enum Status {
		start, wait1, goToEmerald, goToCobble, sellCobble, goToPumkin, sellPumpkin, wait2, goToFarm, farmCobble, eat
	};

	private static final BlockPos SHOP_EMERALD = new BlockPos(16, 198, 100);
	private static final BlockPos COBBLE_SELL_PLACE = new BlockPos(17, 198, 94);
	private static final BlockPos PUMPKIN_SELL_PLACE = new BlockPos(17, 198, 110);
	private static final BlockPos FARM_PLACE = new BlockPos(-2098, 202, 2214);

	private static final ItemStack PUMPKIN = new ItemStack(Blocks.pumpkin);
	private static final ItemStack COBBLE = new ItemStack(Blocks.cobblestone);
	private static final ItemStack STONE_PICKAXE_STACK = new ItemStack(Items.stone_pickaxe);
	private static final ItemStack MELON_STACK = new ItemStack(Items.melon);

	private boolean updateFarmCycle = false;
	private long lastActionTimeFarmCycle = Long.MAX_VALUE;
	private Status status = Status.start;

	private PlayerMoveHelper pmh;
	private PlayerPathNavigateGround png;

	private Minecraft mc;

	private InteractionHelper iah;
	
	private LogScreen testBar;

	public CobbleFarmer(Minecraft minecraft, InteractionHelper interactionHelper, LogScreen testBar) {
		mc = minecraft;
		this.iah = interactionHelper;
		this.testBar = testBar;
	}
	
	public void start(){
		updateFarmCycle = true;
		status = Status.start;
	}
	
	public void stop(){
		updateFarmCycle = false;
		png = null;
		pmh = null;
		iah.goForward(false);
		iah.useItem(false);
		iah.attack(false);
	}

	public void toggleFarmCycle() {
		if (ClientProxy.KEYBINDING_PUMPKIN_CYCLE.isPressed()) {
		testBar.showLog("toggleFarmCycle");
			if (updateFarmCycle) {
				stop();
			} else {
				start();
			}
		}
	}

	public void update() {

		if (png != null)
			png.onUpdateNavigation();

		if (pmh != null)
			pmh.onUpdateMoveHelper();

		if (updateFarmCycle) {

			long currTime = System.currentTimeMillis();
			long dTime = currTime - lastActionTimeFarmCycle;

			if (status == Status.start) {
				pmh = new PlayerMoveHelper(mc);
				png = new PlayerPathNavigateGround(mc.thePlayer, mc.theWorld, pmh);

				mc.thePlayer.sendChatMessage("/warp shop");
				status = Status.wait1;
				lastActionTimeFarmCycle = currTime;
				dTime = 0;

			}
			if (status == Status.wait1 && dTime > 1500) {
				if (iah.itemInInv(COBBLE) < 64 && iah.itemInInv(PUMPKIN) < 64) {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
					mc.thePlayer.sendChatMessage("/home");
					lastActionTimeFarmCycle = currTime;
					dTime = 0;
					status = Status.wait2;

				} else {
					status = Status.goToEmerald;
					png.tryMoveToXYZ(SHOP_EMERALD);
				}
				return;
			}
			if (status == Status.goToEmerald && png.noPath()) {
				if (iah.itemInInv(COBBLE) >= 64) {
					png.tryMoveToXYZ(COBBLE_SELL_PLACE);
					status = Status.goToCobble;
				} else {
					png.tryMoveToXYZ(PUMPKIN_SELL_PLACE);
					status = Status.goToPumkin;
				}
			}
			if (status == Status.goToCobble && png.noPath()) {
				mc.thePlayer.rotationYaw = 90.0f * 3.0f;
				mc.thePlayer.rotationPitch = 0.0f;

				status = Status.sellCobble;
			}
			if (status == Status.sellCobble) {
				if (iah.itemInInv(COBBLE) >= 64) {
					mc.thePlayer.rotationYaw = 90.0f * 3.0f;
					mc.thePlayer.rotationPitch = 0.0f;
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				} else {
					if (iah.itemInInv(PUMPKIN) >= 64) {
						KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
						png.tryMoveToXYZ(PUMPKIN_SELL_PLACE);
						//reachedPathEnd = false;
						status = Status.goToPumkin;
					} else {
						KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
						mc.thePlayer.sendChatMessage("/home");
						lastActionTimeFarmCycle = currTime;
						dTime = 0;
						status = Status.wait2;
					}
				}
			}
			if (status == Status.goToPumkin && png.noPath()) {
				mc.thePlayer.rotationYaw = 90.0f * 3.0f;
				mc.thePlayer.rotationPitch = 0.0f;

				status = Status.sellPumpkin;
			}
			if (status == Status.sellPumpkin) {
				if (iah.itemInInv(new ItemStack(Blocks.pumpkin)) >= 64) {
					mc.thePlayer.rotationYaw = 90.0f * 3.0f;
					mc.thePlayer.rotationPitch = 0.0f;
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				} else {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
					mc.thePlayer.sendChatMessage("/home");
					lastActionTimeFarmCycle = currTime;
					dTime = 0;
					status = Status.wait2;
				}
			}
			if (status == Status.wait2 && dTime > 1500) {
				status = Status.goToFarm;
				png.tryMoveToXYZ(FARM_PLACE);
				//reachedPathEnd = false;
			}
			if (status == Status.goToFarm && png.noPath()) {
				mc.thePlayer.rotationYaw = 90.0f * 2.0f;
				mc.thePlayer.rotationPitch = 0.0f;
				iah.attack(true);
				status = Status.farmCobble;
			}
			if (status == Status.farmCobble && iah.findItemIndex(STONE_PICKAXE_STACK) == -1) {
				iah.attack(false);
				updateFarmCycle = false;
				return;
			}
			if (status == Status.farmCobble && mc.thePlayer.getFoodStats().getFoodLevel() < 6) {
				iah.changeHeldItemTo(MELON_STACK);
				//KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				iah.attack(false);
				iah.useItem(true);
				status = Status.eat;
				return;
			}
			if (status == Status.farmCobble && iah.findItemIndex(null) == -1) {
				iah.attack(false);
				mc.thePlayer.sendChatMessage("/warp shop");
				status = Status.wait1;
				//reachedPathEnd = false;
				lastActionTimeFarmCycle = currTime;
				dTime = 0;
				return;
			}
			if (status == Status.farmCobble) {
				iah.changeHotbarItemTo(STONE_PICKAXE_STACK, 0);
				mc.thePlayer.inventory.currentItem = 0;
				mc.thePlayer.rotationYaw = 90.0f * 2.0f;
				mc.thePlayer.rotationPitch = 0.0f;
				//iah.changeHeldItemTo(STONE_PICKAXE_STACK);
			}
			if (status == Status.eat && mc.thePlayer.getFoodStats().getFoodLevel() >= 20) {
				if (mc.thePlayer.getFoodStats().getFoodLevel() < 20) {
					iah.changeHeldItemTo(MELON_STACK);
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
				} else {
					KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
					mc.thePlayer.rotationYaw = 90.0f * 2.0f;
					mc.thePlayer.rotationPitch = 0.0f;
					iah.attack(true);
					status = Status.farmCobble;
					return;
				}
			}

		}

	}

}
