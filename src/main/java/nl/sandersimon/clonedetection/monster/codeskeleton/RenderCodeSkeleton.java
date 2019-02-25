package nl.sandersimon.clonedetection.monster.codeskeleton;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.model.MetricProblem;

@SideOnly(Side.CLIENT)
public class RenderCodeSkeleton extends RenderBiped<AbstractCodeSkeleton>
{
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public RenderCodeSkeleton(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelCodeSkeleton(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelCodeSkeleton(0.5F, true);
                this.modelArmor = new ModelCodeSkeleton(1.0F, true);
            }
        });
    }

    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(AbstractCodeSkeleton entity)
    {
        return SKELETON_TEXTURES;
    }
    
    @Override
    protected void preRenderCallback(AbstractCodeSkeleton entitylivingbaseIn, float partialTickTime)
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
}