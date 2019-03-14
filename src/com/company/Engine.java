package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Engine {

    private Board board;

    private Map<Particle,Set<Particle>> neighbors;
    int m;

    public Engine(int l, int m, double rc,boolean periodic, Set<Particle> particles) {
        this.m=m;
        board = new Board(l,m,rc,particles,periodic);
        neighbors = new HashMap<>();
    }

    /**
     * Builds Map of each particle in the board and all it's neighbours
     */

    public void getAllNeighboursOfAllParticles(){
        for(int i=0;i<m;i++){
            for(int j=0 ; j<m ;j++){
                Map<Particle,Set<Particle>> map = board.analyzeCell(new Point(i,j));
                for(Particle particle : map.keySet()) {


                    if (neighbors.containsKey(particle)) {
                        neighbors.get(particle).addAll(map.get(particle));
                    } else {
                        neighbors.put(particle, map.get(particle));
                    }

                    for (Particle m : map.get(particle)) {
                        if (neighbors.containsKey(m)) {
                            neighbors.get(m).add(particle);
                        } else {
                            HashSet<Particle> set = new HashSet<>();
                            set.add(particle);
                            neighbors.put(m, set);
                        }
                    }
                }
            }
        }

    }

    public Map<Particle,Set<Particle>> start(){
        getAllNeighboursOfAllParticles();
        return neighbors;
    }

    public static void writeToFile(String data, int inedx, String path){
        try {
            Files.write(Paths.get(path + "/results" + inedx + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String generateFileString(Particle particle, Set<Particle> neighbours,Set<Particle> AllParticles){
        StringBuilder builder = new StringBuilder()
                .append(AllParticles.size())
                .append("\r\n")
                .append("//ID\t X\t Y\t Radius\t R\t G\t B\t\r\n");
        for(Particle current: AllParticles){
            builder.append(current.getId())
                    .append(" ")
                    .append(current.getLocation().getX())
                    .append(" ")
                    .append(current.getLocation().getY())
                    .append(" ")
                    .append(current.getRatio())
                    .append(" ");
            if(particle.getId() == current.getId()){
                builder.append("1 0 0\r\n");
            }else if(neighbours.contains(current)){
                builder.append("0 1 0\r\n");
            }else{
                builder.append("1 1 1\r\n");
            }
        }
        return builder.toString();
    }

    public Map<Particle,Set<Particle>> bruteForce(Set<Particle> particles){
        Map<Particle,Set<Particle>> neighbours = new HashMap<>();

        for(Particle m1: particles){
            neighbours.put(m1,new HashSet<>());
            for(Particle m2 : particles){
                if(Particle.borderDistanceBetweenParticles(m1,m2)-m1.getRatio()-m2.getRatio()<=board.getRc()){
                    neighbours.get(m1).add(m2);
                }
            }
        }
        return neighbours;
    }



}
