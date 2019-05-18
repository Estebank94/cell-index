
public class Engine {
    public static void main(String args[]) {

        double D = 0.2;
        Silo silo = new Silo(2, 0.5, D, 100);

        String martina = "/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Granular Material_D_" + D;
        String esteban= "/Users/estebankramer1/Desktop/results" + D;
        silo.start(martina,10);

    }
}
