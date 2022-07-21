package net.thecorgi.pigeonpost.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class TameableHeadEntity extends TameableEntity {
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
}
