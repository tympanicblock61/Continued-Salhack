package me.ionar.salhack.util.entity;

import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.mixin.ClientPlayerEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class PlayerUtil {
    final static DecimalFormat Formatter = new DecimalFormat("#.#");
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean rotating = false;
    public enum FacingDirection {
        North,
        South,
        East,
        West,
    }

    public static int getItemSlot(Item input) {
        if (mc.player == null) return 0;
        for (int i = 0; i < mc.player.getInventory().size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8) continue;
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == input) return i;
        }
        return -1;
    }

    public static int getRecursiveItemSlot(Item input) {
        if (mc.player == null) return 0;
        for (int i = mc.player.getInventory().size() - 1; i > 0; --i) {
            if (i == 5 || i == 6 || i == 7 || i == 8) continue;
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == input) return i;
        }
        return -1;
    }

    public static int getItemSlotNotHotbar(Item input) {
        if (mc.player == null) return 0;
        for (int i = 9; i < 36; i++) {
            final Item item = mc.player.getInventory().getStack(i).getItem();
            if (item == input) {
                return i;
            }
        }
        return -1;
    }

    public static int getItemCount(Item input) {
        if (mc.player == null) return 0;
        int items = 0;
        for (int i = 0; i < 45; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == input) items += stack.getCount();
        }
        return items;
    }


    public static boolean isEating() {
        return mc.player != null && mc.player.getMainHandStack().getItem().isFood() && mc.player.isUsingItem();
    }

    public static int getItemInHotbar(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != ItemStack.EMPTY && stack.getItem() == item) return i;
        }
        return -1;
    }

    public static BlockPos getLocalPlayerPosFloored() {
        if (mc.player == null) return new BlockPos(-1, -1, -1);
        return new BlockPos((int) Math.floor(mc.player.getX()), (int) Math.floor(mc.player.getY()), (int) Math.floor(mc.player.getZ()));
    }

    public static BlockPos entityPosToFloorBlockPos(Entity e) {
        return new BlockPos((int) Math.floor(e.getX()), (int) Math.floor(e.getY()), (int) Math.floor(e.getZ()));
    }

    public static float getHealthWithAbsorption() {
        if (mc.player == null) return -1;
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static FacingDirection getFacing() {
        if (mc.player == null) return FacingDirection.North;
        return switch (MathHelper.floor((double) (mc.player.getYaw() * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0, 1 -> FacingDirection.South;
            case 2, 3 -> FacingDirection.West;
            case 6, 7 -> FacingDirection.East;
            default -> FacingDirection.North;
        };
    }

    public static float getSpeedInKM() {
        if (mc.player == null) return 0f;
        final double deltaX = mc.player.getX() - mc.player.prevX;
        final double deltaZ = mc.player.getZ() - mc.player.prevZ;
        float distance = MathHelper.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));
        double kMH = Math.floor((distance / 1000.0f) / (0.05f / 3600.0f));
        String formatter = Formatter.format(kMH);
        if (!formatter.contains(".")) formatter += ".0";
        return Float.valueOf(formatter);
    }

    public static boolean canSeeBlock(BlockPos pos) {
        BlockHitResult hitResult = rayCastBlock(new RaycastContext(getEyesPos(mc.player), pos.toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player), pos);
        return hitResult != null && hitResult.getBlockPos() == pos;
    }

    public static Vec3d getEyesPos(Entity entity) {
        return entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
    }

    public static BlockHitResult rayCastBlock(RaycastContext context, BlockPos block) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockPos) -> {
            BlockState blockState;
            if (!blockPos.equals(block)) blockState = Blocks.AIR.getDefaultState();
            else blockState = Blocks.OBSIDIAN.getDefaultState();
            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();
            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, mc.world, blockPos);
            BlockHitResult blockHitResult = mc.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, (raycastContext) -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(raycastContext.getEnd()));
        });
    }

    public static boolean isPlayerTrapped() {
        return isEntityTrapped(mc.player);
    }

    public static boolean isEntityTrapped(Entity entity) {
        BlockPos pos = entityPosToFloorBlockPos(entity);
        List<BlockPos> trapPositions = Arrays.asList(pos.up().up(), pos.north(), pos.south(), pos.east(), pos.west(), pos.north().up(), pos.south().up(), pos.east().up(), pos.west().up());
        if (entity instanceof PlayerEntity) trapPositions.add(pos.down());
        for (BlockPos blockPos : trapPositions) {
            BlockState state = mc.world.getBlockState(blockPos);
            if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK) return false;
        }
        return true;
    }

    public static void packetFacePitchAndYaw(float yaw, float pitch) {
        float pre_Yaw = mc.player.getYaw();
        float pre_Pitch = mc.player.getPitch();
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
        rotating = true;
        ((ClientPlayerEntityAccessor)mc.player).invokeSync();
        mc.player.setYaw(pre_Yaw);
        mc.player.setPitch(pre_Pitch);
    }

    public static PlayerEntity findClosestTarget() {
        if (mc.world.getPlayers().isEmpty()) return null;
        PlayerEntity closestTarget = null;
        for (final PlayerEntity target : mc.world.getPlayers()) {
            if (target == mc.player) continue;
            if (FriendManager.Get().IsFriend(target)) continue;
            if (!EntityUtil.isLiving(target)) continue;
            if (target.getHealth() <= 0.0f) continue;
            if (closestTarget != null && mc.player.squaredDistanceTo(target) > mc.player.squaredDistanceTo(closestTarget)) continue;
            closestTarget = target;
        }
        return closestTarget;
    }
}