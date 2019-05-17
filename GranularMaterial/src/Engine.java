/**
 * Created by estebankramer on 17/05/2019.
 */
public class Engine {
    public static void main(String args[]) {

        double D = 0.2;
        Silo silo = new Silo(2, 0.5, D);

        String esteban= "/Users/estebankramer1/Desktop/results" + D;
        silo.start(esteban,10);

    }
}
