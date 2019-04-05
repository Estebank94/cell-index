import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Engine {
    private final static double INFINITE = Double.MAX_VALUE;
    private Set<Particle> particles;

    private double boxSize;

    /*Radius are in meters and Mass are in grams*/
    private double smallRadius = 0.005;
    private double smallMass = 0.1;
    private double bigRadius = 0.05;
    private double bigMass = 100;

    private double maxSmallVelocity = 0.1;


    public double timeUntilCrashWithParticle(Particle p1, Particle p2) {
        Point deltaR = new Point(p2.getPosition().getX() - p1.getPosition().getX(), p2.getPosition().getY() - p1.getPosition().getY());
        Point deltaV = new Point(p)
    }

    public double timeUntilCrashWithWall(Particle p, int l) {

    }


   /* Las posiciones de todas las partículas deben ser al azar con distribución uniforme dentro del dominio.
    Las partículas pequeñas deben tener velocidades con una distribución uniforme en el rango: |v| < 0.1 m/s*/


    /* n = Amount of small particles*/
    private void addParticles(int n){
        Random random = new Random();

        int id = 1;

        double bigAngle = random.nextDouble() * 2 * Math.PI;

        /* create an unique big particle*/
        particles.add(new Particle(id++, 0, 0, bigRadius,
                new Point(boxSize/2, boxSize/2), bigMass, bigAngle));

        while(particles.size() <= n){

            /* Math.random() * (max - min) + min; */
            double x = random.nextDouble() * (boxSize - 2*smallRadius) + smallRadius;
            double y = random.nextDouble() * (boxSize - 2*smallRadius) + smallRadius;;

            double v = random.nextDouble() * maxSmallVelocity;
            double smallAngle = random.nextDouble() * 2 * Math.PI;
            double vx = v * Math.cos(smallAngle);
            double vy = v * Math.sin(smallAngle);

            Particle newParticle = new Particle(id++, vx, vy, smallRadius, new Point(x, y), smallMass, smallAngle);
            if(!isSuperimposed(newParticle)){
                /* tenemos que tener en cuenta que si no lo agregue aca, no se incremento el size del set
                y nosotros tenemos que asegurarnos que el set tenga las n particulas, por eso es que un for
                comun tipo i=0; i<n, etc no nos sirve
                 */
                particles.add(newParticle);
            }
        }


    }


    /*Cada particula nueva (i) no se puede superponer con ninguna
    de las particulas ya existentes (j) ni con las paredes
    (xi − xj)^2 +(yi − yj)^2 > (Ri − Rj)^2 */

    public boolean isSuperimposed (Particle newParticle) {

        double xDifference;
        double yDifference;
        double rSum;

        for(Particle p : particles){
            xDifference = (newParticle.getPosition().getX() - p.getPosition().getX());
            yDifference = (newParticle.getPosition().getY() - p.getPosition().getY());
            rSum = (newParticle.getRadius() + p.getRadius());

            if(Math.pow(xDifference,2) + Math.pow(yDifference, 2) <= Math.pow(rSum,2)) {
                return true;
            }
        }
        return false;
    }

}
