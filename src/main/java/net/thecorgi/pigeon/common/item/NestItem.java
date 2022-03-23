package net.thecorgi.pigeon.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.entity.NestEntity;
import net.thecorgi.pigeon.common.registry.EntityRegistry;

public class NestItem extends Item{
    public NestItem(Item.Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
            BlockPos blockPos = itemPlacementContext.getBlockPos();
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Box box = EntityRegistry.NEST.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            if (world.isSpaceEmpty((Entity)null, box) && world.getOtherEntities((Entity)null, box).isEmpty()) {
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld)world;
                    NestEntity birdStandEntity = (NestEntity) EntityRegistry.NEST.create(serverWorld, itemStack.getNbt(), (Text)null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
                    if (birdStandEntity == null) {
                        return ActionResult.FAIL;
                    }

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    birdStandEntity.refreshPositionAndAngles(birdStandEntity.getX(), birdStandEntity.getY(), birdStandEntity.getZ(), f, 0.0F);
//                    this.setRotations(birdStandEntity, world.random);
                    serverWorld.spawnEntityAndPassengers(birdStandEntity);
                    world.playSound((PlayerEntity)null, birdStandEntity.getX(), birdStandEntity.getY(), birdStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, birdStandEntity);
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.FAIL;
            }
        }
    }
//
//    private void setRotations(NestEntity stand, Random random) {
//        EulerAngle eulerAngle = stand.getHeadRotation();
//        float f = random.nextFloat() * 5.0F;
//        float g = random.nextFloat() * 20.0F - 10.0F;
//        EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
//        stand.setHeadRotation(eulerAngle2);
//        eulerAngle = stand.getBodyRotation();
//        f = random.nextFloat() * 10.0F - 5.0F;
//        eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
//        stand.setBodyRotation(eulerAngle2);
//    }
}