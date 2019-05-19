
public class Engine {
    public static void main(String args[]) {

        double D = 1;
        double L = 5;
        double W = 2;
        int printingStep = 100;
        Silo silo = new Silo(L, W, D, printingStep);

        String martina = "/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Granular Material_D_" + D;
        String esteban = "/Users/estebankramer1/Desktop/results" + D;
        silo.run(martina,7);

    }
}
