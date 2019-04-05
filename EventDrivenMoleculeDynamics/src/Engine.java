import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;

public class Engine {
    private final static double INFINITE = Double.POSITIVE_INFINITY;
    private Set<Particle> particles;

    private double boxSize;
    public double calculateSigma(Particle p1, Particle p2) {
        return p1.getRadius() + p2.getRadius();
    }

    /*Radius are in meters and Mass are in grams*/
    private double smallRadius = 0.005;
    private double smallMass = 0.1;
    private double bigRadius = 0.05;
    private double bigMass = 100;

    private double maxSmallVelocity = 0.1;


    public Point calculateDeltaR(Particle p1, Particle p2) {
        return new Point(p2.getPosition().getX() - p1.getPosition().getX(), p2.getPosition().getY() - p1.getPosition().getY());
    }

    public Point calculateDeltaV(Particle p1, Particle p2) {
        return new Point(p2.getVx() - p1.getVx(), p2.getVy() - p2.getVy());
    }

    public double calculateDeltaSqr(Point delta) {
        return Math.pow(delta.getX(), 2) + Math.pow(delta.getY(), 2);
    }

    public double calculateDeltaRxDeltaV(Point deltaR, Point deltaV) {
        return deltaR.getX()*deltaV.getX() + deltaR.getY()*deltaV.getY();
    }

    public double calculateD(double deltaRxDeltaV, double deltaRsqr, double deltaVsqr, double sigma ){
        return Math.pow(deltaRxDeltaV, 2) - deltaVsqr*(deltaRsqr - Math.pow(sigma, 2));
    }


    public double timeUntilCrashWithParticle(Particle p1, Particle p2) {
        double sigma = calculateSigma(p1, p2);
        Point deltaR = calculateDeltaR(p1, p2);
        Point deltaV = calculateDeltaV(p1, p2);

        double deltaRsqr = calculateDeltaSqr(deltaR);
        double deltaVsqr = calculateDeltaSqr(deltaV);

        double deltaRxdeltaV = calculateDeltaRxDeltaV(deltaR, deltaV);

        double d = calculateD(deltaRxdeltaV, deltaRsqr, deltaVsqr, sigma);

        if(deltaRxdeltaV >= 0) {
            return INFINITE;
        }
        if(d < 0){
            return INFINITE;
        }

        return - ((deltaRxdeltaV + Math.sqrt(d))/deltaVsqr);
    }

    public double timeUntilCrashWithWall(Particle p) {
        double timeX = Double.POSITIVE_INFINITY;
        double timeY = Double.POSITIVE_INFINITY;
        if(p.getVx() > 0) {
            timeX = (boxSize -  p.getRadius() - p.getPosition().getX()) / p.getVx();
        } else if (p.getVx() < 0) {
            timeX = (p.getRadius() - p.getPosition().getX()) / p.getVx();
        }

        if(p.getVy() > 0) {
            timeY = (boxSize -  p.getRadius() - p.getPosition().getY()) / p.getVy();
        } else if( p.getVy() < 0) {
            timeY = (p.getRadius() - p.getPosition().getY()) / p.getVy();
        }

        return timeX < timeY ? timeX : timeY;
    }

    /**
     * STEP 5
     *
     * **/
    public void evolveCrashedParticles(Set<Particle> crashedParticles){
        Iterator<Particle> pIterator = crashedParticles.iterator();
        if(crashedParticles.size() == 1) {
            Particle p = pIterator.next();
            if(crashedAgainstVerticalWall(p)) {
                p.setVx(-p.getVx());
            } else {
                p.setVy(-p.getVy());
            }
        } else {
            Particle p1 = pIterator.next();
            Particle p2 = pIterator.next();

            double sigma = calculateSigma(p1, p2);
            Point deltaR = calculateDeltaR(p1, p2);
            Point deltaV = calculateDeltaV(p1, p2);
            double deltaRxdeltaV = calculateDeltaRxDeltaV(deltaR, deltaV);

            double j = (2 * p1.getMass() * p2.getMass() * deltaRxdeltaV) / sigma * (p1.getMass() + p2. getMass());
            double Jx = (j * deltaR.getX()) / sigma;
            double Jy = (j * deltaR.getY()) / sigma;

            p1.setVx(p1.getVx() + Jx / p1.getMass());
            p1.setVy(p1.getVy() + Jy / p1.getMass());
            p2.setVx(p2.getVx() - Jx / p2.getMass());
            p2.setVy(p2.getVy() - Jy / p2.getMass());
        }
    }

    public boolean crashedAgainstVerticalWall(Particle p) {
        double DELTA = 0.000001;

        if(p.getPosition().getX() - DELTA <= p.getRadius()  || p.getPosition().getX() + DELTA >= boxSize - p.getRadius()) {
            return true;
        }

        return false;
    }


   /* Las posiciones de todas las partículas deben ser al azar con distribución uniforme dentro del dominio.
    Las partículas pequeñas deben tener velocidades con una distribución uniforme en el rango: |v| < 0.1 m/s*/


    /* n = Amount of small particles*/
    private void addParticles(int n){
        Random random = new Random();

        int id = 1;


        /* create an unique big particle*/
        particles.add(new Particle(id++, 0, 0, bigRadius,
                new Point(boxSize/2, boxSize/2), bigMass));

        while(particles.size() <= n){

            /* Math.random() * (max - min) + min; */
            double x = random.nextDouble() * (boxSize - 2*smallRadius) + smallRadius;
            double y = random.nextDouble() * (boxSize - 2*smallRadius) + smallRadius;;

            double v = random.nextDouble() * maxSmallVelocity;
            double angle = random.nextDouble() * 2 * Math.PI;
            double vx = v * Math.cos(angle);
            double vy = v * Math.sin(angle);

            Particle newParticle = new Particle(id++, vx, vy, smallRadius, new Point(x, y), smallMass);
            if(!isSuperimposed(newParticle)){
                /* tenemos que tener en cuenta que si no lo agregue aca, no se incremento el size del set
                y nosotros tenemos que asegurarnos que el set tenga las n particulas, por eso es que un for
                comun tipo i=0; i<n, etc no nos sirve
                 */
                particles.add(newParticle);
            }
        }


    }


    /*Cada particula nueva (i) no se puede superponer con ninguna
    de las particulas ya existentes (j) ni con las paredes
    (xi − xj)^2 +(yi − yj)^2 > (Ri − Rj)^2 */

    public boolean isSuperimposed (Particle newParticle) {

        double xDifference;
        double yDifference;
        double rSum;

        for(Particle p : particles){
            xDifference = (newParticle.getPosition().getX() - p.getPosition().getX());
            yDifference = (newParticle.getPosition().getY() - p.getPosition().getY());
            rSum = (newParticle.getRadius() + p.getRadius());

            if(Math.pow(xDifference,2) + Math.pow(yDifference, 2) <= Math.pow(rSum,2)) {
                return true;
            }
        }
        return false;
    }

}
