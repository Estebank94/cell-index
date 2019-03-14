package com.company;

import java.util.*;

public class Board {
    private int l;
    private int m;
    private double rc;
    private double cellSize;
    private HashMap<Point,Set<Particle>> board;
    private boolean periodic;

    public Board(int l, int m, double rc, Set<Particle> particles,boolean periodic) {
        this.l = l;
        this.m = m;
        this.rc = rc;
        this.cellSize = (double)l/m;
        this.board = new HashMap<>();
        this.periodic = periodic;

        for(int i = 0; i < m ; i++){
            for(int j = 0 ; j < m ; j++) {
                board.put(new Point(i, j), new HashSet<>());
            }
        }

        for(Particle particle : particles){
            Point j = getCell(particle.getLocation());
            try {
                board.get(j).add(particle);
            }catch (Exception e){
                System.out.println(j + " " +particle);
            }

        }

    }

    public double getRc() {
        return rc;
    }

    private Point getCell(Point location){
        int x = (int)(location.getX()/cellSize);
        int y = (int)(location.getY()/cellSize);
        return new Point(x,y);
    }


    public Set<Particle> getParticlesInCell(int x , int y){
        if(x < 0){
            if(periodic){
                return getParticlesInCell(x+m,y);
            }else{
                return Collections.EMPTY_SET;
            }

        } if(x >= m){
            if(periodic){
                return getParticlesInCell(x-m,y);
            }else{
                return Collections.EMPTY_SET;
            }
        }
        if (y <0){
            if(periodic){
                return getParticlesInCell(x,y+m);
            }else{
                return Collections.EMPTY_SET;
            }
        }
        if( y>=m){
            if(periodic){
                return getParticlesInCell(x,y-m);
            }else{
                return Collections.EMPTY_SET;
            }

        }
        return board.get(new Point(x,y));
    }

    private Set<Particle> getSurroundingParticles(Point field){
        Set<Particle> nearParticles = new HashSet<>();
        nearParticles.addAll(getParticlesInCell((int )field.getX()      ,   (int) field.getY()));
        nearParticles.addAll(getParticlesInCell((int )field.getX()      ,(int) field.getY()+1));
        nearParticles.addAll(getParticlesInCell((int )field.getX()+1 ,(int) field.getY()+1));
        nearParticles.addAll(getParticlesInCell((int )field.getX()+1 ,   (int) field.getY()));
        nearParticles.addAll(getParticlesInCell((int )field.getX()+1 ,(int) field.getY()-1));
        return nearParticles;
    }

    public Set<Particle> getNeighboursOfParticle(Particle particle, Set<Particle> analyzed, Set<Particle> nearParticles){

        Set<Particle> neighbours = new HashSet<>();

        for(Particle other : nearParticles){
            if(!other.equals(particle) && !analyzed.contains(other)) {
                if(particlesInRange(particle,other)){
                    neighbours.add(other);
                }
            }
        }

        return neighbours;
    }

    private boolean particlesInRange(Particle p1, Particle p2) {

        if(Particle.borderDistanceBetweenParticles(p1,p2) <= rc) {
            return true;
        }

        if(periodic){
            double mx = p1.getLocation().getX();
            double my = p1.getLocation().getY();
            double ox = p2.getLocation().getX();
            double oy = p2.getLocation().getY();

            return Point.distanceBetween(new Point(mx,my+l), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx+l,my), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx+l,my+l), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx+l,my-l), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx,my-l), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx-l,my-l), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx-l,my), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc
                    || Point.distanceBetween(new Point(mx-l,my+l), new Point(ox,oy)) - p1.getRatio() - p2.getRatio() <= rc;
        }

        return false;

    }

    public Map<Particle,Set<Particle>> analyzeCell(Point cell){
        Set<Particle> particles = board.get(cell);
        Set<Particle> surroundingParticles = getSurroundingParticles(cell);

        Set<Particle> analyzed = new HashSet<>();
        Map<Particle,Set<Particle>> cellNeighbours = new HashMap<>();

        for(Particle particle : particles){
            Set<Particle> neighbors = getNeighboursOfParticle(particle,analyzed, surroundingParticles);

            analyzed.add(particle);
            cellNeighbours.put(particle,neighbors);
        }
        return cellNeighbours;
    }
}
