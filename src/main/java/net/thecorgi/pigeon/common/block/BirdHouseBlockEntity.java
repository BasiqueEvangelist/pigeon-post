package net.thecorgi.pigeon.common.block;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.inventory.EnvelopeInventory;
import net.thecorgi.pigeon.common.registry.BlockRegistry;
import net.thecorgi.pigeon.common.registry.EntityRegistry;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class BirdHouseBlockEntity extends BlockEntity implements IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);
    private Pigeon pigeon;
    public static final String ENVELOPE_KEY = "Envelope";
    public static final String VARIANT_KEY = "Variant";
    private static final List<String> IRRELEVANT_PIGEON_NBT_KEYS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "Passengers", "Leash", "UUID");
    private final List<ItemStack> stacks = Lists.newArrayList();

    public BirdHouseBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.BIRD_HOUSE_BLOCK_ENTITY, pos, state);
    }

    public boolean hasPigeon() {
        return this.pigeon != null;
    }

    public NbtList getPigeon() {
        NbtList nbtList = new NbtList();
        Pigeon pigeon = this.pigeon;
        NbtCompound nbtCompound = pigeon.entityData.copy();
        nbtCompound.remove("UUID");
        NbtCompound nbtCompound2 = new NbtCompound();
        nbtCompound2.put("EntityData", nbtCompound);
        nbtList.add(nbtCompound2);

        return nbtList;
    }

    public void setPigeon(NbtCompound nbtCompound) {
        this.pigeon = new BirdHouseBlockEntity.Pigeon(nbtCompound);
    }

    public void enterBirdHouse(Entity entity) {
        if (!this.hasPigeon()) {
            entity.stopRiding();
            entity.removeAllPassengers();
            NbtCompound nbtCompound = new NbtCompound();
            entity.saveNbt(nbtCompound);
            this.setPigeon(nbtCompound);
            if (this.world != null) {
                BlockPos blockPos = this.getPos();
                this.world.playSound((PlayerEntity)null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            entity.discard();
            super.markDirty();
        }
    }

    public Entity tryReleasePigeon(BlockState state, PlayerEntity player) {
        Entity entity = new PigeonEntity(EntityRegistry.PIGEON, this.world);
        if (pigeon != null && world != null) {
            releasePigeon(this.world, this.pos, state, pigeon, entity, player);
            this.pigeon = null;
        }
        super.markDirty();

        return entity;
    }


    private static boolean offerOrDropEnvelope(NbtCompound envelope, PlayerEntity player) {
        final EnvelopeInventory inventory = new EnvelopeInventory(envelope, player.getActiveHand(), player);
        inventory.offerOrDropAll(player);
        inventory.clear();
        return false;
    }

    private static boolean releasePigeon(World world, BlockPos pos, BlockState state, BirdHouseBlockEntity.Pigeon pigeon, @Nullable Entity currentPigeon, PlayerEntity player) {
        NbtCompound nbtCompound = pigeon.entityData.copy();
        removeIrrelevantNbtKeys(nbtCompound);
        nbtCompound.putBoolean("NoGravity", true);
        offerOrDropEnvelope(nbtCompound.getCompound("Envelope"), player);
        nbtCompound.put("Envelope", new NbtCompound());
        Direction direction = (Direction)state.get(BirdHouseBlock.FACING);
        BlockPos blockPos = pos.offset(direction);
        boolean bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
        if (bl) {
            return false;
        } else {
            Optional<Entity> entity = EntityType.getEntityFromNbt(nbtCompound, world);
            if (entity.isPresent()) {
                Entity entityx = entity.get();
                if (entity.get() instanceof PigeonEntity pigeonEntity) {
                    float f = entityx.getWidth();
                    double d = bl ? 0.0D : 0.55D + (double)(f / 2.0F);
                    double e = (double)pos.getX() + 0.5D + d * (double)direction.getOffsetX();
                    double g = (double)pos.getY() + 0.5D - (double)(entityx.getHeight() / 2.0F);
                    double h = (double)pos.getZ() + 0.5D + d * (double)direction.getOffsetZ();
                    entityx.refreshPositionAndAngles(e, g, h, entityx.getYaw(), entityx.getPitch());

                    world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    return world.spawnEntity(pigeonEntity);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bird_house.bag", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<BirdHouseBlockEntity>(this, "controller", 0, this::predicate));
    }

    static void removeIrrelevantNbtKeys(NbtCompound compound) {
        for (String string : IRRELEVANT_PIGEON_NBT_KEYS) {
            compound.remove(string);
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    static class Pigeon {
        final NbtCompound entityData;

        Pigeon(NbtCompound entityData) {
            BirdHouseBlockEntity.removeIrrelevantNbtKeys(entityData);
            this.entityData = entityData;
        }
    }
}
