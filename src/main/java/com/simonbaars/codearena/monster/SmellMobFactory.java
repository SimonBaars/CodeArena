package com.simonbaars.codearena.monster;

import com.simonbaars.codearena.problem.CodeProblem;
import com.simonbaars.codearena.problem.ProblemType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Spawns distinctly named vanilla mobs as stand-ins for legacy CodeSpider / CodeZombie /
 * CodeSkeleton / CodeCreeper — scaled by problem severity via {@link Attributes#SCALE} and health.
 */
public final class SmellMobFactory {
	private SmellMobFactory() {}

	public static Mob create(ServerLevel level, CodeProblem problem) {
		Mob mob = switch (problem.getType()) {
			case DUPLICATION -> EntityTypes.SPIDER.create(level, EntitySpawnReason.EVENT);
			case UNITCOMPLEXITY -> EntityTypes.ZOMBIE.create(level, EntitySpawnReason.EVENT);
			case UNITINTERFACESIZE -> EntityTypes.SKELETON.create(level, EntitySpawnReason.EVENT);
			case UNITVOLUME -> EntityTypes.CREEPER.create(level, EntitySpawnReason.EVENT);
		};
		if (mob == null) {
			return null;
		}
		applyPresentation(mob, problem);
		return mob;
	}

	private static void applyPresentation(Mob mob, CodeProblem problem) {
		String label = problem.getType().getDisplayName() + ": " + problem.getName();
		mob.setCustomName(Component.literal(label));
		mob.setCustomNameVisible(true);
		mob.setPersistenceRequired();

		double severity = problem.getSeverity();
		double health = 10.0 + severity * 2.0;
		setBase(mob, Attributes.MAX_HEALTH, health);
		mob.setHealth((float) health);

		double scale = 0.85 + Math.min(severity, 20) * 0.03;
		setBase(mob, Attributes.SCALE, scale);

		setBase(mob, Attributes.MOVEMENT_SPEED, switch (problem.getType()) {
			case DUPLICATION -> 0.28;
			case UNITCOMPLEXITY -> 0.23;
			case UNITINTERFACESIZE -> 0.25;
			case UNITVOLUME -> 0.25;
		});

		if (problem.getType() == ProblemType.UNITCOMPLEXITY) {
			setBase(mob, Attributes.ATTACK_DAMAGE, 3.0 + severity * 0.15);
			setBase(mob, Attributes.ARMOR, 2.0);
			setBase(mob, Attributes.FOLLOW_RANGE, 35.0);
		}
	}

	private static void setBase(Mob mob, Holder<Attribute> attr, double value) {
		AttributeInstance instance = mob.getAttribute(attr);
		if (instance != null) {
			instance.setBaseValue(value);
		}
	}
}
