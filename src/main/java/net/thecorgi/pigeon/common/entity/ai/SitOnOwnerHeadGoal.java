package net.thecorgi.pigeon.common.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.thecorgi.pigeon.common.entity.PigeonEntity;
import net.thecorgi.pigeon.common.entity.TameableHeadEntity;

public class SitOnOwnerHeadGoal extends Goal {
    private final TameableHeadEntity tameable;
    private ServerPlayerEntity owner;
    private boolean mounted;

    public SitOnOwnerHeadGoal(TameableHeadEntity tameable) {
        this.tameable = tameable;
    }

    public boolean canStart() {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.tameable.getOwner();
        boolean bl = serverPlayerEntity != null && !serverPlayerEntity.isSpectator() && !serverPlayerEntity.getAbilities().flying && !serverPlayerEntity.isTouchingWater() && !serverPlayerEntity.inPowderSnow;
        return !this.tameable.isSitting() && bl && this.tameable.isReadyToSitOnPlayer();
    }

    public boolean canStop() {
        return !this.mounted;
    }

    public void start() {
        this.owner = (ServerPlayerEntity)this.tameable.getOwner();
        this.mounted = false;
    }


    public void tick() {
        if (!this.mounted && !this.tameable.isSitting() && !this.tameable.isLeashed()) {
            if (this.tameable.getBoundingBox().intersects(this.owner.getBoundingBox())) {
                this.tameable.mountOnHead(this.owner);
                System.out.println("MOUNTING");
            }

        }
    }
}
