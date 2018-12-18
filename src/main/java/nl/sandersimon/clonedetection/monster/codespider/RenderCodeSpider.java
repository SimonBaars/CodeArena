package nl.sandersimon.clonedetection.monster.codespider;

import org.lwjgl.opengl.GL11;

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
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks){
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 1.5F-1.5F*entity.getScaleFactor(), 0F); 
        GL11.glScalef(entity.getScaleFactor(), entity.getScaleFactor(), entity.getScaleFactor());
        GL11.glPopMatrix();
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(T entity)
    {
        return SPIDER_TEXTURES;
    }
}