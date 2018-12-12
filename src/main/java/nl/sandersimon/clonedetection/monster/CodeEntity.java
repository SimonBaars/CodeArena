package nl.sandersimon.clonedetection.monster;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.model.CloneClass;

public abstract class CodeEntity extends EntityMob {
	
	CloneClass represents;

	public CodeEntity(World worldIn) {
		super(worldIn);
	}
}
