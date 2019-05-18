
public class Vector2D {

    public static final Vector2D ZERO = new Vector2D(0.0, 0.0);

    final double x;
    final double y;
    boolean initialized;



    public Vector2D(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.initialized = true;
    }

    public Vector2D(){
        this.x = 0;
        this.y = 0;
        this.initialized = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D add(final Vector2D vector) {
        return new Vector2D(x + vector.x, y + vector.y);
    }

    public Vector2D subtract(final Vector2D vector) {
        return new Vector2D(x - vector.x, y - vector.y);
    }

    public Vector2D multiplyBy(final double value) {
        return new Vector2D(x*value, y*value);
    }

    public Vector2D dividedBy(final double value) {
        return new Vector2D(x/value, y/value);
    }

    public Vector2D versor() {
        return new Vector2D(x, y).dividedBy(abs());
    }

    public double abs() {
        return Math.sqrt(x * x + y * y );
    }

    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }

    public double projectedOn(Vector2D other) {
        return new Vector2D(x, y).dot(other.versor());
    }

    public Vector2D tangent() {
        return new Vector2D(-y, x);
    }

    public double distance(Vector2D other){
        return this.subtract(other).abs();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2D)) return false;

        Vector2D vector = (Vector2D) o;

        if (Double.compare(vector.getX(), getX()) != 0) return false;
        return Double.compare(vector.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getX());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
