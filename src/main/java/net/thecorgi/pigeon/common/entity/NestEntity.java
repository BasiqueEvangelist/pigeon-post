package net.thecorgi.pigeon.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.registry.EntityRegistry;
import net.thecorgi.pigeon.common.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class NestEntity extends LivingEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private final DefaultedList<ItemStack> armorItems;
    public static final TrackedData<NbtCompound> BAG;
    public static final TrackedData<Boolean> PIGEON;

    static {
        BAG = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
        PIGEON = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    public NestEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PIGEON, false);
    }

    public boolean getPigeon() {
        return this.dataTracker.get(PIGEON);
    }

    public void setPigeon(boolean value) {
        this.dataTracker.set(PIGEON, value);
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.nest.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<NestEntity>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return new ItemStack(Items.CHEST);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return null;
    }

    public boolean isMobOrPlayer() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    protected void pushAway(Entity entity) {
    }

//    public ItemStack getPickBlockStack() {
//        return new ItemStack(PigeonPost.NEST);
//    }

    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(ItemRegistry.ENVELOPE) && player.isSneaking() && !itemStack.getOrCreateNbt().contains("Address")) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            long l = this.getBlockPos().asLong();
            ItemStack envelope = ItemRegistry.ENVELOPE.getDefaultStack();
            NbtCompound nbtCompound = envelope.getOrCreateNbt();
            nbtCompound.putLong("Address", l);
            envelope.writeNbt(nbtCompound);

            player.giveItemStack(envelope);

        } else if (player.hasPassengers() && !this.hasPassengers() && player.isSneaking()) {
                Entity passenger = player.getFirstPassenger();
                if (passenger != null && passenger.getType() == EntityRegistry.PIGEON) {
                    passenger.startRiding(this);
                    PigeonEntity er = EntityRegistry.PIGEON.create(player.world);

//                    ((PigeonEntity) passenger).set(true);
                }
        } else if (!player.hasPassengers() && this.hasPassengers() && player.isSneaking()) {
            Entity passenger = this.getFirstPassenger();
            if (passenger != null && passenger.getType() == EntityRegistry.PIGEON && ((PigeonEntity) passenger).isOwner(player)) {
//                ((PigeonEntity) passenger).setDisplayBag(false);
                passenger.startRiding(player);
            }
        }
        return ActionResult.success(this.world.isClient);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (passenger instanceof PigeonEntity) {
            passenger.setPosition(
                    this.getX(),
                    this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + 0.45,
                    this.getZ()
            );
            passenger.setYaw(this.getYaw());
            passenger.setPitch(this.getPitch());
//            ((PigeonEntity) passenger).setDisplayBag(false);
        }
    }

//    public boolean damage(DamageSource source, float amount) {
//        if (!this.world.isClient && !this.isRemoved()) {
//            if (DamageSource.OUT_OF_WORLD.equals(source)) {
//                this.kill();
//                return false;
//            } else if (!this.isInvulnerableTo(source) && !this.invisible && !this.isMarker()) {
//                if (source.isExplosive()) {
//                    this.onBreak(source);
//                    this.kill();
//                    return false;
//                } else if (DamageSource.IN_FIRE.equals(source)) {
//                    if (this.isOnFire()) {
//                        this.updateHealth(source, 0.15F);
//                    } else {
//                        this.setOnFireFor(5);
//                    }
//
//                    return false;
//                } else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
//                    this.updateHealth(source, 4.0F);
//                    return false;
//                } else {
//                    boolean bl = source.getSource() instanceof PersistentProjectileEntity;
//                    boolean bl2 = bl && ((PersistentProjectileEntity)source.getSource()).getPierceLevel() > 0;
//                    boolean bl3 = "player".equals(source.getName());
//                    if (!bl3 && !bl) {
//                        return false;
//                    } else if (source.getAttacker() instanceof PlayerEntity && !((PlayerEntity)source.getAttacker()).getAbilities().allowModifyWorld) {
//                        return false;
//                    } else if (source.isSourceCreativePlayer()) {
//                        this.playBreakSound();
//                        this.spawnBreakParticles();
//                        this.kill();
//                        return bl2;
//                    } else {
//                        long l = this.world.getTime();
//                        if (l - this.lastHitTime > 5L && !bl) {
//                            this.world.sendEntityStatus(this, (byte)32);
//                            this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
//                            this.lastHitTime = l;
//                        } else {
//                            this.breakAndDropItem(source);
//                            this.spawnBreakParticles();
//                            this.kill();
//                        }
//
//                        return true;
//                    }
//                }
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
}
