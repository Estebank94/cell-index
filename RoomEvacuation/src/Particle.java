public class Particle {

    private final int id;
    private final double radius;
    private final double mass;

    private final Vector2D position;
    private Vector2D nextPosition;

    private final double desiredSpeed;
    private final Vector2D target;

    private final Vector2D previousAcceleration;
    private Vector2D acceleration;
    private Vector2D nextAcceleration;

    private final Vector2D speed;
    private Vector2D nextSpeedPredicted;
    private Vector2D nextSpeedCorrected;

    private double totalFn;

    public Particle(Particle p, Vector2D position, Vector2D speed, Vector2D previousAcc){
        this.id = p.id;
        this.mass = p.mass;
        this.radius = p.radius;
        this.desiredSpeed= p.desiredSpeed;
        this.target=p.target;

        this.previousAcceleration = previousAcc;
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.position = position;
        this.nextPosition = new Vector2D();

        this.speed = speed;
        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();

        this.totalFn = 0;
    }

    public Particle(Particle p, Vector2D position, Vector2D speed, Vector2D previousAcc, double totalFn){
        this.id = p.id;
        this.mass = p.mass;
        this.radius = p.radius;
        this.desiredSpeed= p.desiredSpeed;
        this.target=p.target;

        this.previousAcceleration = previousAcc;
        this.acceleration = new Vector2D();
        this.nextAcceleration = new Vector2D();

        this.position = position;
        this.nextPosition = new Vector2D();

        this.speed = speed;
        this.nextSpeedPredicted = new Vector2D();
        this.nextSpeedCorrected = new Vector2D();

        this.totalFn = totalFn;
    }

    public Particle(int id, double xPosition, double yPosition, double xSpeed, double ySpeed, double radius, double mass, double desiredSpeed, Vector2D target) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
        this.desiredSpeed= desiredSpeed;
        this.target=target;

        this.previousAcceleration = new Vector2D(0,0);
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

    public double getDesiredSpeed() {
        return desiredSpeed;
    }

    public Vector2D getTarget() {
        return target;
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
        }else{
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
//        if(totalFn != 0) {
            this.totalFn = totalFn;
//        }
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
