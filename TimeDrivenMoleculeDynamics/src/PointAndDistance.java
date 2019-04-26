/**
 * Created by estebankramer on 26/04/2019.
 */
public class PointAndDistance {
    private Point point;
    private double distance;

    public PointAndDistance(Point point, double distance) {
        this.point = point;
        this.distance = distance;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
