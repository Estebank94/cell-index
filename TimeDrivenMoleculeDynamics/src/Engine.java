import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Engine{

    /* Particles */
    private static double radius = 1;
    private static double mass = 0.1;
    private static double velocity = 10;
    private static double openingSize = 10;

    private static double boxWidth = 400;
    private static double boxHeight = 200;

    private double numberOfParticles;
    private double time;


    private double K = 0; /* Kinetic energy */
    private double U = 0; /* Potential energy */

    /* Constants */
    private static int epsilon = 2; /* Units Joules */
    private static int rm = 1; /* Units Joules */
    private static int forceCutDistance = 5;
    private static double dt = 1;
    private static double deltaT = Math.pow(10, -5);
    private static double tf = 5;

    private List<Particle> particles;
    private List<Point> previousPositions;

    public Engine(double numberOfParticles, double time) {
        this.numberOfParticles = numberOfParticles;
        this.time = time;
        particles = new ArrayList<>();
        previousPositions = new ArrayList<>();
        addParticles();
        addPreviousPositions();
    }

//    |
//    |      .
//    |
//
//    VERTICAL WALL

    private static PointAndDistance distanceToVerticalWall(Particle p) {
        double xPosition = p.getX() + p.getRadius();
        if(xPosition <= forceCutDistance){
            /* pared vertical izquierda */
            return new PointAndDistance(new Point(0,p.getY()), xPosition);
        } else if(boxWidth - xPosition <= forceCutDistance){
            /* pared vertical derecha */
            return new PointAndDistance(new Point(boxWidth,p.getY()), boxWidth - xPosition);
        }
        /* no estoy a la distancia adecuada */
        return new PointAndDistance(new Point(0,0), Double.POSITIVE_INFINITY);
    }

//    _________
//        .
//
//    HORIZONTAL WALL

    private static PointAndDistance distanceToHorizontalWall(Particle p) {
        double yPosition = p.getY() + p.getRadius();
        if ( yPosition <= forceCutDistance ) {
            /* pared horizontal de abajo */
            return new PointAndDistance(new Point(p.getX(),0), yPosition);
        } else if ( boxHeight - yPosition <= forceCutDistance) {
            /* pared horizontal de arriba */
            return new PointAndDistance(new Point(p.getX(),boxHeight), boxHeight - yPosition);
        }
        /* no estoy a la distancia adecuada */
        return new PointAndDistance(new Point(0,0), Double.POSITIVE_INFINITY);
    }


//         |
//         |
//         |
//     .
//         |
//         |
//         |
//
//    MIDDLE WALL
    private static PointAndDistance distanceToMiddleWall(Particle p) {
        double xPosition = p.getX() + p.getRadius();
        double yPosition = p.getY() + p.getRadius();

        double xDistanceToMiddleWall = Math.abs(boxWidth/2 - xPosition);
        double distance = Double.POSITIVE_INFINITY;
        double aux1, aux2;

        /* si en y estoy a la altura del aujero */
        if(yPosition >= boxHeight/2 - openingSize /2 || yPosition <= (boxHeight - boxHeight/2 + openingSize/2)){

            double yDistanceToBottomWall = Math.abs(yPosition - boxHeight/2 - openingSize /2);
            aux1 = Math.sqrt(yDistanceToBottomWall * yDistanceToBottomWall + xDistanceToMiddleWall * xDistanceToMiddleWall);
            if(aux1 <= forceCutDistance) {
                distance = aux1;
            }

            double yDistanceToTopWall = Math.abs((boxHeight - boxHeight/2 + openingSize/2) - yPosition);
            aux2 = Math.sqrt(yDistanceToTopWall * yDistanceToTopWall + xDistanceToMiddleWall * xDistanceToMiddleWall);
            if(aux2 <= forceCutDistance && aux2 < distance) {
                distance = aux2;
            }
        } else if(xDistanceToMiddleWall <= forceCutDistance) { /* si estoy en x estoy en el rango y en y no estoy en el aujero*/
            distance = xDistanceToMiddleWall;
        }
        return new PointAndDistance(new Point(p.getX(), p.getY()), distance);
    }


    public List<Particle> addParticles() {

        /* Add small particles */
        for (int i = 0; i < numberOfParticles; i++){

            double x;
            double y;

            do {
                x = radius + (boxWidth/2 - 2 * radius) * Math.random();
                y = radius + (boxHeight - 2 * radius) * Math.random();
            }
            while (isSuperimposed(x,y, particles));

            Random r = new Random();
            int rangeMin = 0;
            int rangeMax = 0;
            double angle = rangeMin + (rangeMax - rangeMin) * r.nextDouble();

            double vx = velocity * Math.cos(angle);
            double vy = velocity * Math.sin(angle);

            particles.add(new Particle(i, x, y, vx, vy, mass, radius));
        }

        return particles;
    }

    private static boolean isSuperimposed(double x, double y, List<Particle> particles) {

        for (Particle p: particles){
            boolean superImposed = Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2) <= Math.pow(p.getRadius() + radius, 2);
            if (superImposed){
                return true;
            }
        }
        return false;
    }

    public void start(String path) {
        int t = 0;
        double particleDistance;
        PointAndDistance distanceToHorizontalWall, distanceToVerticalWall, distanceToMiddleWall;
        int count = 0;

        while (t < time) {
            System.out.println(t);
            for(Particle p1 : particles) {
                for(Particle p2 : particles) {
                    if(!p1.equals(p2)){
                        particleDistance = Particle.borderDistanceBetweenParticles(p1,p2);
                        if(particleDistance <= forceCutDistance) {
                            setParticleForce(p1, particleDistance, p2.getX(), p2.getY());
                        }
                    }
                }

                distanceToHorizontalWall = distanceToHorizontalWall(p1);
                if(distanceToHorizontalWall.getDistance() != Double.POSITIVE_INFINITY){
                    setParticleForce(p1, distanceToHorizontalWall.getDistance(), distanceToHorizontalWall.getPoint().getX(), distanceToHorizontalWall.getPoint().getY());
                }

                distanceToVerticalWall = distanceToVerticalWall(p1);
                if(distanceToVerticalWall.getDistance() != Double.POSITIVE_INFINITY){
                    setParticleForce(p1, distanceToVerticalWall.getDistance(), distanceToVerticalWall.getPoint().getX(), distanceToVerticalWall.getPoint().getY());
                }

                distanceToMiddleWall = distanceToMiddleWall(p1);
                if(distanceToMiddleWall.getDistance() != Double.POSITIVE_INFINITY){
                    setParticleForce(p1, distanceToMiddleWall.getDistance(), distanceToMiddleWall.getPoint().getX(), distanceToMiddleWall.getPoint().getY());
                }

                for(Point prevPos : previousPositions){
                    startVerlet(p1, prevPos);
                }
            }

            t += dt;
            String toWrite = generateFileString(particles);
//            System.out.println(toWrite);
            Engine.writeToFile(toWrite,count++, path);
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
                .append("//ID\t X\t Y\t Radius\t\r\n")
                .append("-1 0 0 1\r\n")
                .append("-1 0 200 1\r\n")
                .append("-1 400 200 1\r\n")
                .append("-1 400 0 1\r\n")
                .append(addMiddleWallInGraph());
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

    public static String addMiddleWallInGraph(){
        StringBuilder builder = new StringBuilder();
        for(int y = 0; y < boxHeight;){
            if(y <= boxHeight/2 - openingSize /2 || y >= (boxHeight - boxHeight/2 + openingSize/2)){
                builder.append("-1 " + boxWidth/2 + " " + y + " 1\r\n");
            }
            y+=4;
        }
        return builder.toString();
    }

    public void setParticleForce(Particle p, double distance, double x2, double y2){
        double force = calculateLJForce(distance);
        Point e = calculatePolarity(p, x2, y2);
        double fx = force * e.getX();
        double fy = force * e.getY();
        p.setForce(p.getForce() + force);
        p.setFx(p.getFx() + fx);
        p.setFy(p.getFy() + fy);
    }



    public double calculateLJPotential(double distanceP1P2) {
        return epsilon * (Math.pow(rm/distanceP1P2,12) - 2 * Math.pow(rm/distanceP1P2, 6));
    }

    public double calculateLJForce(double distanceP1P2) {
        return ( 12 * epsilon / rm) * (Math.pow(rm/distanceP1P2,13) - Math.pow(rm/distanceP1P2, 7));
    }

    public Point calculatePolarity(Particle p1, double x, double y){
        double ex = (x - p1.getX())/ Math.sqrt((p1.getX() - x) * (p1.getX() - x) + (p1.getY() - y) * (p1.getY() - y));
        double ey = (y - p1.getY())/ Math.sqrt((p1.getX() - x) * (p1.getX() - x) + (p1.getY() - y) * (p1.getY() - y));

        return new Point(ex, ey);
    }

    /* VERLET */

    public void startVerlet(Particle particle, Point previousPosition){
        double time = 0;
        int iteration = 0;


        while(time <= tf){
            previousPosition = verlet(previousPosition, particle);
            iteration ++;
            time += deltaT;

        }
        System.out.println("Time: " + (time-deltaT) + " Position: { x = " + particle.getX() + " y=" + particle.getY() + " }");
    }

    private Point firstVerlet(Particle particle){
        return getPreviousPositionWithEuler(particle);
    }

    private Point verlet(Point previousPosition, Particle particle){
        double rx = particle.getX();
        double ry = particle.getY();
        double newX = (2*rx) - previousPosition.getX() + ((Math.pow(deltaT,2)*particle.getFx())/mass);
        double newY = (2*ry) - previousPosition.getY() + ((Math.pow(deltaT,2)*particle.getFy())/mass);
//        double newVx = (newX - previousPosition.getX())/(2*deltaT);
//        double newVy = (newY - previousPosition.getY())/(2*deltaT);
        particle.setX(newX);
        particle.setY(newY);
//        particle.setVx(newVx);
//        particle.setVy(newVy);
        return new Point(rx, ry);
    }

    private Point getPreviousPositionWithEuler(Particle p){

        double posX = p.getX() - deltaT * p.getVx();
        double posY = p.getY() - deltaT * p.getVy();
        posX -= Math.pow(deltaT, 2) * p.getFx() / (2 * mass);
        posY -= Math.pow(deltaT, 2) * p.getFy() / (2 * mass);
        return new Point(posX, posY);

    }

    public void addPreviousPositions() {
        for (Particle p : particles) {
            previousPositions.add(firstVerlet(p));
        }
    }


}
