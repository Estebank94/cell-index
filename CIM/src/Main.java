import java.util.Map;
import java.util.Set;

public class Main {

//    /Users/estebankramer1/Documents/ITBA/4A1C/Simulacion/CIM/resources/staticrand.txt  /Users/estebankramer1/Documents/ITBA/4A1C/Simulacion/CIM/resources/dynamicrand.txt

    public static void main (String [ ] args) {

        Parser parser = new Parser(args[0],args[1]);

        int L = parser.getL();

        int M = parser.getM();

        double Rc = parser.getRc();

        boolean periodic = parser.periodic();

        Set<Particle> particles = parser.getParticles();

        Engine engine = new Engine(L,M,Rc,periodic,particles);

        System.out.println("Starting Engine...");
        System.out.println("Periodic board: " + (periodic ? "true" : "false"));
        long start = System.currentTimeMillis();
        Map<Particle,Set<Particle>> ans = engine.start();
//        Map<Particle,Set<Particle>> ans = engine.bruteForce(particles);
        long end = System.currentTimeMillis();

        System.out.println("Finished, total time: " + (end-start));

        for(Particle particle : ans.keySet()){
            String toWrite = Engine.generateFileString(particle, ans.get(particle), particles);
            Engine.writeToFile(toWrite,particle.getId(),"/Users/martinascomazzon/Downloads");
        }

        for(Map.Entry<Particle,Set<Particle>> a : ans.entrySet()){

            System.out.print(a.getKey().getId()+" : ");

            for(Particle p: a.getValue()){
                System.out.print(p.getId()+" ");
            }

            System.out.println();
        }
    }


}
