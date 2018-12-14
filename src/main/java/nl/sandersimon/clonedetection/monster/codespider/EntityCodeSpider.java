package nl.sandersimon.clonedetection.monster.codespider;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.monster.CodeEntity;

public class EntityCodeSpider extends EntitySpider implements CodeEntity
{
	CloneClass represents;

	public EntityCodeSpider(World worldIn, CloneClass cloneClass, int cloneSize) {
		super(worldIn);
		this.represents = cloneClass;
		this.setSize(0.14F*(cloneSize/2), 0.09F*(cloneSize/2));
		this.setHealth(Float.MAX_VALUE);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if(source.getTrueSource() instanceof EntityPlayer)
			represents.open();
        return super.attackEntityFrom(source, amount);
    }

	public CloneClass getRepresents() {
		return represents;
	}

	public void setRepresents(CloneClass represents) {
		this.represents = represents;
	}

	@Override
	public EntityMob getMob() {
		return this;
	}
}