public class Main {

    public static void main(String args[]){

//        for(int i = 0; i<6; i++){

            /* deltaT should go between (10^-2 - 10^-6) or between (10^-1 to 10^-5)*/
            double deltaT = Math.pow(10, -5);

            Simulation simulation = new Simulation(deltaT, 5);
            System.out.println("Start: " + 5);
            simulation.startGearPredictor();
//        }
    }
}
