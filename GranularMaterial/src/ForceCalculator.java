import java.util.Set;



public class ForceCalculator {
    private static double g = 9.8; // m/seg^2
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


    /**
     * Calculates the force implied to the particle considering neighbours and walls
     * @param p particle to which we will calculate the force
     * @param neighbours of the particle evaluating
     * @return (forcex, forcey)
     **/
    public Vector2D calculate(Particle p, Set<Particle> neighbours) {

        Vector2D force = new Vector2D(0,- p.getMass() * g);
        double overlap, d;

        double totalFn = 0;

        /* calculamos la fuerza contra todas las particulas que sean vecinas */
        for(Particle neighbour : neighbours) {
            if(!p.equals(neighbour)) {
                overlap = overlapping(p, neighbour);
                d = overlappingDerivation(p, neighbour);
                if(overlap > 0){
                    double nForce = nForce(overlap, d);
//                    double tForce = tForce(nForce, relativeVelocity(p, neighbour));

                    totalFn += nForce;

                    double enX = neighbour.getPosition().getX() - p.getPosition().getX();
                    double enY = neighbour.getPosition().getY() - p.getPosition().getY();
                    double enT = Math.sqrt(Math.pow(enX,2) + Math.pow(enY,2));

                    Vector2D enV = new Vector2D(enX, enY);
                    Vector2D en = enV.dividedBy(enT);

                    Vector2D newForce = new Vector2D(nForce * en.x /*- tForce * en.y*/,nForce * en.y /*+ tForce * en.x*/);

                    force = force.add(newForce);
                }

            }

        }
        p.setTotalFn(totalFn);

        force = force.add(getWallForces(p));

        return force;
    }


    private double nForce(double overlap, double dOverlap) {
        return -Kn * overlap - Gama * dOverlap;
    }

    private double tForce(double nForce, double vr) {
        return - Mu * Math.abs(nForce) * Math.signum(vr);
    }

    private double overlapping(Particle i, Particle j){

        double resultX = Math.abs(i.getPosition().getX() - j.getPosition().getX());
        double resultY = Math.abs(i.getPosition().getY() - j.getPosition().getY());

        double result = i.getRadius() + j.getRadius() - Math.sqrt(Math.pow(resultX, 2) + Math.pow(resultY,2));

        if(result > 0){
            return result;
        }else {
            return 0;
        }

    }

    private double overlappingDerivation(Particle i, Particle j){
        double directionX = j.getPosition().getX() - i.getPosition().getX();
        double directionY = j.getPosition().getY() - i.getPosition().getY();
        Vector2D direction = new Vector2D(directionX, directionY);

        double result = i.getSpeed().projectedOn(direction) - j.getSpeed().projectedOn(direction);

        if(overlapping(i, j) > 0) {
            return result;
        }
        return 0;
    }

    private double relativeVelocity(Particle i, Particle j) {
        double directionX = j.getPosition().getX() - i.getPosition().getX();
        double directionY = j.getPosition().getY() - i.getPosition().getY();
        Vector2D direction = new Vector2D(directionX, directionY).tangent();

        double speedX = i.getSpeed().getX() - j.getSpeed().getX();
        double speedY = i.getSpeed().getY() - j.getSpeed().getY();
        Vector2D speedV = new Vector2D(speedX, speedY);

        double rv = speedV.projectedOn(direction);

        return rv;
    }

    /**
     * Calculates forces implied by all the possible walls to the particle
     * @param p
     * @return (forceX, forceY)
     */
    private Vector2D getWallForces(Particle p) {

        Vector2D right = rightWall(p);

        Vector2D left = leftWall(p);

        Vector2D horizontal = horizontalWall(p);

        Vector2D total = right.add(left).add(horizontal);

        return total;
    }

    private Vector2D horizontalWall(Particle p){
        double dervOver = 0, overlap = 0;
        double enx = 0, eny = 0;
        double fn, ft;

        boolean shouldCrashBottom = (p.getPosition().getX() < (W/2 - D/2) || p.getPosition().getX() > W - (W/2 - D/2))
                && p.getPosition().getY() > 0;

        if(shouldCrashBottom && p.getPosition().getY() - p.getRadius() < 0) {
            overlap = p.getRadius() - p.getPosition().getY();
            dervOver = p.getSpeed().projectedOn(new Vector2D(0,-1));
            enx = 0;
            eny = -1;
        }

        fn = nForce(overlap, dervOver);
//        ft = tForce(fn, p.getSpeed().projectedOn(new Vector2D(1,0)));

        Vector2D force = new Vector2D(fn * enx /*- ft * eny*/, fn * eny /*+ ft * enx*/);
        return force;
    }

    private Vector2D leftWall(Particle p){
        double overlap = 0, dervOver = 0;
        double enx = 0, eny = 0;
        double fn, ft;
        if(p.getPosition().getX() - p.getRadius() < 0){
            overlap = p.getRadius() - p.getPosition().getX();
            dervOver = p.getSpeed().projectedOn(new Vector2D(-1, 0));
            enx = -1;
            eny = 0;
        }

        fn = nForce(overlap,dervOver);
//        ft = tForce(fn, p.getSpeed().projectedOn(new Vector2D(0,1)));
        Vector2D force = new Vector2D(fn * enx /*- ft * eny*/, fn * eny /*+ ft * enx*/);
        return force;
    }

    private Vector2D rightWall(Particle p) {
        double dervOver = 0;
        double overlap = 0;
        double enx = 0, eny = 0;
        double fn, ft;


        if(p.getPosition().getX() + p.getRadius() > W){
            overlap = p.getPosition().getX() + p.getRadius() - W;
            dervOver = p.getSpeed().projectedOn(new Vector2D(1, 0));
            enx = 1;
            eny = 0;
        }

        fn = nForce(overlap,dervOver);
//        ft = tForce(fn, p.getSpeed().projectedOn(new Vector2D(0,1)));

        Vector2D force = new Vector2D(fn * enx /*- ft * eny*/, fn * eny /*+ ft * enx*/);
        return force;
    }



}

