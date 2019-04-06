import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Engine {
    private final static double INFINITE = Double.POSITIVE_INFINITY;
    private Set<Particle> particles;
    private BufferedWriter file;

    private double boxSize = 0.5;
    public double calculateSigma(Particle p1, Particle p2) {
        return p1.getRadius() + p2.getRadius();
    }

    /*Radius are in meters and Mass are in grams*/
    private double smallRadius = 0.005;
    private double smallMass = 0.1;
    private double bigRadius = 0.05;
    private double bigMass = 100;

    private double maxSmallVelocity = 0.1;

    private int collisionCount = 0;
    private double time = 0;

    public Engine(int amountOfParticles) {
        this.particles = new HashSet<>();
        this.file = null;
        this.collisionCount = 0;
        this.time = 0;
        addParticles(amountOfParticles);
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
            return Double.POSITIVE_INFINITY;
        }
        if(d < 0){
            return Double.POSITIVE_INFINITY;
        }

        return - (deltaRxdeltaV + Math.sqrt(d))/deltaVsqr;
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

        return (timeX < timeY) ? timeX : timeY;
    }

    /**
     * STEP 5
     *
     * **/
    public void evolveCrashedParticles(Particle p1, Particle p2){
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


    public void evolveCrashedParticles(Particle p){
        if(crashedAgainstVerticalWall(p)) {
            p.setVx(-p.getVx());
        } else {
            p.setVy(-p.getVy());
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

    /* STEP 1*/
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

    /* STEP 3
     ** xi(tc) = xi(0) + vxi.tc
     ** yi(tc) = yi(0) + vyi.tc
     */
    public void updatePositions (double tc){
        for(Particle p : particles){
            p.getPosition().setX(p.getPosition().getX() + p.getVx()*tc);
            p.getPosition().setY(p.getPosition().getY() + p.getVy()*tc);
        }
    }

    public void start(int simulationTime, String path) {
        System.out.println("Starting simulation...");
        System.out.println("Number of Particles: " + particles.size());
        double particleCrashTc;
        double tc;
        Particle crashed1 = null;
        Particle crashed2 = null;
        while( time < simulationTime ) {
            tc = Double.POSITIVE_INFINITY;
            for(Particle p1 : particles){
                particleCrashTc = timeUntilCrashWithWall(p1);
                if(particleCrashTc < tc && particleCrashTc > time) {
                    tc = particleCrashTc;
                    crashed1 = p1;
                    crashed2 = null;
                }
                for(Particle p2 : particles){
                    if(!p1.equals(p2)){
                        particleCrashTc = timeUntilCrashWithParticle(p1, p2);
                        if(particleCrashTc < tc && particleCrashTc > time) {
                            tc = particleCrashTc;
                            crashed1 = p1;
                            crashed2 = p2;
                        }
                    }
                }

            }

            updatePositions(tc);
            if(crashed2 != null) {
                evolveCrashedParticles(crashed1, crashed2);
            } else {
                evolveCrashedParticles(crashed1);
            }

            time += tc;

            //System.out.println("tc: " + tc + " - " + pi.toString());
//            System.out.println(particles.size() + 2);
            System.out.println("Collision Count: " + collisionCount++ + " time: " + tc);
//            for (Particle p : particles){
//                System.out.println(p.getPosition().getX() + "\t" + p.getPosition().getY() + "\t" + p.getVx() + "\t" + p.getVy() + "\t" + p.getRadius() + "\t" + tc);
//            }

//            String toWrite = generateFileString();
//            Engine.writeToFile(toWrite,collisionCount,path);

            // Print two particles for Ovito animation
//            System.out.println(0 + "\t" + 0 + "\t" + 0 + "\t" + 0 + "\t" + 0.001 + "\t" + 0);
//            System.out.println(L + "\t" + L + "\t" + 0 + "\t" + 0 + "\t" + 0.001 + "\t" + 0);
        }

    }

    public static void writeToFile(String data, int index, String path){
        try {
            Files.write(Paths.get(path  + "/result" + index + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String generateFileString(){
        StringBuilder builder = new StringBuilder()
                .append("//ID\t X\t Y\t Radius\t R\t G\t B\t\r\n");
        for(Particle current: particles){
            builder.append(current.getId())
                    .append(" ")
                    .append(current.getPosition().getX())
                    .append(" ")
                    .append(current.getPosition().getY())
                    .append(" ")
                    .append(current.getRadius())
                    .append("\r\n");
        }
        return builder.toString();
    }



}
