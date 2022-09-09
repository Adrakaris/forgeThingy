package com.bocbin.forgethingy.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ThiefEntity extends Animal {

	private boolean stealing = false;

	public ThiefEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	// important: allows us to define the tasks and goals that make up the AI of our entity
	// priority based - 0 is highest priority
	// there are a lot of vanilla goals in the goal heirarchy, can use them first
	@Override
	protected void registerGoals() {
		// goalSelector is a local variable of (I think) livingEntity
		goalSelector.addGoal(0, new AvoidEntityGoalNoCombat<>(this, Player.class, 6f, 1.2, 1.2));
		goalSelector.addGoal(1, new ThiefFindsChestGoal(this, 1.3));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.3));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));

	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
		return null;
	}

	// nbt

	@Override
	public void load(CompoundTag pCompound) {
		super.load(pCompound);
		stealing = pCompound.getBoolean("Stealing");
	}

	@Override
	public boolean save(CompoundTag pCompound) {
		pCompound.putBoolean("Stealing", stealing);
		return super.save(pCompound);
	}

	// gettersetter

	public boolean isStealing() {
		return stealing;
	}

	public void setStealing(boolean stealing) {
			this.stealing = stealing;
	}

	// attributes of mob
	// needs to be called with a server-side event
	public static AttributeSupplier.Builder prepareAttributes() {
		return LivingEntity.createLivingAttributes()
				.add(Attributes.ATTACK_DAMAGE, 3.0)
				.add(Attributes.MAX_HEALTH, 20.0)
				.add(Attributes.FOLLOW_RANGE, 40.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3);
	}

	// if entity were not living entity, then to make it spawn correctly would need

//	@Override
//	public Packet<?> getAddEntityPacket() {
//		return NetworkHooks.getEntitySpawningPacket(this);
//	}
}
