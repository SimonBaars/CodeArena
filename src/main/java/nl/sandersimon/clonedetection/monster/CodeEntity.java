package nl.sandersimon.clonedetection.monster;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.model.CloneClass;

public abstract class CodeEntity extends EntityMob {
	
	CloneClass represents;

	public CodeEntity(World worldIn, CloneClass cloneClass, int cloneSize) {
		super(worldIn);
		this.represents = cloneClass;
		this.setSize(0.14F*(cloneSize/2), 0.09F*(cloneSize/2));
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if(source.getTrueSource() instanceof EntityPlayer)
			represents.open();
        return super.attackEntityFrom(source, amount);
    }
}
