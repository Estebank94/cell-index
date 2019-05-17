import java.util.Set;

public class Calculations {

    /* Constants */
    private final static double g = 9.8;
    private final static double kN = Math.pow(10,5);
    private final static double kT = 2 * kN;
    private final static double gamma = 70;
    private final static double mass = 0.01;


    private void setForces(Particle p, Set<Particle> set) {
        double fX = 0;
        double fY = p.getMass() * g;
        for (Particle particle : set) {
            fX += getFN(p, particle) * getENX(p, particle) + getFT(p, particle) * (-(getENY(p, particle)));
            fY += getFN(p, particle) * getENY(p, particle) + getFT(p, particle) * getENX(p, particle);
        }
        p.setFx(fX);
        p.setFx(fY);
    }


    private static double absoluteDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()),2) + (Math.pow((p1.getY() - p2.getY()),2)));
    }

    private static double absoluteVelocity(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow((p1.getVX() - p2.getVX()),2) + (Math.pow((p1.getVX() - p2.getVX()),2)));
    }


    private double getFN(Particle p, Particle o) {
        return -kN * getEpsilon(p, o);
    }


    private double getFT(Particle p, Particle o) {
        return -kT * getEpsilon(p, o) * (((p.getVX() - o.getVX()) * (-getENY(p,o))));
    }

    private double getENY(Particle p, Particle o) {
        return (o.getY() - p.getY()) / getDistance(o.getX(), o.getY(), p.getX(), p.getY());
    }

    private double getENX(Particle p, Particle o) {
        return (o.getX() - p.getX()) / getDistance(o.getX(), o.getY(), p.getX(), p.getY());
    }

    private double getEpsilon(Particle p, Particle o) {
        return p.getR() + o.getR() - (getDistance(p.getX(), p.getY(), o.getX(), o.getY()));
    }

    private double getDistance(double x0, double y0, double x1, double y1) {
        return Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
    }
}


