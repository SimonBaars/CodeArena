package com.simonbaars.codearena.monster;

import com.simonbaars.codearena.problem.CodeProblem;
import com.simonbaars.codearena.problem.ProblemType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;

/**
 * Spawns registered smell entity types (stand-ins for legacy CodeSpider / CodeZombie /
 * CodeSkeleton / CodeCreeper + demo extras), scaled by problem severity.
 */
public final class SmellMobFactory {
	private SmellMobFactory() {}

	public static Mob create(ServerLevel level, CodeProblem problem) {
		EntityType<? extends Mob> type = entityType(problem.getType());
		Mob mob = type.create(level, EntitySpawnReason.EVENT);
		if (mob == null) {
			return null;
		}
		applyPresentation(mob, problem);
		return mob;
	}

	private static EntityType<? extends Mob> entityType(ProblemType type) {
		return switch (type) {
			case DUPLICATION -> ModEntities.CODE_SPIDER;
			case UNITCOMPLEXITY -> ModEntities.CODE_ZOMBIE;
			case UNITINTERFACESIZE -> ModEntities.CODE_SKELETON;
			case UNITVOLUME -> ModEntities.CODE_CREEPER;
			case TYPE2CLONE -> ModEntities.CODE_CAVE_SPIDER;
			case TYPE3CLONE -> ModEntities.CODE_WITCH;
			case NESTINGDEPTH -> ModEntities.CODE_BLAZE;
			case GODCLASS -> ModEntities.CODE_ENDERMAN;
		};
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
			case DUPLICATION, TYPE2CLONE -> 0.28;
			case UNITCOMPLEXITY -> 0.23;
			case UNITINTERFACESIZE -> 0.25;
			case UNITVOLUME -> 0.25;
			case TYPE3CLONE -> 0.24;
			case NESTINGDEPTH -> 0.23;
			case GODCLASS -> 0.22;
		});

		if (problem.getType() == ProblemType.UNITCOMPLEXITY || problem.getType() == ProblemType.GODCLASS) {
			setBase(mob, Attributes.ATTACK_DAMAGE, 3.0 + severity * 0.15);
			setBase(mob, Attributes.ARMOR, 2.0);
			setBase(mob, Attributes.FOLLOW_RANGE, 35.0);
		}

		// CodeArena disables mob griefing for the session; also defuse creeper swell as belt-and-suspenders.
		if (mob instanceof Creeper creeper) {
			creeper.setSwellDir(-1);
		}
	}

	private static void setBase(Mob mob, Holder<Attribute> attr, double value) {
		AttributeInstance instance = mob.getAttribute(attr);
		if (instance != null) {
			instance.setBaseValue(value);
		}
	}
}
