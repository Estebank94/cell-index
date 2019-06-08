import java.util.function.Function;

public class SelfPropellingForce {

    private final double tau;

    public SelfPropellingForce(double tau) {
        this.tau = tau;
    }

    public Vector2D calculate(Particle p) {

        //TODO check desiredSpeed calculation

        Vector2D desiredSpeedVersor = new Vector2D(p.getTarget().getX() - p.getPosition().getX(),
                p.getTarget().getY() - p.getPosition().getY());

        Vector2D desiredSpeed = new Vector2D(desiredSpeedVersor.versor().getX()*p.getDesiredSpeed(),
                desiredSpeedVersor.versor().getX()*p.getDesiredSpeed());

        Vector2D force = new Vector2D(((desiredSpeed.getX() - p.getSpeed().getX())/tau)*p.getMass(),
                ((desiredSpeed.getY() - p.getSpeed().getY())/tau)*p.getMass());

        return force;
    }
}
