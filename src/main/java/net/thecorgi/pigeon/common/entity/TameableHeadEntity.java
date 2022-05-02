package net.thecorgi.pigeon.common.entity;

import net.minecraft.client.render.entity.GiantEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class TameableHeadEntity extends TameableEntity {
    private static final int READY_TO_SIT_COOLDOWN = 100;
    private int ticks;

    protected TameableHeadEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean mountOnHead(PlayerEntity player) {
        if (!player.hasPassenger(this)) {
            this.startRiding(player);
            return true;
        }
        return false;
    }

    public void tick() {
        ++this.ticks;
        super.tick();
    }

    public boolean isReadyToSitOnPlayer() {
        return this.ticks > READY_TO_SIT_COOLDOWN;
    }
}
