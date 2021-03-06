public class Particle {
    private int id;
    private double ratio;
    private Property<String> property;
    private Point location;
    private double velocity;
    private double angle;

    public Particle(int id,double ratio, Property<String> property, Point location,  double velocity, double angle) {
        this.id = id;
        this.ratio = ratio;
        this.property = property;
        this.location = location;
        this.velocity = velocity;
        this.angle = angle;
    }

    public Particle(Point location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public double getRatio() {
        return ratio;
    }

    public Point getLocation() {
        return location;
    }

    public static double borderDistanceBetweenParticles(Particle m1, Particle m2){
        return Point.distanceBetween(m1.location,m2.location) - m1.ratio - m2.ratio;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", ratio=" + ratio +
                ", property=" + property +
                ", location=" + location +
                ", velocity=" + velocity +
                '}';
    }

    public double calculateVx() {
        return Math.cos(angle) * velocity;
    }

    public double calculateVy() {
        return Math.sin(angle) * velocity;
    }
}
