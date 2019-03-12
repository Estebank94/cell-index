package com.company;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Particle> particles = new ArrayList<>();
        Double l = 300.0;
        Integer m = 10;
        Double rc = 5.0;
        for(int i = 0; i < 100; i++){
            particles.add(new Particle(3.0, new Point(Math.random() * l , Math.random() * l)));
        }
        cellIndex(l, m, rc, particles);

        System.out.println("hola");
    }


//    ESTATICO
//    N     -> Numero total de particulas
//    L     -> Longitud del area de simulacion
//    RnPrn -> Radio y propiedad de la particula n
//    M     -> Numero de celdas
//    rc    -> Radio de deteccion de particulas

    public static List<Particle> cellIndex(Double l, Integer m, Double rc, List<Particle> particles) {
        Board board = new Board(l, m, particles);

        for(int i = 0; i < board.getM(); i++){
            for(int j = 0; j < board.getM(); j++){
                checkNeighboursCells(board.getBoard(), i, j, rc);
            }
        }
        return particles;
    }

    public static Double calculateBorderDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p2.getPosition().getX() - p1.getPosition().getX(), 2) - Math.pow(p2.getPosition().getY() - p1.getPosition().getY(), 2)) - p1.getRadius();
    }

    public static void addNeighbours(Cell c1, Cell c2, Double rc) {
        if(!c2.getChecked()) {
            for(Particle p1 : c1.getParticles()) {
                for (Particle p2 : c2.getParticles()) {
                    if (calculateBorderDistance(p1, p2) <= rc) {
                        p1.getNeighbours().add(p2);
                        p2.getNeighbours().add(p1);
                    }
                }
            }
        }
    }


    public static void checkNeighboursCells(Cell[][] board, Integer i, Integer j, Double rc) {
        if( i >= 1) {
            addNeighbours(board[i][j], board[i-1][j], rc);
        }
        if( i < board.length ) {
            addNeighbours(board[i][j], board[i+1][j], rc);
        }
        if( j >= 1) {
            addNeighbours(board[i][j], board[i][j-1], rc);
        }
        if( j < board.length ) {
            addNeighbours(board[i][j], board[i][j+1], rc);
        }
        if( i >= 1 && j >= 1 ){
            addNeighbours(board[i][j], board[i-1][j-1], rc);
        }
        if( i < board.length && j < board.length ){
            addNeighbours(board[i][j], board[i+1][j+1], rc);
        }
        if( i >= 1 && j < board.length ) {
            addNeighbours(board[i][j], board[i-1][j+1], rc);
        }
        if( i < board.length && j >= 1 ) {
            addNeighbours(board[i][j], board[i+1][j-1], rc);
        }
        board[i][j].setChecked(true);
    }



}
