import java.util.Set;

public class Beeman {
    private double deltaT;

    public Beeman(double deltaT) {
        this.deltaT = deltaT;
    }

    public Particle moveParticleWithBeeman(Particle p, Set<Particle> set) {
        Particle updatedP = new Particle(p.getId(), p.getR(), p.getX(), p.getY(), p.getMass());
        updatedP.setPrevVX(p.getVX());
        updatedP.setPrevVY(p.getVY());

        setPositions(updatedP, p, set);
        setVels(updatedP, p, set);

        updatedP.setPrevAccX(p.getFx() / p.getMass());
        updatedP.setPrevAccY(p.getFy() / p.getMass());

        return updatedP;
    }

    private void setVels(Particle updatedP, Particle oldP, Set<Particle> set) {
        double m = oldP.getMass();

        double vx = oldP.getVX() + (1.0 / 3) * (Calculations.getFX(oldP, set) / m) * deltaT
                + (5.0 / 6) * (Calculations.getFX(oldP, set) / m) * deltaT - (1.0 / 6) * oldP.getPrevAccX() * deltaT;
        double vy = oldP.getVY() + (1.0 / 3) * (Calculations.getFY(oldP, set) / m) * deltaT
                + (5.0 / 6) * (Calculations.getFY(oldP, set) / m) * deltaT - (1.0 / 6) * oldP.getPrevAccY() * deltaT;

        updatedP.setVX(vx);
        updatedP.setVY(vy);
    }

    private void setPositions(Particle updatedP, Particle oldP, Set<Particle> set) {
        double m = oldP.getMass();

        double x = oldP.getX() + oldP.getVX() * deltaT + (2.0 / 3) * (Calculations.getFX(oldP, set) / m) * Math.pow(deltaT, 2)
                - (1.0 / 6) * oldP.getPrevAccX() * Math.pow(deltaT, 2);
        double y = oldP.getY() + oldP.getVY() * deltaT + (2.0 / 3) * (Calculations.getFX(oldP, set) / m) * Math.pow(deltaT, 2)
                - (1.0 / 6) * oldP.getPrevAccY() * Math.pow(deltaT, 2);

        updatedP.setX(x);
        updatedP.setY(y);
    }
}
