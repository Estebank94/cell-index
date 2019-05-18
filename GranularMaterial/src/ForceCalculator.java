import java.util.Set;
import java.util.function.Function;

public class ForceCalculator {
    private static double g = 9.8; // m/seg
    private static double Kn = Math.pow(10, 5); // N/m
    private static double Kt = 2 * Kn; // N/m
    private static double Mu = 0.1;
    private static double Gama = 100; // Kg/s

    private double L, W, D;



    public ForceCalculator(double L, double W, double D) {
        this.L = L;
        this.W = W;
        this.D = D;
    }


    public Vector2D calculate(Particle p, Set<Particle> neighbours) {

        Vector2D force = new Vector2D(0,- p.getMass() * g);
        double overlapping, derivate;

        double totalFn = 0;

        for(Particle other: neighbours) {
            if(!p.equals(other)) {
                overlapping = overlaping(p, other);
                derivate = derivateOverlap(p, other);
                if(overlapping>0){
                    double fn = calculateFn(overlapping, derivate);
                    double ft = calculateFt(fn, calculateRelativeVelocity(p, other));

                    totalFn += fn;

                    double enX = other.getPosition().getX() - p.getPosition().getX();
                    double enY = other.getPosition().getY() - p.getPosition().getY();
                    double enT = Math.sqrt(Math.pow(enX,2) + Math.pow(enY,2));
                    Vector2D enV = new Vector2D(enX, enY);
                    Vector2D en = enV.dividedBy(enT);

                    force = force.add(new Vector2D(fn * en.x - ft * en.y,fn * en.y + ft * en.x));
                }

            }


        }
        p.setTotalFn(totalFn);

        force = force.add(getWallForces(p));

        return force;
    }




    private double calculateFn(double overlaping, double derivOverlap) {
        return -Kn * overlaping - Gama * derivOverlap;
    }

    private double calculateFt(double fn, double vrel) {
        return - Mu * Math.abs(fn) * Math.signum(vrel);
    }


    private double overlaping(Particle i, Particle j){

        double resultX = Math.abs(i.getPosition().getX() - j.getPosition().getX());
        double resultY = Math.abs(i.getPosition().getY() - j.getPosition().getY());

        double result = i.getRadius() + j.getRadius() - Math.sqrt(Math.pow(resultX, 2) + Math.pow(resultY,2));

        return result > 0 ? result  : 0;
    }

    private double derivateOverlap(Particle i, Particle j){
        double directionX = j.getPosition().getX() - i.getPosition().getX();
        double directionY = j.getPosition().getY() - i.getPosition().getY();
        Vector2D direction = new Vector2D(directionX, directionY);

        double result = i.getSpeed().projectedOn(direction) - j.getSpeed().projectedOn(direction);

        if(overlaping(i, j) > 0) {
            return result;
        }
        return 0;
    }


    private double calculateRelativeVelocity(Particle i, Particle j) {
        double directionX = j.getPosition().getX() - i.getPosition().getX();
        double directionY = j.getPosition().getY() - i.getPosition().getY();
        Vector2D direction = new Vector2D(directionX, directionY).tangent();

        double speedX = i.getSpeed().getX() - j.getSpeed().getX();
        double speedY = i.getSpeed().getY() - j.getSpeed().getY();
        Vector2D speedV = new Vector2D(speedX, speedY);

        return speedV.projectedOn(direction);
    }




    private Vector2D getWallForces(Particle p) {

        Vector2D right = rightWall(p);

        Vector2D left = leftWall(p);

        Vector2D horizontal = horizontalWall(p);

        return right.add(left).add(horizontal);
    }

    private Vector2D leftWall(Particle p){
        double overlaping = 0, dervOver = 0, enx = 0, eny = 0, fn, ft;
        if(p.getPosition().getX() - p.getRadius() < 0){
            overlaping = p.getRadius() - p.getPosition().getX();
            dervOver = p.getSpeed().projectedOn(new Vector2D(-1, 0));
            enx = -1;
            eny = 0;
        }

        fn = calculateFn(overlaping,dervOver);
        ft = calculateFt(fn, p.getSpeed().projectedOn(new Vector2D(0,1)));
        return new Vector2D(fn * enx - ft * eny, fn * eny + ft * enx);
    }

    private Vector2D rightWall(Particle p) {
        double dervOver = 0;
        double overlaping = 0;
        double enx = 0, eny = 0;
        double fn, ft;


        if(p.getPosition().getX() + p.getRadius() > W){
            overlaping = p.getPosition().getX() + p.getRadius() - W;
            dervOver = p.getSpeed().projectedOn(new Vector2D(1, 0));
            enx = 1;
            eny = 0;
        }

        fn = calculateFn(overlaping,dervOver);
        ft = calculateFt(fn, p.getSpeed().projectedOn(new Vector2D(0,1)));
        return new Vector2D(fn * enx - ft * eny, fn * eny + ft * enx);
    }

    private Vector2D horizontalWall(Particle p){
        double dervOver = 0, overlaping = 0, enx = 0, eny = 0, fn, ft;

        boolean shouldCrashBottom = (p.getPosition().getX() < (W/2 - D/2) || p.getPosition().getX() > W - (W/2 - D/2))
                && p.getPosition().getY() > 0;

        if(shouldCrashBottom && p.getPosition().getY() - p.getRadius() < 0) {
            overlaping = p.getRadius() - p.getPosition().getY();
            dervOver = p.getSpeed().projectedOn(new Vector2D(0,-1));
            enx = 0;
            eny = -1;
        }

        fn = calculateFn(overlaping, dervOver);
        ft = calculateFt(fn, p.getSpeed().projectedOn(new Vector2D(1,0)));
        return  new Vector2D( fn * enx - ft * eny, fn * eny + ft * enx);
    }

}

