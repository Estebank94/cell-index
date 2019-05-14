
public class Calculations {

    /* Constants */
    private final static double g = 9.8;
    private final static double kN = Math.pow(10,5);
    private final static double kT = 2 * kN;
    private final static double gamma = 70;
    private final static double mass = 0.01;
    private final static double mu = 0.001; /* de donde sacan ese valor? */

    private static double overlap(Particle p1, Particle p2 ){
        double result = p1.getRadius() + p2.getRadius() - absoluteDistance(p1,p2);


        return result > 0 ? result  : 0;
    }

    private double derivateOverlap(Particle p1, Particle p2){
        if(overlap(p1, p2) > 0) {
            return absoluteVelocity(p1,p2);
        }
        return 0;
    }

    private static double absoluteDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()),2) + (Math.pow((p1.getY() - p2.getY()),2)));
    }

    private static double absoluteVelocity(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow((p1.getVx() - p2.getVx()),2) + (Math.pow((p1.getVy() - p2.getVy()),2)));
    }

    /* N1 */
    private double getFn(double overlaping, double derivOverlap) {
        return -kN * overlaping - gamma * derivOverlap;
    }

    /* T2 */
    private double getFt(double Fn, double vrel) {
        return - mu * Math.abs(Fn) * Math.signum(vrel);
    }
}
