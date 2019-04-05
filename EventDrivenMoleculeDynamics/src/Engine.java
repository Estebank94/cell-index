import java.util.Iterator;
import java.util.Set;

public class Engine {
    private final static double INFINITE = Double.POSITIVE_INFINITY;
    private double boxSize;

    public double calculateSigma(Particle p1, Particle p2) {
        return p1.getRatio() + p2.getRatio();
    }

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
            timeX = (boxSize -  p.getRatio() - p.getPosition().getX()) / p.getVx();
        } else if (p.getVx() < 0) {
            timeX = (p.getRatio() - p.getPosition().getX()) / p.getVx();
        }

        if(p.getVy() > 0) {
            timeY = (boxSize -  p.getRatio() - p.getPosition().getY()) / p.getVy();
        } else if( p.getVy() < 0) {
            timeY = (p.getRatio() - p.getPosition().getY()) / p.getVy();
        }

        return timeX < timeY ? timeX : timeY;
    }

    public void evolveCrashedParticles(Set<Particle> crashedParticles){
        Iterator<Particle> pIterator = crashedParticles.iterator();
        if(crashedParticles.size() == 1) {
            Particle p = pIterator.next();
            if(crashedAgainstVerticalWall(crashedParticles)) {
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
}
