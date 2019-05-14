public class Vector2D {

    public static final Vector2D ZERO = new Vector2D(0.0, 0.0);

    final double x;
    final double y;

    public static Vector2D of(final double x, final double y) {
        return new Vector2D(x, y);
    }

    public Vector2D(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D add (Vector2D vector) {
        return new Vector2D(x + vector.x, y + vector.y);
    }

    public Vector2D substract (Vector2D vector){
        return new Vector2D(x - vector.x, y - vector.y);
    }

    public Vector2D divideBy (double value){
        return new Vector2D(x/value, y/value);
    }

    public Vector2D versor(){
        return new Vector2D(x,y).divideBy(abs());
    }

    public double abs(){
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }

    public double dot (Vector2D vector){
        return x*vector.x + y*vector.y;
    }

    public double projection(Vector2D vector){
        return new Vector2D(x,y).dot(vector);
    }

    public Vector2D tg(){
        return new Vector2D(-y, x);
    }

    public double distance(Vector2D vector){
        return this.substract(vector).abs();
    }

}
