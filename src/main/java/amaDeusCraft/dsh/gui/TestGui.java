package amaDeusCraft.dsh.gui;

import net.minecraft.client.gui.GuiScreen;

public class TestGui extends GuiScreen{
	public final static int GUI_ID = 20;
	
	public TestGui(){
		
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		//super.drawScreen(mouseX, mouseY, partialTicks);
		this.drawDefaultBackground();
	}
	
}
