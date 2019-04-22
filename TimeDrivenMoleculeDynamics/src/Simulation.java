import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private BufferedWriter bw;

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

    /* Step */
    private int step;

    public Simulation(double deltaT, int step) {

        this.deltaT = deltaT;
        this.particle = new Particle(0, initialPosition, 0, initialVelocity,
                0, mass, 0);
        this.step = step;
    }

    /* Algorithm Gear Predictor-Corrector */
    public void startGearPredictor(String path){
        double time = 0;
        initalizeBW(path,"GearPredictor");
        int iteration = 0;

        while(time <= tf){
            gearPredictor();
            if(iteration % step == 0 || iteration == 0){
                appendToFile(bw,generateFileString(particle));
            }
            time += deltaT;
            iteration ++;
        }
        closeBW();
        System.out.println("Time: " + (time-deltaT) + " Position: " + particle.getX());
    }

    private void gearPredictor(){

        List<Double> derivatives = derivativeGearPredictor();

        List<Double> predictions = predictGearPrediction(derivatives);

        double newAcceleration = getForce(predictions.get(0), predictions.get(1))/mass;

        double deltaA = evaluateGearPrediction(newAcceleration, predictions.get(2));

        List<Double> correct = correctGearPrediction(deltaA, predictions);

        particle.setX(correct.get(0));
        particle.setVx(correct.get(1));

    }

    private double getForce(double position, double velocity){
        return (-k * position) - (gamma * velocity);
    }

    private List<Double> derivativeGearPredictor(){

        /* derivatives are always the same => ri = -kr(i-2) - gamma(i-1) */

        List<Double> derivatives = new ArrayList<>();


        derivatives.add(particle.getX());   /* get(0) - position  */
        derivatives.add(particle.getVx());  /* get(1) - velocity  */
                                            /* get(2) - acceleration */

        for(int i = 2; i<=5; i++){
            double ri = getForce(derivatives.get(i-2), derivatives.get(i-1))/mass;
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
        double[] alphaOrder5 = {3.0/16, 251.0/360, 1.0, 11.0/18, 1.0/6, 1.0/60};

        for(int i = 0; i<predictions.size(); i++){
            double ri = predictions.get(i) + (deltaA*alphaOrder5[i]*factorials[i])/(Math.pow(deltaT,i));
            correct.add(ri);
        }

        return correct;
    }


    /* Algorithm Beeman */
    public void startBeeman(String path){
        double time = 0;
        initalizeBW(path,"Beeman");
        double previousAcceleration = firstBeeman();
        int iteration = 0;

        while(time <= tf){
            previousAcceleration = beeman(previousAcceleration);
            if(iteration % step == 0 || iteration == 0){
                appendToFile(bw,generateFileString(particle));
            }
            iteration ++;
            time += deltaT;
        }
        closeBW();
        System.out.println("Time: " + (time-deltaT) + " Position: " + particle.getX());
    }

    private double beeman(double previousAcceleration){
        double r = particle.getX();          /* distance(t) */
        double v = particle.getVx();         /* velocity(t) */
        double a = getForce(r, v)/mass;      /* acceleration (t) */


        double newPosition = newPositionBeeman(r, v, previousAcceleration, a);

        double predictedVelocity = predictVelocityBeeman(v, a, previousAcceleration);

        double newAcceleration = getForce(newPosition, predictedVelocity)/mass;

        double newVelocity = newVelocityBeeman (predictedVelocity, previousAcceleration, newAcceleration, a);

        particle.setX(newPosition);
        particle.setVx(newVelocity);
        return a;

    }

    private double firstBeeman(){
        double r = particle.getX();          /* distance(t) */
        double v = particle.getVx();         /* velocity(t) */
        double a = getForce(r, v)/mass;      /* acceleration (t) */

        /* acceleration (t - deltaT) */
        double previousAcceleration = getPreviousAccelerationWithEuler(r, v); /* acceleration (t - deltaT) */

        return previousAcceleration;
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

    /* Algorithm Verlet (1) - Slide 12 & Slide 14 - */
    public void startVerlet(String path){
        double time = 0;
        initalizeBW(path,"Verlet");
        double previousPosition = firstVerlet();
        int iteration = 0;


        while(time <= tf){
            previousPosition = verlet(previousPosition);
            if( iteration % step == 0 || iteration == 0 ){
                appendToFile(bw,generateFileString(particle));
            }
            iteration ++;
            time += deltaT;

        }
        closeBW();
        System.out.println("Time: " + (time-deltaT) + " Position: " + particle.getX());
    }

    private double firstVerlet(){
        double r = particle.getX();
        double v = particle.getVx();
        double force = getForce(r,v);
        double previousPosition = getPreviousPositionWithEuler(r, v, force);

        return previousPosition;

    }

    private double verlet(double previousPosition){
        double r = particle.getX();
        double v = particle.getVx();
        double force = getForce(r,v);

        double newPosition = (2*r) - previousPosition + ((Math.pow(deltaT,2)*force)/mass);

        double newVelocity = (newPosition - previousPosition)/(2*deltaT);

        particle.setX(newPosition);
        particle.setVx(newVelocity);
        return r;
    }

    private double getPreviousPositionWithEuler(double position, double velocity,
                                                    double force){

        double pos = position - deltaT * velocity;
        pos -= Math.pow(deltaT, 2) * force / (2 * mass);
        return pos;

    }

    public void startAnaliticSolution(String path){
        double time = 0;
        double value = 0;
        initalizeBW(path,"Analitic");
        int iteration = 0;


        while(time <= tf){
            value = getParticleRealPosition(time);
            if(iteration % step == 0 || iteration == 0){
                appendToFile(bw,generateFileString(particle));
            }
            iteration ++;
            time += deltaT;
            particle.setX(value);
        }
        closeBW();
        System.out.println("Time: " + (time-deltaT) + " Position: " + value);
    }

    private double getParticleRealPosition(double time) {
        return A * Math.exp(-(gamma / (2*mass)) * time) * Math.cos( Math.sqrt((k / mass) - (gamma*gamma / (4 * mass * mass) )) * time);
    }

    /* File */
    private void closeBW() {
        if (bw != null) try {
            bw.flush();
            bw.close();
        } catch (IOException ioe2) {
            // just ignore it
        }
    }

    private boolean initalizeBW(String outPath,String algType) {
        try {

            bw = new BufferedWriter(new FileWriter(outPath+algType+"-dt:"+ deltaT +"-tf:"+ tf + ".txt", true));
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String generateFileString(Particle particle){

        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(12);
        df.setMinimumIntegerDigits(1);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        StringBuilder builder = new StringBuilder()
                .append(df.format(particle.getX()))
                .append("\n");
        return builder.toString();
    }



    public static void appendToFile (BufferedWriter bw , String data) {
        try {
            bw.write(data);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }






}
