package net.thecorgi.pigeonpost.common.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thecorgi.pigeonpost.common.entity.PigeonEntity;
import net.thecorgi.pigeonpost.common.registry.ItemRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.thecorgi.pigeonpost.common.envelope.EnvelopeItem.ADDRESS_KEY;
import static net.thecorgi.pigeonpost.common.envelope.EnvelopeItem.ITEMS_KEY;

public class BirdhouseBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED;
    public static final DirectionProperty FACING;

    static {
        POWERED = Properties.POWERED;
        FACING = Properties.HORIZONTAL_FACING;
    }

    public BirdhouseBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, POWERED);
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BirdhouseBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hand.equals(player.getActiveHand())) {
            ItemStack stack = player.getStackInHand(player.getActiveHand());

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BirdhouseBlockEntity birdhouse) {
                if (birdhouse.hasPigeon() && !birdhouse.getPigeonData().isEmpty()) {
                    if (stack.isOf(ItemRegistry.ENVELOPE)) {

                        NbtCompound nbtCompound = stack.getOrCreateNbt();
                        if (nbtCompound.contains("Items") && nbtCompound.contains("Address")) {

                            Optional<Entity> pigeonx = EntityType.getEntityFromNbt(birdhouse.getPigeonData(), world);
                            if (pigeonx.isPresent() && pigeonx.get() instanceof PigeonEntity pigeon && pigeon.isOwner(player)){
                                if (birdhouse.sendToBirdhouse(pos, BlockPos.fromLong(nbtCompound.getLong(ADDRESS_KEY)), nbtCompound.getList(ITEMS_KEY, 10), world)) {
                                    stack.decrement(stack.getCount());
                                    return ActionResult.SUCCESS;
                                } else {
                                    player.sendMessage(new TranslatableText("block.pigeonpost.birdhouse.full").formatted(Formatting.RED), true);
                                }
                            }
                        }
                    } else if (!player.isSneaking()) {
                        birdhouse.tryReleasePigeon(state, player);
                        return ActionResult.SUCCESS;
                    }
                } else if (player.hasPassengers()) {
                    List<Entity> entities = player.getPassengerList();
                    for (Entity entity : entities) {
                        if (entity instanceof PigeonEntity pigeon) {
                            birdhouse.enterBirdHouse(pigeon);
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            } else {
                player.sendMessage(new TranslatableText("block.pigeonpost.birdhouse.invalid").formatted(Formatting.RED), true);
            }
        }

        return ActionResult.SUCCESS;
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BirdhouseBlockEntity birdhouse) {
                if (birdhouse.hasPigeon()) {
                    birdhouse.tryReleasePigeon(state, player);
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean bl = !world.isReceivingRedstonePower(pos);
        if (bl != state.get(POWERED)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BirdhouseBlockEntity) {
                world.setBlockState(pos, state.with(POWERED, bl), 4);
            }
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof BirdhouseBlockEntity birdhouse) {
            if (birdhouse.hasPigeon()) {
                return 6;
            }
        }
        return 0;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
