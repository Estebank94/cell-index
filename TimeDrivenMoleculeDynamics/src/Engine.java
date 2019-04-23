import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Engine{

    /* Small Particles */
    private static double smallRadius = 1;
    private static double smallMass = 0.1;
    private static double smallVelocity = 0.1;
    private static double openingSize = 10;

    private static double boxWidth = 400;
    private static double boxHeight = 200;

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
            return (boxWidth - particle.getRadius() - particle.getX()) / particle.getVx();
        } else if (particle.getVx() < 0) {
            return (particle.getRadius() - particle.getX()) / particle.getVx();
        }

        return Double.POSITIVE_INFINITY;
    }

    /* time until a particle crashes with an horizontal wall*/
    private static double timeUntilCrashWithHorizontalWall(Particle particle) {
        if (particle.getVy() > 0) {
            return  (boxHeight - particle.getRadius() - particle.getY()) / particle.getVy();
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

    public static double timeUntilCrashWithMiddleWall(Particle p, final double opening) {
        double tc, newY, openingUpperLimit, openingLowerLimit;
    /* posicion en x de la wall */
        double xPosition = boxWidth/2;
        final double v = p.getVx();
        final double r = p.getX();

        if(v == 0) {
            return Double.POSITIVE_INFINITY;
        }

    /*
      The idea is:

      If vx < 0:
        calculate tc ONLY IF particle is on the right side (crash the middle wall from the right)
      If vx > 0
        calculate tc ONLY IF particle is on the left side (crash the middle wall from the left)
      if any of this conditions is match, return Double.POSITIVE_INFINITY to represent that, with the current
      system's conditions, it is not possible that the particle hits a middle wall.

      Supposing we got a non infinite tc, we proceed to check the new y position.
      Notice that we got the tc that matches the condition where the center of the particle/point is at distance
      R from the middle wall.

      For simplification purpose, we will consider the particle's MBB (Minimum Bounding Box) instead of the
      particle itself. This simplification is due to the fact that, if not, we should resolve a quadratic
      equation with the time as the unknown variable, and the complexity of this is out of our scope.
      For more information about how to get the exact distance of the particle to a certain figure, check
      the following link: http://www.learnopengl.com/#!In-Practice/2D-Game/Collisions/Collision-Detection

      OK, back on our argument, we should check if the newY +- the particle's radio will collide with a
      middle wall or it is able to pass across the opening. And that's exactly what we do.
      If collision is detected, the tc return is the previously found; if not, tc is set to POSITIVE_INFINITE, as
      there won't be any collision for this particle with any middle wall.

      With all this in mind, we go on to implement the above's logic.
     */

        // Check that if particle is travelling left, it's currently positioned to the right of the wall. (Same if its going left)
        if(v < 0) {
            if( (r-p.getRadius()) > xPosition){ // r+radio is used to make sure the particle isn't in contact with the wall
                tc = (xPosition + p.getRadius() - r) / v;
            } else{
                return Double.POSITIVE_INFINITY;
            }
        } else{
            if( (r+p.getRadius()) < xPosition){
                tc = (xPosition - p.getRadius() - r) / v;
            } else{
                return Double.POSITIVE_INFINITY;
            }
        }

        // check what happens at the new y position
        newY = p.getY() + p.getVy() * tc;
        openingUpperLimit = (boxHeight/2) + opening/2;
        openingLowerLimit = (boxHeight/2) - opening/2;

        if (     newY-p.getRadius() > openingLowerLimit
                && newY+p.getRadius() < openingUpperLimit){
            // If the MBB of the particle is travelling towards the opening, then there's no collision,
            // and Infinity is returned
            tc = Double.POSITIVE_INFINITY;
        }

        return tc;
    }


    private static void evolveCrashedParticles(Particle p1, Particle p2, boolean verticalCollision, boolean horizontalCollision, boolean middleWallCollision) {

        if (verticalCollision || middleWallCollision) {
            p1.setVx(-p1.getVx());
            return;
        }

        if (horizontalCollision) {
            p1.setVy(-p1.getVy());
            return;
        }


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


    public List<Particle> addParticles() {

        /* Add small particles */
        for (int i = 0; i < numberOfParticles; i++){

            double x;
            double y;

            do {
                x = smallRadius + (boxWidth/2 - 2 * smallRadius) * Math.random();
                y = smallRadius + (boxWidth/2 - 2 * smallRadius) * Math.random();
            }
            while (isSuperimposed(x,y, particles));

            double vx;
            double vy;

            vx = randomSpeed();
            vy = randomSpeed();

//            vx = 2 * smallVelocity * Math.random() - smallVelocity;
//            vy = 2 * smallVelocity * Math.random() - smallVelocity;

            particles.add(new Particle(i, x, y, vx, vy, smallMass, smallRadius));
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

        while (t < time) {
            double tc = Double.POSITIVE_INFINITY;
            Particle crashed1 = null;
            Particle crashed2 = null;
            boolean verticalCollision = false;
            boolean horizontalCollision = false;
            boolean middleWallCollision = false;

            for (Particle p1 : particles) {
                /* Checking wall collision */
                double verticalTc = timeUntilCrashWithVerticalWall(p1);
                double horizontalTc = timeUntilCrashWithHorizontalWall(p1);
                double middleTc = timeUntilCrashWithMiddleWall(p1, openingSize);

                if (verticalTc < tc) {
                    tc = verticalTc;
                    verticalCollision = true;
                    horizontalCollision = false;
                    middleWallCollision = false;
                    crashed1 = p1;
                    crashed2 = null;
                }

                if (horizontalTc < tc) {
                    tc = horizontalTc;
                    verticalCollision = false;
                    horizontalCollision = true;
                    middleWallCollision = false;
                    crashed1 = p1;
                    crashed2 = null;
                }

                if (middleTc < tc) {
                    tc = middleTc;
                    verticalCollision = false;
                    horizontalCollision = false;
                    middleWallCollision = true;
                    crashed1 = p1;
                    crashed2 = null;
                    System.out.println("MIDDLE MOTHER FCKR :" + collisionCount);
                }

                /* Checking particle collision */
                for (Particle p2 : particles) {
                    if (!p1.equals(p2.getId())) {

                        double ptc = timeUntilCrashWithAnotherParticle(p1, p2);
                        if (ptc < tc) {
                            tc = ptc;
                            verticalCollision = false;
                            horizontalCollision = false;
                            middleWallCollision = false;
                            crashed1 = p1;
                            crashed2 = p2;
                        }
                    }
                }
            }

            updatePositions(particles, tc);

            evolveCrashedParticles(crashed1, crashed2, verticalCollision, horizontalCollision, middleWallCollision);

            t += tc;

            String toWrite = generateFileString(particles);
//            System.out.println(toWrite);
            Engine.writeToFile(toWrite,collisionCount++, path);
        }
    }

    public static void writeToFile(String data, int index, String path){
        try {
            Files.write(Paths.get(path  + "/result" + index + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String generateFileString(List<Particle> particles) {
        StringBuilder builder = new StringBuilder()
                .append(particles.size())
                .append("\r\n")
                .append("//ID\t X\t Y\t Radius\t\r\n");
        for(Particle current: particles) {
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




}
