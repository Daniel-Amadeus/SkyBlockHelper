package amaDeusCraft.dsh.ai;

import com.google.code.chatterbotapi.ChatterBotSession;

public class FakeDeusThread implements Runnable {

	private final int MAX_TRYS = 5;
	String[] senderMessageResponse;
	ChatterBotSession chatterBotSession;

	public FakeDeusThread(ChatterBotSession chatterBotSession, String[] senderMessageResponse) {
		this.senderMessageResponse = senderMessageResponse;
		this.chatterBotSession = chatterBotSession;
	}

	@Override
	public void run() {
		boolean rethink = true;
		String response = "Sorry, I don't understand :(";
		for (int i = 0; i < MAX_TRYS && rethink; i++) {
			try {
				String responseTry = chatterBotSession.think(senderMessageResponse[1]);
				if(!responseTry.toLowerCase().contains("clever") && !responseTry.toLowerCase().contains("bot")){
					rethink = false;
					response = responseTry;
				}
			} catch (Exception e) {
			}
		}
		senderMessageResponse[2] = response;
	}

}
