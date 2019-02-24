package nl.sandersimon.clonedetection.monster.codecreeper;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.model.MetricProblem;

@SideOnly(Side.CLIENT)
public class RenderCodeCreeper extends RenderLiving<EntityCodeCreeper>
{
    private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");

    public RenderCodeCreeper(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelCreeper(), 0.5F);
        //this.addLayer(new LayerCreeperCharge(this));
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     
    protected void preRenderCallback(EntityCodeCreeper entitylivingbaseIn, float partialTickTime)
    {
        float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        GlStateManager.scale(f2, f3, f2);
    }*/

    /**
     * Gets an RGBA int color multiplier to apply.
     */
    protected int getColorMultiplier(EntityCodeCreeper entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);

        if ((int)(f * 10.0F) % 2 == 0)
        {
            return 0;
        }
        else
        {
            int i = (int)(f * 0.2F * 255.0F);
            i = MathHelper.clamp(i, 0, 255);
            return i << 24 | 822083583;
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCodeCreeper entity)
    {
        return CREEPER_TEXTURES;
    }
    
    @Override
    protected void preRenderCallback(EntityCodeCreeper entitylivingbaseIn, float partialTickTime)
    {
    	MetricProblem c = entitylivingbaseIn.getRepresents();
    	if(c == null) {
	    	CodeArena arena = CloneDetection.get().getArena();
	    	if(arena == null)
	    		return;
	    	
			c = arena.findEntity(entitylivingbaseIn);
			
			if(c == null)
				return;
    	}
		float scale = (c.volume()-18)*0.03F;
		
        GlStateManager.scale(scale, scale, scale);
        //System.out.println("Scaled by "+entitylivingbaseIn.getScaleFactor()+" because of "+entitylivingbaseIn.getCustomNameTag()+", "+entitylivingbaseIn.getHealth()+", "+entitylivingbaseIn.getAbsorptionAmount());
    }
}