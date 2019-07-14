
public class Engine {
    public static void main(String args[]) {

        double D = 0.0;
        double L = 1.5;
        double W = 0.4;
        int printingStep = 100;
        Silo silo = new Silo(L, W, D, printingStep);

        String martina = "/Users/martinascomazzon/Documents/2019/ITBA/Primer Cuatrimestre/Simulacion de Sistemas/Granular Material/Final/Granular Material_D_" + D;
        String esteban = "/Users/estebankramer1/Desktop/results" + D;
        silo.run(martina,5);

    }
}