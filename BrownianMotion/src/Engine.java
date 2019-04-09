import sun.tools.tree.DoubleExpression;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
// Considerar un dominio cuadrado de lado L=0.5 m. En su interior colocar N partículas pequeñas de
//        radio R1=0.005 m y masa m1 = 0.1 g y una partícula grande de radio R2= 0.05 y masa m2 = 100 g.
//        Condiciones Iniciales:
//        Las posiciones de todas las partículas deben ser al azar con distribución uniforme dentro del
//        dominio. Las partículas pequeñas deben tener velocidades con una distribución uniforme en el
//        rango: |v| < 0.1 m/s. La partícula grande debe tener velocidad inicial v2 = 0 y su posición inicial en
//        x=L/2, y=L/2
//        Condiciones de Contorno:
//        Sistema confinado, es decir, todas las paredes son rígidas.




public class Engine {

    private static double boxSize = 0.5;
    private double smallMass = 0.1;
    private double smallRadius = 0.005;

    private double bigMass = 100;
    private double bigRadius = 0.05;

    private Set<Particle> particles;
    private final double maxSmallVelocity = 0.1;

    private int amountOfParticles;

    private int Nan = 0;

    public static int amount = 0;

    public Engine(int amountOfParticles) {
        this.amountOfParticles = amountOfParticles;
        this.particles = new HashSet<>();
        addParticles(amountOfParticles);
    }

    /* STEP 1 - create particles */
    public void addParticles(int amountOfParticles){
        Random random = new Random();

        particles.add(new Particle(0, 0, 0, new Point(boxSize/2, boxSize/2), bigMass, bigRadius));

        int id = 1;
        while(particles.size() < amountOfParticles + 1){
            double x = random.nextDouble() * (boxSize - 2*smallRadius) + smallRadius;
            double y = random.nextDouble() * (boxSize - 2*smallRadius) + smallRadius;

            double speed = random.nextDouble() * maxSmallVelocity;
            double angle = random.nextDouble() * 2 * Math.PI;
            double vx = speed * Math.cos(angle);
            double vy = speed * Math.sin(angle);

            if(!isSuperimposed(x, y, smallRadius)){
                particles.add(new Particle(id++, vx, vy, new Point(x, y), smallMass, smallRadius));
            }
        }
    }

    public void test_addParticles(){
        for(Particle p : particles){
            System.out.println(p.toString());
        }
    }

    public boolean isSuperimposed (double xPosition, double yPosition, double radius){
        for(Particle p : particles) {
            if(Math.pow((p.getPosition().getX() - xPosition),2) + Math.pow((p.getPosition().getY() - yPosition),2) <= Math.pow((p.getRadius() + radius),2)) {
                return true;
            }
        }
        return false;
    }

    /* STEP 2 - Calculate collisions */

    /* Calculate dr - returns: (dx, dy) */
    public static Point calculateDeltaR(Particle p1, Particle p2) {
        return new Point(p2.getPosition().getX() - p1.getPosition().getX(), p2.getPosition().getY() - p1.getPosition().getY());
    }

    /* Calculate dv - returns (dvx, dvy) */
    public static Point calculateDeltaV(Particle p1, Particle p2) {
        return new Point(p2.getVx() - p1.getVx(), p2.getVy() - p2.getVy());
    }

    /* Calculate drdr or dvdv - returns (double) */
    public static double calculateDeltaSqr(Point delta) {
        return Math.pow(delta.getX(), 2) + Math.pow(delta.getY(), 2);
    }

    /* Calculate sigma = (Ri + Rj) */
    public static double calculateSigma(Particle p1, Particle p2) {
        return p1.getRadius() + p2.getRadius();
    }

    public double calculateDeltaRxDeltaV(Point deltaR, Point deltaV) {
        return deltaR.getX()*deltaV.getX() + deltaR.getY()*deltaV.getY();
    }

    public double calculateD(double deltaRxDeltaV, double deltaRsqr, double deltaVsqr, double sigma ){
        return Math.pow(deltaRxDeltaV, 2) - deltaVsqr*(deltaRsqr - Math.pow(sigma, 2));
    }

