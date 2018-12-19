package nl.sandersimon.clonedetection.monster;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.model.CloneClass;

public abstract class CodeEntity extends EntityMob {
	
	CloneClass represents;

	public CodeEntity(World worldIn, CloneClass cloneClass) {
		super(worldIn);
		this.represents = cloneClass;
		//double f = 0.05*cloneClass.volume();
		//double boundingX = getEntityBoundingBox().maxX-getEntityBoundingBox().minX*3;
		//double boundingY = getEntityBoundingBox().maxY-getEntityBoundingBox().minY/2;
		//double boundingZ = getEntityBoundingBox().maxZ-getEntityBoundingBox().minZ*3;
		//Vec3d center = getEntityBoundingBox().getCenter();
		
		//this.setEntityBoundingBox(new AxisAlignedBB(center.x-((boundingX/2)*f), center.y-((boundingY/2)*f), center.z-((boundingZ/2)*f), center.x+((boundingX/2)*f), center.y+((boundingY/2)*f), center.z+((boundingZ/2)*f)));
		//this.setSize(width*f, height*f);
		this.setHealth(Float.MAX_VALUE);
		this.setAlwaysRenderNameTag(true);
		this.setCustomNameTag(cloneClass.getName());
		this.ignoreFrustumCheck = true;
	}
	
	public CodeEntity(World worldIn) {
        super(worldIn);
    }
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if(represents != null && source.getTrueSource() instanceof EntityPlayer) //{
			represents.open();
			//setDead();
		//}
        return super.attackEntityFrom(source, amount);
    }

	public CloneClass getRepresents() {
		return represents;
	}

	public void setRepresents(CloneClass represents) {
		this.represents = represents;
	}
}
