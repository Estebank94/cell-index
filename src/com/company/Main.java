package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {


    public static void main (String [ ] args) {

        Parser parser = new Parser(args[0],args[1]);

        int L = parser.getL();

        int N = parser.getN();

        int M = parser.getM();

        double Rc = parser.getRc();

        boolean periodic = parser.isPeriodic();

        System.out.println("Is periodic:" + (periodic ? "true" : "false"));

        Set<Particle> molecules = parser.getParticles();

        Engine engine = new Engine(L,N,M,Rc,periodic,molecules);

        System.out.println("Starting algorithm:");
        long start = System.currentTimeMillis();
        Map<Particle,Set<Particle>> ans = engine.startEngine();
        //Map<Molecule,Set<Molecule>> ans = engine.bruteForce(molecules);
        long end = System.currentTimeMillis();
        System.out.println("Algorithm finished");


        System.out.println("Total time:" + (end-start));


        for(Particle particle : ans.keySet()){
            String toWrite = Engine.generateFileforResult(particle,ans.get(particle),molecules);
            Engine.writeResultFile(toWrite,particle.getId(),"/Users/martinascomazzon/Downloads/SimulacionDeSistemas-master/CellIndexMethod/src/main/resources");
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
