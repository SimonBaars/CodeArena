package com.simonbaars.codearena.monster.codespider;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.challenge.CodeArena;
import com.simonbaars.codearena.model.MetricProblem;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCodeSpider<T extends EntityCodeSpider> extends RenderLiving<T>
{
    private static final ResourceLocation SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderCodeSpider(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelSpider(), 1.0F);
        this.addLayer(new LayerCodeSpiderEyes<T>(this));
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn)
    {
        return 180.0F;
    }
    
    @Override
    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime)
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
		float scale = c.volume()*0.03F;
		
        GlStateManager.scale(scale, scale, scale);
        //System.out.println("Scaled by "+entitylivingbaseIn.getScaleFactor()+" because of "+entitylivingbaseIn.getCustomNameTag()+", "+entitylivingbaseIn.getHealth()+", "+entitylivingbaseIn.getAbsorptionAmount());
    }
     

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks){
    	/*GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(entity.getScaleFactor(), entity.getScaleFactor(), entity.getScaleFactor());
        GlStateManager.disableRescaleNormal();
    	GlStateManager.enableCull();
        GlStateManager.popMatrix();*/
    	super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(T entity)
    {
        return SPIDER_TEXTURES;
    }
}