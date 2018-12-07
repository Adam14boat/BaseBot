package robot.subsystems.drivetrain.pure_pursuit;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static robot.Robot.drivetrain;

/**
 * The methods written here are all part of the Pure pursuit algorithm
 * all instances of the name 'the pure pursuit article' refer to this article by team DAWGMA 1712:
 * https://www.chiefdelphi.com/media/papers/download/5533
 */
public class PurePursue extends Command {
    private Path path; //Command specific path to follow
    private Point currentPoint = new Point(0, 0); //holds X and Y variables for the robot
    private Point currentLookahead; //holds X and Y variables for the Lookahead point
    private int direction; //whether the robot drives forward or backwards (-1 or 1)
    private double lastLeftSpeed; //the last speed of the left encoder
    private double lastRightSpeed; //the last speed of the right encoder
    private double lastLeftEncoder; //the last distance of the left encoder
    private double lastRightEncoder; //the last distance of the right encoder
    private double lastLookaheadDistance; //distance of the last lookahead from the start of the path
    private double kP, kA, kV;
    private double lookaheadRadius;

    /**
     * An implementation of these command class. for more information see documentation on the wpilib command class.
     *
     * @param path            the Path class that the robot is going to follow
     * @param isReversed      states if the robot should drivetrain forward or backwards along the path.
     * @param lookaheadRadius constant. the distance of the setpoint the robot tries to follow.
     * @param kP              driving constant. Multiplied by the difference between left and right speeds.
     * @param kA              driving constant. Multiplied by the robots acceleration.
     * @param kV              driving constant. Multiplied by the target velocity of the nearest point.
     */
    public PurePursue(Path path, boolean isReversed, double lookaheadRadius, double kP, double kA, double kV) {
        requires(drivetrain);
        this.lookaheadRadius = lookaheadRadius;
        this.kP = kP;
        this.kA = kA;
        this.kV = kV;
        direction = isReversed ? -1 : 1;
        this.path = path;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        currentPoint = new Waypoint(drivetrain.currentLocation.getX(), drivetrain.currentLocation.getY());
        System.out.println(currentPoint);
        lastLeftEncoder = drivetrain.getLeftDistance();
        lastRightEncoder = drivetrain.getRightDistance();
        //initAngle = drivetrain.getAngle() + (direction == -1 ? 180 : 0);
        currentLookahead = path.getWaypoint(0);
        lastLeftSpeed = direction * drivetrain.getLeftSpeed();
        lastRightSpeed = direction * drivetrain.getRightSpeed();
        drivetrain.resetLocation();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        updatePoint();
        updateLookaheadInPath(path);
        System.out.println(getLeftSpeedVoltage(path) - getRightSpeedVoltage(path));
        SmartDashboard.putNumber("lastlookaheaddistance", lastLookaheadDistance);

        SmartDashboard.putNumber("voltagesent sent left", getLeftSpeedVoltage(path));
        SmartDashboard.putNumber("voltage sent right", getRightSpeedVoltage(path));
        SmartDashboard.putNumber("curvature calculate", curvatureCalculate());
        SmartDashboard.putString("current lookahead point", this.currentLookahead.getX() + " " + this.currentLookahead.getY());

        drivetrain.setSpeed(getLeftSpeedVoltage(path), getRightSpeedVoltage(path));

    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
//        return false;
        boolean closeToLast = (drivetrain.currentLocation.getX() >= path.getWaypoint(path.length() - 1).getX() - 0.1 &&
                drivetrain.currentLocation.getY() >= path.getWaypoint(path.length() - 1).getY() - 0.1);
        return (closeToLast &&
                drivetrain.getLeftSpeed() < Constants.STOP_SPEED_THRESH &&
                drivetrain.getRightSpeed() < Constants.STOP_SPEED_THRESH);
    }

    // Called once after isFinished returns true
    protected void end() {
        drivetrain.setSpeed(0, 0);
        SmartDashboard.putString("pursue command", "end");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }

