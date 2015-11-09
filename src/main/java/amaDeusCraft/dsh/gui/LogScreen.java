package amaDeusCraft.dsh.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LogScreen extends Gui {
	Minecraft mc;
	public boolean show = true;
	private ArrayList<String> log = new ArrayList<String>();
	private final int vertOffset = 10;
	private final int maxLogCount = 10;

	public LogScreen(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		
		String unformattedText = event.message.getUnformattedText();
		String formattedText = event.message.getFormattedText();
		
		if (!unformattedText.startsWith("Welcome ")
				&& (unformattedText.startsWith("[") && unformattedText.contains(" -> me]")
				|| unformattedText.contains("Deus"))
				|| unformattedText.contains(" has requested to teleport to you.")) {
			System.out.println(unformattedText);
			showLog(formattedText);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRender(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}
		int xPos = 2;
		int yPos = 2;
		if(show){
			//this.drawString(mc.fontRendererObj, "hello world", xPos, yPos, 0xffffff);
			int indexOffset = log.size() - maxLogCount ;
			indexOffset = indexOffset < 0 ? 0 : indexOffset;
			for(int i = 0; i < maxLogCount && i < log.size(); i++){
				this.drawString(mc.fontRendererObj, log.get(i + indexOffset), xPos, yPos + vertOffset * i, 0xffffff);
			}
		}
	}
	
	public void showLog(String logMsg){
		log.add(logMsg);
	}

}
