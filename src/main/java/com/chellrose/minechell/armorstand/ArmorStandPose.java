package com.chellrose.minechell.armorstand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

/**
 * Stores the euler angles used for an Armor Stand pose.
 */
public class ArmorStandPose {
    private static final EulerAngle ZERO_ANGLE = new EulerAngle(0f, 0f, 0f);

    public static final ArmorStandPose STRAIGHT_POSE = new ArmorStandPose(
        new EulerAngle(1.0, 0.0, 0.0f),
        new EulerAngle(1.0, 0.0, 0.0f),
        new EulerAngle(1.0, 0.0, 0.0f),
        ZERO_ANGLE,
        ZERO_ANGLE
    );

    public static final ArmorStandPose LOOKHANDS_POSE = new ArmorStandPose(
        new EulerAngle(19.0, 0.0, 0.0f),
        new EulerAngle(298.0, 29.0, 0.0f),
        new EulerAngle(308.0, 328.0, 0.0f),
        ZERO_ANGLE,
        ZERO_ANGLE
    );

    public static final ArmorStandPose LOOKATTHIS_POSE = new ArmorStandPose(
        new EulerAngle(348.0, 0.0, 0.0f),
        new EulerAngle(16.0, 12.0, 0.0f),
        new EulerAngle(325.0, 29.0, 0.0f),
        ZERO_ANGLE,
        ZERO_ANGLE
    );

    public static final ArmorStandPose WAIT_POSE = new ArmorStandPose(
        ZERO_ANGLE,
        new EulerAngle(29.0, 0.0, 0.0f),
        new EulerAngle(231.0, 0.0, 0.0f),
        ZERO_ANGLE,
        ZERO_ANGLE
    );

    public static final ArmorStandPose PRAY_POSE = new ArmorStandPose(
        new EulerAngle(328.0, 0.0, 0.0f),
        new EulerAngle(221.0, 0.0, 322.0f),
        new EulerAngle(221.0, 0.0, 39.0f),
        new EulerAngle(2.0, 0.0, 0.0f),
        ZERO_ANGLE
    );

    public static final ArmorStandPose HUG_POSE = new ArmorStandPose(
        ZERO_ANGLE,
        new EulerAngle(305.0, 0.0, 234.0f),
        new EulerAngle(234.0, 0.0, 305.0f),
        ZERO_ANGLE,
        ZERO_ANGLE
    );

    public static final ArmorStandPose DAB_POSE = new ArmorStandPose(
        new EulerAngle(32.0, 0.0, 0.0f),
        new EulerAngle(248.0, 312.0, 0.0f),
        new EulerAngle(254.0, 325.0, 0.0f),
        new EulerAngle(0.0, 32.0, 0.0f),
        ZERO_ANGLE
    );

    public static final ArmorStandPose BRUH_POSE = new ArmorStandPose(
        ZERO_ANGLE,
        new EulerAngle(254.0, 0.0, 0.0f),
        new EulerAngle(248.0, 0.0, 0.0f),
        ZERO_ANGLE,
        new EulerAngle(318.0, 0.0, 0.0f)
    );

    public static final ArmorStandPose DANCE1_POSE = new ArmorStandPose(
        ZERO_ANGLE,
        new EulerAngle(0.0, 0.0, 238.0f),
        new EulerAngle(0.0, 0.0, 120.0f),
        new EulerAngle(355.0, 42.0, 0.0f),
        new EulerAngle(254.0, 73.0, 0.0f)
    );

    public static final ArmorStandPose DANCE2_POSE = new ArmorStandPose(
        ZERO_ANGLE,
        new EulerAngle(0.0, 0.0, 238.0f),
        new EulerAngle(0.0, 0.0, 120.0f),
        new EulerAngle(268.0, 318.0, 0.0f),
        new EulerAngle(2.0, 90.0, 0.0f)
    );

