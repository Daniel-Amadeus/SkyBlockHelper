package amaDeusCraft.dsh;

import amaDeusCraft.dsh.ai.PlayerMoveHelper;
import amaDeusCraft.dsh.ai.PlayerPathNavigateGround;
import amaDeusCraft.dsh.gui.LogScreen;
import amaDeusCraft.dsh.helper.InteractionHelper;
import amaDeusCraft.dsh.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NetherwartFarm {

	private enum Status {
		start, farm, wait1, goToSubsation, goToNetherwart, sellNetherwart, wait2, goToEntry, goToBreakpoint, eat
	}

	private static final BlockPos NETHERFARM_START = new BlockPos(-2067, 202, 2155);
	private static final BlockPos NETHERFARM_END = new BlockPos(-2132, 202, 2189);
	private static final BlockPos NETHERFARM_ENTRY = new BlockPos(-2099, 202, 2190);

	private static final BlockPos NETHERWART_SUBSTATION = new BlockPos(7, 198, 91); // gravel in grass - works ok
	private static final BlockPos NETHERWART_SELL_PLACE = new BlockPos(-15, 198, 80);

	private static final ItemStack STONE_AXE = new ItemStack(Items.stone_axe);
	private static final ItemStack NETHERWART = new ItemStack(Items.nether_wart);
	//private static final ItemStack MELON = new ItemStack(Items.melon);

	private static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

	private Minecraft mc;

	private long lastActionTimeFarmCycle = Long.MAX_VALUE;

	private Status status;

	private boolean netherFarming = false;

	private PlayerMoveHelper pmh;
	private PlayerPathNavigateGround png;
	private BlockPos netherFarmBreakPoint = NETHERFARM_START;

	private InteractionHelper iah;

	@SuppressWarnings("unused")
	private LogScreen logScreen;

	public NetherwartFarm(Minecraft minecraft, InteractionHelper interactionHelper, LogScreen testBar) {
		mc = minecraft;
		iah = interactionHelper;
		this.logScreen = testBar;
	}
	
	public void start(){
		BlockPos playerPos = mc.thePlayer.getPosition();
		if (playerPos.getX() >= NETHERFARM_START.getX() && playerPos.getX() <= NETHERFARM_END.getX() && playerPos.getZ() >= NETHERFARM_START.getZ() && playerPos.getZ() <= NETHERFARM_END.getZ()) {
			netherFarmBreakPoint = playerPos;
		}
		netherFarming = true;
		lastActionTimeFarmCycle = System.currentTimeMillis();
		mc.thePlayer.sendChatMessage("/warp shop");
		status = Status.wait1;
	}
	
	public void stop(){
		netherFarming = false;
		if (png != null)
			png.clearPathEntity();
		png = null;
		pmh = null;
		iah.useItem(false);
		iah.attack(false);
		iah.goForward(false);

		netherFarmBreakPoint = mc.thePlayer.getPosition();
	}
	
	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		
		String unformattedText = event.message.getUnformattedText();
		
		if(unformattedText.startsWith("Error:")
				|| unformattedText.startsWith("Hey! Sorry")){
			stop();
			start();
		}
		
	}

	public void handleNetherwartFarm() {
		if (ClientProxy.KEYBINDING_NETHERWART_FARM.isPressed()) {
			if (netherFarming) {
				stop();
			} else {
				start();
			}
		}
	}

	public void update() {
		movementUpdate();
		aiUpdate();
	}

	private void movementUpdate() {
		if (png != null)
			png.onUpdateNavigation();
		if (pmh != null)
			pmh.onUpdateMoveHelper();
	}

	private void aiUpdate() {
		if (netherFarming && mc.thePlayer != null) {

			long currTime = System.currentTimeMillis();
			long dTime = currTime - lastActionTimeFarmCycle;

			if(status == Status.eat){
				if(mc.thePlayer.getFoodStats().getFoodLevel() >= 20){
					iah.useItem(false);
					goDirctlyTo(netherFarmBreakPoint);
					status = Status.goToBreakpoint;
				}else{
					iah.useItem(true);
				}
			}

			if (status == Status.farm) {

				int foodLevel = iah.foodLevel();
				if (foodLevel >=-1 && foodLevel < 6) {
					netherFarmBreakPoint = mc.thePlayer.getPosition();
					png = null;
					pmh = null;
					iah.goForward(false);
					mc.thePlayer.inventory.currentItem = 3;
					iah.useItem(true);
					status = Status.eat;
					return;
				}

				mc.thePlayer.rotationPitch = 90.0f;
				MovingObjectPosition mop = mc.objectMouseOver;
				if (png == null || png.noPath()) {

					int x0 = NETHERFARM_START.getX();
					int x1 = NETHERFARM_END.getX();

					int y = NETHERFARM_START.getY();

					int z0 = NETHERFARM_START.getZ();
					int z1 = NETHERFARM_END.getZ();

					PathPoint[] pathPoint = new PathPoint[(z1 - z0 + 1) * 2];

					for (int i = 0; i <= (z1 - z0); i++) {
						if (i % 2 == 0) {
							pathPoint[i * 2] = new PathPoint(x0, y, i + z0);
							pathPoint[i * 2 + 1] = new PathPoint(x1, y, i + z0);
						} else {
							pathPoint[i * 2] = new PathPoint(x1, y, i + z0);
							pathPoint[i * 2 + 1] = new PathPoint(x0, y, i + z0);
						}
					}

					PathEntity path = new PathEntity(pathPoint);
					path.setCurrentPathIndex(0);
					pmh = new PlayerMoveHelper(mc);
					png = new PlayerPathNavigateGround(mc.thePlayer, mc.theWorld, pmh, false);
					png.setPath(path, 1.0f);

					iah.changeHotbarItemTo(STONE_AXE, 1);
					iah.changeHotbarItemTo(NETHERWART, 2);
				}
				if (mop.typeOfHit == MovingObjectType.BLOCK) {

					BlockPos pos = mop.getBlockPos();
					Block block = mc.theWorld.getChunkFromBlockCoords(pos).getBlock(pos);
					if (block.getUnlocalizedName().equals(Blocks.soul_sand.getUnlocalizedName())) {
						mc.thePlayer.inventory.currentItem = 2;
						KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
					} else {
						KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
					}

					if (block.getUnlocalizedName().equals(Blocks.nether_wart.getUnlocalizedName())) {

						Integer age = (Integer) (mc.theWorld.getBlockState(pos).getValue(AGE));
						if (age == 3) {
							if (iah.isEqual(mc.thePlayer.inventory.getCurrentItem(), STONE_AXE)) {
								iah.attack(true);
							} else {
								mc.thePlayer.inventory.currentItem = 1;
							}
						}
					} else {
						iah.attack(false);
					}

				}
				if (iah.findItemIndex(null) == -1) {
					netherFarmBreakPoint = mc.thePlayer.getPosition();
					png.getPath();
					png.clearPathEntity();
					pmh.update = false;

					pmh = null;
					png = null;

					mc.thePlayer.sendChatMessage("/warp shop");
					lastActionTimeFarmCycle = currTime;
					dTime = 0;
					status = Status.wait1;
				}
			}
			if (status == Status.wait1 && dTime > 1500) {
				if (iah.itemInInv(NETHERWART) < 16) {
					mc.thePlayer.sendChatMessage("/home");
					lastActionTimeFarmCycle = currTime;
					dTime = 0;
					status = Status.wait2;
				} else {
					pmh = new PlayerMoveHelper(mc);
					png = new PlayerPathNavigateGround(mc.thePlayer, mc.theWorld, pmh);
					png.tryMoveToXYZ(NETHERWART_SUBSTATION);
					status = Status.goToSubsation;
				}
				return;
			}
			if (status == Status.goToSubsation && png.noPath()) {
				png.tryMoveToXYZ(NETHERWART_SELL_PLACE);
				status = Status.goToNetherwart;
			}
			if (status == Status.goToNetherwart && png.noPath()) {
				status = Status.sellNetherwart;
				mc.thePlayer.rotationYaw = 180.0f;
				mc.thePlayer.rotationPitch = 0.0f;
				iah.useItem(true);
			}
			if (status == Status.sellNetherwart) {
				mc.thePlayer.rotationYaw = 180.0f;
				mc.thePlayer.rotationPitch = 0.0f;

				iah.useItem(true);
				if (iah.itemInInv(NETHERWART) < 16) {
					iah.useItem(false);
					mc.thePlayer.sendChatMessage("/home");
					lastActionTimeFarmCycle = currTime;
					dTime = 0;
					status = Status.wait2;
				}
			}
			if (status == Status.wait2 && dTime > 1500) {
				goDirctlyTo(NETHERFARM_ENTRY);
				status = Status.goToEntry;
			}
			if (status == Status.goToEntry && png.noPath()) {
				goDirctlyTo(netherFarmBreakPoint);
				status = Status.goToBreakpoint;
			}
			if (status == Status.goToBreakpoint && png.noPath()) {

//				private static final BlockPos NETHERFARM_START = new BlockPos(-2067, 202, 2155);
//				private static final BlockPos NETHERFARM_END = new BlockPos(-2132, 202, 2189);
				
				if(netherFarmBreakPoint.getX() < NETHERFARM_END.getX() ||
						netherFarmBreakPoint.getX() > NETHERFARM_START.getX() ||
						netherFarmBreakPoint.getZ() < NETHERFARM_START.getZ() ||
						netherFarmBreakPoint.getZ() > NETHERFARM_END.getZ()){
					netherFarmBreakPoint = NETHERFARM_START;
				}
				
				int x0 = NETHERFARM_START.getX();
				int x1 = NETHERFARM_END.getX();

				int y = NETHERFARM_START.getY();

				int z0 = netherFarmBreakPoint.getZ() + 1;
				int z1 = NETHERFARM_END.getZ();

				PathPoint[] pathPoint = new PathPoint[(z1 - z0 + 1) * 2 + 2];

				pathPoint[0] = new PathPoint(netherFarmBreakPoint.getX(), y, netherFarmBreakPoint.getZ());
				if (netherFarmBreakPoint.getZ() % 2 == 0) {
					pathPoint[1] = new PathPoint(x1, y, netherFarmBreakPoint.getZ());
				} else {
					pathPoint[1] = new PathPoint(x0, y, netherFarmBreakPoint.getZ());
				}

				for (int i = 0; i <= (z1 - z0); i++) {
					if (i % 2 == 0) {
						pathPoint[i * 2 + 2] = new PathPoint(x0, y, i + z0);
						pathPoint[i * 2 + 1 + 2] = new PathPoint(x1, y, i + z0);
					} else {
						pathPoint[i * 2 + 2] = new PathPoint(x1, y, i + z0);
						pathPoint[i * 2 + 1 + 2] = new PathPoint(x0, y, i + z0);
					}
				}

				for (int i = 0; i < pathPoint.length; i++) {
					System.out.println("pathPoint[" + i + "] = " + pathPoint[i]);
				}

				PathEntity path = new PathEntity(pathPoint);
				path.setCurrentPathIndex(0);
				pmh = new PlayerMoveHelper(mc);
				png = new PlayerPathNavigateGround(mc.thePlayer, mc.theWorld, pmh, false);
				png.setPath(path, 1.0f);

				iah.changeHotbarItemTo(new ItemStack(Items.stone_axe), 1);
				iah.changeHotbarItemTo(NETHERWART, 2);
				status = Status.farm;
			}
		}
	}

	private void goDirctlyTo(BlockPos destination) {
		if (pmh == null)
			pmh = new PlayerMoveHelper(mc);
		if (png == null)
			png = new PlayerPathNavigateGround(mc.thePlayer, mc.theWorld, pmh);
		PathEntity path = new PathEntity(new PathPoint[] { new PathPoint(destination.getX(), destination.getY(), destination.getZ()) });
		png.setPath(path, 1.0f);
		png.setShouldTrimToMinimal(false);
	}

}
