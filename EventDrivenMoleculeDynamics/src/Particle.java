import java.util.Objects;

public class Particle {

    private int id;
    private double x;
    private double y;
    private double prevX;
    private double prevY;
    private double vx;
    private double vy;
    private double mass;
    private double radius;
    private double fx;
    private double fy;
    private double prevFx;
    private double prevFy;
    private long updateCount;

    public Particle(int id, double x, double y, double vx, double vy, double mass, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
        this.updateCount = 0;
    }

    public Particle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Particle(int id, double x, double y, double prevX, double prevY, double vx, double vy, double mass, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
        this.updateCount = 0;
        this.fx = 0;
        this.fy = 0;
        this.prevFx = 0;
        this.prevFy = 0;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public double getPrevFx() {
        return prevFx;
    }

    public double getPrevFy() {
        return prevFy;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public static double borderDistanceBetweenParticles(Particle p1, Particle p2){
        return Point.distanceBetween(new Point(p1.getX(), p1.getY()), new Point(p2.getX(), p2.getY()));
    }

//    public static double borderDistanceBetweenParticles(Particle p1, Particle p2){
//        return Point.distanceBetween(new Point(p1.getX(), p1.getY()), new Point(p2.getX(), p2.getY())) - p1.getRadius() - p2.getRadius();
//    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    public void setPrevFx(double prevFx) {
        this.prevFx = prevFx;
    }

    public void setPrevFy(double prevFy) {
        this.prevFy = prevFy;
    }

    public void increaseUpdateCount(){
        updateCount++;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", vx=" + vx +
                ", vy=" + vy +
                ", mass=" + mass +
                ", radius=" + radius +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return Integer.compare(particle.getId(), getId()) == 0;

    }

    @Override
    public int hashCode() {

        return Objects.hash(getVx(), getVy(), getRadius(), getX(), getY(), getMass());
    }
}