    public static final ArmorStandPose PROPOSE_POSE = new ArmorStandPose(
        new EulerAngle(0.0, 66.0, 0.0f),
        new EulerAngle(349.0, 0.0, 0.0f),
        new EulerAngle(187.0, 0.0, 288.0f),
        new EulerAngle(2.0, 42.0, 0.0f),
        new EulerAngle(0.0, 32.0, 0.0f)
    );

    private static final ArmorStandPose[] POSES = {
        STRAIGHT_POSE,
        LOOKHANDS_POSE,
        LOOKATTHIS_POSE,
        WAIT_POSE,
        PRAY_POSE,
        HUG_POSE,
        DAB_POSE,
        BRUH_POSE,
        DANCE1_POSE,
        DANCE2_POSE,
        PROPOSE_POSE
    };

    private final EulerAngle headAngles;
    private final EulerAngle leftArmAngles;
    private final EulerAngle rightArmAngles;
    private final EulerAngle leftLegAngles;
    private final EulerAngle rightLegAngles;

    // Accepts Euler angles for all posable parts of an Armor Stand in degrees
    public ArmorStandPose(EulerAngle headAngles, EulerAngle leftArmAngles, EulerAngle rightArmAngles, EulerAngle leftLegAngles, EulerAngle rightLegAngles) {
        this.headAngles = new EulerAngle(Math.toRadians(headAngles.getX()), Math.toRadians(headAngles.getY()), Math.toRadians(headAngles.getZ()));
        this.leftArmAngles = new EulerAngle(Math.toRadians(leftArmAngles.getX()), Math.toRadians(leftArmAngles.getY()), Math.toRadians(leftArmAngles.getZ()));
        this.rightArmAngles = new EulerAngle(Math.toRadians(rightArmAngles.getX()), Math.toRadians(rightArmAngles.getY()), Math.toRadians(rightArmAngles.getZ()));
        this.leftLegAngles = new EulerAngle(Math.toRadians(leftLegAngles.getX()), Math.toRadians(leftLegAngles.getY()), Math.toRadians(leftLegAngles.getZ()));
        this.rightLegAngles = new EulerAngle(Math.toRadians(rightLegAngles.getX()), Math.toRadians(rightLegAngles.getY()), Math.toRadians(rightLegAngles.getZ()));
    }

    // Construct ArmorStandPose based on an existing ArmorStand
    public ArmorStandPose(ArmorStand armorStand) {
        this.headAngles = armorStand.getHeadPose();
        this.leftArmAngles = armorStand.getLeftArmPose();
        this.rightArmAngles = armorStand.getRightArmPose();
        this.leftLegAngles = armorStand.getLeftLegPose();
        this.rightLegAngles = armorStand.getRightLegPose();
    }

    /**
     * Applies this pose to the given ArmorStand.
     *
     * @param armorStand the ArmorStand to apply this pose to
     */
    public void apply(ArmorStand armorStand) {
        armorStand.setHeadPose(headAngles);
        armorStand.setLeftArmPose(leftArmAngles);
        armorStand.setRightArmPose(rightArmAngles);
        armorStand.setLeftLegPose(leftLegAngles);
        armorStand.setRightLegPose(rightLegAngles);
    }

    public boolean equals(ArmorStandPose other) {
        return this.headAngles.equals(other.headAngles) &&
               this.leftArmAngles.equals(other.leftArmAngles) &&
               this.rightArmAngles.equals(other.rightArmAngles) &&
               this.leftLegAngles.equals(other.leftLegAngles) &&
               this.rightLegAngles.equals(other.rightLegAngles);
    }

    /**
     * Returns the index of the current pose in the list of available poses.
     *
     * @return The index of the current pose, or -1 if the pose is not found.
     */
    public int index() {
        for (int i = 0; i < POSES.length; i++) {
            if (this.equals(POSES[i])) {
                return i;
            }
        }
        return -1;
    }

    public static ArmorStandPose fromIndex(int index) {
        return POSES[Math.floorMod(index, POSES.length)];
    }
}
