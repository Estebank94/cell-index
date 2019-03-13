package com.company;

import java.util.*;


/**
 * Created by estebankramer on 12/03/2019.
 */
public class Board {
    private Double l;
    private Integer m;
    private Double rc;
    private Double cellSize;
    private HashMap<Point, Set<Particle>> board;
    private Boolean periodic;

    public Board(Double l, Integer m, Double rc, Set<Particle> particles, boolean periodic) {
        this.l = l;
        this.m = m;
        this.rc = rc;
        this.cellSize = l/m;
        this.board = new HashMap<>();
        this.periodic = periodic;

        for(int i = 0; i < m ; i++)
            for(int j = 0; j < m; j++) {
                board.put(new Point(i * 1.0,j * 1.0), new HashSet<>());
            }

    }

    public Point


}
