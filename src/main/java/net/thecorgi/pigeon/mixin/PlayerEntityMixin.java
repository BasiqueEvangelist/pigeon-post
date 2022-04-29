package net.thecorgi.pigeon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.entity.TameableHeadEntity;
import net.thecorgi.pigeon.common.registry.EntityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    private void dropHeadEntities() {
        if (this.hasPassengers()) {
            Entity passenger = this.getFirstPassenger();
            if (passenger.getType() == EntityRegistry.PIGEON) {
                passenger.stopRiding();
            }
        }
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "TAIL"))
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isInvulnerableTo(source)) {
            this.dropHeadEntities();
        }
    }

    @Inject(method = "jump()V", at = @At(value = "TAIL"))
    public void jump(CallbackInfo ci) {
        this.dropHeadEntities();
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (passenger.getType() == EntityRegistry.PIGEON) {
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
}