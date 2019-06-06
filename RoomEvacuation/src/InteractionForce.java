import java.util.Set;
import java.util.function.Function;

/**
 * Created by estebankramer on 06/06/2019.
 */
public class InteractionForce {

    private final double A;
    private final double B;
    private final double kn;
    private final double kt;

    private final double W;
    private final double door;

    public InteractionForce(double A, double B,double kn, double kt, double W, double door){
        this.A = A;
        this.B = B;
        this.kn = kn;
        this.kt = kt;

        this.W = W;
        this.door = door;
    }

    public Vector2D calculate(Particle p, Set<Particle> neighbours) {
        Vector2D force = Vector2D.ZERO;
        double distance;


        for(Particle other: neighbours) {
            if(!p.equals(other)) {
                distance = distance(p,other);


                Vector2D en = p.getPosition().subtract(other.getPosition()).versor();
                Vector2D et = en.tangent();


                double fn = socialFn(distance);
                double ft = 0;
                if(distance > 0){
                    fn += granularFn(distance);
                    ft  = granularFt(distance,relSpeed(p,other,et));
                }

                force = force.add(en.multiplyBy(fn).add(et.multiplyBy(ft)));

            }


        }


        //TODO add wall forces
        //force = force.add(getWallForces(p,position,speed));
        force = force.add(bottomWallForce(p));
        return force;
    }


    private double distance(Particle i, Particle j){
        return i.getRadius()+j.getRadius() - i.getPosition().subtract(i.getPosition()).abs();
    }

    private double socialFn(double distance){
        return A*Math.exp(distance/B);
    }

    private double granularFn(double distance){
        return kn * distance;
    }
    private double granularFt(double distance, double relSpeed){
        return kt * distance * relSpeed;
    }

    private double relSpeed(Particle i, Particle j, Vector2D et){
        return j.getSpeed().subtract(i.getSpeed()).dot(et);
    }

    private Vector2D bottomWallForce(Particle p) {
        if(p.getPosition().x > W/2 - door/2 && p.getPosition().x < (W/2 + door/2)) {
            return Vector2D.ZERO;
        }

        double distance = p.getRadius() - p.getPosition().y;

        Vector2D en = new Vector2D(0, 1);
        Vector2D et = en.tangent();

        double relSpeed = p.getSpeed().dot(et);

        double fn = socialFn(distance);
        double ft = 0;

        if(distance > 0) {
            fn += granularFn(distance);
            ft -= granularFt(distance, relSpeed);
        }

        return en.multiplyBy(fn).add(et.multiplyBy(ft));
    }

}