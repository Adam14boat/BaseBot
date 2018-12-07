package robot.subsystems.drivetrain.pure_pursuit;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author Paulo Khayat
 * @author Lior Barkai
 * <p>
 * This class is the instance of the path, holding the points.
 * The generation methods written here are all part of the Pure pursuit algorithm
 * all instances of the name 'the pure pursuit article' refer to this article by team DAWGMA 1712:
 * https://www.chiefdelphi.com/media/papers/download/5533
 */
public class Path {
    private ArrayList<Waypoint> path = new ArrayList<>();

    /**
     * Create an empty Path instance
     */
    public Path() {

    }

    /**
     * Create a Path instance
     *
     * @param array the array of points to add into the arraylist
     */
    public Path(Waypoint[] array) {
        path.addAll(Arrays.asList(array));
    }

    public Path(ArrayList<Waypoint> w) {
        path.addAll(w);
    }

    private static double[][] doubleArrayCopy(double[][] arr) {
        //size first dimension of array
        double[][] temp = new double[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            //Resize second dimension of array
            temp[i] = new double[arr[i].length];
            //Copy Contents
            if (arr[i].length >= 0) System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
        }
        return temp;
    }

    /**
     * Set a point at an index.
     *
     * @param index index of the desired point starting at zero, use -1 for last Point.
     */
    private void setWaypoint(int index, Waypoint p) {
        if (!(index <= path.size() && index > -path.size()))
            throw new ArrayIndexOutOfBoundsException("Waypoint index " + index + " is out of bounds.");
        if (index == path.size()) //if set is just out of bounds the method appends the waypoint instead.
            this.appendWaypoint(p);
        else
            path.set(Math.floorMod(index, path.size()), p);
    }

    /**
     * Add a point to the path.
     *
     * @param index the index of the point to append.
     * @param p     the waypoint to add.
     */
    public void addWaypoint(int index, Waypoint p) {
        if (!(index <= path.size() && index > -path.size()))
            throw new ArrayIndexOutOfBoundsException("Waypoint index " + index + " is out of bounds.");
        if (path.get(Math.floorMod(index, path.size())) == null)
            throw new ClassCastException("Tried to call a non Point object from the path list.");
        if (index == path.size()) //if set is just out of bounds the method appends the waypoint instead.
            this.appendWaypoint(p);
        else
            path.add(Math.floorMod(index, path.size()), p);
    }

    /**
     * Append an object to the end of the list.
     *
     * @param p the waypoint to add.
     */
    public void appendWaypoint(Waypoint p) {
        path.add(p);
    }

    /**
     * Return a new subPath of the path.
     *
     * @param start  first index of the path.
     * @param length last index (subtracted by one).
     * @return returns a new Path class which holds the specified sublist of waypoints.
     */
    public Path getSubPath(int start, int length) {
        return new Path(new ArrayList<>(path.subList(start, length)));
    }

    /**
     * Return a Waypoint at a specific index.
     *
     * @param index index of the desired point starting at zero, use -1 for last Point.
     * @return returns the Point.
     */
    public Waypoint getWaypoint(int index) {
        if (!(index < path.size() && index > -path.size()))
            throw new ArrayIndexOutOfBoundsException("Waypoint index " + index + " is out of bounds.");
        return path.get(Math.floorMod(index, path.size()));
    }

    /**
     * Add all of a Point array to the end of the path list.
     * The equivalent of 'addAll(-1, array)'
     *
     * @param array the array of points to add to the end of the array.
     */
    public void addAll(Waypoint[] array) {
        path.addAll(Arrays.asList(array));
    }

    /**
     * Append all of a Point array at a certain index.
     * Adds the first Point at the specified index.
     * all Points at an index greater than the specified index get moved to after the array.
     *
     * @param index the index to add the Point array (0 to place array at start)
     * @param array the array of points to add.
     */
    public void addAll(int index, Waypoint[] array) {
        if (!(index < path.size() && index > -path.size()))
            throw new ArrayIndexOutOfBoundsException("Waypoint index " + index + " is out of bounds.");
        path.addAll(Math.floorMod(index, path.size()), Arrays.asList(array));
    }

    /**
     * Adds all of a Point array to the end of the path list.
     * The equivalent of 'addAll(-1, array)'
     *
     * @param path the path class of points to add to the end of the array.
     */
    public void addAll(Path path) {
        addAll(path.toArray());
    }

