import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Engine{

    /* Small Particles */
    private static double smallRadius = 0.005;
    private static double smallMass = 0.1;
    private static double smallVelocity = 0.1;

    /* Big Particle */
    private static double bigRadius = 0.05;
    private static double bigMass = 100;

    private static double boxSize = 0.5;

    private double numberOfParticles;
    private double time;
    private int collisionCount;
    private double temperature;

    private List<Particle> particles;

    public Engine(double numberOfParticles, double time, double temperature) {
        this.numberOfParticles = numberOfParticles;
        this.time = time;
        particles = new ArrayList<>();
        this.collisionCount = 0;
        this.temperature = temperature;
        addParticles();
    }

    public Engine(double numberOfParticles, double time) {
        this.numberOfParticles = numberOfParticles;
        this.time = time;
        particles = new ArrayList<>();
        this.collisionCount = 0;
        addParticles();
    }

    private static double timeUntilCrashWithVerticalWall(Particle particle) {

        if (particle.getVx() > 0) {
            return (boxSize - particle.getRadius() - particle.getX()) / particle.getVx();
        } else if (particle.getVx() < 0) {
            return (particle.getRadius() - particle.getX()) / particle.getVx();
        }

        return Double.POSITIVE_INFINITY;
    }

    private static double timeUntilCrashWithHorizontalWall(Particle particle) {
        if (particle.getVy() > 0) {
            return  (boxSize - particle.getRadius() - particle.getY()) / particle.getVy();
        } else if (particle.getVy() < 0) {
            return  (particle.getRadius() - particle.getY())  / particle.getVy();
        }

        return Double.POSITIVE_INFINITY;
    }

    private static double timeUntilCrashWithAnotherParticle(Particle p1, Particle p2) {
        double dX = p2.getX() - p1.getX();
        double dY = p2.getY() - p1.getY();
        double dVx = p2.getVx() - p1.getVx();
        double dVy = p2.getVy() - p1.getVy();

        double dVdR = dVx*dX + dVy*dY;
        double dVdV = Math.pow(dVx, 2) + Math.pow(dVy, 2);
        double dRdR = Math.pow(dX, 2) + Math.pow(dY, 2);
        double sigma = p1.getRadius() + p2.getRadius();

        double d = Math.pow(dVdR, 2) - dVdV * (dRdR - Math.pow(sigma ,2));

        if (dVdR < 0 && d >= 0) {
            return -((dVdR + Math.sqrt(d)) / dVdV);
        }

        return Double.POSITIVE_INFINITY;
    }


    private static void evolveCrashedParticles(Particle p1, Particle p2, boolean verticalCollision, boolean horizontalCollision) {

        if (verticalCollision) {
            p1.setVx(-p1.getVx());
            return;
        }

        if (horizontalCollision) {
            p1.setVy(-p1.getVy());
            return;
        }

        if (!verticalCollision && !horizontalCollision){

            double dX = p2.getX() - p1.getX();
            double dY = p2.getY() - p1.getY();
            double dVx = p2.getVx() - p1.getVx();
            double dVy = p2.getVy() - p1.getVy();

            double dVdR = dVx*dX + dVy*dY;
            double sigma = p1.getRadius() + p2.getRadius();

            double J = (2 * p1.getMass() * p2.getMass() * dVdR) / (sigma * (p1.getMass() + p2.getMass()));
            double Jx = J * dX / sigma;
            double Jy = J * dY / sigma;

            p1.setVx(p1.getVx() + Jx/p1.getMass());
            p1.setVy(p1.getVy() + Jy/p1.getMass());

            p2.setVx(p2.getVx() - Jx/p2.getMass());
            p2.setVy(p2.getVy() - Jy/p2.getMass());
        }

    }


    public List<Particle> addParticles() {

        /* Add big particle */
        particles.add(new Particle(1, boxSize / 2, boxSize / 2, 0, 0, bigMass, bigRadius));

        /* Add small particles */
        for (int i = 0; i < numberOfParticles; i++){

            double x;
            double y;

            do {
                x = smallRadius + (boxSize - 2 * smallRadius) * Math.random();
                y = smallRadius + (boxSize - 2 * smallRadius) * Math.random();
            }
            while (isSuperimposed(x,y, particles));

            double vx;
            double vy;

//            if(temperature == 0){
//                vx = randomSpeed();
//                vy = randomSpeed();
//            }else {
//                double v = temperature/numberOfParticles;
//                vx = v/Math.sqrt(2);
//                vy = v/Math.sqrt(2);
//
//                double sign = Math.random();
//                if(sign > 0.5){
//                    vx = vx * (-1);
//                }else if(sign < 0.5){
//                    vy = vy * (-1);
//                }
//            }
            vx = 2 * smallVelocity * Math.random() - smallVelocity;
            vy = 2 * smallVelocity * Math.random() - smallVelocity;

            particles.add(new Particle(i+2, x, y, vx, vy, smallMass, smallRadius));
        }

        return particles;
    }

    private static double randomSpeed(){
        return  2 * smallVelocity * Math.random() - smallVelocity;
    }

    private static boolean isSuperimposed(double x, double y, List<Particle> particles) {

        for (Particle p: particles){
            boolean superImposed = Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2) <= Math.pow(p.getRadius() + smallRadius, 2);
            if (superImposed){
                return true;
            }
        }
        return false;
    }

    private static void updatePositions(List<Particle> particles, double tc) {
        for (Particle particle : particles){
            particle.setX(particle.getX() + particle.getVx() * tc);
            particle.setY(particle.getY() + particle.getVy() * tc);
        }
    }

    public void start(String path) {
        double t = 0;
        int seconds = 0;
        int prevSeconds = 0;
        Map<Integer, Integer> timerMap = new HashMap<>();
        int count = 0;
        int countBySecond = 0;
        List<Point> bigMovement = new ArrayList<>();

        while(t < time){
            double tc = Double.POSITIVE_INFINITY;
            Particle crashed1 = null;
            Particle crashed2 = null;
            boolean verticalCollision = false;
            boolean horizontalCollision = false;

            for (Particle p1 : particles){
                /* Checking wall collision */
                double verticalTc = timeUntilCrashWithVerticalWall(p1);
                double horizontalTc = timeUntilCrashWithHorizontalWall(p1);

                if (verticalTc < tc){
                    tc = verticalTc;
                    verticalCollision = true;
                    horizontalCollision = false;
                    crashed1 = p1;
                    crashed2 = null;
                }

                if (horizontalTc < tc) {
                    tc = horizontalTc;
                    verticalCollision = false;
                    horizontalCollision = true;
                    crashed1 = p1;
                    crashed2 = null;
                }

                /* Checking particle collision */
                for (Particle p2 : particles){
                    if (!p1.equals(p2.getId())){

                        double ptc = timeUntilCrashWithAnotherParticle(p1, p2);
                        if (ptc < tc){
                            tc = ptc;
                            verticalCollision = false;
                            horizontalCollision = false;
                            crashed1 = p1;
                            crashed2 = p2;
                        }
                    }
                }
            }


            bigMovement.add(new Point(particles.get(0).getX(), particles.get(0).getY()));
            updatePositions(particles, tc);

            evolveCrashedParticles(crashed1, crashed2, verticalCollision, horizontalCollision);

            t += tc;


            seconds = (int)t;
            countBySecond++;
            if(prevSeconds < seconds){
                timerMap.put(prevSeconds, countBySecond);
                prevSeconds = seconds;
                countBySecond = 0;
            }




            System.out.println("Tiempo:" + t);

            String toWrite = generateFileString(particles);
            Engine.writeToFile(toWrite,collisionCount, path);


            System.out.println("Collision Count: " + collisionCount++ + " |  Collision time: " + tc);
            System.out.println("Promedio de tiempo entre colisiones: " + (t / (double) collisionCount));
            System.out.println("Promedio de colisiones por segundo: " + ((double) collisionCount) / (t));
            System.out.println();
        }
        /* collisions per second */
        for(int mapTime : timerMap.keySet()){
            System.out.println(mapTime + " " + timerMap.get(mapTime));
        }

        /* big particle movement */
        for(Point movement : bigMovement){
            System.out.println(movement.getX() + " " + movement.getY());
        }

    }

    public static void writeToFile(String data, int index, String path){
        try {
            Files.write(Paths.get(path  + "/result" + index + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String generateFileString(List<Particle> particles){
        StringBuilder builder = new StringBuilder()
                .append(particles.size())
                .append("\r\n")
                .append("//ID\t X\t Y\t Radius\t\r\n")
                .append("-1\t 0\t 0\t 0.005\t\r\n")
                .append("-1\t 0\t 0.5\t 0.005\t\r\n")
                .append("-1\t 0.5\t 0\t 0.005\t\r\n")
                .append("-1\t 0.5\t 0.5\t 0.005\t\r\n");
        for(Particle current: particles){
            builder.append(current.getId())
                    .append(" ")
                    .append(current.getX())
                    .append(" ")
                    .append(current.getY())
                    .append(" ")
                    .append(current.getRadius()+"\r\n");
        }
        return builder.toString();
    }


//    public String generateFileString(){
//        StringBuilder builder = new StringBuilder()
//                .append(collisionCount)
//                .append("\r\n")
//                .append("//ID\t X\t Y\t Radius\t R\t G\t B\t\r\n")
//                .append(-1)
//                .append(" ")
//                .append(0)
//                .append(" ")
//                .append(0)
//                .append(" ")
//                .append(smallRadius)
//                .append("\r\n")
//                .append(-1)
//                .append(" ")
//                .append(boxSize)
//                .append(" ")
//                .append(0)
//                .append(" ")
//                .append(smallRadius)
//                .append("\r\n")
//                .append(-1)
//                .append(" ")
//                .append(0)
//                .append(" ")
//                .append(boxSize)
//                .append(" ")
//                .append(smallRadius)
//                .append("\r\n")
//                .append(-1)
//                .append(" ")
//                .append(boxSize)
//                .append(" ")
//                .append(boxSize)
//                .append(" ")
//                .append(smallRadius)
//                .append("\r\n");
//        for(Particle current: particles){
//            builder
//                    .append(current.getId())
//                    .append(" ")
//                    .append(current.getX())
//                    .append(" ")
//                    .append(current.getY())
//                    .append(" ");
//
//
//            if(current.getId() < numberOfParticles + 1) {
//                builder.append(current.getRadius() + "\r\n");
//            } else {
//                builder.append(current.getRadius());
//            }
//
//        }
//        return builder.toString();
//    }
}
