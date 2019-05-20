package com.simonbaars.codearena.monster;

import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.monster.codecreeper.EntityCodeCreeper;
import com.simonbaars.codearena.monster.codeskeleton.AbstractCodeSkeleton;
import com.simonbaars.codearena.monster.codespider.EntityCodeSpider;
import com.simonbaars.codearena.monster.codezombie.EntityCodeZombie;

public interface UsesCustomScaleFactors {
	
	public default float getScaleFactor(CodeEntity codeEntity, MetricProblem p) {
		if(codeEntity instanceof EntityCodeSpider) { //Code clones
			return checkF(p.volume()*0.03F);
		} else if(codeEntity instanceof AbstractCodeSkeleton) { // Unit interface size
			return checkF(((p.volume()*2)+4)*0.03F);
		} else if(codeEntity instanceof EntityCodeCreeper) { // Unit volume
			return checkF(((p.volume()/1.5F)-5)*0.03F);
		} else if(codeEntity instanceof EntityCodeZombie) { // Unit complexity
			return checkF((p.volume()-5)*0.03F);
		}
		return 1F;
	}
	
	public default float getScaleFactor(CodeEntity codeEntity) {
		return getScaleFactor(codeEntity, codeEntity.getRepresents());
	}
	
	public default float checkF(float f) {
		if(f>4.0F)
			f = 4.0F;
		return 0.4F+f;
	}
}
