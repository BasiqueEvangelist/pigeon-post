package net.thecorgi.pigeon.common.block;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.handler.EnvelopeScreenHandler;
import net.thecorgi.pigeon.common.inventory.EnvelopeInventory;
import net.thecorgi.pigeon.common.inventory.EnvelopeInventoryInterface;
import net.thecorgi.pigeon.common.item.EnvelopeItem;
import net.thecorgi.pigeon.common.registry.EntityRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.thecorgi.pigeon.PigeonPost.id;

public class BirdHouseBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public BirdHouseBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BirdHouseBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BirdHouseBlockEntity) {
                BirdHouseBlockEntity birdHouse = (BirdHouseBlockEntity) blockEntity;
                System.out.println(birdHouse.getPigeon());
                if (birdHouse.getPigeon()) {
                    final EnvelopeInventory inventory = new EnvelopeInventory(birdHouse.getEnvelope(), hand, player);
                    player.getInventory().offerOrDrop(inventory.getStack(1));
                    player.getInventory().offerOrDrop(inventory.getStack(2));
                    player.getInventory().offerOrDrop(inventory.getStack(3));
                    player.getInventory().offerOrDrop(inventory.getStack(4));
                    inventory.clear();
                    birdHouse.setEnvelope(new NbtCompound());
                } else if (player.hasPassengers()) {
                    Entity passenger = player.getFirstPassenger();
                    if (passenger != null && passenger.getType() == EntityRegistry.PIGEON) {
                        NbtCompound envelope = ((PigeonEntity) passenger).getEnvelope();
                        birdHouse.setPigeon(true);
                        birdHouse.setEnvelope(envelope);
                        passenger.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
                if (birdHouse.getPigeon() && player.isSneaking()) {
                    PigeonEntity pigeon = new PigeonEntity(EntityRegistry.PIGEON, world);
                    player.startRiding(pigeon);
                }
            }
        }
        return ActionResult.PASS;
    }
}
