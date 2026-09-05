package com.simonbaars.codearena.monster;

import com.simonbaars.codearena.model.MetricProblem;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class CodeEntity extends EntityMob {
	
	MetricProblem represents;
	private boolean isServer = false;

	public CodeEntity(World world, MetricProblem cloneClass) {
		super(world);
		this.represents = cloneClass;
		//float f = 0.03F*cloneClass.volume();
		//double boundingX = getEntityBoundingBox().maxX-getEntityBoundingBox().minX*3;
		//double boundingY = getEntityBoundingBox().maxY-getEntityBoundingBox().minY/2;
		//double boundingZ = getEntityBoundingBox().maxZ-getEntityBoundingBox().minZ*3;
		//Vec3d center = getEntityBoundingBox().getCenter();
		
		//this.setEntityBoundingBox(new AxisAlignedBB(center.x-((boundingX/2)*f), center.y-((boundingY/2)*f), center.z-((boundingZ/2)*f), center.x+((boundingX/2)*f), center.y+((boundingY/2)*f), center.z+((boundingZ/2)*f)));
		//this.setSize(1.5F*f, 0.5F*f);
		this.isServer = true;
		this.setHealth(Float.MAX_VALUE);
		this.setAlwaysRenderNameTag(true);
		this.setCustomNameTag(cloneClass.getName());
		this.ignoreFrustumCheck = true;
	}
	
	public CodeEntity(World worldIn) {
        super(worldIn);
    }
	
	public void setSizePublic(float w, float h) {
		this.setSize(w, h);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if(isServer && represents != null && source.getTrueSource() instanceof EntityPlayer) //{
			represents.open();
			//setDead();
		//}
        return super.attackEntityFrom(source, amount);
    }

	public MetricProblem getRepresents() {
		return represents;
	}

	public void setRepresents(MetricProblem represents) {
		this.represents = represents;
	}
	
	 /*@Override
	    public void setPosition(double par1, double par2, double par3) {
	    	AxisAlignedBB b = this.getEntityBoundingBox();
	    	double boxSX = b.maxX - b.minX;
	    	double boxSY = b.maxY - b.minY;
	    	double boxSZ = b.maxZ - b.minZ;
	    	this.setEntityBoundingBox(new AxisAlignedBB(posX - boxSX/2D, posY, posZ - boxSZ/2D, posX + boxSX/2D, posY + boxSY, posZ + boxSZ/2D));
	    }*/
}