    /**
     * Appends all of a Point array at a certain index.
     * Adds the first Point at the specified index.
     * all Points at an index greater than the specified index get moved to after the array.
     *
     * @param index the index to add the Point array (0 to place array at start)
     * @param path  the path class of points to add.
     */
    public void addAll(int index, Path path) {
        addAll(index, path.toArray());
    }

    /**
     * Clears the path
     */
    public void clear() {
        path.clear();
    }

    /**
     * /**
     * Return the size of the path.
     *
     * @return returns the size() of the array.
     */
    public int length() {
        return path.size();
    }

    /**
     * Create a new instance of the path.
     *
     * @return
     */
    private Path copy() {
        return new Path(path);
    }

    // ----== Functions for path generation and optimisation: ==----

    /**
     * Convert the path ArrayList to an array.
     *
     * @return returns a Waypoint[] array.
     */
    public Waypoint[] toArray() {
        return path.toArray(new Waypoint[] {});
    }

    /**
     * Run all generate methods at once.
     *
     * @param weight_data        generateSmoothing parameter. See documentation of the generateSmoothing method for more information.
     * @param weight_smooth      generateSmoothing parameter. See documentation of the generateSmoothing method for more information.
     * @param tolerance          generateSmoothing parameter. See documentation of the generateSmoothing method for more information.
     * @param const_acceleration generateVelocities parameter. See documentation of the generateVelocities method for more information.
     */
    public void generateAll(double weight_data, double weight_smooth, double tolerance, double const_acceleration, double max_path_velocity) {
        this.generateFillPoint();
        this.generateSmoothing(weight_data,weight_smooth,tolerance);
        this.generateCurvature();
        this.generateDistance();
        this.generateVelocity(const_acceleration, max_path_velocity);
    }

    /**
     * Add points at a certain spacing between them into all the segments.
     * <p>
     * The first of the five methods used in the path generation, needed for the pure pursuit.
     * (Pure pursuit article, 'Path Generation' > 'Injecting points' , Page 5)
     */
    public void generateFillPoint() {
        Vector[] pathVectors = new Vector[path.size()]; //create an array of vectors per point.
        Path newPathClass = new Path(); //create a new path class
        int AmountOfPoints;
        for (int i = 0; i < pathVectors.length - 1; i++) {//for every point on the path
            //Creates a vector with the slope of the two points we are checking
            //Sets the vectors magnitude to the constant of the spacing
            //calculates the amount of fill-points that can fit between the two points.
            pathVectors[i] = new Vector(this.getWaypoint(i), this.getWaypoint(i + 1));
            AmountOfPoints = (int) Math.ceil(pathVectors[i].magnitude() / Constants.SPACING_BETWEEN_WAYPOINTS);
            pathVectors[i] = pathVectors[i].normalize().multiply(Constants.SPACING_BETWEEN_WAYPOINTS);
            for (int j = 0; j < AmountOfPoints; j++) {
                newPathClass.appendWaypoint(pathVectors[i].multiply(j).add(this.getWaypoint(i)));
            }
        }
        clear();
        addAll(newPathClass);


    }

    /**
     * Smooth the points in the path class.
     * <p>
     * The second of the five methods used in the path generation, needed for the pure pursuit.
     * (Pure pursuit article, 'Path Generation' > 'Smoothing' , Page 5)
     *
     * @param weight_data   smooth constant. controls the proportional distance from the original point.
     * @param weight_smooth smooth constant. controls the proportional distance from the midpoint.
     * @param tolerance     the minimum change between points.
     * @return the new path with the way points.
     * @author Paulo
     * @author Lior
     */
    public void generateSmoothing(double weight_data, double weight_smooth, double tolerance) {
        /*
         * For simplification and parallelism with the article with the pure pursuit article, we converted the Waypoint
         * path into a matrix of doubles. the first dimension represents the index of the waypoint, the second dimension
         * the first column (column [0]) holding the x values and the second column (column [1]) holding the Y values.
         *
         * this smoothing algorithm takes each point on the path, tries to move it towards the midpoint of the next and
         * previous point, proportionally to the distance from the midpoint, minus the amount it already moved until now.
         * the program ends when the amount of change passes the tolerance.
         */
        Path newPathClass = this.copy();
        double[][] newPath = new double[this.length()][2];
        for (int i = 0; i < this.length(); i++) { //copying the path to an array.
            newPath[i][0] = this.getWaypoint(i).getX();
            newPath[i][1] = this.getWaypoint(i).getY();

        }
        double[][] path = doubleArrayCopy(newPath);
        double change = tolerance;
        while (change >= tolerance) {
            change = 0.0;
            for (int i = 1; i < path.length - 1; i++)
                for (int j = 0; j < path[i].length; j++) {
                    double aux = newPath[i][j];
                    newPath[i][j] += weight_data * (path[i][j] - newPath[i][j]) + weight_smooth *
                            (newPath[i - 1][j] + newPath[i + 1][j] - (2.0 * newPath[i][j]));
                    change += Math.abs(aux - newPath[i][j]);
                }
        }
        for (int i = 0; i < this.length(); i++) {
            Waypoint p = newPathClass.getWaypoint(i);
            p.setX(newPath[i][0]);
            p.setY(newPath[i][1]);
            newPathClass.setWaypoint(i, p);
        }
        this.clear();
        this.addAll(newPathClass);
    }

