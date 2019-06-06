
public class Engine {
    public static void main(String args[]) {

        double D = 1.2;
        double L = 20;
        double W = 20;
        int printingStep = 100;
        Room room = new Room(L, W, D, printingStep);

        String martina = "/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Granular Material_D_" + D;
        String esteban = "/Users/estebankramer1/Desktop/results/" + D;
        room.run(esteban, 100);

    }
}