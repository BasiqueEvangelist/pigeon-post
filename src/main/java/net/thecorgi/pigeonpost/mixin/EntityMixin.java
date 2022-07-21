package net.thecorgi.pigeonpost.mixin;

import net.minecraft.entity.Entity;
import net.thecorgi.pigeonpost.common.registry.EntityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;)V", at = @At(value = "TAIL"))
    public void updatePassengerPosition(Entity passenger, CallbackInfo ci) {
        Entity vehicle = passenger.getVehicle();
        if (passenger.getType() == EntityRegistry.PIGEON && vehicle.isPlayer()) {
            passenger.setPosition(
                    vehicle.getX(),
                    vehicle.getY() + vehicle.getMountedHeightOffset() + passenger.getHeightOffset(),
                    vehicle.getZ()
            );

            passenger.setYaw(vehicle.getYaw());
            passenger.setPitch(vehicle.getPitch());
        }
    }

}