    /**
     * Attribute to all points their distance from the start.
     * sets the distance of each point from the start and saves as a parameter, so that it doesnt have to be calculated real time.
     * <p>
     * The third of the five methods used in the path generation, needed for the pure pursuit.
     * (see the Pure pursuit article, 'Path Generation' > 'Distances Between Points' , Page 6)
     */
    public void generateDistance() {
        this.recursiveDistance(this.length() - 1); //uses a recursive method to attribute each distance from the start.
    }

    /**
     * Return the size of the largest length in the list.
     *
     * @param i index of current point
     * @return returns sum of all distances before this point.
     */
    private double recursiveDistance(int i) {
        if (i == 0)
            return 0;
        double d = recursiveDistance(i - 1) + Point.distance(path.get(i), path.get(i - 1));
        Waypoint p = path.get(i);
        p.setDistance(d);
        path.set(i, p);
        return d;
    }

    /**
     * Attribute to all points their curvature in correlation to their adjacent points.
     * sets the curvature of each point and saves as a parameter, so that it doesnt have to be calculated real time.
     * <p>
     * The fourth of the five methods used in the path generation, needed for the pure pursuit.
     * (see the Pure pursuit article, 'Path Generation' > 'Curvature of Path' , Page 6)
     */
    public void generateCurvature() {

        for (int i = 1; i < path.size() - 1; i++) {
            Waypoint prev = path.get(i - 1); //Waypoint
            Waypoint curr = path.get(i);
            Waypoint next = path.get(i + 1);
            double area = (curr.getX() - prev.getX()) * (next.getY() - prev.getY()) -
                          (curr.getY() - prev.getY()) * (next.getX() - prev.getX());
            double curvature = 2 * area / (Waypoint.distance(prev, curr) * Waypoint.distance(prev, next) * Waypoint.distance(next, curr));
            path.get(i).setCurvature(curvature);
        }
    }


    /**
     * Each point on the path will have a target velocity the robot tries to reach.
     * The robot uses the target velocity of the point closest to it when calculating the target left and right wheel speeds.
     * When calculating the target velocity for a point we take into account the curvature at the point so the robot slows down around sharp turns.
     * (For this reason you must run the methods in order).
     * <p>
     * The last of the five methods used in the path generation, needed for the pure pursuit.
     * (see the Pure pursuit article, 'Path Generation' > 'Velocities' , Page 8)
     *
     * @param maxAcceleration rhe acceleration constant
     * @author paulo
     */
    public void generateVelocity(double maxAcceleration, double pathMaximumVelocity) {
        //Each point is given a speed based on its curvature, and the maximum velocity allowed.
        for (int i = 0; i < this.length(); i++){
            if(this.getWaypoint(i).getCurvature() != 0) //prevent zero division error
                this.getWaypoint(i).setSpeed(Math.min(pathMaximumVelocity, Constants.K_CURVE / this.getWaypoint(i).getCurvature()));
            else
                this.getWaypoint(i).setSpeed(pathMaximumVelocity);
        }
        this.getWaypoint(-1).setSpeed(0);

        //Goes in reverse from the end to the beggining, lowering the speeds so that the robot doesn't de accelerate as fast.
        for (int i = this.length() - 2; i >= 0; i--) {
            getWaypoint(i).setSpeed(
                    Math.min( getWaypoint(i).getSpeed(), Math.sqrt(
                            Math.pow(getWaypoint(i+1).getSpeed(),2) + 2 * maxAcceleration * Waypoint.distance(getWaypoint(i), getWaypoint(i+1))
                            )
                    )
            );
        }
    }

    @Override
    public String toString() {
        return "Path{" + "path=" + path + '}';
    }
}
