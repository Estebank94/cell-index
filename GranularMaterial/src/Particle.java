import java.util.ArrayList;
import java.util.List;

public class Particle {

    private final int id;
    private final double radius;
    private final double mass;

    private Vector2D previousPosition;
    private Vector2D position;
    private Vector2D nextPosition;


    private Vector2D previousAcceleration;
    private Vector2D acceleration;
    private Vector2D nextAcceleration;

    private Vector2D speed;
    private Vector2D nextSpeedPredicted;
    private Vector2D nextSpeedCorrected;

    private List<Vector2D> derivatives;

    private Vector2D force;

    private double totalFn;

    public Particle(Particle p, Vector2D position, Vector2D speed, Vector2D previousAcc){
        this.id = p.id;
        this.mass = p.mass;
        this.radius = p.radius;

        this.previousAcceleration = previousAcc;
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.position = position;
        this.nextPosition = new Vector2D();

        this.speed = speed;
        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();

        this.totalFn = 0;

        this.derivatives = new ArrayList<>();
    }

    public Particle(Particle p, Vector2D position){
        this.id = p.id;
        this.mass = p.mass;
        this.radius = p.radius;

        this.previousAcceleration = new Vector2D();
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.position = position;
        this.nextPosition = new Vector2D();

        this.speed = new Vector2D();
        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();

        this.totalFn = 0;

        this.derivatives = new ArrayList<>();
    }

    public Particle(Particle p, Vector2D position, Vector2D speed){
        this.id = p.id;
        this.mass = p.mass;
        this.radius = p.radius;

        this.previousAcceleration = new Vector2D();
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.position = position;
        this.nextPosition = new Vector2D();

        this.speed = speed;
        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();

        this.totalFn = 0;

        this.derivatives = new ArrayList<>();
    }

    public Particle(Vector2D position, Vector2D prevPosition, Vector2D speed, Particle p) {
        this.id = p.id;
        this.mass = p.mass;
        this.radius = p.radius;

        this.previousPosition = Vector2D.ZERO;
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.previousPosition = prevPosition;
        this.position = position;
        this.nextPosition = new Vector2D();

        this.speed = speed;
        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();

        this.totalFn = 0;

        this.derivatives = new ArrayList<>();
    }



    public Particle(int id, double xPosition, double yPosition, double xSpeed, double ySpeed, double radius, double mass) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;

        this.previousAcceleration = new Vector2D();
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.position = new Vector2D(xPosition,yPosition);
        this.nextPosition = new Vector2D();

        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();
        this.speed = new Vector2D (xSpeed,ySpeed);

    }


    public int getId() {
        return id;
    }

    public Vector2D getPosition() {
        return position;
    }

    public Vector2D getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(Vector2D previousPosition) {
        this.previousPosition = previousPosition;
    }

    public Vector2D getSpeed() {
        return speed;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public Vector2D getPreviousAcceleration() {
        return previousAcceleration;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setPreviousAcceleration(Vector2D previousAcceleration) {
        this.previousAcceleration = previousAcceleration;
    }

    public void setSpeed(Vector2D speed) {
        this.speed = speed;
    }

    public Vector2D getAcceleration() {
        if(!acceleration.isInitialized()) {
            throw new IllegalStateException();
        }
        return acceleration;
    }

    public Vector2D getNextAcceleration() {
        if(!nextAcceleration.isInitialized()) {
            throw new IllegalStateException();
        }
        return nextAcceleration;
    }

    public Vector2D getNextPosition() {
        if(!nextPosition.isInitialized()) {
            throw new IllegalStateException();
        }
        return nextPosition;
    }

    public Vector2D getNextSpeedPredicted() {
        if(!nextSpeedPredicted.isInitialized()) {
            throw new IllegalStateException();
        }
        return nextSpeedPredicted;
    }

    public Vector2D getNextSpeedCorrected() {
        if(!nextSpeedCorrected.isInitialized()) {
            throw new IllegalStateException();
        }
        return nextSpeedCorrected;
    }

    public void setNextPosition(Vector2D nextPosition){
        if(this.nextPosition.isInitialized()){
            throw new IllegalStateException();
        }else{
            this.nextPosition = nextPosition;
        }
    }

    public void setNextSpeedCorrected(Vector2D nextVelocity){
        if(this.nextSpeedCorrected.isInitialized()){
            throw new IllegalStateException();
        } else {
            this.nextSpeedCorrected = nextVelocity;
        }
    }

    public void setNextSpeedPredicted(Vector2D nextVelocity){
        if(this.nextSpeedPredicted.isInitialized()){
            throw new IllegalStateException();
        }else{
            this.nextSpeedPredicted = nextVelocity;
        }
    }

    public void setNextAcceleration(Vector2D nextAcceleration){
        if(this.nextAcceleration.isInitialized()){
            throw new IllegalStateException();
        }else{
            this.nextAcceleration = nextAcceleration;
        }
    }

    public boolean overlaps(Particle other) {
        double distance = this.position.distance(other.position);

        if(distance < radius + other.radius) {
            return true;
        }

        return false;
    }

    public void setAcceleration(Vector2D acceleration){
        if(this.acceleration.isInitialized()){
            throw new IllegalStateException();
        }else{
            this.acceleration = acceleration;
        }
    }

    public double getTotalFn() {
        return totalFn;
    }

    public void setTotalFn(double totalFn) {
        if(totalFn != 0) {
            this.totalFn = totalFn;
        }
    }


    public Vector2D getForce() {
        return force;
    }

    public void setForce(Vector2D force) {
        this.force = force;
    }

    public void setDerivatives(List<Vector2D> derivatives) {
        this.derivatives = derivatives;
    }

    public List<Vector2D> getDerivatives() {
        return derivatives;
    }

    public void clearFn() {
        this.totalFn = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;

        Particle particle = (Particle) o;

        if (getId() != particle.getId()) return false;
        if (Double.compare(particle.getRadius(), getRadius()) != 0) return false;
        return Double.compare(particle.getMass(), getMass()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        temp = Double.doubleToLongBits(getRadius());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getMass());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
