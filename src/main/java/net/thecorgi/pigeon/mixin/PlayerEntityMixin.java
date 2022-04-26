package net.thecorgi.pigeon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.entity.TameableHeadEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
//
//    @Override
//    public double getMountedHeightOffset() {
//        if (this.isSneaking()) {
//            return super.getMountedHeightOffset() - 0.15;
//        }
//        return super.getMountedHeightOffset();
//    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!(passenger instanceof TameableHeadEntity) || !this.hasPassenger(passenger)) return;

        System.out.println(passenger);
        passenger.setPosition(
                this.getX(),
                this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + 0.35,
                this.getZ()
        );
        passenger.setYaw(this.getYaw());
        passenger.setPitch(this.getPitch());
        super.updatePassengerPosition(passenger);
    }
}