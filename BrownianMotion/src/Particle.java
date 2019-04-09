import java.util.Objects;

public class Particle {
    private int id;
    private double vx;
    private double vy;
    private Point position;
    private double mass;
    private double radius;

    private int amountOfCrashes;

    public Particle(int id, double vx, double vy, Point position, double mass, double radius) {
        this.id = id;
        this.vx = vx;
        this.vy = vy;
        this.position = position;
        this.mass = mass;
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getAmountOfCrashes() {
        return amountOfCrashes;
    }

    public void setAmountOfCrashes(int amountOfCrashes) {
        this.amountOfCrashes = amountOfCrashes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return Double.compare(particle.getVx(), getVx()) == 0 &&
                Double.compare(particle.getVy(), getVy()) == 0 &&
                Double.compare(particle.getMass(), getMass()) == 0 &&
                Double.compare(particle.getRadius(), getRadius()) == 0 &&
                Objects.equals(getPosition(), particle.getPosition());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getVx(), getVy(), getPosition(), getMass(), getRadius());
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", vx=" + vx +
                ", vy=" + vy +
                ", position=" + position +
                ", mass=" + mass +
                ", radius=" + radius +
                '}';
    }
}
