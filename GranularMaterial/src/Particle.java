
public class Particle {

    final int id;

    private double x;
    private double y;

    private double prevX;
    private double prevY;

    private double vX;
    private double vY;

    private double prevVx;
    private double prevVy;

    private double prevAccX = 0;
    private double prevAccY = 0;

    private Double fx = null;
    private Double fy = null;

    double[] rListX = new double[6];
    double[] rListY = new double[6];

    final double mass;
    final double r;

    public Particle(int id, double x, double y, double prevX, double prevY, double vX, double vY, double prevVx, double prevVy, double mass, double r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.vX = vX;
        this.vY = vY;
        this.prevVx = prevVx;
        this.prevVy = prevVy;
        this.mass = mass;
        this.r = r;
    }

    public Particle(int id, double x, double y, double prevX, double prevY, double vX, double vY, double mass, double r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.vX = vX;
        this.vY = vY;
        this.mass = mass;
        this.r = r;
    }

    public Particle(int id, double x, double y, double vX, double vY, double mass, double r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
        this.mass = mass;
        this.r = r;
    }

    public Particle(int id, double prevX, double prevY, double mass, double r) {
        this.id = id;
        this.prevX = prevX;
        this.prevY = prevY;
        this.mass = mass;
        this.r = r;
    }

    public Particle(int id, double mass, double r) {
        this.id = id;
        this.mass = mass;
        this.r = r;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getPrevX() {
        return prevX;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    public double getvX() {
        return vX;
    }

    public void setvX(double vX) {
        this.vX = vX;
    }

    public double getvY() {
        return vY;
    }

    public void setvY(double vY) {
        this.vY = vY;
    }

    public double getPrevVx() {
        return prevVx;
    }

    public void setPrevVx(double prevVx) {
        this.prevVx = prevVx;
    }

    public double getPrevVy() {
        return prevVy;
    }

    public void setPrevVy(double prevVy) {
        this.prevVy = prevVy;
    }

    public double getPrevAccX() {
        return prevAccX;
    }

    public void setPrevAccX(double prevAccX) {
        this.prevAccX = prevAccX;
    }

    public double getPrevAccY() {
        return prevAccY;
    }

    public void setPrevAccY(double prevAccY) {
        this.prevAccY = prevAccY;
    }

    public Double getFx() {
        return fx;
    }

    public void setFx(Double fx) {
        this.fx = fx;
    }

    public Double getFy() {
        return fy;
    }

    public void setFy(Double fy) {
        this.fy = fy;
    }

    public double[] getrListX() {
        return rListX;
    }

    public void setrListX(double[] rListX) {
        this.rListX = rListX;
    }

    public double[] getrListY() {
        return rListY;
    }

    public void setrListY(double[] rListY) {
        this.rListY = rListY;
    }

    public double getMass() {
        return mass;
    }

    public double getR() {
        return r;
    }
}