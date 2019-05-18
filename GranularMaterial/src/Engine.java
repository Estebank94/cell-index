
public class Engine {
    public static void main(String args[]) {

        double D = 0.2;
        double L = 2;
        double W = 0.5;
        int printingStep = 100;
        Silo silo = new Silo(L, W, D, printingStep);

        String martina = "/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Granular Material_D_" + D;
        String esteban = "/Users/estebankramer1/Desktop/results" + D;
        silo.run(martina,1);

    }
}
