import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Engine {

    /* Silo dimensions */
    private double W = 0.3;
    private double L = 1;
    private double D = 0.15;
    private double time = 0;

    /* Particles */
    private int maxParticles = 500;
    private double minRadius = 0.02;
    private double maxRadius = 0.03;
    private double mass = 0.01;
    public Set<Particle> particles;

    private double deltaT;


    public Engine() {
        particles = new HashSet<>();
        addParticles();
    }

    public void addParticles(){
        while(maxParticles > 0){
            addParticle();
        }
    }

    public void addParticle() {

        Random rand = new Random();

        double radius = rand.nextDouble() * (maxRadius - minRadius) + minRadius;
        double x = rand.nextDouble() * (W - 2 * radius) + radius;
        double y = rand.nextDouble() * (L - 2 * radius) + radius;

        Particle p = new Particle(particles.size(), x, y, 0, 0, mass, radius);

        if(!isSuperimposed(p)){
            particles.add(p);
            maxParticles--;
        }
    }

    private boolean isSuperimposed(Particle p) {

        for (Particle p2: particles){
            boolean superImposed = Math.pow(p2.getX() - p.getX(), 2) + Math.pow(p2.getY() - p.getY(), 2) <= Math.pow(p.getR() + p2.getR(), 2);
            if (superImposed){
                return true;
            }
        }
        return false;
    }

    public void start (String outPath, double finalTime){

        int iterations = 0;
        while(time < finalTime && iterations < 100000){
            for(Particle p : particles){
                if(time == 0){
                    setPreviousPositionWithEuler(p);
                }else{
                    verlet(p);
                }
            }

            if(iterations % 100 == 0){
                System.out.println("Time: " + time + " Iterations: " + iterations);
            }

            time +=deltaT;
            iterations++;
        }

    }

    /* VERLET */

    private void verlet(Particle particle){
        double rx = particle.getX();
        double ry = particle.getY();
        double newX = (2*rx) - particle.getPrevX() + ((Math.pow(deltaT,2)*particle.getFx())/mass);
        double newY = (2*ry) - particle.getPrevY() + ((Math.pow(deltaT,2)*particle.getFy())/mass);
        double newVx = (newX - particle.getPrevX())/(2*deltaT);
        double newVy = (newY - particle.getPrevY())/(2*deltaT);
        particle.setPosition(new Vector2D(newX, newY));
        particle.setVelocity(new Vector2D(newVx, newVy));
        particle.setPrevPosition(new Vector2D(rx, ry));
        particle.setForce(new Vector2D(0, 0));
    }

    private void setPreviousPositionWithEuler(Particle p){

        double posX = p.getX() - deltaT * p.getVx();
        double posY = p.getY() - deltaT * p.getVy();
        posX += Math.pow(deltaT, 2) * p.getFx() / (2 * mass);
        posY += Math.pow(deltaT, 2) * p.getFy() / (2 * mass);
        p.setPrevPosition(new Vector2D(posX, posY));
    }


}
