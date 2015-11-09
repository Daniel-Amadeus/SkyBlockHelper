package amaDeusCraft.dsh.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AuctionScreen extends Gui {

	Minecraft mc;

	private int xPos = 200;
	private int yPos = 22;

	private String auctioneer = "nobody";
	private int count = 0;
	private String item = "no item";
	private int countdownTime = 0;
	private long startTime = System.currentTimeMillis();
	private int bid = 0;
	private int bidInc = 0;
	private boolean wasBidOn = false;
	
	private boolean hideChat = true;

	public AuctionScreen(Minecraft mc) {
		this.mc = mc;
	}

	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		String unformattedText = event.message.getUnformattedText();

		//[Auction] Attention, an auction is beginning!

		//[Auction] Liwnoc111 is auctioning 5 Chicken Egg for 1 min, 0 sec.
		// Starting bid: $1. Bid increment: $5. Type /auction info for full details.
		//[Auction] is auctioning 5 Chick Egg for 1 min, 0 sec. Start bid: $1. Bid inc: $5. Type /auction 

		
		//[Auction] Auction has 30 sec remaining.
		//[Auction] Auction has 3 sec remaining.
		//[Auction] Auction has 2 sec remaining.
		//[Auction] Auction has 1 sec remaining.
		//[Auction] The auction ended with no bids.
		//TODO canceled?
		if (unformattedText.startsWith("[Auction]")) {
			if (unformattedText.contains("is auctioning")) {
				wasBidOn = false;
				String[] str = unformattedText.split(" ");
				auctioneer = str[1];
				count = Integer.parseInt(str[4]);
				item = str[5];
				String word = str[6];
				int i = 6;
				while (!word.equals("for")) {
					item += " " + word;
					i++;
					word = str[i];
				}
				i++;
				int secs = Integer.parseInt(str[i]) * 60;
				i += 2;
				secs += Integer.parseInt(str[i]);
				setCountDown(secs);

				i += 4;
				String[] bidStr = str[i].split("\\$")[1].split("\\.");
				bid = Integer.parseInt(bidStr[0]) * 100;
				if (bidStr.length > 1) {
					bid += Integer.parseInt(bidStr[1]);
				}
				i += 3;
				String[] incStr = str[i].split("\\$")[1].split("\\.");
				bidInc = Integer.parseInt(incStr[0]) * 100;
				if (incStr.length > 1) {
					bidInc += Integer.parseInt(incStr[1]);
				}
			}
			//[Auction] Sarah1492 has bid $20 on the Nether Wart.
			if(unformattedText.contains(" has bid ")){
				wasBidOn = true;
				String[] str = unformattedText.split(" ");
				String[] bidStr = str[4].split("\\$")[1].split("\\.");
				bid = Integer.parseInt(bidStr[0]) * 100;
				if (bidStr.length > 1) {
					bid += Integer.parseInt(bidStr[1]);
				}
			}
			//[Auction] Sarah1492 raised the bid to $20.
			if(unformattedText.contains(" raised the bid to ")){
				wasBidOn = true;
				String[] str = unformattedText.split(" ");
				System.out.println(str);
				String[] bidStr = str[4].split("\\$")[1].split("\\.");
				bid = Integer.parseInt(bidStr[0]) * 100;
				if (bidStr.length > 1) {
					bid += Integer.parseInt(bidStr[1]);
				}
			}
			//[Auction] The auction has been cancelled.
			if(unformattedText.contains("cancelled")){
				setCountDown(0);
			}
			if(hideChat) event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRender(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}
		if (mc.currentScreen != null)
			xPos = mc.currentScreen.width / 2;

		long timeDiff = System.currentTimeMillis() - startTime;
		int leftTime = countdownTime - ((int) timeDiff / 1000);

		if (leftTime >= 0) {
			String bidStr = String.valueOf(bid/100);
			if(bid%100>0){
				bidStr += "." + String.format("%02d", bid % 100);
			}
			String incStr = String.valueOf(bidInc/100);
			if(bidInc%100>0){
				incStr += "." + String.format("%02d", bidInc % 100);
			}
			String bidText = "start bid: $";
			if(wasBidOn) bidText = "current bid: $";
			this.drawCenteredString(mc.fontRendererObj, auctioneer + " sells " + count + " " + item + " ", xPos, yPos, 0x99ff99);
			this.drawCenteredString(mc.fontRendererObj, bidText + bidStr, xPos, yPos + 10, 0x99ff99);
			this.drawCenteredString(mc.fontRendererObj, "bid increment: $" + incStr, xPos, yPos + 20, 0x99ff99);
			this.drawCenteredString(mc.fontRendererObj, "left time: " + leftTime + "secs", xPos, yPos + 30, 0x99ff99);
		}
	}

	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent event) {
		String msg = event.message;
		if(msg.equals("#auctionShow")){
			hideChat = false;
		}
		if(msg.equals("#auctionHide")){
			hideChat = true;
		}
	}

	public void setCountDown(int seconds) {
		startTime = System.currentTimeMillis();
		countdownTime = seconds;
	}
}
