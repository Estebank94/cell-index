import java.util.Set;
import java.util.function.Function;

/**
 * Created by estebankramer on 06/06/2019.
 */
public class SocialForce {

    private final double A;
    private final double B;

    public SocialForce(double A, double B){
        this.A = A;
        this.B = B;
    }

    public Vector2D calculate(Particle p, Set<Particle> neighbours) {
        Vector2D force = Vector2D.ZERO;
        double overlapping;

        for(Particle other: neighbours) {
            if(!p.equals(other)) {
                overlapping= overlapping(p, other);
                double fn = getFn(overlapping);

                Vector2D en = other.getPosition().subtract(p.getPosition())
                        .dividedBy(other.getPosition().subtract(p.getPosition()).abs());

                force = force.add(new Vector2D(fn * en.x ,fn * en.y));

            }
        }
        return force;
    }

    private double getFn(double overlaping){
        return A*Math.exp(- overlaping /B);
    }

    private double overlapping(Particle i, Particle j){
        //TODO check overlapping signs
        double result =  i.getPosition().subtract(j.getPosition()).abs() - ( i.getRadius() + j.getRadius());
        return result ;
    }
}