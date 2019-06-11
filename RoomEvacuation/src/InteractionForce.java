import java.util.Set;
import java.util.function.Function;

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

    public Vector2D calculate(Particle p, Set<Particle> neighbours, Function<Particle,Vector2D> position, Function<Particle,Vector2D> speed) {
        Vector2D force = Vector2D.ZERO;
        double distance;
        double fn = 0;
        double fnOnly = 0;

        for(Particle other: neighbours) {
            if(!p.equals(other)) {
                distance = distance(p,other,position);


                Vector2D en = position.apply(p).subtract(position.apply(other)).versor();
                Vector2D et = en.tangent();


                fn = socialFn(distance);
                double ft = 0;
                if(distance > 0){
                    fn += granularFn(distance);
                    fnOnly += granularFn(distance);
                    ft  = granularFt(distance,relSpeed(p,other,speed,et));
                }

                force = force.add(en.multiplyBy(fn).add(et.multiplyBy(ft)));

            }


        }

        p.setTotalFn(p.getTotalFn() + fnOnly);
        //TODO add wall forces
        //force = force.add(getWallForces(p,position,speed));
        force = force.add(bottomWallForce(p, position, speed));
        return force;
    }


    private double distance(Particle i, Particle j, Function<Particle,Vector2D> position){
        return i.getRadius()+j.getRadius() - position.apply(i).subtract(position.apply(j)).abs();
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

    private double relSpeed(Particle i, Particle j, Function<Particle,Vector2D> speed,Vector2D et){
        return speed.apply(j).subtract(speed.apply(i)).dot(et);
    }

    private Vector2D bottomWallForce(Particle p, Function<Particle, Vector2D> position, Function<Particle, Vector2D> speed) {
        if(position.apply(p).x > W/2 - door/2 && position.apply(p).x < (W/2 + door/2)) {
            return Vector2D.ZERO;
        }

        double distance = p.getRadius() - position.apply(p).y;

        Vector2D en = new Vector2D(0, 1);
        Vector2D et = en.tangent();

        double relSpeed = speed.apply(p).dot(et);

        double fn = socialFn(distance);
        double ft = 0;
        double fnOnly = 0;
        if(distance > 0) {
            fn += granularFn(distance);
            fnOnly += granularFn(distance);
            ft -= granularFt(distance, relSpeed);
        }

        p.setTotalFn(p.getTotalFn() + fnOnly);
        return en.multiplyBy(fn).add(et.multiplyBy(ft));
    }

}