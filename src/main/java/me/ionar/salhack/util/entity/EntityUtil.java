package me.ionar.salhack.util.entity;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.ionar.salhack.main.Wrapper.mc;

public class EntityUtil {
    public static ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<>();
        if (mc.world != null) for (Entity entity : mc.world.getEntities()) entities.add(entity);
        return entities;
    }

    public static boolean isPassive(Entity entity) {
        if (entity instanceof Angerable mob) return !mob.isUniversallyAngry(mc.world) && mob.getTarget() == null;
        else return entity instanceof AmbientEntity || entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public static boolean isFakeLocalPlayer(Entity entity) {
        return entity != null && entity.getId() == -100 && mc.player != entity;
    }

    public static BlockPos getPositionVectorBlockPos(Entity entity, @Nullable BlockPos toAdd) {
        final Vec3d vec = entity.getPos();
        if (toAdd == null) return BlockPos.ofFloored(vec.x, vec.y, vec.z);
        return BlockPos.ofFloored(vec.x, vec.y, vec.z).add(toAdd);
    }

    /**
     * If the mob by default won't attack the player, but will if the player attacks
     * it
     */
    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof Angerable;
    }

    /**
     * If the mob is hostile
     */
    public static boolean isHostileMob(Entity entity) {
        return entity instanceof HostileEntity;
    }

    public static boolean isInWater(Entity entity) {
        if (entity == null || mc.world == null) return false;
        double y = entity.getY() + 0.01;
        for (int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); x++)
            for (int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); z++) {
                BlockPos pos = new BlockPos(x, (int) y, z);
                if (mc.world.getBlockState(pos).getFluidState() != null) return true;
            }
        return false;
    }

    public static boolean isDrivenByPlayer(Entity entity) {
        return mc.player != null && entity != null && entity.equals(mc.player.getVehicle());
    }

    public static boolean isAboveWater(Entity entity) {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(Entity entity, boolean packet) {
        if (entity == null || mc.world == null) return false;
        double y = entity.getY() - (packet ? 0.03 : (EntityUtil.isPlayer(entity) ? 0.2 : 0.5));
        // increasing this seems
        // to flag more in NCP but
        // needs to be increased
        // so the player lands on
        // solid water
        for (int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); x++)
            for (int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); z++) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getFluidState() != null) return true;
            }
        return false;
    }

    public static double[] calculateLookAt(double x, double y, double z, PlayerEntity player) {
        double dirx = player.getX() - x;
        double diry = player.getY() - y;
        double dirz = player.getZ() - z;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;
        yaw += 90f;
        return new double[]{ yaw, pitch };
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    public static double getRelativeX(float yaw) {
        return MathHelper.sin(-yaw * 0.017453292F);
    }

    public static double getRelativeZ(float yaw) {
        return MathHelper.cos(yaw * 0.017453292F);
    }

    public static int GetPlayerMS(PlayerEntity player) {
        if (player.getUuid() != null) {
            ClientPlayNetworkHandler handler = mc.getNetworkHandler();
            if (handler != null) {
                PlayerListEntry entry = handler.getPlayerListEntry(player.getUuid());
                if (entry != null) return entry.getLatency();
            }
        }
        return 0;
    }
}
