package net.thecorgi.pigeon.common.entity;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.thecorgi.pigeon.common.block.BirdHouseBlock;
import net.thecorgi.pigeon.common.block.BirdHouseBlockEntity;
import net.thecorgi.pigeon.common.registry.ItemRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PigeonEntity extends TameableEntity implements IAnimatable, Flutterer {
    public static final TrackedData<Integer> VARIANT;
    public static final TrackedData<NbtCompound> ENVELOPE;
    public static final TrackedData<Boolean> SITTING;
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final Set<Item> TAMING_INGREDIENTS;
    private boolean songPlaying;
    @Nullable
    private BlockPos songSource;
    private int fleeingTicks = 0;

    static {
        VARIANT = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ENVELOPE = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
        SITTING = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        TAMING_INGREDIENTS = Sets.newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    }

    public PigeonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 10, false);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
    }

    public static DefaultAttributeContainer.Builder createPigeonAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5.0F)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.42F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.14F);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeGroupDangerGoal(this, 1.25D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.add(4, new SitGoal(this));
        this.goalSelector.add(5, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(8, new FlyGoal(this, 1.0D));
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation nav = new BirdNavigation(this, world);
        nav.setCanPathThroughDoors(true);
        nav.setCanSwim(true);
        nav.setCanEnterOpenDoors(true);
        return nav;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.hasVehicle()) {
            return PlayState.STOP;
        } else if (this.dataTracker.get(SITTING)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.pigeon.on_stand", true));
        } else if (this.isInAir()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.pigeon.fall_flight", true));
        } else if (this.isSongPlaying()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.pigeon.dance", true));
        } else if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.pigeon.walk", true));
        } else {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setVariant(random.nextInt(1, 6));
        this.setEnvelope(new NbtCompound());
        if (entityData == null) {
            entityData = new PassiveData(false);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(ENVELOPE, new NbtCompound());
        this.dataTracker.startTracking(SITTING, false);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant());
        nbt.put("Envelope", this.getEnvelope());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(nbt.getInt("Variant"));
        this.setEnvelope(nbt.getCompound("Envelope"));
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.get(VARIANT), 1, 6);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    public NbtCompound getEnvelope() {
        return this.dataTracker.get(ENVELOPE);
    }

    public void setEnvelope(NbtCompound envelope) {
        this.dataTracker.set(ENVELOPE, envelope);
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public void tickMovement() {
        if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46D) || !this.world.getBlockState(this.songSource).isOf(Blocks.JUKEBOX)) {
            this.songPlaying = false;
            this.songSource = null;
        }

        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0D) {
            this.setVelocity(vec3d.multiply(1.0D, 0.55D, 1.0D));
        }

        if (this.fleeingTicks > 0) { --fleeingTicks; }

        this.flapWings();
        super.tickMovement();
    }

    private void flapWings() {
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0D) {
            this.setVelocity(vec3d.multiply(1.0D, 1.3D, 1.0D));
        }
    }

    public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
        this.songSource = songPosition;
        this.songPlaying = playing;
    }

    public boolean isSongPlaying() {
        return this.songPlaying;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!this.isTamed() && TAMING_INGREDIENTS.contains(stack.getItem())) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            if (!this.isSilent()) {
                this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.world.isClient) {
                if (this.random.nextInt(10) == 0) {
                    this.setOwner(player);
                    this.world.sendEntityStatus(this, (byte)7);
                } else {
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }

            return ActionResult.success(this.world.isClient);
        } else if (this.isOwner(player) && stack.isOf(ItemRegistry.ENVELOPE) && player.isSneaking()) {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            if (nbtCompound.contains("Address") && nbtCompound.contains("Envelope")) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                if (!this.world.isClient) {
                    long l = stack.getOrCreateNbt().getLong("Address");
                    BlockPos pos = new BlockPos(BlockPos.unpackLongX(l), BlockPos.unpackLongY(l), BlockPos.unpackLongZ(l));

                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity instanceof BirdHouseBlockEntity birdHouse) {
                        if (!birdHouse.hasPigeon()) {
                            this.setEnvelope(nbtCompound.getCompound("Envelope"));
                            this.stopRiding();
                            birdHouse.enterBirdHouse(this);
                        } else {
                            player.sendMessage(new TranslatableText("item.pigeon.envelope.bird_house_full").formatted(Formatting.RED), true);
                        }
                    } else {
                        player.sendMessage(new TranslatableText("item.pigeon.envelope.not_valid_bird_house").formatted(Formatting.RED), true);
                    }
                }

                return ActionResult.success(this.world.isClient);
            }
        } else if (this.isTamed() && this.isOwner(player) && player.isSneaking()) {
            if (!player.hasPassenger(this)) {
                this.startRiding(player);
            }
        } else {
            return super.interactMob(player, hand);
        }
        return ActionResult.success(this.world.isClient);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public boolean isInAir() {
        return !this.onGround;
    }

    public boolean isPushable() {
        return true;
    }

    protected void pushAway(Entity entity) {
        if (!(entity instanceof PlayerEntity)) {
            super.pushAway(entity);
        }
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.setSitting(false);
            if (!world.isClient) {
                Entity attacker = source.getAttacker();
                if ((attacker != null) && attacker.isLiving()) {
                    Box box = this.getBoundingBox().expand(7.0F, 7.0F, 7.0F);
                    List<PigeonEntity> pigeons = this.world.getEntitiesByClass(PigeonEntity.class, box, EntityPredicates.VALID_LIVING_ENTITY);
                    if (!this.isOwner((LivingEntity) attacker)) {
                        pigeons.iterator().next().fleeFromEntity(attacker);
                    }
                }
            }
            return super.damage(source, amount);
        }
    }

    protected void fleeFromEntity(Entity entity) {
        this.fleeingTicks = 60;
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.fleeingTicks = 0;
        Entity vehicle = this.getVehicle();
        if (vehicle != null) {
            if (vehicle instanceof PlayerEntity)
                if (!vehicle.isOnGround()) {
                    this.dismountVehicle();
                }
        }
    }

    public class EscapeGroupDangerGoal extends EscapeDangerGoal {
        public static final int field_36271 = 1;
        protected final PigeonEntity mob;
        protected final double speed;
        protected double targetX;
        protected double targetY;
        protected double targetZ;
        protected boolean active;

        public EscapeGroupDangerGoal(PigeonEntity mob, double speed) {
            super(mob, speed);
            this.mob = mob;
            this.speed = speed;
        }

        public boolean canStart() {
            if (mob.fleeingTicks > 0) {
                return true;
            }
            super.canStart();
            return false;
        }
    }
}