    /* Crash between particles */
//    public double timeUntilCrashWithParticle(Particle p1, Particle p2) {
//////        double sigma = calculateSigma(p1, p2);
//////        Point deltaR = calculateDeltaR(p1, p2);
//////        Point deltaV = calculateDeltaV(p1, p2);
//////
//////        double deltaRsqr = calculateDeltaSqr(deltaR);
//////        double deltaVsqr = calculateDeltaSqr(deltaV);
//////
//////        double deltaRxdeltaV = calculateDeltaRxDeltaV(deltaR, deltaV);
//////
//////        double d = calculateD(deltaRxdeltaV, deltaRsqr, deltaVsqr, sigma);
//////
//////        if(deltaRxdeltaV >= 0) {
//////            return Double.POSITIVE_INFINITY;
//////        }
//////        if(d < 0){
//////            return Double.POSITIVE_INFINITY;
//////        }
//////        /*if(- ((deltaRxdeltaV + Math.sqrt(d))/deltaVsqr) < 0){
//////            amount++;
//////            System.out.println("final " + - ((deltaRxdeltaV + Math.sqrt(d))/deltaVsqr));
//////        }*/
//////        return - (deltaRxdeltaV + Math.sqrt(d))/deltaVsqr;
//////    }
    static double timeUntilCrashWithParticle(Particle pi, Particle pj) {
        double dvx = pj.getVx()-pi.getVx();
        double dvy = pj.getVy()-pi.getVy();

        double dx = pj.getPosition().getX()-pi.getPosition().getX();
        double dy = pj.getPosition().getY()-pi.getPosition().getY();

        double dvdr = dvx*dx + dvy*dy;

        if(dvdr >= 0) {
            return Double.POSITIVE_INFINITY;
        }

        double dvdv = dvx*dvx + dvy*dvy;
        double drdr = dx*dx + dy*dy;
        double phi = pi.getRadius()+pj.getRadius();

        double d = dvdr*dvdr - dvdv*(drdr-phi*phi);

        if(d < 0) {
            return Double.POSITIVE_INFINITY;
        }


        return -(dvdr+Math.sqrt(d))/dvdv;
    }

    /* Calculo todos los choques entre particulas */
    public void test_timeUntilCrashWithParticle(){
        double tc;
        int crashes = 0;
        for(Particle p1 : particles){
            for (Particle p2: particles){
                if(!p1.equals(p2)){
                    tc = timeUntilCrashWithParticle(p1, p2);
                    if (tc != Double.POSITIVE_INFINITY && tc < 0){
                        crashes ++;
                        System.out.println("Time crashed:" + tc);
                    }
                }
            }
        }
        System.out.println("Amount of crashes:" + crashes);
        System.out.println("Final, amount of times it gave negative:" + amount);
    }

    /* Crash between particles and walls */
    private double timeUntilCrashWithWall(Particle p) {
        double timeX = Double.POSITIVE_INFINITY;
        double timeY = Double.POSITIVE_INFINITY;

        if(p.getVx() > 0) {
            timeX = (boxSize - p.getRadius() - p.getPosition().getX()) / p.getVx();
        } else if(p.getVx() < 0) {
            timeX = (p.getRadius() - p.getPosition().getX()) / p.getVx();
        }


        if(p.getVy() > 0) {
            timeY = (boxSize - p.getRadius() - p.getPosition().getY()) / p.getVy();

        } else if(p.getVy() < 0) {
            timeY = (p.getRadius() - p.getPosition().getY()) / p.getVy();
        }

        if(timeX < 0 && timeY > 0){
            return timeY;
        }else if(timeY < 0 && timeX > 0){
            return timeX;
        }

        return (timeX < timeY) ? timeX : timeY;
    }


    /* Calculo todos los choques de particulas con las paredes */
    public void test_timeCrashedWithWall(){
        double tc;
        double amount = 0;
        for(Particle p : particles){
            tc = timeUntilCrashWithWall(p);
            if(tc != Double.POSITIVE_INFINITY && tc < 0){
                amount ++;
                System.out.println("Time of wall crash:" + tc);
            }
        }
        System.out.println("Amount of wall crashes:" + amount);
    }

    /* STEP 3 - Evolve positions of all the particles until tc - */

    public void updatePositions (double tc){
        for(Particle p : particles){
            double t1 = p.getPosition().getX() + p.getVx()*tc;
            double t2 = p.getPosition().getY() + p.getVy()*tc;
            if(Double.isNaN(t1) || Double.isNaN(t2)){
                System.out.println("Nan:" + Nan++);
            }
            p.getPosition().setX(p.getPosition().getX() + p.getVx()*tc);
            p.getPosition().setY(p.getPosition().getY() + p.getVy()*tc);
        }
    }

    /* STEP 4 - Save system state, position & velocity - */
    private void saveCrashedParticle(Set<Particle> crashedParticles, Particle p) {
        crashedParticles.clear();
        crashedParticles.add(p);
    }

    private void saveCrashedParticle(Set<Particle> crashedParticles, Particle p, Particle o) {
        crashedParticles.clear();
        crashedParticles.add(p);
        crashedParticles.add(o);
    }

