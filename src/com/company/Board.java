package com.company;

import java.util.List;

/**
 * Created by estebankramer on 12/03/2019.
 */
public class Board {
    private Double L;
    private Integer M;
    private Cell[][] board;

    public Board(Double l, Integer m, List<Particle> particles) {
        L = l;
        M = m;
        board = setParticles(m, l, particles);
    }

    public Double getL() {
        return L;
    }

    public void setL(Double l) {
        L = l;
    }

    public Integer getM() {
        return M;
    }

    public void setM(Integer m) {
        M = m;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public Cell[][] setParticles(Integer m, Double l, List<Particle> particles) {
        Cell[][] board = new Cell[m][];
        Double cellLength = l / m;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                for (Particle particle : particles) {
                    double x = particle.getPosition().getX();
                    double y = particle.getPosition().getY();
                    if (x >= i * cellLength && x <= i * cellLength + cellLength &&
                            y >= j * cellLength && y <= j * cellLength + cellLength) {
                        board[i][j].getParticles().add(particle);
                        particles.remove(particle);
                    }
                }
            }
        }

        return board;
    }


}
