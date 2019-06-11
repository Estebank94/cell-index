import java.util.function.Function;

public class DrivingForce {

    private final double tau;

    public DrivingForce(double tau) {
        this.tau = tau;
    }

    public Vector2D calculate(Particle p, Function<Particle,Vector2D> position, Function<Particle,Vector2D> speed) {
        //TODO check desiredSpeed calculation
        Vector2D desiredSpeed = p.getTarget().subtract(position.apply(p)).versor().multiplyBy(p.getDesiredSpeed());

        Vector2D force = desiredSpeed.subtract(speed.apply(p)).dividedBy(tau).multiplyBy(p.getMass());


        return force;
    }
}
