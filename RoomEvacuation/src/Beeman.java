import java.util.*;

public class Beeman {

    private final DrivingForce selfPropellingForce;
    private final InteractionForce interactionForce;
    private final double dt;
    private NeighbourCalculator neighbourCalculator;
    private Map<Particle, Set<Particle>> neighbours;

    public Beeman(InteractionForce interactionForce,
                  DrivingForce selfPropellingForce, NeighbourCalculator neighbourCalculator,
                  double dt, Set<Particle> allparticles) {
        this.selfPropellingForce = selfPropellingForce;
        this.interactionForce = interactionForce;
        this.dt = dt;
        this.neighbourCalculator = neighbourCalculator;

        /* Intialize neighbours */
        neighbours = new HashMap<>();
        for (Particle p : allparticles) {
            neighbours.put(p, Collections.emptySet());
        }
    }


    public Set<Particle> integrate(Set<Particle> allParticles) {

        calculateAcceleration(allParticles);

        calculateNextPosition(allParticles);

        calculateNextSpeedPredicted(allParticles);

        calculateNextAcceleration(allParticles);

        calculateNextSpeedCorrected(allParticles);

        return getUpdatedParticles(allParticles);
    }

    private void calculateAcceleration(Set<Particle> allParticles) {
        for (Particle p : allParticles) {
            Vector2D force = interactionForce.calculate(p, neighbours.get(p), Particle::getPosition, Particle::getSpeed)
                    .add(selfPropellingForce.calculate(p, Particle::getPosition, Particle::getSpeed));

            Vector2D acceleration = force.dividedBy(p.getMass());
            p.setAcceleration(acceleration);
        }
    }


    private void calculateNextPosition(Set<Particle> allParticles) {
        for (Particle p : allParticles) {
            Vector2D pos = p.getPosition();
            Vector2D sp = p.getSpeed();
            Vector2D ac = p.getAcceleration();
            Vector2D prAc = p.getPreviousAcceleration();

            double nextPx = pos.getX() + sp.getX() * dt + 2.0 / 3.0 * ac.getX() * dt * dt - 1.0 / 6.0 * prAc.getX() * dt * dt;
            double nextPy = pos.getY() + sp.getY() * dt + 2.0 / 3.0 * ac.getY() * dt * dt - 1.0 / 6.0 * prAc.getY() * dt * dt;

            p.setNextPosition(new Vector2D(nextPx, nextPy));
        }
    }


    private void calculateNextSpeedPredicted(Set<Particle> allParticles) {
        for (Particle p : allParticles) {
            Vector2D sp = p.getSpeed();
            Vector2D ac = p.getAcceleration();
            Vector2D prAc = p.getPreviousAcceleration();

            double nextVx = sp.getX() + 3.0 / 2.0 * ac.getX() * dt - 1.0 / 2.0 * prAc.getX() * dt;
            double nextVy = sp.getY() + 3.0 / 2.0 * ac.getY() * dt - 1.0 / 2.0 * prAc.getY() * dt;


            p.setNextSpeedPredicted(new Vector2D(nextVx, nextVy));
        }

    }

    private void calculateNextAcceleration(Set<Particle> allParticles) {

        neighbours = neighbourCalculator.getNeighbours(allParticles);
        for (Particle p : allParticles) {

            Vector2D force = interactionForce.calculate(p, neighbours.get(p), Particle::getNextPosition, Particle::getNextSpeedPredicted)
                    .add(selfPropellingForce.calculate(p, Particle::getNextPosition, Particle::getNextSpeedPredicted));
            Vector2D acceleration = force.dividedBy(p.getMass());
            p.setNextAcceleration(acceleration);
        }


    }


    private void calculateNextSpeedCorrected(Set<Particle> allParticles) {
        for (Particle p : allParticles) {
            Vector2D sp = p.getSpeed();
            Vector2D ac = p.getAcceleration();
            Vector2D prAc = p.getPreviousAcceleration();
            Vector2D neAc = p.getNextAcceleration();

            double nextVx = sp.getX() + 1.0 / 3.0 * neAc.getX() * dt + 5.0 / 6.0 * ac.getX() * dt - 1.0 / 6.0 * prAc.getX() * dt;
            double nextVy = sp.getY() + 1.0 / 3.0 * neAc.getY() * dt + 5.0 / 6.0 * ac.getY() * dt - 1.0 / 6.0 * prAc.getY() * dt;


            p.setNextSpeedCorrected(new Vector2D(nextVx, nextVy));
        }

    }


    private Set<Particle> getUpdatedParticles(Set<Particle> allParticles) {
        Set<Particle> updatedParticles = new HashSet<>();

        for (Particle p : allParticles) {
            Particle newP = new Particle(p, p.getNextPosition(), p.getNextSpeedCorrected(), p.getAcceleration(), p.getTotalFn());
//            newP.setTotalFn(p.getTotalFn());
            updatedParticles.add(newP);

        }
        return updatedParticles;
    }
}

