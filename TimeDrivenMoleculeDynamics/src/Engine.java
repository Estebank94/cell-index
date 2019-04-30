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
    private static double epsilon = 2; /* Units Joules */
    private static double rm = 1; /* Units Joules */
    private static double forceCutDistance = 5;
    private static double deltaT = Math.pow(10, -5);
    private static double tf = 1;

    private List<Particle> particles;

    public Engine(double numberOfParticles, double time) {
        this.numberOfParticles = numberOfParticles;
        this.time = time;
        particles = new ArrayList<>();
        addParticles();
//        addPreviousPositionsWithEuler();
    }

//
//    public List<Particle> addParticles() {
//
//        /* Add small particles */
//        for (int i = 0; i < numberOfParticles; i++){
//
//            double x;
//            double y;
//
//            do {
//                x = radius + (boxWidth/2 - 2 * radius) * Math.random();
//                y = radius + (boxHeight - 2 * radius) * Math.random();
//            }
//            while (isSuperimposed(x,y, particles));
//
//            Random r = new Random();
//            int rangeMin = 0;
//            int rangeMax = 0;
//            double angle = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
//
//            double vx = velocity * Math.cos(angle);
//            double vy = velocity * Math.sin(angle);
//
//            particles.add(new Particle(i, x, y, 0,0, vx, vy, mass, radius));
//        }
//
//
//        return particles;
//    }

    public List<Particle> addParticles() {

        /* Add small particles */
//        for (int i = 0; i < numberOfParticles; i++){

            double x;
            double y;

//            do {
//                x = radius + (boxWidth/2 - 2 * radius) * Math.random();
//                y = radius + (boxHeight - 2 * radius) * Math.random();
//            }
//            while (isSuperimposed(x,y, particles));
//
//            Random r = new Random();
//            int rangeMin = 0;
//            int rangeMax = 0;
//            double angle = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
//
//            double vx = velocity * Math.cos(angle);
//            double vy = velocity * Math.sin(angle);

            particles.add(new Particle(0, 100, 50, 0,0, 0, 0, mass, radius));
            particles.add(new Particle(1, 100 + 4, 50, 0,0, 0, 0, mass, radius));
//        }


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
        double t = 0;
        double particleDistance;
        int count = 0;
        int index = 0;

        while (t < time) {
            /* me fijo particula por particula  que particulas estan en mi radio de accion para calcular
            * la fuerza entre mi particula y las otras particulas en el radio*/
            for(Particle p1 : particles) {
                for(Particle p2 : particles) {
                    if(!p1.equals(p2)){
                        particleDistance = Particle.borderDistanceBetweenParticles(p1,p2);
                        if(particleDistance <= forceCutDistance) {
//                            System.out.println("Particula");
                            setParticleForce(p1, particleDistance, p2.getX(), p2.getY());
                        }
                    }
                }


                //    |
                //    |      .
                //    |
                //
                //    VERTICAL WALL
                /* trato a la pared como una particula */
                Particle leftWall = new Particle(0, p1.getY(), radius);
                particleDistance = Particle.borderDistanceBetweenParticles(p1,leftWall);
                if(particleDistance <= forceCutDistance){
//                    System.out.println("Pared horizontal izquierda");
                    setParticleForce(p1, particleDistance, leftWall.getX(),leftWall.getY());

                }

                Particle rightWall = new Particle(boxWidth, p1.getY(), radius);
                particleDistance = Particle.borderDistanceBetweenParticles(p1,rightWall);
                if(particleDistance <= forceCutDistance){
//                    System.out.println("Pared horizontal derecha");
                    setParticleForce(p1, particleDistance, rightWall.getX(),rightWall.getY());

                }

                //    _________
                //        .
                //
                //    HORIZONTAL WALL

                Particle bottomWall = new Particle(p1.getX(), 0, radius);
                particleDistance = Particle.borderDistanceBetweenParticles(p1,bottomWall);
                if(particleDistance <= forceCutDistance){
//                    System.out.println("Pared vertical de abajo");
                    setParticleForce(p1, particleDistance, bottomWall.getX(),bottomWall.getY());

                }

                Particle topWall = new Particle(p1.getX(), boxHeight, radius);
                particleDistance = Particle.borderDistanceBetweenParticles(p1,topWall);
                if(particleDistance <= forceCutDistance){
//                    System.out.println("Pared vertical de arriba");
                    setParticleForce(p1, particleDistance, topWall.getX(), topWall.getY());
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

                double y;
                /* si estoy a la altura del aujero de la pared de la mitad */
                if(p1.getY() < boxHeight - boxHeight/2 - openingSize/2 && p1.getY() > boxHeight/2 - openingSize/2) {
                    double dist1 = Math.abs(p1.getY() - boxHeight - boxHeight/2 - openingSize/2);
                    double dist2 = Math.abs(p1.getY() - boxHeight/2 - openingSize/2);
                    y = dist1 < dist2 ? dist1 : dist2;
                } else {
                    y = p1.getY();
                }
                Particle middleWall = new Particle(boxWidth/2, y, radius);
                particleDistance = Particle.borderDistanceBetweenParticles(p1,middleWall);
                if(particleDistance <= forceCutDistance){
//                    System.out.println("Pared del medio particle { x = " + p1.getX() + " " + p1.getY() + " } wall : { x = "+ middleWall.getX() + " y= "+ middleWall.getY());
                    setParticleForce(p1, particleDistance, middleWall.getX(),middleWall.getY());

                }


                /* si todavia no tengo la posicion anterior es que no use euler */
                if(p1.getPrevX() == 0){
                    setPreviousPositionWithEuler(p1);
                } else {
                    verlet(p1);
                }


                Particle p = p1;
                if(p.getFx() == Double.NaN) {
                    System.out.println("hola");
                }
                if(p.getId() == 0)
                    System.out.println("PARTICLE " + p.getId() + ", X = " + p.getX() +", Fx = " + p.getFx() + ", Fy = " + p.getFy());

//                System.out.println("CUENTA: " + count++);
//                System.out.println();
//                System.out.println();
//                System.out.println("Time = "+ t + "| Count = "+ count +"| Particle { id = "+ p1.getId() + ", x = " + p1.getX() + ", y = " + p1.getY() + " }");
            }

            t += deltaT;
            String toWrite = generateFileString(particles);

//            System.out.println(toWrite);
            if(count == 0 || count % 1000 == 0){
                Engine.writeToFile(toWrite,index++, path);
            }
            count++;
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
//                .append("-1 0 0 1\r\n")
//                .append("-1 0 200 1\r\n")
//                .append("-1 400 200 1\r\n")
//                .append("-1 400 0 1\r\n")
//                .append(addMiddleWallInGraph());
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
        p.setFx(p.getFx() + fx);
        p.setFy(p.getFy() + fy);

        if( p.getFx() == Double.NaN || fx == Double.NaN || fy == Double.NaN){
            System.out.println("mamaaaa");
        }

//        if(p.getId() == 0)
//            System.out.println("PARTICLE " + p.getId() + ", X = " + p.getX() + ", DISTANCE = " + distance + ", F = " + force +", Fx = " + fx + ", Fy = " + fy);
    }



    public double calculateLJPotential(double distanceP1P2) {
        return epsilon * (Math.pow(rm/distanceP1P2,12) - 2 * Math.pow(rm/distanceP1P2, 6));
    }

    public double calculateLJForce(double distanceP1P2) {
        return ( 12 * epsilon / rm) * (Math.pow(rm/distanceP1P2,13) - Math.pow(rm/distanceP1P2, 7));
    }

    public Point calculatePolarity(Particle p1, double x, double y){
        double ex = (p1.getX() - x)/ Math.sqrt(Math.pow((p1.getX() + x),2) - (Math.pow((p1.getY() + y),2)));
        double ey = (p1.getY() - y)/ Math.sqrt(Math.pow((p1.getX() + x),2) - (Math.pow((p1.getY() + y),2)));

        return new Point(ex, ey);
    }

    /* VERLET */


    private void verlet(Particle particle){
        double rx = particle.getX();
        double ry = particle.getY();
        double newX = (2*rx) - particle.getPrevX() + ((Math.pow(deltaT,2)*particle.getFx())/mass);
        double newY = (2*ry) - particle.getPrevY() + ((Math.pow(deltaT,2)*particle.getFy())/mass);
        if( newX == Double.NaN || newY == Double.NaN ){
            System.out.println("mierda");
        }
        particle.setX(newX);
        particle.setY(newY);
        particle.setPrevX(rx);
        particle.setPrevY(ry);
    }

    private void setPreviousPositionWithEuler(Particle p){

        double posX = p.getX() - deltaT * p.getVx();
        double posY = p.getY() - deltaT * p.getVy();
        posX += Math.pow(deltaT, 2) * p.getFx() / (2 * mass);
        posY += Math.pow(deltaT, 2) * p.getFy() / (2 * mass);
        p.setPrevX(posX);
        p.setPrevY(posY);
//        de aca no salen los negativos, chequeado

    }


}
