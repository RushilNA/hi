package frc.robot.utils;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class stores predefined Pose2d positions for both blue and red alliances. It provides a
 * method to return the closest pose from the appropriate list based on the current robot pose and
 * the alliance reported by the DriverStation.
 */
public class SidePoseMatcher {

  // Hard-coded list of poses for the blue alliance.
  private static final List<Pose2d> bluePoses =
      Arrays.asList(
          new Pose2d(new Translation2d(3.97, 2.78), new Rotation2d(Math.toRadians(56.79))),
          new Pose2d(new Translation2d(3.69, 2.95), new Rotation2d(Math.toRadians(60.54))),
          // 17
          new Pose2d(new Translation2d(5.02, 2.8), new Rotation2d(Math.toRadians(121.84))),
          new Pose2d(new Translation2d(5.34, 2.98), new Rotation2d(Math.toRadians(119.11))),
          // 22
          new Pose2d(new Translation2d(5.82, 3.87), new Rotation2d(Math.toRadians(-178.91))),
          new Pose2d(new Translation2d(5.83, 4.24), new Rotation2d(Math.toRadians(177.52))),
          // 21
          new Pose2d(new Translation2d(5.3, 5.09), new Rotation2d(Math.toRadians(-118.12))),
          new Pose2d(new Translation2d(4.99, 5.28), new Rotation2d(Math.toRadians(-121.69))),
          // 20
          new Pose2d(new Translation2d(3.93, 5.23), new Rotation2d(Math.toRadians(-57.35))),
          new Pose2d(new Translation2d(3.63, 5.07), new Rotation2d(Math.toRadians(-61.68))),
          // 19
          new Pose2d(new Translation2d(3.16, 4.15), new Rotation2d(Math.toRadians(2.04))),
          new Pose2d(new Translation2d(3.15, 3.8), new Rotation2d(Math.toRadians(0.42)))
          // 18
          );
  // Hard-coded list of poses for the red alliance.
  private static final List<Pose2d> redPoses =
      Arrays.asList(
          new Pose2d(new Translation2d(12.28, 2.95), new Rotation2d(Math.toRadians(62.05))),
          new Pose2d(new Translation2d(12.56, 2.83), new Rotation2d(Math.toRadians(61.31))),
          // 11
          new Pose2d(new Translation2d(11.73, 3.83), new Rotation2d(Math.toRadians(-1.03))),
          new Pose2d(new Translation2d(11.73, 4.15), new Rotation2d(Math.toRadians(3.55))),
          // 8
          new Pose2d(new Translation2d(12.24, 5.09), new Rotation2d(Math.toRadians(-60.93))),
          new Pose2d(new Translation2d(12.55, 5.27), new Rotation2d(Math.toRadians(-59.06))),
          // 6
          new Pose2d(new Translation2d(13.56, 5.27), new Rotation2d(Math.toRadians(-120.87))),
          new Pose2d(new Translation2d(13.88, 5.09), new Rotation2d(Math.toRadians(-116.9))),
          new Pose2d(new Translation2d(14.39, 4.21), new Rotation2d(Math.toRadians(178.93))),
          new Pose2d(new Translation2d(14.39, 3.88), new Rotation2d(Math.toRadians(-177.46))),
          new Pose2d(new Translation2d(13.89, 2.97), new Rotation2d(Math.toRadians(118.9))),
          new Pose2d(new Translation2d(13.6, 2.81), new Rotation2d(Math.toRadians(121.77))));

  // 7
  /**
   * Iterates over a list of Pose2d and returns the one closest to the currentPose.
   *
   * @param poses The list of Pose2d objects.
   * @param currentPose The current robot pose.
   * @return The closest Pose2d from the list.
   */
  private static Pose2d findClosestInList(List<Pose2d> poses, Pose2d currentPose) {
    Pose2d closestPose = null;
    double minDistance = Double.POSITIVE_INFINITY;
    for (Pose2d pose : poses) {
      double distance = currentPose.getTranslation().getDistance(pose.getTranslation());
      if (distance < minDistance) {
        minDistance = distance;
        closestPose = pose;
      }
    }
    return closestPose;
  }

  /**
   * Returns the closest pose to the currentPose from the list corresponding to the alliance. The
   * alliance is retrieved automatically using DriverStation.getAlliance().
   *
   * @param currentPose The current robot pose.
   * @return The closest Pose2d from the appropriate alliance list, or null if the list is empty.
   */
  private static double squaredDistance(Pose2d a, Pose2d b) {
    double dx = a.getTranslation().getX() - b.getTranslation().getX();
    double dy = a.getTranslation().getY() - b.getTranslation().getY();
    return dx * dx + dy * dy;
  }

  public static Pose2d getClosestPose(Pose2d currentPose) {
    DriverStation.Alliance alliance = DriverStation.getAlliance().get();
    List<Pose2d> selectedPoses =
        (alliance == DriverStation.Alliance.Blue)
            ? bluePoses
            : (alliance == DriverStation.Alliance.Red ? redPoses : bluePoses);

    return selectedPoses.stream()
        .min(Comparator.comparingDouble(pose -> squaredDistance(currentPose, pose)))
        .orElse(null);
  }

  /**
   * Returns a Pose2d that is 2 meters behind the closest predefined pose based on the current robot
   * pose and alliance.
   *
   * @param currentPose The current robot pose
   * @return A new Pose2d 2 meters behind the closest predefined pose
   */
  public static Pose2d getBackedUpClosestPose(Pose2d currentPose) {
    Pose2d closestPose = getClosestPose(currentPose);
    if (closestPose == null) {
      return null; // Safety check
    }
    return moveBackward2Meters(closestPose);
  }

  /**
   * Returns a new Pose2d that is 2 meters backward from the given pose, in the opposite direction
   * of its current rotation.
   *
   * @param pose The original pose
   * @return A new Pose2d 2 meters behind the original
   */
  public static Pose2d moveBackward2Meters(Pose2d pose) {
    double distance = -0.7; // Negative for backward movement

    Rotation2d rotation = pose.getRotation();

    Translation2d backwardOffset = new Translation2d(distance, rotation);
    Translation2d newTranslation = pose.getTranslation().plus(backwardOffset);

    return new Pose2d(newTranslation, rotation);
  }
}