    /* STEP 5 - Evolve velocities after crash for particles that have crashed - */
//    private void updateSpeedCrashedParticles(Set<Particle> crashedParticles) {
//        Iterator<Particle> it = crashedParticles.iterator();
//
//        /* Crashed against wall */
//        if(crashedParticles.size() == 1) {
//            Particle p = it.next();
//            if(crashedAgainstVerticalWall(p)) {
//                p.setVx(-p.getVx());
//            } else {
//                p.setVy(-p.getVy());
//            }
//        }
//
//        /* Crashed against other particle */
//        if(crashedParticles.size() == 2) {
//            Particle pi = it.next();
//            Particle pj = it.next();
//
//            //double dVdR = (p2.getxSpeed() - p1.getxSpeed()) * (p2.getxPosition() - p2.getxPosition()) + (p2.getySpeed() - p1.getySpeed()) * (p2.getyPosition() - p2.getyPosition());
//            double dvx = pj.getVx()-pi.getVx();
//            double dvy = pj.getVy()-pi.getVy();
//
//            double dx = pj.getPosition().getX()-pi.getPosition().getX();
//            double dy = pj.getPosition().getY()-pi.getPosition().getY();
//
//            double dvdr = dvx*dx + dvy*dy;
//
//            double phi = pi.getRadius() + pj.getRadius();
//            double J = (2 * pi.getMass() * pj.getMass() * dvdr) / ( phi * (pi.getMass() + pj.getMass()) );
//            double Jx = J * (pj.getPosition().getX() - pi.getPosition().getX()) / phi;
//            double Jy = J * (pj.getPosition().getY() - pi.getPosition().getY()) / phi;
//
//            pi.setVx(pi.getVx() + Jx / pi.getMass());
//            pi.setVy(pi.getVy() + Jy / pi.getMass());
//            pj.setVx(pj.getVx() - Jx / pj.getMass());
//            pj.setVy(pj.getVy() - Jy / pj.getMass());
//        }

    /* when two particles crash */
    public void evolveCrashedParticles(Particle p1, Particle p2){
        double sigma = calculateSigma(p1, p2);

        Point deltaR = calculateDeltaR(p1, p2);
        double dx = deltaR.getX();
        double dy = deltaR.getY();

        Point deltaV = calculateDeltaV(p1, p2);
        double dvx = deltaV.getX();
        double dvy = deltaV.getY();

        double dvdr = dvx*dx + dvy*dy;
//      double deltaRxdeltaV = calculateDeltaRxDeltaV(deltaR, deltaV);

        double j = (2 * p1.getMass() * p2.getMass() * dvdr) / (sigma * (p1.getMass() + p2.getMass()));
        double Jx = (j * deltaR.getX()) / sigma;
        double Jy = (j * deltaR.getY()) / sigma;

        p1.setVx(p1.getVx() + Jx / p1.getMass());
        p1.setVy(p1.getVy() + Jy / p1.getMass());
        p2.setVx(p2.getVx() - Jx / p2.getMass());
        p2.setVy(p2.getVy() - Jy / p2.getMass());
    }


    /* when a particle crashes against a wall */
    public void evolveCrashedParticles(Particle p){
        if(crashedAgainstVerticalWall(p)) {
            p.setVx(-p.getVx());
        } else {
            p.setVy(-p.getVy());
        }
    }

    public boolean crashedAgainstVerticalWall(Particle p) {
        double delta = 0.00000001;

        if(p.getPosition().getX() - delta <= p.getRadius()  || p.getPosition().getX() + delta >= boxSize - p.getRadius()) {
            return true;
        }

        return false;
    }

    public void test_evolution(){
        double tc;
        for(Particle p : particles){
                System.out.print("Pre Update: ");
                System.out.println(p.toString());
                tc = timeUntilCrashWithWall(p);
                if(tc != Double.POSITIVE_INFINITY){
                    updatePositions(tc);
                    evolveCrashedParticles(p);
                    System.out.print("Post Update: ");
                    System.out.println(p.toString());
                }
        }
        System.out.println("Amount of Nan: " + Nan);
    }



    public void start(double simulationTime){
        double time = 0;
        Particle crashed1 = null;
        Particle crashed2 = null;

        while(time < simulationTime){
            double tc = Double.POSITIVE_INFINITY;
            crashed1 = null;
            crashed2 = null;

            for(Particle p : particles){

                /* wall crash */
                double wallCrashTime = timeUntilCrashWithWall(p);
                if(wallCrashTime < 0){
                    System.out.println("hola");
                }
                if(wallCrashTime < tc){
                    tc = wallCrashTime;
                    crashed1 = p;
                    crashed2 = null;
                }

                /* particles crash */
                for(Particle other : particles){
                    if(!p.equals(other)){
                        double particleCrashTime = timeUntilCrashWithParticle(p, other);
                        if(particleCrashTime < 0){
                            System.out.println("hola");
                        }
                        if(particleCrashTime < tc){
                            tc = particleCrashTime;
                            crashed1 = p;
                            crashed2 = other;
                        }
                    }
                }

                if(tc == Double.POSITIVE_INFINITY){
                    System.out.println("hola");
                }

                updatePositions(tc);
                if(crashed2 != null){
                    evolveCrashedParticles(crashed1, crashed2);
                }else {
                    evolveCrashedParticles(crashed1);
                }

            }

            time += tc;
            System.out.println("Collision Count: " + amount++ + " |  Collision time: " + tc);
        }
    }



}
