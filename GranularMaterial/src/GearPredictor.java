import java.util.*;

public class GearPredictor {

    private ForceCalculator forceCalculator;
    private double dt;
    private NeighbourCalculator neighbourCalculator;
    private Map<Particle, Set<Particle>> neighbours;
    private Vector2D rCorr3 = Vector2D.ZERO;
    private Vector2D rCorr4 = Vector2D.ZERO;
    private Vector2D rCorr5 = Vector2D.ZERO;


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
    public Set<Particle> integrate(Set<Particle> particles, int i) {

//        calculateNextAcceleration(particles);

        neighbours = neighbourCalculator.getNeighbours(particles);

//        calculateAcceleration(particles);

        updateParticles(particles, i);

        return getUpdatedParticles(particles);
    }


    public void updateParticles(Set<Particle> particles, int i) {
        double kn = ForceCalculator.getKn();
        double gamma = ForceCalculator.getGama();

        for(Particle p1 : particles) {
            Vector2D r0 = p1.getPosition();
            Vector2D r1 = p1.getSpeed();
            Vector2D force = forceCalculator.calculate(p1, neighbours.get(p1));
            Vector2D r2 = force.dividedBy(p1.getMass());
            Vector2D r3;
            Vector2D r4;
            Vector2D r5;
            if(rCorr3.equals(Vector2D.ZERO)){
                r3 = Vector2D.ZERO;
                r4 = Vector2D.ZERO;
                r5 = Vector2D.ZERO;

            } else {
                r3 = rCorr3;
                r4 = rCorr4;
                r5 = rCorr5;
            }



            Vector2D rp0 = r0.add(r1.multipledBy(dt)).add(r2.multipledBy((dt*dt)/2)).add(r3.multipledBy((dt*dt*dt)/6)).add(r4.multipledBy((dt*dt*dt*dt)/24)).add(r5.multipledBy((dt*dt*dt*dt*dt)/120));
            Vector2D rp1 = r1.add(r2.multipledBy(dt)).add(r3.multipledBy((dt*dt)/2)).add(r4.multipledBy((dt*dt*dt)/6)).add(r5.multipledBy((dt*dt*dt*dt)/24));
            Vector2D rp2 = r2.add(r3.multipledBy(dt)).add(r4.multipledBy((dt*dt)/2)).add(r5.multipledBy((dt*dt*dt)/6));
            Vector2D rp3 = r3.add(r4.multipledBy(dt)).add(r5.multipledBy((dt*dt)/2));
            Vector2D rp4 = r4.add(r5.multipledBy(dt));
            Vector2D rp5 = r5;



            Particle aux = new Particle(p1, rp0, rp1);
            Vector2D force1 = forceCalculator.calculate(aux, neighbours.get(p1));
            Vector2D nextAcceleration = force1.dividedBy(aux.getMass());

            Vector2D deltaA = nextAcceleration.subtract(rp2);
            Vector2D delta2 = deltaA.multipledBy((dt*dt)/2);

            Vector2D rc0 = rp0.add(delta2.multipledBy(3.0/16));
            Vector2D rc1 = rp1.add(delta2.multipledBy(251.0/(360*dt)));
            Vector2D rc2 = rp2.add(delta2.multipledBy(2.0/(dt*dt)));
            Vector2D rc3 = rp3.add(delta2.multipledBy((11.0 * 6)/(18*dt*dt*dt)));
            Vector2D rc4 = rp4.add(delta2.multipledBy((1.0 * 24)/(6*dt*dt*dt*dt)));
            Vector2D rc5 = rp5.add(delta2.multipledBy((1.0 * 120)/(60*dt*dt*dt*dt*dt)));

            rCorr3 = rc3;
            rCorr4 = rc4;
            rCorr5 = rc5;

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