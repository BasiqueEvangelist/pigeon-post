package net.thecorgi.pigeonpost.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.thecorgi.pigeonpost.common.registry.EntityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    @Shadow public abstract boolean isPlayer();

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private void dropHeadEntities() {
        if (this.hasPassengers()) {
            Entity passenger = this.getFirstPassenger();
            if (passenger.getType() == EntityRegistry.PIGEON) {
                passenger.stopRiding();
            }
        }
    }

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "TAIL"))
    public void pigeon$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isInvulnerableTo(source)) {
            this.dropHeadEntities();
        }
    }

    @Inject(method = "jump()V", at = @At(value = "TAIL"))
    public void pigeon$jump(CallbackInfo ci) {
        this.dropHeadEntities();
    }
}