import java.util.*;

public class Beeman {

    private ForceCalculator forceCalculator;
    private double dt;
    private NeighbourCalculator neighbourCalculator;
    private Map<Particle, Set<Particle>> neighbours;

    public Beeman(ForceCalculator forceCalculator, NeighbourCalculator neighbourCalculator, double dt, Set<Particle> particles) {
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

        calculateAcceleration(particles);

        calculateNextPosition(particles);

        calculateNextSpeedPredicted(particles);

        calculateNextAcceleration(particles);

        calculateNextSpeedCorrected(particles);

        return getUpdatedParticles(particles);
    }

    private void calculateAcceleration(Set<Particle> particles) {
        for (Particle p : particles) {
            Vector2D acceleration = forceCalculator.calculate(p, neighbours.get(p))
                    .dividedBy(p.getMass());
            p.setAcceleration(acceleration);
        }
    }

    private void calculateNextPosition(Set<Particle> particles) {
        for (Particle p : particles) {
            Vector2D pos = p.getPosition();
            Vector2D sp = p.getSpeed();
            Vector2D ac = p.getAcceleration();
            Vector2D prAc = p.getPreviousAcceleration();

            double nextPx = pos.getX() + sp.getX() * dt + 2.0 / 3.0 * ac.getX() * dt * dt - 1.0 / 6.0 * prAc.getX() * dt * dt;
            double nextPy = pos.getY() + sp.getY() * dt + 2.0 / 3.0 * ac.getY() * dt * dt - 1.0 / 6.0 * prAc.getY() * dt * dt;

            p.setNextPosition(new Vector2D(nextPx, nextPy));
        }
    }

    private void calculateNextSpeedPredicted(Set<Particle> particles) {
        for (Particle p : particles) {
            Vector2D sp = p.getSpeed();
            Vector2D ac = p.getAcceleration();
            Vector2D prAc = p.getPreviousAcceleration();

            double nextVx = sp.getX() + 3.0 / 2.0 * ac.getX() * dt - 1.0 / 2.0 * prAc.getX() * dt;
            double nextVy = sp.getY() + 3.0 / 2.0 * ac.getY() * dt - 1.0 / 2.0 * prAc.getY() * dt;


            p.setNextSpeedPredicted(new Vector2D(nextVx, nextVy));
        }

    }

    private void calculateNextAcceleration(Set<Particle> particles) {

        neighbours = neighbourCalculator.getNeighbours(particles);
        for (Particle p : particles) {
            Vector2D acceleration = forceCalculator.calculate(p, neighbours.get(p))
                    .dividedBy(p.getMass());
            p.setNextAcceleration(acceleration);
        }
    }

    private void calculateNextSpeedCorrected(Set<Particle> particles) {
        for (Particle p : particles) {
            Vector2D sp = p.getSpeed();
            Vector2D ac = p.getAcceleration();
            Vector2D prAc = p.getPreviousAcceleration();
            Vector2D neAc = p.getNextAcceleration();

            double nextVx = sp.getX() + 1.0 / 3.0 * neAc.getX() * dt + 5.0 / 6.0 * ac.getX() * dt - 1.0 / 6.0 * prAc.getX() * dt;
            double nextVy = sp.getY() + 1.0 / 3.0 * neAc.getY() * dt + 5.0 / 6.0 * ac.getY() * dt - 1.0 / 6.0 * prAc.getY() * dt;

            p.setNextSpeedCorrected(new Vector2D(nextVx, nextVy));
        }

    }

    private Set<Particle> getUpdatedParticles(Set<Particle> particles) {
        Set<Particle> updatedParticles = new HashSet<>();

        for (Particle p : particles) {
            Particle newP = new Particle (p, p.getNextPosition(), p.getNextSpeedCorrected(), p.getAcceleration());
            newP.setTotalFn(p.getTotalFn());
            updatedParticles.add(newP);

        }
        return updatedParticles;
    }


}