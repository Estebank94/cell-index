import java.util.*;

public class GearPredictor {

    private ForceCalculator forceCalculator;
    private double dt;
    private NeighbourCalculator neighbourCalculator;
    private Map<Particle, Set<Particle>> neighbours;

    public GearPredictor(ForceCalculator forceCalculator, NeighbourCalculator neighbourCalculator, double dt, Set<Particle> particles) {
        this.forceCalculator = forceCalculator;
        this.dt = dt;
        this.neighbourCalculator = neighbourCalculator;

        /* Initialize neighbours */
        neighbours = new HashMap<>();
        for(Particle p : particles){
            neighbours.put(p, Collections.emptySet());
        }
    }

    /**
     * Uses Beeman formulas seen in class
     * @param particles
     * @return a set of the particles with its information: speed, location & acceleration updated
     */
    public Set<Particle> integrate(Set<Particle> particles) {

//        calculateNextAcceleration(particles);

        neighbours = neighbourCalculator.getNeighbours(particles);

//        calculateAcceleration(particles);

        updateParticles(particles);

        return getUpdatedParticles(particles);
    }


    public void updateParticles(Set<Particle> particles) {
        double kn = ForceCalculator.getKn();
        double gamma = ForceCalculator.getGama();

        for(Particle p1 : particles) {
            Vector2D r0 = p1.getPosition();
            Vector2D r1 = p1.getSpeed();
            Vector2D force = forceCalculator.calculate(p1, neighbours.get(p1));
            Vector2D r2 = force.dividedBy(p1.getMass());
            Vector2D r3 = (r1.multipledBy(kn).add(r2.multipledBy(gamma))).dividedBy(p1.getMass());
            Vector2D r4 = (r2.multipledBy(kn).add(r3.multipledBy(gamma))).dividedBy(p1.getMass());
            Vector2D r5 = (r3.multipledBy(kn).add(r4.multipledBy(gamma))).dividedBy(p1.getMass());

            Vector2D rp0 = r0.add(r1.multipledBy(dt)).add(r2.multipledBy((dt*dt)/2)).add(r3.multipledBy((dt*dt*dt)/6)).add(r4.multipledBy((dt*dt*dt*dt)/24)).add(r5.multipledBy((dt*dt*dt*dt*dt)/120));
            Vector2D rp1 = r1.add(r2.multipledBy(dt)).add(r3.multipledBy((dt*dt)/2)).add(r4.multipledBy((dt*dt*dt)/6)).add(r5.multipledBy((dt*dt*dt*dt)/24));
            Vector2D rp2 = r2.add(r3.multipledBy(dt)).add(r4.multipledBy((dt*dt)/2)).add(r5.multipledBy((dt*dt*dt)/6));

            Particle aux = new Particle(p1, rp0, rp1);
            Vector2D force1 = forceCalculator.calculate(aux, neighbours.get(p1));
            Vector2D nextAcceleration = force1.dividedBy(aux.getMass());

            Vector2D deltaA = nextAcceleration.subtract(rp2);
            Vector2D delta2 = deltaA.multipledBy((dt*dt)/2);

            Vector2D rc0 = rp0.add(delta2.multipledBy(3.0/16));
            Vector2D rc1 = rp1.add(delta2.multipledBy(251.0/(360*dt)));

            p1.setPosition(rc0);
            p1.setSpeed(rc1);
        }
    }

    private void calculateAcceleration(Set<Particle> particles) {
        for (Particle p : particles) {
            Vector2D acceleration = forceCalculator.calculate(p, neighbours.get(p))
                    .dividedBy(p.getMass());
            p.setAcceleration(acceleration);
        }
    }

    private void calculateNextAcceleration(Set<Particle> particles) {
        neighbours = neighbourCalculator.getNeighbours(particles);
        for (Particle p : particles) {
            Vector2D force = forceCalculator.calculate(p, neighbours.get(p));
            Vector2D acceleration = force.dividedBy(p.getMass());
            p.setAcceleration(acceleration);
        }
    }


    private Set<Particle> getUpdatedParticles(Set<Particle> particles) {
        Set<Particle> updatedParticles = new HashSet<>();

        for (Particle p : particles) {
            Particle newP = new Particle (p, p.getPosition(), p.getSpeed());
            updatedParticles.add(newP);
        }
        return updatedParticles;
    }


}