import java.util.*;

public class Verlet {

    private ForceCalculator forceCalculator;
    private double deltaT;
    private NeighbourCalculator neighbourCalculator;
    private Map<Particle, Set<Particle>> neighbours;

    public Verlet(ForceCalculator forceCalculator, NeighbourCalculator neighbourCalculator, double deltaT, Set<Particle> particles) {
        this.forceCalculator = forceCalculator;
        this.deltaT = deltaT;
        this.neighbourCalculator = neighbourCalculator;

        /* Initialize neighbours */
        neighbours = new HashMap<>();
        for(Particle p : particles){
            neighbours.put(p, Collections.emptySet());
        }
    }

    public Set<Particle> integrate(Set<Particle> particles, boolean first) {
        calculateAcceleration(particles);

        if(first) {
            calculateNextPositionEuler(particles);
            calculateNextVelocityEuler(particles);
        } else {
            calculateNextPosition(particles);
            calculateNextVelocity(particles);
        }

        return getUpdatedParticles(particles);
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
            p.setNextAcceleration(acceleration);
        }
    }

    private void calculateNextPosition(Set<Particle> particles) {
        for (Particle p : particles) {
            Vector2D r = p.getPosition();
            Vector2D prevR = p.getPreviousPosition();
            Vector2D f = forceCalculator.calculate(p, neighbours.get(p));

            Vector2D nextPosition = r.multipledBy(2).subtract(prevR).add(f.multipledBy((deltaT*deltaT/p.getMass())));

            p.setNextPosition(nextPosition);
        }
    }

    private void calculateNextVelocity(Set<Particle> particles) {
        for(Particle p : particles) {
            Vector2D nextR = p.getNextPosition();
            Vector2D prevR = p.getPreviousPosition();

            Vector2D nextVelocity = nextR.subtract(prevR).dividedBy(2 * deltaT);

            p.setNextSpeedPredicted(nextVelocity);
        }
    }

    private void calculateNextPositionEuler(Set<Particle> particles) {
        for(Particle p : particles) {
            Vector2D r = p.getPosition();
            Vector2D v = p.getSpeed();
            Vector2D f = forceCalculator.calculate(p, neighbours.get(p));

            Vector2D nextPositionEuler = r.add(v.multipledBy(deltaT)).add(f.multipledBy((deltaT*deltaT)/2 * p.getMass()));
            p.setNextPosition(nextPositionEuler);
        }
    }

    private void calculateNextVelocityEuler(Set<Particle> particles) {
        for(Particle p : particles) {
            Vector2D v = p.getSpeed();
            Vector2D f = forceCalculator.calculate(p, neighbours.get(p));

            Vector2D nextVelocityEuler = v.add(f.multipledBy(deltaT/p.getMass()));
            p.setNextSpeedPredicted(nextVelocityEuler);
        }
    }

    private Set<Particle> getUpdatedParticles(Set<Particle> particles) {
        Set<Particle> updatedParticles = new HashSet<>();

        for (Particle p : particles) {
            Particle newP = new Particle (p.getNextPosition(), p.getPosition(), p.getNextSpeedPredicted(), p);
            newP.setTotalFn(p.getTotalFn());
            updatedParticles.add(newP);

        }
        return updatedParticles;
    }

}
