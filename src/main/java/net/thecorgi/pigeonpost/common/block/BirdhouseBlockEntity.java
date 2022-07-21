package net.thecorgi.pigeonpost.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thecorgi.pigeonpost.common.entity.PigeonEntity;
import net.thecorgi.pigeonpost.common.registry.BlockRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BirdhouseBlockEntity extends BlockEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private Pigeon pigeon;
    private NbtList storedItems = new NbtList();
    private BlockPos returnLocation;

    private static final List<String> IRRELEVANT_PIGEON_NBT_KEYS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "Passengers", "Leash", "UUID");

    public BirdhouseBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.BIRDHOUSE_BLOCK_ENTITY, pos, state);
    }

    public boolean hasPigeon() {
        return this.pigeon != null && !this.pigeon.entityData.isEmpty();
    }
    public boolean hasStoredItems() {
        return this.storedItems != null && !this.storedItems.isEmpty();
    }

    public void setReturnLocation(BlockPos pos) {
        this.returnLocation = pos;
    }

    public NbtCompound getPigeonData() {
        if (this.pigeon != null) {
            return this.pigeon.entityData.copy();
        } else {
            return new NbtCompound();
        }
    }



    public void setPigeon(NbtCompound nbtCompound) {
        this.pigeon = new Pigeon(nbtCompound);
    }

    public boolean enterBirdHouse(Entity entity) {
        if (!this.hasPigeon()) {

            entity.stopRiding();
            entity.removeAllPassengers();
            NbtCompound nbtCompound = new NbtCompound();
            entity.saveNbt(nbtCompound);
            this.setPigeon(nbtCompound);

            if (this.world != null) {
                BlockPos blockPos = this.getPos();
                this.world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            entity.discard();
            this.updateListeners();

            return true;
        }
        return false;
    }

    public boolean tryReleasePigeon(BlockState state, PlayerEntity player) {
        if (pigeon != null && world != null) {
            releasePigeon(this.world, this.pos, state, pigeon, player, this.storedItems, this.returnLocation);
            this.pigeon = null;
            this.setStoredItems(new NbtList());
            this.returnLocation = null;

            this.updateListeners();
            return true;
        }
        return false;
    }


    private static void offerOrDropEnvelope(NbtList items, PlayerEntity player) {
        PlayerInventory pl = player.getInventory();
        for(int i = 0; i < items.size(); ++i) {
            NbtCompound item = items.getCompound(i);
            pl.offerOrDrop(ItemStack.fromNbt(item));
        }
    }

    private static boolean releasePigeon(World world, BlockPos pos, BlockState state, Pigeon pigeon, PlayerEntity player, NbtList items, BlockPos returnLocation) {
        NbtCompound nbtCompound = pigeon.entityData.copy();
        removeIrrelevantNbtKeys(nbtCompound);

        offerOrDropEnvelope(items, player);

        Direction direction = state.get(BirdhouseBlock.FACING);
        BlockPos exitPos = pos.offset(direction);

        boolean bl = !world.getBlockState(exitPos).getCollisionShape(world, exitPos).isEmpty();
        if (bl) {
            return false;
        } else {
            Optional<Entity> entity = EntityType.getEntityFromNbt(nbtCompound, world);
            if (entity.isPresent()) {
                if (entity.get() instanceof PigeonEntity pigeonEntity) {
                    float f = pigeonEntity.getWidth();
                    double d = 0.55D + (double)(f / 2.0F);
                    double e = (double)pos.getX() + 0.5D + d * (double)direction.getOffsetX();
                    double g = (double)pos.getY() + 0.5D - (double)(pigeonEntity.getHeight() / 2.0F);
                    double h = (double)pos.getZ() + 0.5D + d * (double)direction.getOffsetZ();
                    pigeonEntity.refreshPositionAndAngles(e, g, h, pigeonEntity.getYaw(), pigeonEntity.getPitch());

                    world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.spawnEntity(pigeonEntity);

                    if (returnLocation != null) {
                        BlockEntity be = world.getBlockEntity(returnLocation);
                        if (be instanceof BirdhouseBlockEntity birdhouse) {
                            birdhouse.enterBirdHouse(pigeonEntity);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public boolean sendToBirdhouse(BlockPos returnLocation, BlockPos newLocation, NbtList items, World world) {
        if (this.hasPigeon() && !this.hasStoredItems()) {
            if (enterNewBirdhouse(this.pigeon, returnLocation, items, newLocation, world)) {
                this.pigeon = null;
                this.storedItems = new NbtList();
                this.returnLocation = null;

//                this.setStoredItems(new NbtList());
                this.updateListeners();

                return true;
            }
        }
        return false;
    }

    static boolean enterNewBirdhouse(Pigeon pigeon, BlockPos returnLocation, NbtList storedItems, BlockPos newLocation, World world) {
        boolean bl = false;
        if (!world.isClient) {
            if (!world.isChunkLoaded(newLocation.getX(), newLocation.getZ())) {
                ServerWorld serverWorld = world.getServer().getWorld(world.getRegistryKey());
                bl = serverWorld.setChunkForced(newLocation.getX(), newLocation.getZ(), true);
            }
        }

        BlockEntity blockEntity = world.getBlockEntity(newLocation);
        if (blockEntity instanceof BirdhouseBlockEntity birdHouse) {
            Optional<Entity> entity = EntityType.getEntityFromNbt(pigeon.entityData.copy(), world);
            if (entity.isPresent() && entity.get() instanceof PigeonEntity pigeonEntity) {
                if (birdHouse.enterBirdHouse(pigeonEntity)) {
                    birdHouse.setStoredItems(storedItems);
                    birdHouse.setReturnLocation(returnLocation);

                    if (bl) {
                        ServerWorld serverWorld = world.getServer().getWorld(world.getRegistryKey());
                        serverWorld.setChunkForced(newLocation.getX(), newLocation.getZ(), false);
                    }
                }

                return true;
            }
        }
        return false;
    }


    public NbtList getStoredItems() {
        return this.storedItems;
    }

    public void setStoredItems(NbtList items) {
        if (items != null) {
            this.storedItems = items;
        }
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.pigeon = null;
        NbtCompound pigeon = nbt.getCompound("Pigeon");
        this.setPigeon(pigeon);
        this.storedItems = nbt.getList("Items", 10);

        if (nbt.contains("Return")) {
            this.returnLocation = BlockPos.fromLong(nbt.getLong("Return"));
        } else {
            this.returnLocation = null;
        }
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Pigeon", this.getPigeonData());
        nbt.put("Items", this.getStoredItems());
        if (this.returnLocation != null) {
            nbt.putLong("Return", this.returnLocation.asLong());
        } else {
            nbt.remove("Return");
        }
    }

    private void updateListeners() {
        super.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bird_house.bag", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
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

    record Pigeon(NbtCompound entityData) {
        Pigeon {
            BirdhouseBlockEntity.removeIrrelevantNbtKeys(entityData);
        }
    }
}
