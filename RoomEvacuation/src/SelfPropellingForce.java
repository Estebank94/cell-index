import java.util.function.Function;

/**
 * Created by estebankramer on 06/06/2019.
 */
public class SelfPropellingForce {

    private final double tau;

    public SelfPropellingForce(double tau) {
        this.tau = tau;
    }

    public Vector2D calculate(Particle p, Function<Particle,Vector2D> position, Function<Particle,Vector2D> speed) {
        //TODO check desiredSpeed calculation
        Vector2D desiredSpeed = p.getTarget().subtract(position.apply(p)).versor().multiplyBy(p.getDesiredSpeed());

        Vector2D force = desiredSpeed.subtract(speed.apply(p)).dividedBy(tau).multiplyBy(p.getMass());


        return force;
    }
}
