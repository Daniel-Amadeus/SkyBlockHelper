package amaDeusCraft.dsh.helper;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class InteractionHelper {
	
	private Minecraft mc;

	private boolean isMining = false;
	private int leftClickCounter = 0;
	
	public InteractionHelper(Minecraft minecraft) {
		this.mc = minecraft;
	}
	
	public EntityPlayerSP player(){
		if(mc != null)
			return mc.thePlayer;
		return null;
	}
	
	public int foodLevel(){
		EntityPlayerSP player = player();
		if(player != null){
			FoodStats foodStats = player.getFoodStats();
			if(foodStats != null){
				return foodStats.getFoodLevel();
			}
		}
		return -1;
	}
	
	public void update(){
		autoMine();
	}

	public void changeHeldItemTo(ItemStack itemStack) {
		changeHotbarItemTo(itemStack, mc.thePlayer.inventory.currentItem);
	}

	public void changeHotbarItemTo(ItemStack itemStack, int hotbarIndex) {

		int offsetedHotbarIndex = hotbarIndex + 36;
		ItemStack heldItemStack = mc.thePlayer.inventoryContainer.getSlot(offsetedHotbarIndex).getStack();

		if (!isEqual(heldItemStack, itemStack)) {

			ItemStack cursorStack = mc.thePlayer.inventory.getItemStack();

			if (isEqual(cursorStack, itemStack)) {
				mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, offsetedHotbarIndex, 0, 0, mc.thePlayer);
			} else {

				int itemStackIndex = findItemIndex(itemStack);

				if (heldItemStack == null || (itemStackIndex != -1 && itemStackIndex != offsetedHotbarIndex)) {
					mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, itemStackIndex, 0, 0, mc.thePlayer);
					mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, offsetedHotbarIndex, 0, 0, mc.thePlayer);
					mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, itemStackIndex, 0, 0, mc.thePlayer);
				}
			}
		}
	}


	public int findItemIndex(ItemStack filterItemStack) {
		int result = -1;
		for (int i = 9; i < 45 && result == -1; i++) {
			ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if ((filterItemStack != null && itemStack != null && itemStack.getUnlocalizedName().equals(filterItemStack.getUnlocalizedName())) || (filterItemStack == null && itemStack == null)) {
				result = i;
			}
		}
		return result;
	}
	
	public int itemInInv(ItemStack filterItemStack) {
		int summ = 0;
		for (int i = 9; i < 45; i++) {
			ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			//TODO test if try is needed
			try {
				if ((filterItemStack != null && itemStack != null && itemStack.getUnlocalizedName().equals(filterItemStack.getUnlocalizedName()))) {
					summ += itemStack.stackSize;
				}
				if (filterItemStack == null && itemStack == null) {
					summ++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return summ;
	}
	
	private void putBlockFromInvAt(ItemStack itemStack, int index) {
		int sourceIndex = findItemIndex(itemStack);
		putBlockFromInvAt(sourceIndex, index);
		//		if (sourceIndex >= 0) {
		//			sourceIndex++;
		//			//mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, sourceIndex, 0, 0, mc.thePlayer);
		//			//mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, index, 1, 0, mc.thePlayer);
		//			//mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, sourceIndex, 0, 0, mc.thePlayer);
		//			invClicks.add(new int[] { sourceIndex, 0, 0 });
		//			invClicks.add(new int[] { index, 1, 0 });
		//			invClicks.add(new int[] { sourceIndex, 0, 0 });
		//		}
	}

	private void putBlockFromInvAt(int sourceIndex, int index) {
		if (sourceIndex >= 0) {
			sourceIndex++;
			//mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, sourceIndex, 0, 0, mc.thePlayer);
			//mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, index, 1, 0, mc.thePlayer);
			//mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, sourceIndex, 0, 0, mc.thePlayer);
//			invClicks.add(new int[] { sourceIndex, 0, 0 });
//			invClicks.add(new int[] { index, 1, 0 });
//			invClicks.add(new int[] { sourceIndex, 0, 0 });
		}
	}

	@SuppressWarnings("unused")
	private void putBlockFromInvAt(ItemStack itemStack, int index, int count) {
		for (int i = 0; i < count; i++) {
			putBlockFromInvAt(itemStack, index);
		}
	}

	public boolean isEqual(ItemStack itemStack1, ItemStack itemStack2) {
		// TODO use item?
		if (itemStack1 == null && itemStack2 == null)
			return true;
		if (itemStack1 == null || itemStack2 == null)
			return false;
		return itemStack1.getUnlocalizedName().equals(itemStack2.getUnlocalizedName());
	}
	
	private void clickMouse() {
		mc.thePlayer.swingItem();

		if (mc.objectMouseOver != null) {
			if (mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
				BlockPos blockpos = mc.objectMouseOver.getBlockPos();

				if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
					mc.playerController.func_180511_b(blockpos, mc.objectMouseOver.sideHit);
				}

			}
		}
	}
	private void sendClickBlockToController(boolean leftClick) {

		if (mc.thePlayer != null && !mc.thePlayer.isUsingItem()) {
			if (leftClick && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos blockpos = mc.objectMouseOver.getBlockPos();

				if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && mc.playerController.func_180512_c(blockpos, mc.objectMouseOver.sideHit)) {
					mc.effectRenderer.addBlockHitEffects(blockpos, mc.objectMouseOver);
					mc.thePlayer.swingItem();
				}
			} else {
				mc.playerController.resetBlockRemoving();
			}
		}
	}
	
	private void autoMine() {
		if (leftClickCounter == 0) {
			this.sendClickBlockToController(isMining);
			leftClickCounter = 1;
		} else {
			leftClickCounter--;
		}
	}
	
	public void attack(boolean val) {
		if (val) {
			isMining = true;
			this.clickMouse();
		} else {
			isMining = false;
		}
	}

	public void goForward(boolean val) {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), val);
	}

	public void useItem(boolean val) {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), val);
	}
}
