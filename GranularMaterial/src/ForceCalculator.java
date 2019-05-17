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


    public Vector2D calculate(Particle p, Set<Particle> neighbours, Function<Particle, Vector2D> position, Function<Particle, Vector2D> speed) {
        Vector2D force = Vector2D.of(0,- p.getMass() * g);
        double overlapping, derivate;

        double totalFn = 0;

        for(Particle other: neighbours) {
            if(!p.equals(other)) {
                overlapping= overlaping(p, other, position);
                derivate = derivateOverlap(p, other, position, speed);
                if(overlapping>0){
                    double fn = calculateFn(overlapping, derivate);
                    double ft = calculateFt(fn, calculateRelativeVelocity(p, other, speed, position));

                    totalFn += fn;

                    Vector2D en = position.apply(other).subtract(position.apply(p))
                            .dividedBy(position.apply(other).subtract(position.apply(p)).abs());

                    force = force.add(Vector2D.of(fn * en.x - ft * en.y,fn * en.y + ft * en.x));
                }

            }


        }
        p.setTotalFn(totalFn);

        force = force.add(getWallForces(p,position,speed));

        return force;
    }




    private double calculateFn(double overlaping, double derivOverlap) {
        return -Kn * overlaping - Gama * derivOverlap;
    }

    private double calculateFt(double fn, double vrel) {
        return - Mu * Math.abs(fn) * Math.signum(vrel);
    }


    private double overlaping(Particle i, Particle j, Function<Particle, Vector2D> position){
        double result = i.getRadius() + j.getRadius() - position.apply(i).subtract(position.apply(j)).abs();


        return result > 0 ? result  : 0;
    }

    private double derivateOverlap(Particle i, Particle j, Function<Particle, Vector2D> position , Function<Particle, Vector2D> speed){
        Vector2D direction = position.apply(j).subtract(position.apply(i));
        double result = speed.apply(i).projectedOn(direction) - speed.apply(j).projectedOn(direction);

        if(overlaping(i, j, position) > 0) {
            return result;
        }
        return 0;
    }


    private double calculateRelativeVelocity(Particle i, Particle j, Function<Particle, Vector2D> speed, Function<Particle, Vector2D> position) {
        Vector2D direction = position.apply(j).subtract(position.apply(i)).tangent();
        return speed.apply(i).subtract(speed.apply(j)).projectedOn(direction);
    }




    private Vector2D getWallForces(Particle p, Function<Particle, Vector2D> position, Function<Particle, Vector2D> speed) {

        Vector2D right = rightWall(p, position, speed);

        Vector2D left = leftWall(p, position, speed);

        Vector2D horizontal = horizontalWall(p, position, speed);

        return right.add(left).add(horizontal);
    }

    private Vector2D leftWall(Particle p, Function<Particle, Vector2D> position, Function<Particle, Vector2D> speed){
        double overlaping = 0, dervOver = 0, enx = 0, eny = 0, fn, ft;
        if(position.apply(p).x - p.getRadius() < 0){
            overlaping = p.getRadius() - position.apply(p).x;
            dervOver = speed.apply(p).projectedOn(Vector2D.of(-1, 0));
            enx = -1;
            eny = 0;
        }

        fn = calculateFn(overlaping,dervOver);
        ft = calculateFt(fn, speed.apply(p).projectedOn(Vector2D.of(0,1)));
        return Vector2D.of(fn * enx - ft * eny, fn * eny + ft * enx);
    }

    private Vector2D rightWall(Particle p, Function<Particle, Vector2D> position, Function<Particle, Vector2D> speed) {
        double dervOver = 0, overlaping = 0, enx = 0, eny = 0, fn, ft;
        if(position.apply(p).x + p.getRadius() > W){
            overlaping = position.apply(p).x + p.getRadius() - W;
            dervOver = speed.apply(p).projectedOn(Vector2D.of(1, 0));
            enx = 1;
            eny = 0;
        }

        fn = calculateFn(overlaping,dervOver);
        ft = calculateFt(fn, speed.apply(p).projectedOn(Vector2D.of(0,1)));
        return Vector2D.of(fn * enx - ft * eny, fn * eny + ft * enx);
    }

    private Vector2D horizontalWall(Particle p, Function<Particle, Vector2D> position, Function<Particle, Vector2D> speed){
        double dervOver = 0, overlaping = 0, enx = 0, eny = 0, fn, ft;

        boolean shouldCrashBottom = (position.apply(p).x < (W/2 - D/2) || position.apply(p).x > W - (W/2 - D/2)) && position.apply(p).y > 0;

        if(shouldCrashBottom && position.apply(p).y - p.getRadius() < 0) {
            overlaping = p.getRadius() - position.apply(p).y;
            dervOver = speed.apply(p).projectedOn(Vector2D.of(0,-1));
            enx = 0;
            eny = -1;
        }

        fn = calculateFn(overlaping, dervOver);
        ft = calculateFt(fn, speed.apply(p).projectedOn(Vector2D.of(1,0)));
        return  Vector2D.of( fn * enx - ft * eny, fn * eny + ft * enx);
    }

}

