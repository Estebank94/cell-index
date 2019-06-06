/**
 * Created by estebankramer on 06/06/2019.
 */
public class SelfPropellingForce {

    private final double tau;

    public SelfPropellingForce(double tau) {
        this.tau = tau;
    }

    public Vector2D calculate(Particle p){
        Vector2D desiredSpeed = p.getTarget().subtract(p.getPosition()).versor().multiplyBy(p.getDesiredSpeed());
        Vector2D force = desiredSpeed.subtract(p.getSpeed()).dividedBy(tau).multiplyBy(p.getMass());
        return force;
    }
}
