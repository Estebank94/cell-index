import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Engine {
    public static void main(String args[]) {

        double D = 1.2;
        double L = 20;
        double W = 20;
        int printingStep = 100;
        Room room = new Room(L, W, D, printingStep);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String martina = "/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/RoomEvacuation/" + dtf.format(now) ;
        String esteban = "/Users/estebankramer1/Desktop/results/" + dtf.format(now);
        room.run(martina);

    }
}