    /**
     * @author Paulo
     * Updates the Point object holding the robots x and y cooridinates
     */
    private void updatePoint() {
        //change in (change left encoder value + change in right encoder value)/2
        double distance = ((drivetrain.getLeftDistance() - lastLeftEncoder) + (drivetrain.getRightDistance() - lastRightEncoder)) / 2;

        //update the x, y coordinates based on the robot angle and the distance the robot moved.
        currentPoint.setX(currentPoint.getX() + direction * distance * Math.sin(drivetrain.getAngle() * (Math.PI / 180.0)));
        currentPoint.setY(currentPoint.getY() + direction * distance * Math.cos(drivetrain.getAngle() * (Math.PI / 180.0)));

        //updates values for next run
        lastLeftEncoder = drivetrain.getLeftDistance();
        lastRightEncoder = drivetrain.getRightDistance();
        drivetrain.currentLocation.setX(currentPoint.getX());
        drivetrain.currentLocation.setY(currentPoint.getY());
    }


    //Only Part of the function! doesn't run function through all of the path
    //https://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm/1084899#1084899

    /**
     * This method finds the furthest point in a segment that is in a specified distance from the robot.
     * (Pure pursuit article, 'Following the path' > 'lookahead point', Page 10)
     *
     * @param ref       Center point of the robot
     * @param lookahead lookahead distance (units of measurements are the same as those stored in the points)
     * @param point1    start of a segment
     * @param point2    end of a segment
     * @return returns the furthest point
     * @author Lior barkai
     * @author Paulo
     */
    private Waypoint findNearPath(Point ref, double lookahead, Waypoint point1, Waypoint point2) {
        Vector p = new Vector(point1, point2);
        Vector f = new Vector(ref, point1);

        /* using the equation: |t*p + f| = r, we try to find the value(s) of 't' where the length of the vector from the
         * robot center to the point is equal to the radius 'r'.
         * if you square both sides we get a quadratic formula with a, b, c. we use the quadratic formula and check for
         * the intersections, and then check if the intersection is within the segment (if t is between zero and one.)
         * the final method is: (p^2)*t^2 + (2*p*f)*t + (f^2 - r^2) = 0
         */

        double a = p.dot(p);
        double b = 2 * f.dot(p);
        double c = f.dot(f) - lookahead * lookahead;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0)
            return null; //means that the circle doesn't reach the line
        else {
            discriminant = Math.sqrt(discriminant);
            double opt1 = (-b - discriminant) / (2 * a); //solve format of a quardatic formula
            double opt2 = (-b + discriminant) / (2 * a);
            SmartDashboard.putNumber("opt", opt1);
            SmartDashboard.putString("opt point", p.multiply(opt1).add(point1).toString());
            if (opt1 >= 0 && opt1 <= 1) {
                return p.multiply(opt1).add(point1);
            }
            if (opt2 >= 0 && opt2 <= 1)
                return p.multiply(opt2).add(point1);
        }
        return null;
    }

    /**
     * Uses the 'FindNearPath' method on all segments to find the closest point.
     * Checks for the next intersection thats index is higher than the current lookahead point.
     *
     * @return the Lookahead Point.
     * @path the path the robot is driving on.
     */
    private void updateLookaheadInPath(Path path) {
        for (int i = 0; i < path.length() - 1; i++) {
            Waypoint wp = findNearPath(currentPoint, lookaheadRadius, path.getWaypoint(i), path.getWaypoint(i + 1));
            if (wp != null) {
                if (Point.distance(wp, path.getWaypoint(i)) + path.getWaypoint(i).getDistance() > lastLookaheadDistance) {
                    lastLookaheadDistance = Point.distance(wp, path.getWaypoint(i)) + path.getWaypoint(i).getDistance();
                    currentLookahead = wp;
                    SmartDashboard.putNumber("testnumber", i);
                    SmartDashboard.putString("currentlookahead again", wp.getX() + " " + wp.getY());
                    return;
                }
            }
        }
    }

    /**
     * Runs through a specified path and finds the closest waypoint to the robot.
     *
     * @param path the path that this method work on
     * @return the closest point to the robots position
     * @author orel
     */
    private Waypoint closestPoint(Path path) {
        Waypoint closest = path.getWaypoint(0).copy();
        for (int i = 1; i < path.length(); i++) {

            if (Point.distance(this.currentPoint, path.getWaypoint(i)) < Point.distance(this.currentPoint, closest)) {
                closest = path.getWaypoint(i);
            }
        }
        return closest;
    }

    /**
     * Calculates the curvature between the tangent of the robot and its lookahead point.
     *
     * @return The curvature from the tangent of the robot to the setpoint
     * @author orel
     * @author Paulo
     */
    private double curvatureCalculate() {
        double x = distanceLookahead();

        double L = Point.distance(currentPoint, currentLookahead);
        double radius = Math.pow(L, 2);
        SmartDashboard.putNumber("x distance", x);

//        return 2 * (Math.cos(drivetrain.getAngle()+Math.toDegrees(Math.atan2(currentLookahead.getY() - currentPoint.getY(), currentLookahead.getX() - currentPoint.getY())) - 90)) / Point.distance(currentLookahead, currentPoint);

        if (radius == 0) {
            return Math.pow(10, 6);
        } else {
            return 2 * x / radius;
        }
    }

    /**
     * Calculates how far the robots tangent is from the lookahead point.
     * This method is used to check whether the robot is left or right of the point aswell.
     *
     * @return The X axis distance (relative to robot) from the lookahead, right side being positive.
     * @author orel
     * @author Paulo
     * @author Lior
     */
    private double distanceLookahead() {
        double a = -Math.tan(Math.toRadians(drivetrain.getAngle()+90));
        double b = 1;
        double c = Math.tan(Math.toRadians(drivetrain.getAngle()+90)) * drivetrain.currentLocation.getX() - this.currentLookahead.getY();
        double x = Math.abs(a * this.currentLookahead.getX() + b * this.currentLookahead.getY() + c) / Math.hypot(a, b);
        double side = Math.signum(Math.sin(Math.toRadians(drivetrain.getAngle()+90)) * (this.currentLookahead.getX() - drivetrain.currentLocation.getX()
        ) - Math.cos(Math.toRadians(drivetrain.getAngle()+90)) * (this.currentLookahead.getY() - drivetrain.currentLocation.getY()));
        SmartDashboard.putString("a, c", a + " " + c);
        SmartDashboard.putNumber("current x value", x);
        SmartDashboard.putNumber("current side value ", side);
        return x * side;
    }


    /**
     * calculates the speed needed in the right wheel and makes so we can apply it straight to the right engine
     *
     * @param path current path
     * @return applied voltage to right engine
     * @author lior
     */
    public double getRightSpeedVoltage(Path path) {
        double target_accel = (drivetrain.getRightSpeed() - lastRightSpeed) / 0.02;
        lastRightSpeed = drivetrain.getRightSpeed();
        return kV * (closestPoint(path).getSpeed() * (2 - curvatureCalculate() * Constants.ROBOT_WIDTH) / 2) +
                kA * (target_accel) +
                kP * (closestPoint(path).getSpeed() - drivetrain.getRightSpeed());

    }

    /**
     * calculates the speed needed in the left wheel and makes so we can apply it straight to the left engine
     *
     * @param path current path
     * @return applied voltage to left engine
     * @author lior
     */
    public double getLeftSpeedVoltage(Path path) {
        double target_accel = (drivetrain.getLeftSpeed() - lastLeftSpeed) / 0.02;
        lastLeftSpeed = drivetrain.getLeftSpeed();
        SmartDashboard.putNumber("current closest point speed", closestPoint(path).getSpeed());
        SmartDashboard.putNumber("current curvature calculate", curvatureCalculate());
        return kV * (closestPoint(path).getSpeed() * (2 + curvatureCalculate() * Constants.ROBOT_WIDTH) / 2) +
                kA * (target_accel) +
                kP * (closestPoint(path).getSpeed() - drivetrain.getLeftSpeed());
    }

}