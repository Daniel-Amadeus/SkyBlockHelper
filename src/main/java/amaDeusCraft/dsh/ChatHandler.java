package amaDeusCraft.dsh;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import amaDeusCraft.dsh.ai.FakeDeusThread;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ChatHandler {
	
	private String [] START_BLACK_LIST = new String[]{"[Skyblock]", "[MobArena]", "[SBLottery]", "[ClearLag]"};
	private String [] END_BLACK_LIST = new String[]{
				" voted at vote.skyblock.org and vote2.skyblock.org for 50 Skybucks and a voter crate key!"};
	
	private static ChatterBotSession fakeDeusSession;
	String[] senderMessageResponse = new String[3];
	//amaDeusCraft voted at vote.skyblock.org and vote2.skyblock.org for 50 Skybucks and a voter crate key!
	public ChatHandler() {
		ChatterBotFactory factory = new ChatterBotFactory();
		ChatterBot fakeDeus;
		try {
			fakeDeus = factory.create(ChatterBotType.CLEVERBOT);
			fakeDeusSession = fakeDeus.createSession();
			System.out.println("created session");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("didn't create session");
		}
	}

	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent event) {
	}

	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		
		String unformattedText = event.message.getUnformattedText();
		
		//===chat bot test===
		
		String[] str = unformattedText.split(":", 2);
		if(str.length == 2){
			String sender = str[0];
			String message = str[1].trim();
			try {
				senderMessageResponse = new String[3];
				senderMessageResponse[0] = sender;
				senderMessageResponse[1] = message;
				senderMessageResponse[2] = null;
				if(fakeDeusSession == null) System.out.println("fakeDeusSession == null");
				FakeDeusThread fakeDeusThreadObj = new FakeDeusThread(fakeDeusSession, senderMessageResponse);
				Thread fakeDeusThread = new Thread(fakeDeusThreadObj);
				fakeDeusThread.start();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//===chat bot test===
				
		for (String badWord : START_BLACK_LIST) {
			if(unformattedText.startsWith(badWord)){
				event.setCanceled(true);
				System.out.println(unformattedText);
				return;
			}
		}
		for (String badWord : END_BLACK_LIST) {
			if(unformattedText.endsWith(badWord)){
				event.setCanceled(true);
				System.out.println(unformattedText);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent tick) {
		if(senderMessageResponse[2] != null){
			String sender = senderMessageResponse[0];
			String message = senderMessageResponse[1];
			String response = senderMessageResponse[2];
			senderMessageResponse = new String[3];

			System.out.println("\n\n");
			System.out.println("\n<" + sender + " -> fakeDeus>: " + message);
			System.out.println("\n<fakeDeus -> " + sender + ">: " + response);
			System.out.println("\n\n");
		}
	}
}
