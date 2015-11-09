package amaDeusCraft.dsh.proxy;

import amaDeusCraft.dsh.gui.TestGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler{
	public void registerRenders(){
		
	}

	public void init(FMLInitializationEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == TestGui.GUI_ID){
			return new TestGui();
		}
		return null;
	}
}
