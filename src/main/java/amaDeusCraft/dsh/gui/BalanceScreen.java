package amaDeusCraft.dsh.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BalanceScreen extends Gui{
	
	Minecraft mc;
	
	private int xPos = 200;
	private int yPos = 12;
	
	private long balance = 0;
	private String balanceString = "$0.00";
	
	public BalanceScreen(Minecraft mc) {
		this.mc = mc;
	}
	
	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		String unformattedText = event.message.getUnformattedText();
		
		
		if(unformattedText.startsWith("Welcome amaDeusCraft")){
			if(mc.thePlayer != null){
				mc.thePlayer.sendChatMessage("/balance");
			}else{
				System.out.println("player == null");
			}
		}
		//Balance: $343212411.12
		if(unformattedText.startsWith("Balance")){
			
			String balanceString = unformattedText.split("\\$")[1];
			String[] splittedBalanceString = balanceString.split("\\.");
			
			balance = Long.parseLong(splittedBalanceString[0]) * 100;
			if(splittedBalanceString.length > 1){
				balance += Long.parseLong(splittedBalanceString[1]);
			}
			event.setCanceled(true);
		}
		if(unformattedText.startsWith("$") && unformattedText.contains("has been added")){
			//$20 has been added to your account.
			String partAfterDollar = unformattedText.split("\\$")[1];
			String amountString = partAfterDollar.split(" ")[0];
			balance += Long.parseLong(amountString) * 100;
			event.setCanceled(true);
		}
		if(unformattedText.startsWith("$") && unformattedText.contains("has been taken")){
			//$20 has been added to your account.
			String partAfterDollar = unformattedText.split("\\$")[1];
			String amountString = partAfterDollar.split(" ")[0];
			balance -= Long.parseLong(amountString) * 100;
			event.setCanceled(true);
		}
		//$850000 has been sent to [Overlord] Remo.
		if(unformattedText.contains(" has been sent to ")){
			String[] strs = unformattedText.split(" ")[0].split("\\$")[1].split("\\.");
			balance -= Integer.parseInt(strs[0]) * 100;
			if(strs.length >= 2){
				balance -= Integer.parseInt(strs[1]);
			}
			
			
		}
		
		String dollars = "$" + (balance / 100);
		String cents = String.format("%02d", balance % 100);
		
		balanceString = dollars + "." + cents;
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRender(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}
		if(mc.currentScreen != null) xPos = mc.currentScreen.width / 2;
		
		this.drawCenteredString(
				mc.fontRendererObj,
				balanceString,
				xPos, yPos, 0xff9999);
	}
}
