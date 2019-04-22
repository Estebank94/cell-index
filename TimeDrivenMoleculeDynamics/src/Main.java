public class Main {

    public static void main(String args[]){

        /* step */
        int step = 5;

        /* DO NOT DELETE: /Users/martinascomazzon/Documents/2019/ITBA/Simulacion\ de\ Sistemas/Oscilador/Archivos */
        String path = "/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Oscilador/Archivos/";

//        for(int i = 0; i<7; i++){

            /* deltaT should go between (10^-2 - 10^-6) or between (10^-1 to 10^-5)*/
            double deltaT = Math.pow(10, -5);
            
            System.out.println("Gear Predictor Simulation");
            Simulation gearSimulation = new Simulation(deltaT, step);
            gearSimulation.startGearPredictor(path);
            /* TODO: check why it is giving slightly different
            * Time: 5.000009999979879 Position: -0.028097721635724177
            * Time: 5.000009999979879 Position: -0.028197956501623076
            */
            System.out.println("Beeman Simulation");
            Simulation beemanSimulation = new Simulation(deltaT, step);
            beemanSimulation.startBeeman(path);

            System.out.println("Verlet Simualtion");
            Simulation verletSimulation = new Simulation(deltaT, step);
            verletSimulation.startVerlet(path);

            System.out.println("Analitic Solution");
            Simulation analiticSimulation = new Simulation(deltaT, step);
            analiticSimulation.startAnaliticSolution(path);
//        }
    }
}
