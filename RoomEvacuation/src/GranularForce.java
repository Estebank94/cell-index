import java.util.Set;


public class GranularForce {
    private static double g = 9.8; // m/seg^2
    private static double Kn; // N/m
    private static double Kt; // N/m
    private static double Mu = 0.1;
    private static double Gama = 70; // Kg/s

    private double L, W, D;



    public GranularForce(double kn, double kt) {
        this.Kn = kn;
        this.Kt = kt;
    }

    public Vector2D calculate(Particle p, Set<Particle> neighbours) {
        Vector2D force = Vector2D.ZERO;
        double overlapping;


        for(Particle other: neighbours) {
            if(!p.equals(other)) {
                overlapping= overlaping(p, other);
                if(overlapping<0){
                    double fn = getFn(overlapping);
                    double ft = getFt(overlapping, vrel(p, other));


                    Vector2D en = other.getPosition().subtract(p.getPosition()).versor();
                    Vector2D et = en.tangent();

                    force = force.add(en.multiplyBy(fn).add(et.multiplyBy(ft)));
                }

            }
        }
        //TODO add wall forces
        //force = force.add(getWallForces(p,position,speed));
        return force;
    }


    private double getFn(double overlaping){
        return - overlaping * Kn;
    }
    private double getFt(double tanSpeed, double overlaping){
        return tanSpeed * overlaping * Kt;
    }

    private double overlaping(Particle i, Particle j){

        // Equation on slide 31 of ppt is with  the sign altered from previos overlapping implementation
        double result =  i.getPosition().subtract(j.getPosition()).abs() - ( i.getRadius() + j.getRadius());


        return result < 0 ? result  : 0;
    }

    private double vrel(Particle i, Particle j) {
        Vector2D direction = j.getPosition().subtract(i.getPosition()).tangent();
        return i.getSpeed().subtract(j.getPosition()).projectedOn(direction);
    }




}