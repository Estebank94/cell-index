import java.awt.*;
import java.util.*;


public class CIM {

    private Cell[][] grid;
    Map<Particle, Point> allParticles;
    double L;  /* Silo Length */
    double W;  /* Silo Width */
    private double cellSize;
    private int rows;
    private int cols;
    private double rc;
    boolean periodic;
    private Set<Particle> particles;

    public CIM(double L, double rc, boolean periodic){
        this.L = L;
        this.rc = rc;
        this.periodic = periodic;
        this.cols = (int) Math.floor(L/rc);
        this.cellSize = ((double) L) / cols;
        this.grid = new Cell[cols][cols];

        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j <  grid.length; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    private void addNeighbors(Cell cell, Particle particle, Map<Particle, Set<Particle>> particleMap, int deltaX, int deltaY) {
        for (Particle p : cell.getParticles()) {
            if (!p.equals(particle)) {
                if (!particleMap.get(particle).contains(p)) {
                    double distance = Math.max(getDistance(particle, p, deltaX, deltaY), 0);
                    if (distance <= rc) {
                        particleMap.get(particle).add(p);
                        if (!particleMap.containsKey(p))
                            particleMap.put(p, new HashSet<>());
                        particleMap.get(p).add(particle);
                    }
                }
            }
        }
    }

    /* con deltaX & deltaY ya consideramos los cells aledaños */
    private double getDistance(Particle fixed, Particle moving, int deltaX, int deltaY) {
        return Math.sqrt(Math.pow(fixed.getX() - (moving.getX() + deltaX * L), 2) +
                        Math.pow(fixed.getY() - (moving.getY() + deltaY * L), 2)) - fixed.r - moving.r;
    }


    public Map<Particle, Set<Particle>> findNeighbors() {
        Map<Particle, Set<Particle>> map = new HashMap<>();

        for (Particle particle : allParticles.keySet()) {
            if (!map.containsKey(particle))
                map.put(particle, new HashSet<>());
            Point coords = allParticles.get(particle);
            Cell aux;

            aux = grid[coords.x][coords.y];
            addNeighbors(aux, particle, map, 0, 0);

            aux = grid[(coords.x - 1 + cols) % cols][coords.y];
            if (coords.x - 1 >= 0) {
                addNeighbors(aux, particle, map, 0, 0);
            } else if (periodic) {
                addNeighbors(aux, particle, map, -1, 0);
            }

            aux = grid[(coords.x - 1 + cols) % cols][(coords.y + 1) % cols];
            if (coords.x - 1 >= 0 && coords.y + 1 < cols) {
                addNeighbors(aux, particle, map, 0, 0);
            } else if (periodic) {
                addNeighbors(aux, particle, map, coords.x - 1 >= 0 ? 0 : -1, coords.y + 1 < cols ? 0 : 1);
            }

            aux = grid[coords.x][(coords.y + 1) % cols];
            if (coords.y + 1 < cols) {
                addNeighbors(aux, particle, map, 0, 0);
            } else if (periodic) {
                addNeighbors(aux, particle, map, 0, 1);
            }

            aux = grid[(coords.x + 1) % cols][(coords.y + 1) % cols];
            if (coords.x + 1 < cols && coords.y + 1 < cols) {
                addNeighbors(aux, particle, map, 0, 0);
            } else if (periodic) {
                addNeighbors(aux, particle, map, coords.x + 1 < cols ? 0 : 1, coords.y + 1 < cols ? 0 : 1);
            }

        }

        return map;
    }


    public void load(Set<Particle> particles) {

        allParticles = new HashMap<>();

        double maxRad = 0;

        for (Particle p : particles) {

            int x = (int) (p.getX() / cellSize);
            int y = (int) (p.getY() / cellSize);
            grid[x][y].getParticles().add(p);
            allParticles.put(p, new Point(x, y));
            if (maxRad < p.r)
                maxRad = p.r;
        }
    }

}
