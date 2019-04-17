import java.util.ArrayList;
import java.util.List;

public class Simulation {

   /* Parameters:
        * m = 70 kg;
        * k = 10^4 N/m;
        * 𝛾 = 100 kg/s;
        * tf = 5 s
    */

    /* Initial Conditions:
        * r (t=0) = 1 m;
        * v (t=0) = - A 𝛾/(2m) m/s;
     */

    private Particle particle;

    /* Parameters */
    private static double mass = 70; /* measured in kg */
    private static double k = Math.pow(10,4); /* measured in N/m [Elastic constant]*/
    private static double gamma = 100; /* measured in kg/s [Coefficient]*/
    private static double tf = 5;


    /* Initial Conditions */
    private static double initialPosition = 1; /* measured in m */
    private static double A = 1; /* TODO: Como se calcula esta constante ? */
    private static double initialVelocity = - A * (gamma/(2*mass));

    private static double deltaT;

    /* Used for prediction and correction */
    private static final int[] factorials = {1, 1, 2, 6, 24, 120};

    public Simulation(double deltaT) {

        this.deltaT = deltaT;
        this.particle = new Particle(0, initialPosition, 0, initialVelocity,
                0, mass, 0);
    }

    /* Algorithm Gear Predictor-Corrector */
    public void startGearPredictor(){
        double time = 0;

        while(time < tf){
            gearPredictor();
            time += deltaT;
        }
        System.out.println("Time: " + time + " Position: " + particle.getX());
    }

    private void gearPredictor(){

        List<Double> derivatives = derivativeGearPredictor();

        List<Double> predictions = predictGearPrediction(derivatives);

        double newAcceleration = getForce(predictions.get(0), predictions.get(1));

        double deltaA = evaluateGearPrediction(newAcceleration, predictions.get(2));

        List<Double> correct = correctGearPrediction(deltaA, predictions);

        particle.setX(correct.get(0));
        particle.setVx(correct.get(1));

    }

    private double getForce(double position, double velocity){
        return ((-k * position) - (gamma * velocity))/mass;
    }

    private List<Double> derivativeGearPredictor(){

        /* derivatives are always the same => ri = -kr(i-2) - gamma(i-1) */

        List<Double> derivatives = new ArrayList<>();


        derivatives.add(particle.getX());   /* get(0) - position  */
        derivatives.add(particle.getVx());  /* get(1) - velocity  */
                                            /* get(2) - acceleration */

        for(int i = 2; i<=5; i++){
            double ri = getForce(derivatives.get(i-2), derivatives.get(i-1));
            derivatives.add(ri);
        }

        return derivatives;
    }

    private List<Double> predictGearPrediction(List<Double> derivatives){

        List<Double> predictions = new ArrayList<>();
        int length = 5;
        double ri = 0;

        for(int i = 0; i<derivatives.size(); i++){
            for(int j = length; j >= i; j--){
                ri += derivatives.get(j)*(Math.pow(deltaT, j-i)/factorials[j-i]);
            }
            predictions.add(ri);
            ri = 0;
        }
        return predictions;
    }

    private double evaluateGearPrediction (double newAcceleration, double predictions){
        double error = newAcceleration - predictions;
        return ((error*Math.pow(deltaT,2))/factorials[2]);
    }

    private List<Double> correctGearPrediction (double deltaA, List<Double> predictions){

        List<Double> correct = new ArrayList<>();
        double[] alphaOrder5 = {3.0/20, 251.0/360, 1.0, 11.0/18, 1.0/6, 1.0/60};

        for(int i = 0; i<predictions.size(); i++){
            double ri = predictions.get(i) + (deltaA*alphaOrder5[i]*factorials[i])/(Math.pow(deltaT,i));
            correct.add(ri);
        }

        return correct;
    }


    /* Algorithm Beeman */
    public void startBeeman(){
        double time = 0;

        while(time < tf){
            beeman();
            time += deltaT;
        }
        System.out.println("Time: " + time + " Position: " + particle.getX());

    }

    private void beeman(){
        double r = particle.getX();     /* distance(t) */
        double v = particle.getVx();    /* velocity(t) */
        double a = getForce(r, v);      /* acceleration (t) */

        /* acceleration (t - deltaT) */
        double previousAcceleration = getPreviousAccelerationWithEuler(r, v); /* acceleration (t - deltaT) */

        double newPosition = newPositionBeeman(r, v, previousAcceleration, a);

        double predictedVelocity = predictVelocityBeeman(v, a, previousAcceleration);

        double newAcceleration = getForce(newPosition, predictedVelocity);

        double newVelocity = newVelocityBeeman (predictedVelocity, previousAcceleration, newAcceleration, a);

        particle.setX(newPosition);
        particle.setVx(newVelocity);

    }

    private double getPreviousAccelerationWithEuler (double position, double velocity){

        double previousForce = getForce(position, velocity);

        double newPosition = position + velocity*(-deltaT) + ((Math.pow(deltaT, 2) /(2*mass)) * previousForce);
        double newVelocity = velocity + ((-deltaT/mass)*previousForce);

        return getForce(newPosition, newVelocity);
    }

    private double newPositionBeeman(double position, double velocity, double previousAcceleration,
                                     double acceleration){

        return position + velocity*deltaT + ((2/3)*acceleration*Math.pow(deltaT,2))
                - ((1/6)*previousAcceleration*Math.pow(deltaT,2));

    }

    private double newVelocityBeeman (double velocity, double previousAcceleration,
                                      double newAcceleration, double currenAcceleration){

        return velocity + ((1/3)*newAcceleration*deltaT) + ((5/6)*currenAcceleration*deltaT)
                - ((1/6)*previousAcceleration*deltaT);
    }

    /* slide 19 from class */
    private double predictVelocityBeeman (double velocity, double acceleration,
                                          double previousAcceleration){

        return velocity + (3/2*acceleration*deltaT) - ((1/2)*previousAcceleration*deltaT);
    }


}
