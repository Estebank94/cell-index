package com.company;

import java.util.*;


public class Board {
    private double l;
    private int m;
    private double rc;
    private double cellSize;
    private HashMap<Point, Set<Particle>> board;
    private Boolean periodic;

    public Board(double l, int m, double rc, Set<Particle> particles, boolean periodic) {
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


    public Point getCellPosition(Point position) {
        return new Point( (int) position.getX()/cellSize, (int) position.getY()/cellSize);
    }


    public Set<Particle> getParticlesInCell(int x, int y) {
        if(x < 0) {
            if(periodic) {
                return getParticlesInCell(x+m,y);
            } else {
                return Collections.EMPTY_SET;
            }

        } if(x >= m) {
            if(periodic){
                return getParticlesInCell(x-m,y);
            }else{
                return Collections.EMPTY_SET;
            }
        } if(y < 0) {
            if(periodic) {
                return getParticlesInCell(x,y+m);
            } else {
                return Collections.EMPTY_SET;
            }
        } if(y >= m) {
            if(periodic) {
                return getParticlesInCell(x,y-m);
            } else {
                return Collections.EMPTY_SET;
            }

        }
        return board.get(new Point(x,y));
    }

    private Set<Particle> getSurroundingParticles(Point cell) {
        Set<Particle> surroundingParticles = new HashSet<>();
        surroundingParticles.addAll(getParticlesInCell((int) cell.getX(), (int) cell.getY()));
        surroundingParticles.addAll(getParticlesInCell((int) cell.getX(), (int) cell.getY() + 1));
        surroundingParticles.addAll(getParticlesInCell((int) cell.getX() + 1, (int) cell.getY() + 1));
        surroundingParticles.addAll(getParticlesInCell((int) cell.getX() + 1, (int) cell.getY()));
        surroundingParticles.addAll(getParticlesInCell((int) cell.getX() + 1, (int) cell.getY() - 1));
        return surroundingParticles;
    }

    private Set<Particle> getParticleNeighbours(Particle particle, Set<Particle> surroundingParticles, Set<Particle> checkedParticles) {
        Set<Particle> particleNeighbours = new HashSet<>();

        for(Particle p : surroundingParticles) {
            if(!p.equals(particle) && !checkedParticles.contains(p) && particlesInRange(particle, p)){
                particleNeighbours.add(p);
            }
        }

        return particleNeighbours;
    }

    private boolean particlesInRange(Particle p1, Particle p2) {
        if(Particle.calculateBorderDistance(p1,p2) <= rc) {
            return true;
        }

        if(!periodic) {
            return false;
        }

        /*TODO: falta caso periodic*/
        return false;
    }

    /* me devuelve todas las particulas que cumplen con el radio de acción y estan en las celdas vecinas*/
    public Map<Particle, Set<Particle>> getAllCellNeighbours(Point cell) {
        /*me va a dar todas las particulas de una cell determianda*/
        Set<Particle> cellParticles = board.get(cell);
        Set<Particle> surroundingParticles = getSurroundingParticles(cell);

        Set<Particle> checkedParticles = new HashSet<>();
        Map<Particle, Set<Particle>> allCellNeighbours = new HashMap<>();

        for(Particle p : cellParticles) {
            /*aca ya le voy a estar "adjuntando" a la particula las particulas vecinas que cumplan
            con el radio de accion
             */
            Set<Particle> particleNeighbours = getParticleNeighbours(p, surroundingParticles, checkedParticles);
            checkedParticles.add(p);
            allCellNeighbours.put(p, particleNeighbours);
        }

        return allCellNeighbours;
    }




}
