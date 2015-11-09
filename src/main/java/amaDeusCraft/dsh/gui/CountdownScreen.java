package amaDeusCraft.dsh.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CountdownScreen extends Gui {

	private int CLEAR_LAG_PHASE = 460;

	Minecraft mc;

	private int countdownTime = CLEAR_LAG_PHASE;

	private int xPos = 200;
	private int yPos = 2;

	private long startTime;

	private String prefix = "< ";

	public CountdownScreen(Minecraft mc) {
		super();
		System.out.println("new CountdownScreen(Minecraft mc)");
		this.mc = mc;
		startTime = System.currentTimeMillis();
	}

	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {

		String unformattedText = event.message.getUnformattedText();
		
		//[ClearLag] Warning Ground items will be removed in 60 seconds!
		if (unformattedText.startsWith("[ClearLag]")) {
			if (unformattedText.contains("60")) {
				setCountDown(60);
			} else if (unformattedText.contains("20")) {
				setCountDown(20);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRender(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}
		
		if(mc.currentScreen != null) xPos = mc.currentScreen.width / 2;

		int leftTime = 0;
		if (startTime > 0) {
			long timeDiff = System.currentTimeMillis() - startTime;
			leftTime = countdownTime - ((int) timeDiff / 1000);
			if (leftTime < 0) {
				setCountDown(CLEAR_LAG_PHASE);
			}
		}

		this.drawCenteredString(
				mc.fontRendererObj, 
				"[ClearLag]: " + prefix + leftTime + " secs",
				xPos, yPos, 0xffffff);
	}

	public void setCountDown(int seconds) {
		prefix = "";
		startTime = System.currentTimeMillis();
		countdownTime = seconds;
	}

	//	private String decorate(int time){
	//		String prefix = "§r";
	//		switch ((time - 1) / 10) {
	//		case 5: return prefix += "§2" + time;
	//		case 4: return prefix += "§a" + time;
	//		case 3: return prefix += "§e" + time;
	//		case 2: return prefix += "§6" + time;
	//		case 1: return prefix += "§c" + time;
	//		case 0: return prefix += "§4" + time;
	//
	//		default:
	//			break;
	//		}
	//		return prefix + time;
	//	}
}
