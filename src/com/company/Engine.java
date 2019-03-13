package com.company;

import javax.print.attribute.HashPrintJobAttributeSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Engine {

    private Board board;

    private Map<Particle, Set<Particle>> neighbours;
    int m;
    private int n;

    /*
    Input values
    ** n     -> cantidad total de particulas
    ** l     -> Longitud del area de simulacion
    ** RnPrn -> Set de particulas: Radio y propiedad de la particulas
    ** m     -> Numero de celdas
    ** rc    -> Radio de deteccion de particulas
    */

    public Engine(int l, int n, int m, double rc,boolean periodic, Set<Particle> molecules) {
        this.m=m;
        this.n=n;
        board = new Board(l,m,rc,molecules,periodic);
        neighbours = new HashMap<>();
    }

    /* une el board ENTERO en un solo mapa, en la funcion del board solo me ocupo de una unica cell, aca de todas*/
    public void getNeighborsOfParticles(){
        for(int i=0;i<m;i++){
            for(int j=0 ; j<m ;j++){
                Map<Particle, Set<Particle>> map = board.getAllCellNeighbours(new Point(i,j));
                for(Particle particle : map.keySet()) {


                    if (neighbours.containsKey(particle)) {
                        neighbours.get(particle).addAll(map.get(particle));
                    } else {
                        neighbours.put(particle, map.get(particle));
                    }

                    for (Particle p : map.get(particle)) {
                        if (neighbours.containsKey(p)) {
                            neighbours.get(p).add(particle);
                        } else {
                            HashSet<Particle> set = new HashSet<>();
                            set.add(particle);
                            neighbours.put(p, set);
                        }
                    }
                }
            }
        }
    }

    public Map<Particle, Set<Particle>> startEngine(){
        getNeighborsOfParticles();
        return neighbours;
    }

    public static void writeResultFile(String data, int index, String path){
        try {
            Files.write(Paths.get(path + "/results" + index + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateFileforResult(Particle particle, Set<Particle> neighbours,Set<Particle> allMolcules){
        StringBuilder builder = new StringBuilder()
                .append(allMolcules.size())
                .append("\r\n")
                .append("//Particle number\t Position X\t Position Y\t Particle Radius\t R\t G\t B\t\r\n");
        for(Particle p: allMolcules){
            builder.append(p.getId())
                    .append(" ")
                    .append(p.getPosition().getX())
                    .append(" ")
                    .append(p.getPosition().getY())
                    .append(" ")
                    .append(p.getRadius())
                    .append(" ");
            if(particle.getId() == p.getId()){
                builder.append("1 0 0\r\n");
            }else if(neighbours.contains(p)){
                builder.append("0 1 0\r\n");
            }else{
                builder.append("1 1 1\r\n");
            }
        }
        return builder.toString();
    }


}
