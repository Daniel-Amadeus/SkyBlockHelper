package amaDeusCraft.dsh.ai;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

public class PlayerMoveHelper {
    /** The EntityLiving that is being moved */
    protected EntityPlayerSP player;
    protected double posX;
    protected double posY;
    protected double posZ;
    /** The speed at which the entity should move */
    protected double speed;
    public boolean update;
    //private static final String __OBFID = "CL_00001573";
    private Minecraft mc;

    private PlayerMoveHelper(Minecraft minecraft, EntityPlayerSP player)
    {
        this.player = player;
        this.posX = player.posX;
        this.posY = player.posY;
        this.posZ = player.posZ;
        this.mc = minecraft;
    }

    public PlayerMoveHelper(Minecraft minecraft){
    	this(minecraft, minecraft.thePlayer);
    }
    public boolean isUpdating()
    {
        return this.update;
    }

    public double getSpeed()
    {
        return this.speed;
    }

    /**
     * Sets the speed and location to move to
     */
    public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_)
    {
        this.posX = p_75642_1_;
        this.posY = p_75642_3_;
        this.posZ = p_75642_5_;
        this.speed = p_75642_7_;
        this.update = true;
    }

    public void onUpdateMoveHelper()
    {
        //this.player.setMoveForward(0.0F);
        this.player.moveForward = 0.0F;

        if (this.update)
        {
            //this.update = false;
            int i = MathHelper.floor_double(this.player.getEntityBoundingBox().minY + 0.5D);
            double d0 = this.posX - this.player.posX;
            double d1 = this.posZ - this.player.posZ;
            double d2 = this.posY - (double)i;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 >= 2.500000277905201E-2D) // 2.500000277905201E-7D
            {
                float f = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
                this.player.rotationYaw = this.limitAngle(this.player.rotationYaw, f, 30.0F);
                //this.player.setAIMoveSpeed((float)(this.speed * this.player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
                //this.player.setAIMoveSpeed((float)this.speed);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                //player.sendChatMessage("d2 = " + d2 + " | d0 = " + d0 + " | d1 = " + d1 + " | res = " + (d0 * d0 + d1 * d1));
                if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D)
                {
                	//player.sendChatMessage("jump");
                    //this.player.getJumpHelper().setJumping();
                    //this.player.setJumping(true);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                }else{
                	KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                }
            }else{

                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            	this.update = false;
            }
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */
    protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_)
    {
        float f3 = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

        if (f3 > p_75639_3_)
        {
            f3 = p_75639_3_;
        }

        if (f3 < -p_75639_3_)
        {
            f3 = -p_75639_3_;
        }

        float f4 = p_75639_1_ + f3;

        if (f4 < 0.0F)
        {
            f4 += 360.0F;
        }
        else if (f4 > 360.0F)
        {
            f4 -= 360.0F;
        }

        return f4;
    }

    public double func_179917_d()
    {
        return this.posX;
    }

    public double func_179919_e()
    {
        return this.posY;
    }

    public double func_179918_f()
    {
        return this.posZ;
    }
}