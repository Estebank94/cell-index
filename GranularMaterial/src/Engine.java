import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by estebankramer on 10/05/2019.
 */

public class Engine {

    /* Silo dimensions */
    double W = 0.3;
    double L = 1;
    double D = 0.15;

    /* Particles */
    int maxParticles = 500;
    double minRadius = 0.02;
    double maxRadius = 0.03;
    double mass = 0.01;
    public Set<Particle> particles;

    /* Constants */
    double kN = Math.pow(10,5);
    double kT = 2;
    double gamma = 70;

    public Engine(){
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
        }
    }

    private boolean isSuperimposed(Particle p) {

        for (Particle p2: particles){
            boolean superImposed = Math.pow(p2.getX() - p.getX(), 2) + Math.pow(p2.getY() - p.getY(), 2) <= Math.pow(p.getRadius() + p2.getRadius(), 2);
            if (superImposed){
                return true;
            }
        }
        return false;
    }






}
