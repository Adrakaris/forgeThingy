package com.bocbin.forgethingy.entities;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

// vanilla minecraft AvoidEntityGoal only works in combat, so McJty makes a version for outside
// of combat
public class AvoidEntityGoalNoCombat<T extends LivingEntity> extends Goal {
	protected final PathfinderMob mob;
	private final double walkSpeedModifier;
	private final double sprintSpeedModifier;
	@Nullable protected T toAvoid;
	protected final float maxDist;
	@Nullable protected Path path;
	protected final PathNavigation pathNav;
	// class of entity this goal seeks to avoid
	protected final Class<T> toAvoidClass;
	private final TargetingConditions avoidEntityTargeting;

	// this goal helps mobs avoid other mobs of a specific class
	public AvoidEntityGoalNoCombat(PathfinderMob mob, Class<T> entityToAvoidClass, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
		this.mob = mob;
		this.toAvoidClass = entityToAvoidClass;
		this.maxDist = maxDist;
		this.walkSpeedModifier = walkSpeedModifier;
		this.sprintSpeedModifier = sprintSpeedModifier;
		this.pathNav = mob.getNavigation();
		this.setFlags(EnumSet.of(Flag.MOVE));
		this.avoidEntityTargeting = TargetingConditions.forNonCombat().range(maxDist).selector(EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);

	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	@Override
	public boolean canUse() {
		List<T> entitesOfClass = mob.level.getEntitiesOfClass(
				toAvoidClass,
				mob.getBoundingBox().inflate(maxDist, 3, maxDist),
				(e) -> true
		);
		toAvoid = mob.level.getNearestEntity(entitesOfClass, avoidEntityTargeting,
				mob, mob.getX(), mob.getY(), mob.getZ());

		if (toAvoid == null) {
			return false;
		}
		Vec3 vec3 = DefaultRandomPos.getPosAway(mob, 16, 7, toAvoid.position());
		if (vec3 == null) {
			return false;
		} else if (toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < toAvoid.distanceToSqr(mob)) {
			return false;
		} else {
			path = pathNav.createPath(vec3.x, vec3.y, vec3.z,  0);
			return path != null;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean canContinueToUse() {
		return !pathNav.isDone();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void start() {
		pathNav.moveTo(path, walkSpeedModifier);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void stop() {
		toAvoid = null;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void tick() {
		if (mob.distanceToSqr(toAvoid) < 49) {
			mob.getNavigation().setSpeedModifier(sprintSpeedModifier);
		} else {
			mob.getNavigation().setSpeedModifier(walkSpeedModifier);
		}
	}
}
