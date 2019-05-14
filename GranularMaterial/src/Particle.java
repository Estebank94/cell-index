
public class Particle {

    private int id;
    private Vector2D position;
    private Vector2D prevPosition;
    private Vector2D velocity;
    private double mass;
    private double radius;
    private Vector2D force;
    private Vector2D prevForce;
    private long updateCount;

    public Particle(int id, double x, double y, double vx, double vy, double mass, double radius) {
        this.id = id;
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(vx, vy);
        this.mass = mass;
        this.radius = radius;
        this.updateCount = 0;
    }

    public Particle(double x, double y, double radius) {
        this.position = new Vector2D(x, y);
        this.radius = radius;
    }

    public Particle(int id, double x, double y, double prevX, double prevY, double vx, double vy, double mass, double radius) {
        this.id = id;
        this.position = new Vector2D(x, y);
        this.prevPosition = new Vector2D(prevX, prevY);
        this.velocity = new Vector2D(vx, vy);
        this.mass = mass;
        this.radius = radius;
        this.updateCount = 0;
        this.force = new Vector2D(0, 0);
        this.prevForce = new Vector2D(0, 0);
    }

    public double getX(){
        return position.getX();
    }

    public double getY(){
        return position.getY();
    }

    public double getPrevX(){
        return prevPosition.getX();
    }

    public double getPrevY(){
        return prevPosition.getY();
    }

    public double getVx(){
        return velocity.getX();
    }

    public double getVy(){
        return velocity.getY();
    }

    public double getFx(){
        return force.getX();
    }

    public double getFy(){
        return force.getY();
    }

    public double getPrevFx(){
        return prevForce.getX();
    }

    public double getPrevFy(){
        return prevForce.getY();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getPrevPosition() {
        return prevPosition;
    }

    public void setPrevPosition(Vector2D prevPosition) {
        this.prevPosition = prevPosition;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getForce() {
        return force;
    }

    public void setForce(Vector2D force) {
        this.force = force;
    }

    public Vector2D getPrevForce() {
        return prevForce;
    }

    public void setPrevForce(Vector2D prevForce) {
        this.prevForce = prevForce;
    }
}
