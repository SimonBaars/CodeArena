package nl.sandersimon.clonedetection.monster;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.model.CloneClass;

public abstract class CodeEntity extends EntityMob {
	
	CloneClass represents;

	public CodeEntity(World worldIn, CloneClass cloneClass) {
		super(worldIn);
		this.represents = cloneClass;
		float f = 0.05F*cloneClass.volume();
		this.setSize(width*f, height*f);
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
