package nl.sandersimon.clonedetection.monster;

import net.minecraft.entity.monster.EntityMob;
import nl.sandersimon.clonedetection.model.CloneClass;

public interface CodeEntity2 {
	public CloneClass getRepresents();

	public void setRepresents(CloneClass represents);
	
	public EntityMob getMob();
}
