public class Main {

    public static void main(String args[]){

//        for(int i = 0; i<6; i++){

            /* deltaT should go between (10^-2 - 10^-6) or between (10^-1 to 10^-5)*/
            double deltaT = Math.pow(10, -5);

            Simulation simulation = new Simulation(deltaT);
            simulation.startGearPredictor();
            /* TODO: check why it is giving slightly different
            * Time: 5.000009999979879 Position: -0.028097721635724177
            * Time: 5.000009999979879 Position: -0.028197956501623076
            */
            Simulation simulation2 = new Simulation(deltaT);
            simulation2.startBeeman();

            Simulation simulation3 = new Simulation(deltaT);
            simulation3.startVerlet();
        }
//    }
}
